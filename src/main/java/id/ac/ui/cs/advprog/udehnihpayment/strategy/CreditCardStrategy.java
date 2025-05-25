package id.ac.ui.cs.advprog.udehnihpayment.strategy;

import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategi untuk memproses pembayaran dengan metode Kartu Kredit
 */
@Component
public class CreditCardStrategy implements PaymentStrategy {    
    @Override
    public String processPayment(Payment payment) {
        // Implementasi proses pembayaran kartu kredit
        return "Credit Card payment for course ID " + payment.getCourseId() +
                " with amount " + payment.getAmount() + 
                " processed successfully.";
    }
    
    @Override
    public String generateInstructions(Payment payment) {
        StringBuilder instructions = new StringBuilder();
        instructions.append("Untuk menyelesaikan pembayaran dengan Kartu Kredit:\n");
        instructions.append("1. Masukkan nomor kartu kredit (16 digit)\n");
        instructions.append("2. Masukkan nama pemilik kartu\n");
        instructions.append("3. Masukkan tanggal kadaluarsa (MM/YY)\n");
        instructions.append("4. Masukkan Card Verification Code (CVC)\n\n");
        instructions.append("Total pembayaran: Rp").append(payment.getAmount());
        return instructions.toString();
    }
    
    @Override
    public boolean validatePayment(Payment payment) {
        // Validasi pembayaran kartu kredit
        // Minimal harus memiliki payment, amount > 0, dan courseId
        return payment != null && 
               payment.getAmount().compareTo(BigDecimal.ZERO) > 0 &&
               payment.getCourseId() != null;
    }
    
    @Override
    public boolean supports(String paymentMethodName) {
        return PaymentMethod.CREDIT_CARD.getValue().equals(paymentMethodName);
    }
}
