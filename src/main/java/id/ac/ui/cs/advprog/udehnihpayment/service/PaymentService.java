package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    Payment createPayment(Payment payment);
    List<Payment> getPaymentsByUser(String userId);
    List<String> getPaymentMethods();
    Payment processPayment(UUID transactionId, String paymentMethod);
    Payment findByIdTransaksi(UUID transactionId);
    Payment savePayment(Payment payment);
}
