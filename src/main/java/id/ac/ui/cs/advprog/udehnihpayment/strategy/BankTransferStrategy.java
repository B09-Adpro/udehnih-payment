package id.ac.ui.cs.advprog.udehnihpayment.strategy;

import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BankTransferStrategy implements PaymentStrategy {
    private static final String BANK_NAME = "BCA";
    private static final String ACCOUNT_NUMBER = "123-456-7890";
    private static final String ACCOUNT_HOLDER = "Udehnih";
    
    @Override
    public String processPayment(Payment payment) {
        // Implementasi proses pembayaran bank transfer
        return "Bank Transfer payment for course ID " + payment.getCourseId() +
                " with amount " + payment.getAmount() + 
                " processed successfully.";
    }
    
    @Override
    public String generateInstructions(Payment payment) {
        return "Silakan transfer ke rekening " + BANK_NAME + " " + 
               ACCOUNT_NUMBER + " a.n " + ACCOUNT_HOLDER + 
               " dengan nominal Rp" + payment.getAmount();
    }
    
    @Override
    public boolean validatePayment(Payment payment) {
        // Validasi pembayaran bank transfer
        return payment != null && 
               payment.getAmount().compareTo(BigDecimal.ZERO) > 0 &&
               payment.getCourseId() != null;
    }
    
    @Override
    public boolean supports(String paymentMethodName) {
        return PaymentMethod.BANK_TRANSFER.getValue().equals(paymentMethodName);
    }
}