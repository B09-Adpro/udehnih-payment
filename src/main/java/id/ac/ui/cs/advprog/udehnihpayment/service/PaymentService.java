package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;

import java.util.List;

public interface PaymentService {
    Payment createPayment(Payment payment);
    List<Payment> getPaymentsByUser(String userId);
    List<String> getPaymentMethods();
    Payment processPayment(Long transactionId, String paymentMethod);
    Payment findByIdTransaksi(Long transactionId);
    Payment savePayment(Payment payment);
}
