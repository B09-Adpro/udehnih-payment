package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.enums.RefundStatus;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.model.Refund;
import id.ac.ui.cs.advprog.udehnihpayment.repository.PaymentRepository;
import id.ac.ui.cs.advprog.udehnihpayment.repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RefundRepository refundRepository;

    @Override
    public Refund requestRefund(UUID transactionId, String reason, String details) {
        Payment payment = paymentRepository.findByTransactionId(transactionId);

        if (payment == null) {
            throw new RuntimeException("Payment not found for transactionId: " + transactionId);
        }

        // Validasi alasan refund
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Refund reason cannot be empty");
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
}
