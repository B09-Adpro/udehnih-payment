package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.clients.CourseServiceClient;
import id.ac.ui.cs.advprog.udehnihpayment.clients.DashboardServiceClient;
import id.ac.ui.cs.advprog.udehnihpayment.dto.response.PaymentDetailDTO;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.exception.TransactionNotFoundException;
import id.ac.ui.cs.advprog.udehnihpayment.exception.UnauthorizedAccessException;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.model.PaymentDetails;
import id.ac.ui.cs.advprog.udehnihpayment.repository.PaymentRepository;
import id.ac.ui.cs.advprog.udehnihpayment.strategy.PaymentStrategy;
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

    private final List<PaymentStrategy> paymentStrategies;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            CourseServiceClient courseServiceClient,
            DashboardServiceClient dashboardServiceClient,
            @Value("${services.course.api-key}") String courseApiKey,
            @Value("${services.dashboard.api-key}") String dashboardApiKey,
            List<PaymentStrategy> paymentStrategies) {
        this.paymentRepository = paymentRepository;
        this.courseServiceClient = courseServiceClient;
        this.dashboardServiceClient = dashboardServiceClient;
        this.courseApiKey = courseApiKey;
        this.dashboardApiKey = dashboardApiKey;
        this.paymentStrategies = paymentStrategies;
    }

    @Override
    public Payment createPayment(Payment payment) {
        if (payment.getPaymentStatus() == null) {
            payment.setPaymentStatus(PaymentStatus.PENDING);
        }
        if (payment.getExpiresAt() == null) {
            payment.setExpiresAt(LocalDateTime.now().plusDays(1));
        }

        Payment saved = paymentRepository.save(payment);

        PaymentStrategy strategy = findStrategy(saved.getPaymentMethod());
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

        PaymentStrategy strategy = findStrategy(method);
        String result = strategy.processPayment(payment);
        System.out.println("Payment processing result: " + result);

        return payment;
    }

    private PaymentStrategy findStrategy(PaymentMethod method) {
        return paymentStrategies.stream()
                .filter(s -> s.supports(method.getValue()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No strategy found for method: " + method));
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

    @Override
    public Payment confirmBankTransfer(Long transactionId, Long userId) {
        Payment payment = findByTransactionId(transactionId);
        if (payment == null) throw new TransactionNotFoundException("Payment not found");
        if (!payment.getUserId().equals(userId)) throw new UnauthorizedAccessException("Not your payment");
        if (payment.getPaymentMethod() != PaymentMethod.BANK_TRANSFER)
            throw new IllegalArgumentException("Confirmation only for bank transfer payments");
        if (payment.getPaymentStatus() != PaymentStatus.PENDING)
            throw new IllegalStateException("Payment is not in PENDING state");
        if (payment.getPaymentDetails() != null && payment.getPaymentDetails().isConfirmation())
            throw new IllegalStateException("Transfer already confirmed");

        if (payment.getPaymentDetails() == null) payment.setPaymentDetails(new PaymentDetails());
        payment.getPaymentDetails().setConfirmation(true);
        payment.getPaymentDetails().setConfirmedAt(LocalDateTime.now());
        payment.setPaymentStatus(PaymentStatus.PENDING);

        payment.preUpdate();
        return paymentRepository.save(payment);
    }
}