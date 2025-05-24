package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.dto.response.PaymentDetailDTO;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;

import java.util.List;

public interface PaymentService {
    Payment createPayment(Payment payment);
    List<String> getPaymentMethods();
    List<Payment> getAllPayments(Long userId);
    List<Payment> getAllPayments();
    Payment processPayment(Long transactionId, String paymentMethod);
    Payment findByTransactionId(Long transactionId);
    Payment updatePaymentStatus(Long transactionId, PaymentDetailDTO.Details updateRequest);
}
