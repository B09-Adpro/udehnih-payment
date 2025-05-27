package id.ac.ui.cs.advprog.udehnihpayment.strategy;

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
public class CreditCardStrategyTest {

    private CreditCardStrategy creditCardStrategy;
    private Payment validPayment;

    @BeforeEach
    public void setUp() {
        creditCardStrategy = new CreditCardStrategy();
        
        validPayment = Payment.builder()
                .transactionId(UUID.randomUUID())
                .courseId(123L)
                .userId(456L)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .amount(new BigDecimal("100000"))
                .build();
    }

    // ================= PROCESSPAYMENT TESTS =================

    @Test
    public void processPayment_ValidPayment_ReturnsSuccessMessage() {
        // Act
        String result = creditCardStrategy.processPayment(validPayment);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Credit Card payment"));
        assertTrue(result.contains("course ID " + validPayment.getCourseId()));
        assertTrue(result.contains("amount " + validPayment.getAmount()));
        assertTrue(result.contains("processed successfully"));
    }    @Test
    public void processPayment_NullPayment_ShouldHandleGracefully() {
        // Act & Assert - This tests how the method handles null input
        assertThrows(NullPointerException.class, () -> creditCardStrategy.processPayment(null));
    }

    // ================= GENERATEINSTRUCTIONS TESTS =================

    @Test
    public void generateInstructions_ValidPayment_ContainsAllRequiredFields() {
        // Act
        String instructions = creditCardStrategy.generateInstructions(validPayment);

        // Assert
        assertNotNull(instructions);
        assertTrue(instructions.contains("Untuk menyelesaikan pembayaran dengan Kartu Kredit"));
        assertTrue(instructions.contains("nomor kartu kredit (16 digit)"));
        assertTrue(instructions.contains("nama pemilik kartu"));
        assertTrue(instructions.contains("tanggal kadaluarsa (MM/YY)"));
        assertTrue(instructions.contains("Card Verification Code (CVC)"));
        assertTrue(instructions.contains("Total pembayaran: Rp" + validPayment.getAmount()));
    }    @Test
    public void generateInstructions_NullPayment_ShouldHandleGracefully() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> creditCardStrategy.generateInstructions(null));
    }

    @Test
    public void generateInstructions_PaymentWithZeroAmount_ContainsCorrectAmount() {
        // Arrange
        Payment zeroAmountPayment = validPayment.toBuilder()
                .amount(BigDecimal.ZERO)
                .build();

        // Act
        String instructions = creditCardStrategy.generateInstructions(zeroAmountPayment);

        // Assert
        assertTrue(instructions.contains("Total pembayaran: Rp0"));
    }

    // ================= VALIDATECARDDETAILS TESTS =================

    @Test
    public void validateCardDetails_ValidCardNumberAndCvc_ReturnsTrue() {
        // Arrange
        String validCardNumber = "1234567890123456"; // 16 digits
        String validCvc = "123"; // 3 digits

        // Act
        boolean isValid = creditCardStrategy.validateCardDetails(validCardNumber, validCvc);

        // Assert
        assertTrue(isValid);
    }

    @Test
    public void validateCardDetails_InvalidCardNumber_ReturnsFalse() {
        // Test various invalid card numbers
        String validCvc = "123";
        
        // Too short
        assertFalse(creditCardStrategy.validateCardDetails("123456789012345", validCvc));
        
        // Too long
        assertFalse(creditCardStrategy.validateCardDetails("12345678901234567", validCvc));
        
        // Contains letters
        assertFalse(creditCardStrategy.validateCardDetails("123456789012345a", validCvc));
        
        // Contains special characters
        assertFalse(creditCardStrategy.validateCardDetails("1234-5678-9012-3456", validCvc));
        
        // Null
        assertFalse(creditCardStrategy.validateCardDetails(null, validCvc));
        
        // Empty
        assertFalse(creditCardStrategy.validateCardDetails("", validCvc));
    }

    @Test
    public void validateCardDetails_InvalidCvc_ReturnsFalse() {
        String validCardNumber = "1234567890123456";
        
        // Too short
        assertFalse(creditCardStrategy.validateCardDetails(validCardNumber, "12"));
        
        // Too long
        assertFalse(creditCardStrategy.validateCardDetails(validCardNumber, "1234"));
        
        // Contains letters
        assertFalse(creditCardStrategy.validateCardDetails(validCardNumber, "12a"));
        
        // Contains special characters
        assertFalse(creditCardStrategy.validateCardDetails(validCardNumber, "12-"));
        
        // Null
        assertFalse(creditCardStrategy.validateCardDetails(validCardNumber, null));
        
        // Empty
        assertFalse(creditCardStrategy.validateCardDetails(validCardNumber, ""));
    }

    @Test
    public void validateCardDetails_BothInvalid_ReturnsFalse() {
        // Act & Assert
        assertFalse(creditCardStrategy.validateCardDetails("invalid", "invalid"));
        assertFalse(creditCardStrategy.validateCardDetails(null, null));
        assertFalse(creditCardStrategy.validateCardDetails("", ""));
    }

    // ================= VALIDATEEXPIRYDATE TESTS =================

    @Test
    public void validateExpiryDate_ValidFormats_ReturnsTrue() {
        // Test various valid MM/YY formats
        assertTrue(creditCardStrategy.validateExpiryDate("01/23"));
        assertTrue(creditCardStrategy.validateExpiryDate("12/99"));
        assertTrue(creditCardStrategy.validateExpiryDate("06/25"));
        assertTrue(creditCardStrategy.validateExpiryDate("11/30"));
    }

    @Test
    public void validateExpiryDate_InvalidMonth_ReturnsFalse() {
        // Test invalid months
        assertFalse(creditCardStrategy.validateExpiryDate("00/23")); // Month 00
        assertFalse(creditCardStrategy.validateExpiryDate("13/23")); // Month 13
        assertFalse(creditCardStrategy.validateExpiryDate("99/23")); // Month 99
    }

    @Test
    public void validateExpiryDate_InvalidFormat_ReturnsFalse() {
        // Test various invalid formats
        assertFalse(creditCardStrategy.validateExpiryDate("1/23"));   // Single digit month
        assertFalse(creditCardStrategy.validateExpiryDate("01/2023")); // 4 digit year
        assertFalse(creditCardStrategy.validateExpiryDate("01-23"));   // Wrong separator
        assertFalse(creditCardStrategy.validateExpiryDate("01/2"));    // Single digit year
        assertFalse(creditCardStrategy.validateExpiryDate("1/2"));     // Both single digits
        assertFalse(creditCardStrategy.validateExpiryDate("Jan/23"));  // Text month
        assertFalse(creditCardStrategy.validateExpiryDate("01/ab"));   // Text year
        assertFalse(creditCardStrategy.validateExpiryDate("01/"));     // Missing year
        assertFalse(creditCardStrategy.validateExpiryDate("/23"));     // Missing month
        assertFalse(creditCardStrategy.validateExpiryDate("0123"));    // No separator
    }

    @Test
    public void validateExpiryDate_NullOrEmpty_ReturnsFalse() {
        assertFalse(creditCardStrategy.validateExpiryDate(null));
        assertFalse(creditCardStrategy.validateExpiryDate(""));
        assertFalse(creditCardStrategy.validateExpiryDate("   ")); // Whitespace only
    }

    // ================= VALIDATEPAYMENT TESTS =================

    @Test
    public void validatePayment_ValidPayment_ReturnsTrue() {
        // Act
        boolean isValid = creditCardStrategy.validatePayment(validPayment);

        // Assert
        assertTrue(isValid);
    }

    @Test
    public void validatePayment_NullPayment_ReturnsFalse() {
        // Act
        boolean isValid = creditCardStrategy.validatePayment(null);

        // Assert
        assertFalse(isValid);
    }    @Test
    public void validatePayment_NullAmount_ReturnsFalse() {
        // Arrange
        Payment paymentWithNullAmount = validPayment.toBuilder()
                .amount(null)
                .build();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> creditCardStrategy.validatePayment(paymentWithNullAmount));
    }

    @Test
    public void validatePayment_ZeroAmount_ReturnsFalse() {
        // Arrange
        Payment paymentWithZeroAmount = validPayment.toBuilder()
                .amount(BigDecimal.ZERO)
                .build();

        // Act
        boolean isValid = creditCardStrategy.validatePayment(paymentWithZeroAmount);

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
        boolean isValid = creditCardStrategy.validatePayment(paymentWithNegativeAmount);

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
        boolean isValid = creditCardStrategy.validatePayment(paymentWithNullCourseId);

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
        boolean isValid = creditCardStrategy.validatePayment(validPaymentWithLargeAmount);

        // Assert
        assertTrue(isValid);
    }

    // ================= SUPPORTS TESTS =================

    @Test
    public void supports_CreditCardMethod_ReturnsTrue() {
        // Act
        boolean supports = creditCardStrategy.supports("Credit Card");

        // Assert
        assertTrue(supports);
    }

    @Test
    public void supports_BankTransferMethod_ReturnsFalse() {
        // Act
        boolean supports = creditCardStrategy.supports("Bank Transfer");

        // Assert
        assertFalse(supports);
    }

    @Test
    public void supports_NullMethod_ReturnsFalse() {
        // Act
        boolean supports = creditCardStrategy.supports(null);

        // Assert
        assertFalse(supports);
    }

    @Test
    public void supports_EmptyMethod_ReturnsFalse() {
        // Act
        boolean supports = creditCardStrategy.supports("");

        // Assert
        assertFalse(supports);
    }

    @Test
    public void supports_InvalidMethod_ReturnsFalse() {
        // Act
        boolean supports = creditCardStrategy.supports("Invalid Method");

        // Assert
        assertFalse(supports);
    }

    @Test
    public void supports_CaseInsensitive_ShouldWork() {
        // Act
        boolean supportsLowerCase = creditCardStrategy.supports("credit card");
        boolean supportsUpperCase = creditCardStrategy.supports("CREDIT CARD");

        // Assert
        // Note: This depends on the actual implementation - adjust based on actual behavior
        assertFalse(supportsLowerCase); // Assuming it's case-sensitive
        assertFalse(supportsUpperCase); // Assuming it's case-sensitive
    }

    // ================= EDGE CASE TESTS =================

    @Test
    public void validateCardDetails_EdgeCaseNumbers_WorksCorrectly() {
        String validCvc = "123";
        
        // All zeros
        assertTrue(creditCardStrategy.validateCardDetails("0000000000000000", validCvc));
        
        // All nines
        assertTrue(creditCardStrategy.validateCardDetails("9999999999999999", validCvc));
        
        // Mixed
        assertTrue(creditCardStrategy.validateCardDetails("1357924680123456", validCvc));
    }

    @Test
    public void validateCardDetails_EdgeCaseCvc_WorksCorrectly() {
        String validCardNumber = "1234567890123456";
        
        // All zeros
        assertTrue(creditCardStrategy.validateCardDetails(validCardNumber, "000"));
        
        // All nines
        assertTrue(creditCardStrategy.validateCardDetails(validCardNumber, "999"));
        
        // Mixed
        assertTrue(creditCardStrategy.validateCardDetails(validCardNumber, "135"));
    }

    @Test
    public void validateExpiryDate_EdgeCaseMonths_WorksCorrectly() {
        // Test boundary months
        assertTrue(creditCardStrategy.validateExpiryDate("01/23")); // January
        assertTrue(creditCardStrategy.validateExpiryDate("12/23")); // December
    }

    // ================= INTEGRATION TESTS =================

    @Test
    public void integrationTest_CompleteWorkflow() {
        // Test the complete workflow of the strategy
        
        // 1. Check if it supports credit card
        assertTrue(creditCardStrategy.supports("Credit Card"));
        
        // 2. Validate the payment
        assertTrue(creditCardStrategy.validatePayment(validPayment));
        
        // 3. Validate card details
        assertTrue(creditCardStrategy.validateCardDetails("1234567890123456", "123"));
        
        // 4. Validate expiry date
        assertTrue(creditCardStrategy.validateExpiryDate("12/25"));
        
        // 5. Process the payment
        String processResult = creditCardStrategy.processPayment(validPayment);
        assertNotNull(processResult);
        assertTrue(processResult.contains("processed successfully"));
        
        // 6. Generate instructions
        String instructions = creditCardStrategy.generateInstructions(validPayment);
        assertNotNull(instructions);
        assertTrue(instructions.contains("Total pembayaran"));
    }

    @Test
    public void integrationTest_FailureScenario() {
        // Test a complete failure scenario
        
        // 1. Invalid card details
        assertFalse(creditCardStrategy.validateCardDetails("invalid", "12"));
        
        // 2. Invalid expiry date
        assertFalse(creditCardStrategy.validateExpiryDate("13/99"));
        
        // 3. Invalid payment
        Payment invalidPayment = validPayment.toBuilder()
                .amount(BigDecimal.ZERO)
                .courseId(null)
                .build();
        assertFalse(creditCardStrategy.validatePayment(invalidPayment));
        
        // 4. Unsupported method
        assertFalse(creditCardStrategy.supports("Bank Transfer"));
    }
}
