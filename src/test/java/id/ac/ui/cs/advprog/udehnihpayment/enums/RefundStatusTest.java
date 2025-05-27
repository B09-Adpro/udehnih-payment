package id.ac.ui.cs.advprog.udehnihpayment.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RefundStatusTest {

    @Test
    public void testRefundStatusValues_ShouldHaveCorrectCount() {
        RefundStatus[] statuses = RefundStatus.values();
        assertEquals(3, statuses.length);
    }

    @Test
    public void testPending_ShouldHaveCorrectValue() {
        RefundStatus pending = RefundStatus.PENDING;
        assertEquals("PENDING", pending.getValue());
        assertEquals("PENDING", pending.toString());
    }

    @Test
    public void testApproved_ShouldHaveCorrectValue() {
        RefundStatus approved = RefundStatus.APPROVED;
        assertEquals("APPROVED", approved.getValue());
        assertEquals("APPROVED", approved.toString());
    }

    @Test
    public void testRejected_ShouldHaveCorrectValue() {
        RefundStatus rejected = RefundStatus.REJECTED;
        assertEquals("REJECTED", rejected.getValue());
        assertEquals("REJECTED", rejected.toString());
    }

    @Test
    public void testRefundStatusValueOf_ValidStatus_ShouldReturnCorrectEnum() {
        assertEquals(RefundStatus.PENDING, RefundStatus.valueOf("PENDING"));
        assertEquals(RefundStatus.APPROVED, RefundStatus.valueOf("APPROVED"));
        assertEquals(RefundStatus.REJECTED, RefundStatus.valueOf("REJECTED"));
    }

    @Test
    public void testRefundStatusValueOf_InvalidStatus_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> RefundStatus.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> RefundStatus.valueOf(""));
        assertThrows(IllegalArgumentException.class, () -> RefundStatus.valueOf("pending"));
        assertThrows(IllegalArgumentException.class, () -> RefundStatus.valueOf("Approved"));
        assertThrows(IllegalArgumentException.class, () -> RefundStatus.valueOf("COMPLETED"));
        assertThrows(IllegalArgumentException.class, () -> RefundStatus.valueOf("CANCELLED"));
        assertThrows(IllegalArgumentException.class, () -> RefundStatus.valueOf("PROCESSING"));
    }

    @Test
    public void testRefundStatusValueOf_NullInput_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> RefundStatus.valueOf(null));
    }

    @Test
    public void testRefundStatusContains_ValidValues_ShouldReturnTrue() {
        assertTrue(RefundStatus.contains("PENDING"));
        assertTrue(RefundStatus.contains("APPROVED"));
        assertTrue(RefundStatus.contains("REJECTED"));
        
        // Test case insensitive
        assertTrue(RefundStatus.contains("pending"));
        assertTrue(RefundStatus.contains("approved"));
        assertTrue(RefundStatus.contains("rejected"));
        
        // Test mixed case
        assertTrue(RefundStatus.contains("Pending"));
        assertTrue(RefundStatus.contains("Approved"));
        assertTrue(RefundStatus.contains("Rejected"));
        assertTrue(RefundStatus.contains("PeNdInG"));
        assertTrue(RefundStatus.contains("ApPrOvEd"));
        assertTrue(RefundStatus.contains("ReJeCtEd"));
    }

    @Test
    public void testRefundStatusContains_InvalidValues_ShouldReturnFalse() {
        assertFalse(RefundStatus.contains("COMPLETED"));
        assertFalse(RefundStatus.contains("CANCELLED"));
        assertFalse(RefundStatus.contains("PROCESSING"));
        assertFalse(RefundStatus.contains(""));
        assertFalse(RefundStatus.contains("INVALID"));
        assertFalse(RefundStatus.contains("PEND"));
        assertFalse(RefundStatus.contains("APPROVE"));
        assertFalse(RefundStatus.contains("REJECT"));
    }

    @Test
    public void testRefundStatusContains_NullInput_ShouldReturnFalse() {
        assertFalse(RefundStatus.contains(null));
    }

    @Test
    public void testRefundStatusFromString_ValidValues_ShouldReturnCorrectEnum() {
        assertEquals(RefundStatus.PENDING, RefundStatus.fromString("PENDING"));
        assertEquals(RefundStatus.APPROVED, RefundStatus.fromString("APPROVED"));
        assertEquals(RefundStatus.REJECTED, RefundStatus.fromString("REJECTED"));
        
        // Test case insensitive
        assertEquals(RefundStatus.PENDING, RefundStatus.fromString("pending"));
        assertEquals(RefundStatus.APPROVED, RefundStatus.fromString("approved"));
        assertEquals(RefundStatus.REJECTED, RefundStatus.fromString("rejected"));
        
        // Test mixed case
        assertEquals(RefundStatus.PENDING, RefundStatus.fromString("Pending"));
        assertEquals(RefundStatus.APPROVED, RefundStatus.fromString("Approved"));
        assertEquals(RefundStatus.REJECTED, RefundStatus.fromString("Rejected"));
        assertEquals(RefundStatus.PENDING, RefundStatus.fromString("PeNdInG"));
        assertEquals(RefundStatus.APPROVED, RefundStatus.fromString("ApPrOvEd"));
        assertEquals(RefundStatus.REJECTED, RefundStatus.fromString("ReJeCtEd"));
    }

    @Test
    public void testRefundStatusFromString_InvalidValues_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> RefundStatus.fromString("COMPLETED"));
        assertThrows(IllegalArgumentException.class, () -> RefundStatus.fromString("CANCELLED"));
        assertThrows(IllegalArgumentException.class, () -> RefundStatus.fromString(""));
        assertThrows(IllegalArgumentException.class, () -> RefundStatus.fromString("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> RefundStatus.fromString("PROCESSING"));
    }

    @Test
    public void testRefundStatusFromString_NullInput_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> RefundStatus.fromString(null));
        assertTrue(exception.getMessage().contains("No refund status with value null found"));
    }

    @Test
    public void testRefundStatusFromString_ExceptionMessage() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> RefundStatus.fromString("Invalid Status"));
        assertTrue(exception.getMessage().contains("No refund status with value Invalid Status found"));
    }

    @Test
    public void testRefundStatusEquality_SameStatus_ShouldBeEqual() {
        RefundStatus pending1 = RefundStatus.PENDING;
        RefundStatus pending2 = RefundStatus.valueOf("PENDING");
        assertEquals(pending1, pending2);
        assertSame(pending1, pending2);
    }

    @Test
    public void testRefundStatusInequality_DifferentStatuses_ShouldNotBeEqual() {
        assertNotEquals(RefundStatus.PENDING, RefundStatus.APPROVED);
        assertNotEquals(RefundStatus.APPROVED, RefundStatus.REJECTED);
        assertNotEquals(RefundStatus.REJECTED, RefundStatus.PENDING);
    }

    @Test
    public void testRefundStatusOrdinalValues() {
        assertEquals(0, RefundStatus.PENDING.ordinal());
        assertEquals(1, RefundStatus.APPROVED.ordinal());
        assertEquals(2, RefundStatus.REJECTED.ordinal());
    }

    @Test
    public void testRefundStatusHashCodeConsistency() {
        RefundStatus pending1 = RefundStatus.PENDING;
        RefundStatus pending2 = RefundStatus.valueOf("PENDING");
        assertEquals(pending1.hashCode(), pending2.hashCode());
    }

    @Test
    public void testRefundStatusPropertiesNotNull() {
        for (RefundStatus status : RefundStatus.values()) {
            assertNotNull(status.getValue(), "Value should not be null for " + status);
            assertNotNull(status.toString(), "toString should not be null for " + status);
        }
    }

    @Test
    public void testRefundStatusPropertiesNotEmpty() {
        for (RefundStatus status : RefundStatus.values()) {
            assertFalse(status.getValue().isEmpty(), "Value should not be empty for " + status);
            assertFalse(status.toString().isEmpty(), "toString should not be empty for " + status);
        }
    }

    @Test
    public void testRefundStatusUniqueValues() {
        RefundStatus[] statuses = RefundStatus.values();
        
        for (int i = 0; i < statuses.length; i++) {
            for (int j = i + 1; j < statuses.length; j++) {
                assertNotEquals(statuses[i].getValue(), statuses[j].getValue(),
                    "Refund status values should be unique: " + statuses[i] + " vs " + statuses[j]);
            }
        }
    }

    @Test
    public void testRefundStatusValueEqualsToString() {
        // Test that getValue() returns the same as toString()
        for (RefundStatus status : RefundStatus.values()) {
            assertEquals(status.getValue(), status.toString(),
                "getValue() should match toString() for " + status);
        }
    }

    @Test
    public void testRefundStatusWorkflowOrder() {
        // Test logical workflow order based on ordinal values
        assertTrue(RefundStatus.PENDING.ordinal() < RefundStatus.APPROVED.ordinal(),
            "PENDING should come before APPROVED");
        assertTrue(RefundStatus.PENDING.ordinal() < RefundStatus.REJECTED.ordinal(),
            "PENDING should come before REJECTED");
        // APPROVED and REJECTED are terminal states, so their relative order doesn't matter for workflow
    }

    @Test
    public void testRefundStatusCaseInsensitiveMatching() {
        // Test various case combinations for each status
        String[] pendingVariations = {
            "PENDING", "pending", "Pending", "PENDING", "PeNdInG", "pEnDiNg"
        };
        
        for (String variation : pendingVariations) {
            assertTrue(RefundStatus.contains(variation), 
                "Should contain variation: " + variation);
            assertEquals(RefundStatus.PENDING, RefundStatus.fromString(variation),
                "Should return PENDING for variation: " + variation);
        }
        
        String[] approvedVariations = {
            "APPROVED", "approved", "Approved", "ApPrOvEd", "aPpRoVeD"
        };
        
        for (String variation : approvedVariations) {
            assertTrue(RefundStatus.contains(variation),
                "Should contain variation: " + variation);
            assertEquals(RefundStatus.APPROVED, RefundStatus.fromString(variation),
                "Should return APPROVED for variation: " + variation);
        }
        
        String[] rejectedVariations = {
            "REJECTED", "rejected", "Rejected", "ReJeCtEd", "rEjEcTeD"
        };
        
        for (String variation : rejectedVariations) {
            assertTrue(RefundStatus.contains(variation),
                "Should contain variation: " + variation);
            assertEquals(RefundStatus.REJECTED, RefundStatus.fromString(variation),
                "Should return REJECTED for variation: " + variation);
        }
    }

    @Test
    public void testRefundStatusPartialMatches_ShouldReturnFalse() {
        // Test that partial matches don't work
        assertFalse(RefundStatus.contains("PEND"));
        assertFalse(RefundStatus.contains("ING"));
        assertFalse(RefundStatus.contains("APPROVE"));
        assertFalse(RefundStatus.contains("PROVED"));
        assertFalse(RefundStatus.contains("REJECT"));
        assertFalse(RefundStatus.contains("ECTED"));
    }

    @Test
    public void testRefundStatusBusinessLogic() {
        // Test that statuses represent correct business states
        assertNotNull(RefundStatus.PENDING); // Initial state when refund is requested
        assertNotNull(RefundStatus.APPROVED); // Success state - refund granted
        assertNotNull(RefundStatus.REJECTED); // Denial state - refund denied
    }

    @Test
    public void testRefundStatusTransitions() {
        // Test logical status transitions
        // From PENDING, can go to either APPROVED or REJECTED
        RefundStatus initialStatus = RefundStatus.PENDING;
        assertNotNull(initialStatus);
        
        // Terminal states
        RefundStatus approvedStatus = RefundStatus.APPROVED;
        RefundStatus rejectedStatus = RefundStatus.REJECTED;
        assertNotNull(approvedStatus);
        assertNotNull(rejectedStatus);
        
        // Ensure all statuses are different
        assertNotEquals(initialStatus, approvedStatus);
        assertNotEquals(initialStatus, rejectedStatus);
        assertNotEquals(approvedStatus, rejectedStatus);
    }

    @Test
    public void testRefundStatusConstants() {
        // Test that the constants have the expected string values
        assertEquals("PENDING", RefundStatus.PENDING.getValue());
        assertEquals("APPROVED", RefundStatus.APPROVED.getValue());
        assertEquals("REJECTED", RefundStatus.REJECTED.getValue());
    }

    @Test
    public void testRefundStatusComprehensiveFunctionality() {
        // Test all methods work together correctly
        for (RefundStatus status : RefundStatus.values()) {
            String value = status.getValue();
            
            // Test round-trip conversion
            assertTrue(RefundStatus.contains(value));
            assertEquals(status, RefundStatus.fromString(value));
            
            // Test case insensitive round-trip
            assertTrue(RefundStatus.contains(value.toLowerCase()));
            assertEquals(status, RefundStatus.fromString(value.toLowerCase()));
            
            // Test toString consistency
            assertEquals(value, status.toString());
        }
    }
}
