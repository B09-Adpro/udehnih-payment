package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.dto.response.PaymentDetailDTO;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    Payment createPayment(Payment payment);
    List<String> getPaymentMethods();
    List<Payment> getAllPayments(Long userId);
    List<Payment> getAllPayments();
    Payment processPayment(UUID transactionId, String paymentMethod);
    Payment findByTransactionId(UUID transactionId);
    Payment updatePaymentStatus(UUID transactionId, PaymentDetailDTO.Details updateRequest);
    Payment confirmBankTransfer(UUID transactionId, Long userId);
}
