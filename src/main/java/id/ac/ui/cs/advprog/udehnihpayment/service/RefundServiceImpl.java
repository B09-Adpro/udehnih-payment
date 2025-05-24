package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.clients.CourseServiceClient;
import id.ac.ui.cs.advprog.udehnihpayment.clients.DashboardServiceClient;
import id.ac.ui.cs.advprog.udehnihpayment.enums.RefundStatus;
import id.ac.ui.cs.advprog.udehnihpayment.exception.InvalidRefundReasonException;
import id.ac.ui.cs.advprog.udehnihpayment.exception.RefundAlreadyRequestedException;
import id.ac.ui.cs.advprog.udehnihpayment.exception.RefundTooLateException;
import id.ac.ui.cs.advprog.udehnihpayment.exception.TransactionNotFoundException;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.model.Refund;
import id.ac.ui.cs.advprog.udehnihpayment.repository.PaymentRepository;
import id.ac.ui.cs.advprog.udehnihpayment.repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private CourseServiceClient courseServiceClient;

    @Autowired
    private DashboardServiceClient dashboardServiceClient;

    @Value("${services.course.api-key}")
    private String courseApiKey;

    @Value("${services.dashboard.api-key}")
    private String dashboardApiKey;

    @Override
    public Refund requestRefund(Long transactionId, String reason, String details) {
        Payment payment = paymentRepository.findByTransactionId(transactionId);

        // Error 404 - Transaction not found
        if (payment == null) {
            throw new TransactionNotFoundException("Payment with ID " + transactionId + " not found");
        }

        // Error 400 - Invalid refund reason
        if (reason == null || reason.trim().isEmpty()) {
            throw new InvalidRefundReasonException("Refund reason cannot be empty");
        }
        
        // Error 409 - Refund already requested
        List<Refund> existingRefunds = refundRepository.findByPaymentTransactionId(transactionId);
        if (!existingRefunds.isEmpty()) {
            throw new RefundAlreadyRequestedException("A refund has already been requested for transaction ID: " + transactionId);
        }
        
        // Error 403 - Refund no longer available (purchased too long ago)
        LocalDateTime purchaseDate = payment.getCreatedAt();
        LocalDateTime refundCutoff = LocalDateTime.now().minusDays(30); // Example: 30-day refund policy
        
        if (purchaseDate.isBefore(refundCutoff)) {
            throw new RefundTooLateException("Refund is no longer available. Course was purchased more than 30 days ago.");
        }

        Refund refund = Refund.builder()
                .payment(payment)
                .reason(reason)
                .details(details != null ? details : "")
                .refundStatus(RefundStatus.PENDING)
                .build();

        return refundRepository.save(refund);
    }

    @Override
    public List<Refund> getAllRefunds() {
        return refundRepository.findAll();
    }

    @Override
    public Refund updateRefundStatus(Long refundId, RefundStatus status, String approvedBy) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RuntimeException("Refund not found with ID: " + refundId));
        
        RefundStatus oldStatus = refund.getRefundStatus();
        refund.setRefundStatus(status);
        refund.preUpdate(); // Update timestamp
        
        Refund updatedRefund = refundRepository.save(refund);
        
        if (status != oldStatus) {
            try {
                // Notifikasi ke Course Service
                notifyCourseServiceAboutRefund(updatedRefund, approvedBy, status);
                notifyDashboardServiceAboutRefund(updatedRefund, approvedBy);
            } catch (Exception e) {
                System.err.println("Error notifying services about refund: " + e.getMessage());
            }
        }
        
        return updatedRefund;
    }

    @Override
    public Refund findById(Long refundId) {
        return refundRepository.findById(refundId)
                .orElseThrow(() -> new RuntimeException("Refund not found with ID: " + refundId));
    }

    private void notifyDashboardServiceAboutRefund(Refund refund, String approvedBy) {
        Payment payment = refund.getPayment();
        
        Map<String, Object> refundData = new HashMap<>();
        refundData.put("refundId", refund.getId());
        refundData.put("transactionId", payment.getTransactionId());
        refundData.put("courseId", payment.getCourseId());
        refundData.put("userId", payment.getUserId());
        refundData.put("amount", payment.getAmount());
        refundData.put("status", refund.getRefundStatus().getValue());
        refundData.put("reason", refund.getReason());
        refundData.put("details", refund.getDetails());
        refundData.put("approvedBy", approvedBy);
        refundData.put("updatedAt", refund.getUpdatedAt().toString());
        
        try {
            dashboardServiceClient.notifyRefundUpdate(dashboardApiKey, refundData);
        } catch (Exception e) {
            System.err.println("Error notifying dashboard service: " + e.getMessage());
        }
    }

    private void notifyCourseServiceAboutRefund(Refund refund, String approvedBy, RefundStatus status) {
        Payment payment = refund.getPayment();
        
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("enrollmentId", payment.getEnrollmentId());
        paymentData.put("studentId", payment.getUserId());
        paymentData.put("courseId", payment.getCourseId());
        
        boolean isApproved = false; // Default untuk APPROVED status
        String message;
        
        if (status == RefundStatus.APPROVED) {
            isApproved = false; // Untuk membatalkan enrollment
            message = "Refund approved by " + approvedBy + " at " + LocalDateTime.now();
        } else if (status == RefundStatus.REJECTED) {
            isApproved = true; // Enrollment tetap aktif
            message = "Refund rejected by " + approvedBy + " at " + LocalDateTime.now();
        } else {
            return;
        }
        
        paymentData.put("approved", isApproved);
        paymentData.put("message", message);
        paymentData.put("refundId", refund.getId());
        paymentData.put("refundReason", refund.getReason());
        
        courseServiceClient.updateEnrollmentStatus(courseApiKey, paymentData);
    }
}
