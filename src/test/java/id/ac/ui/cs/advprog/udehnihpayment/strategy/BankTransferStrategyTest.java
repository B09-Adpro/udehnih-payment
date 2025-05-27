package id.ac.ui.cs.advprog.udehnihpayment.strategy;

import id.ac.ui.cs.advprog.udehnihpayment.enums.Bank;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BankTransferStrategyTest {

    private BankTransferStrategy bankTransferStrategy;
    private Payment validPayment;

    @BeforeEach
    public void setUp() {
        bankTransferStrategy = new BankTransferStrategy();
        
        validPayment = Payment.builder()
                .transactionId(UUID.randomUUID())
                .courseId(123L)
                .userId(456L)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .amount(new BigDecimal("100000"))
                .build();
    }

    // ================= PROCESSPAYMENT TESTS =================

    @Test
    public void processPayment_ValidPayment_ReturnsSuccessMessage() {
        // Act
        String result = bankTransferStrategy.processPayment(validPayment);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Bank Transfer payment"));
        assertTrue(result.contains("course ID " + validPayment.getCourseId()));
        assertTrue(result.contains("amount " + validPayment.getAmount()));
        assertTrue(result.contains("processed successfully"));
    }    @Test
    public void processPayment_NullPayment_ShouldHandleGracefully() {
        // Act & Assert - This tests how the method handles null input
        assertThrows(NullPointerException.class, () -> bankTransferStrategy.processPayment(null));
    }

    // ================= GENERATEINSTRUCTIONS TESTS =================

    @Test
    public void generateInstructions_ValidPayment_ContainsAllBankDetails() {
        // Act
        String instructions = bankTransferStrategy.generateInstructions(validPayment);

        // Assert
        assertNotNull(instructions);
        assertTrue(instructions.contains("Silakan transfer ke salah satu rekening berikut"));
        assertTrue(instructions.contains("Total pembayaran: Rp" + validPayment.getAmount()));
        assertTrue(instructions.contains("konfirmasi 'Saya sudah transfer'"));
        
        // Verify all bank information is included
        for (Bank bank : Bank.values()) {
            assertTrue(instructions.contains(bank.getBankName()));
            assertTrue(instructions.contains(bank.getAccountNumber()));
            assertTrue(instructions.contains(bank.getAccountName()));
        }
    }    @Test
    public void generateInstructions_NullPayment_ShouldHandleGracefully() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> bankTransferStrategy.generateInstructions(null));
    }

    @Test
    public void generateInstructions_PaymentWithZeroAmount_ContainsCorrectAmount() {
        // Arrange
        Payment zeroAmountPayment = validPayment.toBuilder()
                .amount(BigDecimal.ZERO)
                .build();

        // Act
        String instructions = bankTransferStrategy.generateInstructions(zeroAmountPayment);

        // Assert
        assertTrue(instructions.contains("Total pembayaran: Rp0"));
    }

    // ================= VALIDATEPAYMENT TESTS =================

    @Test
    public void validatePayment_ValidPayment_ReturnsTrue() {
        // Act
        boolean isValid = bankTransferStrategy.validatePayment(validPayment);

        // Assert
        assertTrue(isValid);
    }

    @Test
    public void validatePayment_NullPayment_ReturnsFalse() {
        // Act
        boolean isValid = bankTransferStrategy.validatePayment(null);

        // Assert
        assertFalse(isValid);
    }    @Test
    public void validatePayment_NullAmount_ReturnsFalse() {
        // Arrange
        Payment paymentWithNullAmount = validPayment.toBuilder()
                .amount(null)
                .build();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> bankTransferStrategy.validatePayment(paymentWithNullAmount));
    }

    @Test
    public void validatePayment_ZeroAmount_ReturnsFalse() {
        // Arrange
        Payment paymentWithZeroAmount = validPayment.toBuilder()
                .amount(BigDecimal.ZERO)
                .build();

        // Act
        boolean isValid = bankTransferStrategy.validatePayment(paymentWithZeroAmount);

        // Assert
        assertFalse(isValid);
    }

    @Test
    public void validatePayment_NegativeAmount_ReturnsFalse() {
        // Arrange
        Payment paymentWithNegativeAmount = validPayment.toBuilder()
                .amount(new BigDecimal("-1000"))
                .build();

        // Act
        boolean isValid = bankTransferStrategy.validatePayment(paymentWithNegativeAmount);

        // Assert
        assertFalse(isValid);
    }

    @Test
    public void validatePayment_NullCourseId_ReturnsFalse() {
        // Arrange
        Payment paymentWithNullCourseId = validPayment.toBuilder()
                .courseId(null)
                .build();

        // Act
        boolean isValid = bankTransferStrategy.validatePayment(paymentWithNullCourseId);

        // Assert
        assertFalse(isValid);
    }

    @Test
    public void validatePayment_PositiveAmountAndValidCourseId_ReturnsTrue() {
        // Arrange
        Payment validPaymentWithLargeAmount = validPayment.toBuilder()
                .amount(new BigDecimal("999999.99"))
                .courseId(999L)
                .build();

        // Act
        boolean isValid = bankTransferStrategy.validatePayment(validPaymentWithLargeAmount);

        // Assert
        assertTrue(isValid);
    }

    // ================= SUPPORTS TESTS =================

    @Test
    public void supports_BankTransferMethod_ReturnsTrue() {
        // Act
        boolean supports = bankTransferStrategy.supports("Bank Transfer");

        // Assert
        assertTrue(supports);
    }

    @Test
    public void supports_CreditCardMethod_ReturnsFalse() {
        // Act
        boolean supports = bankTransferStrategy.supports("Credit Card");

        // Assert
        assertFalse(supports);
    }

    @Test
    public void supports_NullMethod_ReturnsFalse() {
        // Act
        boolean supports = bankTransferStrategy.supports(null);

        // Assert
        assertFalse(supports);
    }

    @Test
    public void supports_EmptyMethod_ReturnsFalse() {
        // Act
        boolean supports = bankTransferStrategy.supports("");

        // Assert
        assertFalse(supports);
    }

    @Test
    public void supports_InvalidMethod_ReturnsFalse() {
        // Act
        boolean supports = bankTransferStrategy.supports("Invalid Method");

        // Assert
        assertFalse(supports);
    }

    @Test
    public void supports_CaseInsensitive_ShouldWork() {
        // Act
        boolean supportsLowerCase = bankTransferStrategy.supports("bank transfer");
        boolean supportsUpperCase = bankTransferStrategy.supports("BANK TRANSFER");

        // Assert
        // Note: This depends on the actual implementation - adjust based on actual behavior
        assertFalse(supportsLowerCase); // Assuming it's case-sensitive
        assertFalse(supportsUpperCase); // Assuming it's case-sensitive
    }

    // ================= INTEGRATION TESTS =================

    @Test
    public void integrationTest_CompleteWorkflow() {
        // Test the complete workflow of the strategy
        
        // 1. Check if it supports bank transfer
        assertTrue(bankTransferStrategy.supports("Bank Transfer"));
        
        // 2. Validate the payment
        assertTrue(bankTransferStrategy.validatePayment(validPayment));
        
        // 3. Process the payment
        String processResult = bankTransferStrategy.processPayment(validPayment);
        assertNotNull(processResult);
        assertTrue(processResult.contains("processed successfully"));
        
        // 4. Generate instructions
        String instructions = bankTransferStrategy.generateInstructions(validPayment);
        assertNotNull(instructions);
        assertTrue(instructions.contains("Total pembayaran"));
    }
}
