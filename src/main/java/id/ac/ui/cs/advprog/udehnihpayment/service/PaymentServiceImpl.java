package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment createPayment(Payment payment) {
        payment.setTransactionId(UUID.randomUUID());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        Payment saved = paymentRepository.save(payment);

        PaymentStrategy strategy;
        PaymentMethod method = PaymentMethod.fromString(saved.getPaymentMethod().toString());
        
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
        return paymentRepository.findAll();
    }

    @Override
    public List<String> getPaymentMethods() {
        return Arrays.stream(PaymentMethod.values())
               .map(PaymentMethod::getValue)
               .collect(Collectors.toList());
    }

    @Override
    public Payment processPayment(UUID transactionId, String paymentMethod) {
        Payment payment = paymentRepository.findByTransactionId(transactionId);
        PaymentMethod method = PaymentMethod.fromString(paymentMethod);
        if (payment == null) {
            throw new IllegalArgumentException("Payment with ID " + transactionId + " not found");
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
    public Payment findByTransactionId(UUID transactionId) {
        return paymentRepository.findByTransactionId(transactionId);
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