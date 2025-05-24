package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.clients.CourseServiceClient;
import id.ac.ui.cs.advprog.udehnihpayment.clients.DashboardServiceClient;
import id.ac.ui.cs.advprog.udehnihpayment.dto.response.PaymentDetailDTO;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.exception.TransactionNotFoundException;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.model.PaymentDetails;
import id.ac.ui.cs.advprog.udehnihpayment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CourseServiceClient courseServiceClient;

    @Autowired
    private DashboardServiceClient dashboardServiceClient;

    @Value("${services.course.api-key}")
    private String courseApiKey;

    @Value("${services.dashboard.api-key}")
    private String dashboardApiKey;

    @Override
    public Payment createPayment(Payment payment) {
        if (payment.getPaymentStatus() == null) {
            payment.setPaymentStatus(PaymentStatus.PENDING);
        }
        if (payment.getExpiresAt() == null) {
            payment.setExpiresAt(LocalDateTime.now().plusDays(1));
        }

        Payment saved = paymentRepository.save(payment);

        PaymentStrategy strategy;
        PaymentMethod method = saved.getPaymentMethod();

        switch (method) {
            case BANK_TRANSFER:
                strategy = new BankTransferPaymentStrategy();
                break;
            case CREDIT_CARD:
                strategy = new CreditCardPaymentStrategy();
                break;
            default:
                throw new IllegalArgumentException("Metode pembayaran tidak dikenal: " + saved.getPaymentMethod());
        }

        String instructions = strategy.generateInstructions(saved);
        System.out.println("Payment Instructions: " + instructions);

        return saved;
    }

    @Override
    public List<Payment> getAllPayments(Long userId) {
        return paymentRepository.findAllByUserId(userId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public List<String> getPaymentMethods() {
        return Arrays.stream(PaymentMethod.values())
               .map(PaymentMethod::getValue)
               .collect(Collectors.toList());
    }

    @Override
    public Payment processPayment(Long transactionId, String paymentMethod) {
        Payment payment = paymentRepository.findByTransactionId(transactionId);
        PaymentMethod method = PaymentMethod.fromString(paymentMethod);
        if (payment == null) {
            throw new TransactionNotFoundException("Payment with ID " + transactionId + " not found");
        }

        if (!payment.getPaymentMethod().equals(method)) {
            throw new IllegalArgumentException("Payment method mismatch. Expected: " +
                payment.getPaymentMethod() + ", Received: " + paymentMethod);
        }

        PaymentStrategy strategy;

        switch (method) {
            case BANK_TRANSFER:
                strategy = new BankTransferPaymentStrategy();
                break;
            case CREDIT_CARD:
                strategy = new CreditCardPaymentStrategy();
                break;
            default:
                throw new IllegalArgumentException("Unsupported payment method: " + method);
        }

        String result = processPaymentWithStrategy(payment, strategy);
        System.out.println("Payment processing result: " + result);

        return payment;
    }

    @Override
    public Payment findByTransactionId(Long transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
    }

    @Override
    public Payment updatePaymentStatus(Long transactionId, PaymentDetailDTO.Details updateRequest) {
        Payment payment = findByTransactionId(transactionId);
        
        if (payment == null) {
            throw new TransactionNotFoundException("Payment not found for transactionId: " + transactionId);
        }
        
        // Update payment status berdasarkan approval status
        if (updateRequest.isAdminApproval()) {
            payment.setPaymentStatus(PaymentStatus.PAID);
        }
        
        // Pastikan objek PaymentDetails ada
        if (payment.getPaymentDetails() == null) {
            payment.setPaymentDetails(new PaymentDetails());
        }

        boolean wasApproved = payment.getPaymentDetails() != null && payment.getPaymentDetails().isAdminApproval();
        
        // Set semua properti dari DTO ke entitas
        PaymentDetails details = payment.getPaymentDetails();
        
        details.setConfirmation(updateRequest.isConfirmation());
        if (updateRequest.isConfirmation()) {
            details.setConfirmedAt(updateRequest.getConfirmedAt() != null ? 
                                updateRequest.getConfirmedAt() : LocalDateTime.now());
        }
        
        details.setAdminApproval(updateRequest.isAdminApproval());
        if (updateRequest.isAdminApproval()) {
            details.setApprovedAt(updateRequest.getApprovedAt() != null ? 
                                updateRequest.getApprovedAt() : LocalDateTime.now());
            details.setApprovedBy(updateRequest.getApprovedBy());
        }
        
        payment.preUpdate();
        payment = paymentRepository.save(payment);

        if (updateRequest.isAdminApproval() && !wasApproved) {
            try {
                notifyCourseService(payment);
                notifyDashboardAboutPayment(payment);
            } catch (Exception e) {
                System.err.println("Error notifying services: " + e.getMessage());
            }
        }
        
        return payment;
    }

    private void notifyCourseService(Payment payment) {
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("enrollmentId", payment.getEnrollmentId());
        paymentData.put("studentId", payment.getUserId());
        paymentData.put("courseId", payment.getCourseId());
        paymentData.put("approved", payment.getPaymentStatus() == PaymentStatus.PAID);
        paymentData.put("message", "Payment processed and approved by " + 
                    payment.getPaymentDetails().getApprovedBy() + 
                    " at " + payment.getPaymentDetails().getApprovedAt());
        
        courseServiceClient.updateEnrollmentStatus(courseApiKey, paymentData);
    }

    private void notifyDashboardAboutPayment(Payment payment) {
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("transactionId", payment.getTransactionId());
        paymentData.put("courseId", payment.getCourseId());
        paymentData.put("userId", payment.getUserId());
        paymentData.put("enrollmentId", payment.getEnrollmentId());
        paymentData.put("amount", payment.getAmount());
        paymentData.put("status", payment.getPaymentStatus().getValue());
        paymentData.put("paymentMethod", payment.getPaymentMethod().getValue());
        
        if (payment.getPaymentDetails() != null) {
            paymentData.put("approvedBy", payment.getPaymentDetails().getApprovedBy());
            if (payment.getPaymentDetails().getApprovedAt() != null) {
                paymentData.put("approvedAt", payment.getPaymentDetails().getApprovedAt().toString());
            }
            paymentData.put("adminApproval", payment.getPaymentDetails().isAdminApproval());
        }
        
        paymentData.put("updatedAt", payment.getUpdatedAt().toString());
        paymentData.put("createdAt", payment.getCreatedAt().toString());
        
        try {
            dashboardServiceClient.notifyPaymentUpdate(dashboardApiKey, paymentData);
        } catch (Exception e) {
            System.err.println("Error notifying dashboard service about payment: " + e.getMessage());
        }
    }

    private String processPaymentWithStrategy(Payment payment, PaymentStrategy strategy) {
        return strategy.processPayment(payment);
    }

    // -- Strategy Pattern Implementation --
    private interface PaymentStrategy {
        String generateInstructions(Payment payment);

        default String processPayment(Payment payment) {
            return "Payment for course ID " + payment.getCourseId() +
                   " with amount " + payment.getAmount() + 
                   " processed successfully.";
        }
    }

    private class BankTransferPaymentStrategy implements PaymentStrategy {
        @Override
        public String generateInstructions(Payment payment) {
            return "Silakan transfer ke rekening BCA 123-456-7890 a.n Udehnih dengan nominal Rp" + payment.getAmount();
        }
    }

    private class CreditCardPaymentStrategy implements PaymentStrategy {
        @Override
        public String generateInstructions(Payment payment) {
            return "Silakan masukkan detail kartu kredit Anda di halaman pembayaran. Total tagihan: Rp" + payment.getAmount();
        }
    }
}