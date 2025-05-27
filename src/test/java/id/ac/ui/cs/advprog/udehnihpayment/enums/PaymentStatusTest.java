package id.ac.ui.cs.advprog.udehnihpayment.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentStatusTest {

    @Test
    public void testPaymentStatusValues_ShouldHaveCorrectCount() {
        PaymentStatus[] statuses = PaymentStatus.values();
        assertEquals(4, statuses.length);
    }

    @Test
    public void testWaitingPayment_ShouldHaveCorrectValue() {
        PaymentStatus waitingPayment = PaymentStatus.WAITING_PAYMENT;
        assertEquals("WAITING_PAYMENT", waitingPayment.getValue());
        assertEquals("WAITING_PAYMENT", waitingPayment.toString());
    }

    @Test
    public void testPending_ShouldHaveCorrectValue() {
        PaymentStatus pending = PaymentStatus.PENDING;
        assertEquals("PENDING", pending.getValue());
        assertEquals("PENDING", pending.toString());
    }

    @Test
    public void testPaid_ShouldHaveCorrectValue() {
        PaymentStatus paid = PaymentStatus.PAID;
        assertEquals("PAID", paid.getValue());
        assertEquals("PAID", paid.toString());
    }

    @Test
    public void testFailed_ShouldHaveCorrectValue() {
        PaymentStatus failed = PaymentStatus.FAILED;
        assertEquals("FAILED", failed.getValue());
        assertEquals("FAILED", failed.toString());
    }

    @Test
    public void testPaymentStatusValueOf_ValidStatus_ShouldReturnCorrectEnum() {
        assertEquals(PaymentStatus.WAITING_PAYMENT, PaymentStatus.valueOf("WAITING_PAYMENT"));
        assertEquals(PaymentStatus.PENDING, PaymentStatus.valueOf("PENDING"));
        assertEquals(PaymentStatus.PAID, PaymentStatus.valueOf("PAID"));
        assertEquals(PaymentStatus.FAILED, PaymentStatus.valueOf("FAILED"));
    }

    @Test
    public void testPaymentStatusValueOf_InvalidStatus_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> PaymentStatus.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> PaymentStatus.valueOf(""));
        assertThrows(IllegalArgumentException.class, () -> PaymentStatus.valueOf("waiting_payment"));
        assertThrows(IllegalArgumentException.class, () -> PaymentStatus.valueOf("Pending"));
        assertThrows(IllegalArgumentException.class, () -> PaymentStatus.valueOf("COMPLETED"));
        assertThrows(IllegalArgumentException.class, () -> PaymentStatus.valueOf("CANCELLED"));
    }

    @Test
    public void testPaymentStatusValueOf_NullInput_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> PaymentStatus.valueOf(null));
    }

    @Test
    public void testPaymentStatusContains_ValidValues_ShouldReturnTrue() {
        assertTrue(PaymentStatus.contains("WAITING_PAYMENT"));
        assertTrue(PaymentStatus.contains("PENDING"));
        assertTrue(PaymentStatus.contains("PAID"));
        assertTrue(PaymentStatus.contains("FAILED"));
        
        // Test case insensitive
        assertTrue(PaymentStatus.contains("waiting_payment"));
        assertTrue(PaymentStatus.contains("pending"));
        assertTrue(PaymentStatus.contains("paid"));
        assertTrue(PaymentStatus.contains("failed"));
        
        // Test mixed case
        assertTrue(PaymentStatus.contains("Waiting_Payment"));
        assertTrue(PaymentStatus.contains("Pending"));
        assertTrue(PaymentStatus.contains("Paid"));
        assertTrue(PaymentStatus.contains("Failed"));
    }

    @Test
    public void testPaymentStatusContains_InvalidValues_ShouldReturnFalse() {
        assertFalse(PaymentStatus.contains("COMPLETED"));
        assertFalse(PaymentStatus.contains("CANCELLED"));
        assertFalse(PaymentStatus.contains("PROCESSING"));
        assertFalse(PaymentStatus.contains(""));
        assertFalse(PaymentStatus.contains("INVALID"));
        assertFalse(PaymentStatus.contains("WAIT"));
        assertFalse(PaymentStatus.contains("PAYMENT"));
    }

    @Test
    public void testPaymentStatusContains_NullInput_ShouldReturnFalse() {
        assertFalse(PaymentStatus.contains(null));
    }

    @Test
    public void testPaymentStatusFromString_ValidValues_ShouldReturnCorrectEnum() {
        assertEquals(PaymentStatus.WAITING_PAYMENT, PaymentStatus.fromString("WAITING_PAYMENT"));
        assertEquals(PaymentStatus.PENDING, PaymentStatus.fromString("PENDING"));
        assertEquals(PaymentStatus.PAID, PaymentStatus.fromString("PAID"));
        assertEquals(PaymentStatus.FAILED, PaymentStatus.fromString("FAILED"));
        
        // Test case insensitive
        assertEquals(PaymentStatus.WAITING_PAYMENT, PaymentStatus.fromString("waiting_payment"));
        assertEquals(PaymentStatus.PENDING, PaymentStatus.fromString("pending"));
        assertEquals(PaymentStatus.PAID, PaymentStatus.fromString("paid"));
        assertEquals(PaymentStatus.FAILED, PaymentStatus.fromString("failed"));
        
        // Test mixed case
        assertEquals(PaymentStatus.WAITING_PAYMENT, PaymentStatus.fromString("Waiting_Payment"));
        assertEquals(PaymentStatus.PENDING, PaymentStatus.fromString("Pending"));
        assertEquals(PaymentStatus.PAID, PaymentStatus.fromString("Paid"));
        assertEquals(PaymentStatus.FAILED, PaymentStatus.fromString("Failed"));
    }

    @Test
    public void testPaymentStatusFromString_InvalidValues_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> PaymentStatus.fromString("COMPLETED"));
        assertThrows(IllegalArgumentException.class, () -> PaymentStatus.fromString("CANCELLED"));
        assertThrows(IllegalArgumentException.class, () -> PaymentStatus.fromString(""));
        assertThrows(IllegalArgumentException.class, () -> PaymentStatus.fromString("INVALID"));
    }

    @Test
    public void testPaymentStatusFromString_NullInput_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> PaymentStatus.fromString(null));
        assertTrue(exception.getMessage().contains("No payment status with value null found"));
    }

    @Test
    public void testPaymentStatusFromString_ExceptionMessage() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> PaymentStatus.fromString("Invalid Status"));
        assertTrue(exception.getMessage().contains("No payment status with value Invalid Status found"));
    }

    @Test
    public void testPaymentStatusEquality_SameStatus_ShouldBeEqual() {
        PaymentStatus pending1 = PaymentStatus.PENDING;
        PaymentStatus pending2 = PaymentStatus.valueOf("PENDING");
        assertEquals(pending1, pending2);
        assertSame(pending1, pending2);
    }

    @Test
    public void testPaymentStatusInequality_DifferentStatuses_ShouldNotBeEqual() {
        assertNotEquals(PaymentStatus.WAITING_PAYMENT, PaymentStatus.PENDING);
        assertNotEquals(PaymentStatus.PENDING, PaymentStatus.PAID);
        assertNotEquals(PaymentStatus.PAID, PaymentStatus.FAILED);
        assertNotEquals(PaymentStatus.FAILED, PaymentStatus.WAITING_PAYMENT);
    }

    @Test
    public void testPaymentStatusOrdinalValues() {
        assertEquals(0, PaymentStatus.WAITING_PAYMENT.ordinal());
        assertEquals(1, PaymentStatus.PENDING.ordinal());
        assertEquals(2, PaymentStatus.PAID.ordinal());
        assertEquals(3, PaymentStatus.FAILED.ordinal());
    }

    @Test
    public void testPaymentStatusHashCodeConsistency() {
        PaymentStatus pending1 = PaymentStatus.PENDING;
        PaymentStatus pending2 = PaymentStatus.valueOf("PENDING");
        assertEquals(pending1.hashCode(), pending2.hashCode());
    }

    @Test
    public void testPaymentStatusPropertiesNotNull() {
        for (PaymentStatus status : PaymentStatus.values()) {
            assertNotNull(status.getValue(), "Value should not be null for " + status);
            assertNotNull(status.toString(), "toString should not be null for " + status);
        }
    }

    @Test
    public void testPaymentStatusPropertiesNotEmpty() {
        for (PaymentStatus status : PaymentStatus.values()) {
            assertFalse(status.getValue().isEmpty(), "Value should not be empty for " + status);
            assertFalse(status.toString().isEmpty(), "toString should not be empty for " + status);
        }
    }

    @Test
    public void testPaymentStatusUniqueValues() {
        PaymentStatus[] statuses = PaymentStatus.values();
        
        for (int i = 0; i < statuses.length; i++) {
            for (int j = i + 1; j < statuses.length; j++) {
                assertNotEquals(statuses[i].getValue(), statuses[j].getValue(),
                    "Payment status values should be unique: " + statuses[i] + " vs " + statuses[j]);
            }
        }
    }

    @Test
    public void testPaymentStatusValueEqualsToString() {
        // Test that getValue() returns the same as toString()
        for (PaymentStatus status : PaymentStatus.values()) {
            assertEquals(status.getValue(), status.toString(),
                "getValue() should match toString() for " + status);
        }
    }

    @Test
    public void testPaymentStatusWorkflowOrder() {
        // Test logical workflow order based on ordinal values
        assertTrue(PaymentStatus.WAITING_PAYMENT.ordinal() < PaymentStatus.PENDING.ordinal(),
            "WAITING_PAYMENT should come before PENDING");
        assertTrue(PaymentStatus.PENDING.ordinal() < PaymentStatus.PAID.ordinal(),
            "PENDING should come before PAID");
        // FAILED can happen at any stage, so we don't test its position relative to PAID
    }

    @Test
    public void testPaymentStatusCaseInsensitiveMatching() {
        // Test various case combinations for each status
        String[] waitingPaymentVariations = {
            "WAITING_PAYMENT", "waiting_payment", "Waiting_Payment", 
            "WAITING_payment", "waiting_PAYMENT"
        };
        
        for (String variation : waitingPaymentVariations) {
            assertTrue(PaymentStatus.contains(variation), 
                "Should contain variation: " + variation);
            assertEquals(PaymentStatus.WAITING_PAYMENT, PaymentStatus.fromString(variation),
                "Should return WAITING_PAYMENT for variation: " + variation);
        }
        
        String[] pendingVariations = {
            "PENDING", "pending", "Pending", "PENDING", "PeNdInG"
        };
        
        for (String variation : pendingVariations) {
            assertTrue(PaymentStatus.contains(variation),
                "Should contain variation: " + variation);
            assertEquals(PaymentStatus.PENDING, PaymentStatus.fromString(variation),
                "Should return PENDING for variation: " + variation);
        }
    }

    @Test
    public void testPaymentStatusPartialMatches_ShouldReturnFalse() {
        // Test that partial matches don't work
        assertFalse(PaymentStatus.contains("WAITING"));
        assertFalse(PaymentStatus.contains("PAYMENT"));
        assertFalse(PaymentStatus.contains("PEND"));
        assertFalse(PaymentStatus.contains("ING"));
        assertFalse(PaymentStatus.contains("PAI"));
        assertFalse(PaymentStatus.contains("FAIL"));
    }

    @Test
    public void testPaymentStatusBusinessLogic() {
        // Test that statuses represent correct business states
        assertNotNull(PaymentStatus.WAITING_PAYMENT); // Initial state
        assertNotNull(PaymentStatus.PENDING); // Processing state
        assertNotNull(PaymentStatus.PAID); // Success state
        assertNotNull(PaymentStatus.FAILED); // Error state
    }
}
