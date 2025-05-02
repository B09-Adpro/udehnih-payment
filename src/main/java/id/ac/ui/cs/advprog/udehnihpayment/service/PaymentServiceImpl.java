package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment createPayment(Payment payment) {
        // Set status awal
        payment.setPaymentStatus(PaymentStatus.PENDING.getValue());
        Payment saved = paymentRepository.save(payment);

        PaymentStrategy strategy;
        PaymentMethod method = PaymentMethod.fromString(saved.getPaymentMethod());
        
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
    public List<Payment> getPaymentsByUser(String userId) {
        return paymentRepository.findAllByUserId(userId);
    }

    @Override
    public List<String> getPaymentMethods() {
        return Arrays.stream(PaymentMethod.values())
               .map(PaymentMethod::getValue)
               .collect(Collectors.toList());
    }

    @Override
    public Payment processPayment(Long transactionId, String paymentMethod) {
        Payment payment = paymentRepository.findByIdTransaksi(transactionId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment with ID " + transactionId + " not found");
        }
        
        if (!payment.getPaymentMethod().equals(paymentMethod)) {
            throw new IllegalArgumentException("Payment method mismatch. Expected: " + 
                payment.getPaymentMethod() + ", Received: " + paymentMethod);
        }
        
        // Only validate that status is PENDING, don't change it
        if (!payment.getPaymentStatus().equals(PaymentStatus.PENDING.getValue())) {
            throw new IllegalStateException("Payment cannot be processed. Current status: " + 
                payment.getPaymentStatus());
        }
        
        PaymentMethod method = PaymentMethod.fromString(paymentMethod);
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
    public Payment findByIdTransaksi(Long transactionId) {
        return paymentRepository.findByIdTransaksi(transactionId);
    }

    @Override
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    private String processPaymentWithStrategy(Payment payment, PaymentStrategy strategy) {
        return strategy.processPayment(payment);
    }

    // -- Strategy Pattern Implementation --
    private interface PaymentStrategy {
        String generateInstructions(Payment payment);

        default String processPayment(Payment payment) {
            return "Payment for course ID " + payment.getCourseId() + 
                   " with amount " + payment.getCoursePrice() + 
                   " processed successfully.";
        }
    }

    private class BankTransferPaymentStrategy implements PaymentStrategy {
        @Override
        public String generateInstructions(Payment payment) {
            return "Silakan transfer ke rekening BCA 123-456-7890 a.n Udehnih dengan nominal Rp" + payment.getCoursePrice();
        }
    }

    private class CreditCardPaymentStrategy implements PaymentStrategy {
        @Override
        public String generateInstructions(Payment payment) {
            return "Silakan masukkan detail kartu kredit Anda di halaman pembayaran. Total tagihan: Rp" + payment.getCoursePrice();
        }
    }
}