package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment createPayment(Payment payment) {
        // Set status awal
        payment.setPaymentStatus("PENDING");
        Payment saved = paymentRepository.save(payment);

        PaymentStrategy strategy;
        switch (saved.getPaymentMethod()) {
            case "BankTransfer":
                strategy = new BankTransferPaymentStrategy();
                break;
            case "CreditCard":
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

    // -- Strategy Pattern Implementation --

    private interface PaymentStrategy {
        String generateInstructions(Payment payment);
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