package id.ac.ui.cs.advprog.udehnihpayment.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentMethodTest {

    @Test
    public void testPaymentMethodValues_ShouldHaveCorrectCount() {
        PaymentMethod[] methods = PaymentMethod.values();
        assertEquals(2, methods.length);
    }

    @Test
    public void testBankTransfer_ShouldHaveCorrectValue() {
        PaymentMethod bankTransfer = PaymentMethod.BANK_TRANSFER;
        assertEquals("Bank Transfer", bankTransfer.getValue());
        assertEquals("Bank Transfer", bankTransfer.toString());
    }

    @Test
    public void testCreditCard_ShouldHaveCorrectValue() {
        PaymentMethod creditCard = PaymentMethod.CREDIT_CARD;
        assertEquals("Credit Card", creditCard.getValue());
        assertEquals("Credit Card", creditCard.toString());
    }

    @Test
    public void testPaymentMethodValueOf_ValidMethod_ShouldReturnCorrectEnum() {
        assertEquals(PaymentMethod.BANK_TRANSFER, PaymentMethod.valueOf("BANK_TRANSFER"));
        assertEquals(PaymentMethod.CREDIT_CARD, PaymentMethod.valueOf("CREDIT_CARD"));
    }

    @Test
    public void testPaymentMethodValueOf_InvalidMethod_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> PaymentMethod.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> PaymentMethod.valueOf(""));
        assertThrows(IllegalArgumentException.class, () -> PaymentMethod.valueOf("bank_transfer"));
        assertThrows(IllegalArgumentException.class, () -> PaymentMethod.valueOf("Bank Transfer"));
        assertThrows(IllegalArgumentException.class, () -> PaymentMethod.valueOf("CASH"));
    }

    @Test
    public void testPaymentMethodValueOf_NullInput_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> PaymentMethod.valueOf(null));
    }

    @Test
    public void testPaymentMethodContains_ValidValues_ShouldReturnTrue() {
        assertTrue(PaymentMethod.contains("Bank Transfer"));
        assertTrue(PaymentMethod.contains("Credit Card"));
        assertTrue(PaymentMethod.contains("bank transfer")); // case insensitive
        assertTrue(PaymentMethod.contains("CREDIT CARD")); // case insensitive
        assertTrue(PaymentMethod.contains("BANK TRANSFER")); // case insensitive
        assertTrue(PaymentMethod.contains("credit card")); // case insensitive
    }

    @Test
    public void testPaymentMethodContains_InvalidValues_ShouldReturnFalse() {
        assertFalse(PaymentMethod.contains("Cash"));
        assertFalse(PaymentMethod.contains("PayPal"));
        assertFalse(PaymentMethod.contains("Debit Card"));
        assertFalse(PaymentMethod.contains(""));
        assertFalse(PaymentMethod.contains("INVALID"));
        assertFalse(PaymentMethod.contains("Bank"));
        assertFalse(PaymentMethod.contains("Transfer"));
    }

    @Test
    public void testPaymentMethodContains_NullInput_ShouldReturnFalse() {
        assertFalse(PaymentMethod.contains(null));
    }

    @Test
    public void testPaymentMethodFromString_ValidValues_ShouldReturnCorrectEnum() {
        assertEquals(PaymentMethod.BANK_TRANSFER, PaymentMethod.fromString("Bank Transfer"));
        assertEquals(PaymentMethod.CREDIT_CARD, PaymentMethod.fromString("Credit Card"));
        assertEquals(PaymentMethod.BANK_TRANSFER, PaymentMethod.fromString("bank transfer"));
        assertEquals(PaymentMethod.CREDIT_CARD, PaymentMethod.fromString("CREDIT CARD"));
        assertEquals(PaymentMethod.BANK_TRANSFER, PaymentMethod.fromString("BANK TRANSFER"));
        assertEquals(PaymentMethod.CREDIT_CARD, PaymentMethod.fromString("credit card"));
    }

    @Test
    public void testPaymentMethodFromString_InvalidValues_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> PaymentMethod.fromString("Cash"));
        assertThrows(IllegalArgumentException.class, () -> PaymentMethod.fromString("PayPal"));
        assertThrows(IllegalArgumentException.class, () -> PaymentMethod.fromString(""));
        assertThrows(IllegalArgumentException.class, () -> PaymentMethod.fromString("INVALID"));
    }

    @Test
    public void testPaymentMethodFromString_NullInput_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> PaymentMethod.fromString(null));
        assertTrue(exception.getMessage().contains("No payment method with value null found"));
    }

    @Test
    public void testPaymentMethodFromString_ExceptionMessage() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> PaymentMethod.fromString("Invalid Method"));
        assertTrue(exception.getMessage().contains("No payment method with value Invalid Method found"));
    }

    @Test
    public void testPaymentMethodEquality_SameMethod_ShouldBeEqual() {
        PaymentMethod bankTransfer1 = PaymentMethod.BANK_TRANSFER;
        PaymentMethod bankTransfer2 = PaymentMethod.valueOf("BANK_TRANSFER");
        assertEquals(bankTransfer1, bankTransfer2);
        assertSame(bankTransfer1, bankTransfer2);
    }

    @Test
    public void testPaymentMethodInequality_DifferentMethods_ShouldNotBeEqual() {
        assertNotEquals(PaymentMethod.BANK_TRANSFER, PaymentMethod.CREDIT_CARD);
    }

    @Test
    public void testPaymentMethodOrdinalValues() {
        assertEquals(0, PaymentMethod.BANK_TRANSFER.ordinal());
        assertEquals(1, PaymentMethod.CREDIT_CARD.ordinal());
    }

    @Test
    public void testPaymentMethodHashCodeConsistency() {
        PaymentMethod bankTransfer1 = PaymentMethod.BANK_TRANSFER;
        PaymentMethod bankTransfer2 = PaymentMethod.valueOf("BANK_TRANSFER");
        assertEquals(bankTransfer1.hashCode(), bankTransfer2.hashCode());
    }

    @Test
    public void testPaymentMethodPropertiesNotNull() {
        for (PaymentMethod method : PaymentMethod.values()) {
            assertNotNull(method.getValue(), "Value should not be null for " + method);
            assertNotNull(method.toString(), "toString should not be null for " + method);
        }
    }

    @Test
    public void testPaymentMethodPropertiesNotEmpty() {
        for (PaymentMethod method : PaymentMethod.values()) {
            assertFalse(method.getValue().isEmpty(), "Value should not be empty for " + method);
            assertFalse(method.toString().isEmpty(), "toString should not be empty for " + method);
        }
    }

    @Test
    public void testPaymentMethodUniqueValues() {
        PaymentMethod[] methods = PaymentMethod.values();
        
        for (int i = 0; i < methods.length; i++) {
            for (int j = i + 1; j < methods.length; j++) {
                assertNotEquals(methods[i].getValue(), methods[j].getValue(),
                    "Payment method values should be unique: " + methods[i] + " vs " + methods[j]);
            }
        }
    }

    @Test
    public void testPaymentMethodJsonValueAnnotation() {
        // Test that toString() returns the same as getValue() for JSON serialization
        for (PaymentMethod method : PaymentMethod.values()) {
            assertEquals(method.getValue(), method.toString(),
                "toString() should match getValue() for JSON serialization");
        }
    }

    @Test
    public void testPaymentMethodCaseInsensitiveMatching() {
        // Test various case combinations
        String[] bankTransferVariations = {
            "Bank Transfer", "bank transfer", "BANK TRANSFER", 
            "Bank transfer", "bank Transfer", "BANK transfer"
        };
        
        for (String variation : bankTransferVariations) {
            assertTrue(PaymentMethod.contains(variation), 
                "Should contain variation: " + variation);
            assertEquals(PaymentMethod.BANK_TRANSFER, PaymentMethod.fromString(variation),
                "Should return BANK_TRANSFER for variation: " + variation);
        }
        
        String[] creditCardVariations = {
            "Credit Card", "credit card", "CREDIT CARD",
            "Credit card", "credit Card", "CREDIT card"
        };
        
        for (String variation : creditCardVariations) {
            assertTrue(PaymentMethod.contains(variation),
                "Should contain variation: " + variation);
            assertEquals(PaymentMethod.CREDIT_CARD, PaymentMethod.fromString(variation),
                "Should return CREDIT_CARD for variation: " + variation);
        }
    }

    @Test
    public void testPaymentMethodPartialMatches_ShouldReturnFalse() {
        // Test that partial matches don't work
        assertFalse(PaymentMethod.contains("Bank"));
        assertFalse(PaymentMethod.contains("Transfer"));
        assertFalse(PaymentMethod.contains("Credit"));
        assertFalse(PaymentMethod.contains("Card"));
        assertFalse(PaymentMethod.contains("Bank Trans"));
        assertFalse(PaymentMethod.contains("dit Card"));
    }
}
