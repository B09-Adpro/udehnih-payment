package id.ac.ui.cs.advprog.udehnihpayment.strategy;

import id.ac.ui.cs.advprog.udehnihpayment.enums.Bank;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategi untuk memproses pembayaran dengan metode Transfer Bank
 */
@Component
public class BankTransferStrategy implements PaymentStrategy {    
    @Override
    public String processPayment(Payment payment) {
        // Implementasi proses pembayaran bank transfer
        return "Bank Transfer payment for course ID " + payment.getCourseId() +
                " with amount " + payment.getAmount() + 
                " processed successfully.";
    }
      @Override
    public String generateInstructions(Payment payment) {
        StringBuilder instructions = new StringBuilder("Silakan transfer ke salah satu rekening berikut:\n");
        for (Bank bank : Bank.values()) {
            instructions.append(String.format("- %s: %s a.n %s\n", 
                bank.getBankName(), bank.getAccountNumber(), bank.getAccountName()));
        }
        instructions.append("\nSetelah melakukan transfer, harap lakukan konfirmasi 'Saya sudah transfer'.\n");
        instructions.append("Total pembayaran: Rp").append(payment.getAmount());
        return instructions.toString();
    }
    
    @Override
    public boolean validatePayment(Payment payment) {
        return payment != null && 
               payment.getAmount().compareTo(BigDecimal.ZERO) > 0 &&
               payment.getCourseId() != null;
    }
    
    @Override
    public boolean supports(String paymentMethodName) {
        return PaymentMethod.BANK_TRANSFER.getValue().equals(paymentMethodName);
    }
}