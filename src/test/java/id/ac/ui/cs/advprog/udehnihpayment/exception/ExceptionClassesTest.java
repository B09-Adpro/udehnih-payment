package id.ac.ui.cs.advprog.udehnihpayment.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionClassesTest {

    // ================= TransactionNotFoundException Tests =================
    
    @Test
    public void testTransactionNotFoundException_WithMessage_ShouldCreateCorrectException() {
        String message = "Transaction not found with ID: 12345";
        TransactionNotFoundException exception = new TransactionNotFoundException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
    }
    
    @Test
    public void testTransactionNotFoundException_WithNullMessage_ShouldAcceptNull() {
        TransactionNotFoundException exception = new TransactionNotFoundException(null);
        
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
    }
    
    @Test
    public void testTransactionNotFoundException_WithEmptyMessage_ShouldAcceptEmpty() {
        String message = "";
        TransactionNotFoundException exception = new TransactionNotFoundException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
    }
    
    @Test
    public void testTransactionNotFoundException_Inheritance_ShouldExtendRuntimeException() {
        TransactionNotFoundException exception = new TransactionNotFoundException("test");
        
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }
    
    @Test
    public void testTransactionNotFoundException_CanBeThrown() {
        String message = "Test transaction not found";
        
        assertThrows(TransactionNotFoundException.class, () -> {
            throw new TransactionNotFoundException(message);
        });
    }
    
    @Test
    public void testTransactionNotFoundException_CatchAsRuntimeException() {
        String message = "Test transaction not found";
        
        try {
            throw new TransactionNotFoundException(message);
        } catch (RuntimeException e) {
            assertTrue(e instanceof TransactionNotFoundException);
            assertEquals(message, e.getMessage());
        }
    }

    // ================= UnauthorizedAccessException Tests =================
    
    @Test
    public void testUnauthorizedAccessException_WithMessage_ShouldCreateCorrectException() {
        String message = "Access denied for user: testuser";
        UnauthorizedAccessException exception = new UnauthorizedAccessException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
    }
    
    @Test
    public void testUnauthorizedAccessException_WithNullMessage_ShouldAcceptNull() {
        UnauthorizedAccessException exception = new UnauthorizedAccessException(null);
        
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
    }
    
    @Test
    public void testUnauthorizedAccessException_WithEmptyMessage_ShouldAcceptEmpty() {
        String message = "";
        UnauthorizedAccessException exception = new UnauthorizedAccessException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
    }
    
    @Test
    public void testUnauthorizedAccessException_Inheritance_ShouldExtendRuntimeException() {
        UnauthorizedAccessException exception = new UnauthorizedAccessException("test");
        
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }
    
    @Test
    public void testUnauthorizedAccessException_CanBeThrown() {
        String message = "Test unauthorized access";
        
        assertThrows(UnauthorizedAccessException.class, () -> {
            throw new UnauthorizedAccessException(message);
        });
    }
    
    @Test
    public void testUnauthorizedAccessException_CatchAsRuntimeException() {
        String message = "Test unauthorized access";
        
        try {
            throw new UnauthorizedAccessException(message);
        } catch (RuntimeException e) {
            assertTrue(e instanceof UnauthorizedAccessException);
            assertEquals(message, e.getMessage());
        }
    }

    // ================= RefundTooLateException Tests =================
    
    @Test
    public void testRefundTooLateException_WithMessage_ShouldCreateCorrectException() {
        String message = "Refund request is too late - past 30 day limit";
        RefundTooLateException exception = new RefundTooLateException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
    }
    
    @Test
    public void testRefundTooLateException_WithNullMessage_ShouldAcceptNull() {
        RefundTooLateException exception = new RefundTooLateException(null);
        
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
    }
    
    @Test
    public void testRefundTooLateException_Inheritance_ShouldExtendRuntimeException() {
        RefundTooLateException exception = new RefundTooLateException("test");
        
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }
    
    @Test
    public void testRefundTooLateException_CanBeThrown() {
        String message = "Test refund too late";
        
        assertThrows(RefundTooLateException.class, () -> {
            throw new RefundTooLateException(message);
        });
    }

    // ================= RefundAlreadyRequestedException Tests =================
    
    @Test
    public void testRefundAlreadyRequestedException_WithMessage_ShouldCreateCorrectException() {
        String message = "Refund already requested for transaction: 12345";
        RefundAlreadyRequestedException exception = new RefundAlreadyRequestedException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
    }
    
    @Test
    public void testRefundAlreadyRequestedException_WithNullMessage_ShouldAcceptNull() {
        RefundAlreadyRequestedException exception = new RefundAlreadyRequestedException(null);
        
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
    }
    
    @Test
    public void testRefundAlreadyRequestedException_Inheritance_ShouldExtendRuntimeException() {
        RefundAlreadyRequestedException exception = new RefundAlreadyRequestedException("test");
        
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }
    
    @Test
    public void testRefundAlreadyRequestedException_CanBeThrown() {
        String message = "Test refund already requested";
        
        assertThrows(RefundAlreadyRequestedException.class, () -> {
            throw new RefundAlreadyRequestedException(message);
        });
    }

    // ================= InvalidRefundReasonException Tests =================
    
    @Test
    public void testInvalidRefundReasonException_WithMessage_ShouldCreateCorrectException() {
        String message = "Invalid refund reason provided";
        InvalidRefundReasonException exception = new InvalidRefundReasonException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
    }
    
    @Test
    public void testInvalidRefundReasonException_WithNullMessage_ShouldAcceptNull() {
        InvalidRefundReasonException exception = new InvalidRefundReasonException(null);
        
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
    }
    
    @Test
    public void testInvalidRefundReasonException_Inheritance_ShouldExtendRuntimeException() {
        InvalidRefundReasonException exception = new InvalidRefundReasonException("test");
        
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }
    
    @Test
    public void testInvalidRefundReasonException_CanBeThrown() {
        String message = "Test invalid refund reason";
        
        assertThrows(InvalidRefundReasonException.class, () -> {
            throw new InvalidRefundReasonException(message);
        });
    }

    // ================= UnauthorizedRefundException Tests =================
    
    @Test
    public void testUnauthorizedRefundException_WithMessage_ShouldCreateCorrectException() {
        String message = "Unauthorized refund operation";
        UnauthorizedRefundException exception = new UnauthorizedRefundException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
    }
    
    @Test
    public void testUnauthorizedRefundException_WithNullMessage_ShouldAcceptNull() {
        UnauthorizedRefundException exception = new UnauthorizedRefundException(null);
        
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception instanceof RuntimeException);
    }
    
    @Test
    public void testUnauthorizedRefundException_Inheritance_ShouldExtendRuntimeException() {
        UnauthorizedRefundException exception = new UnauthorizedRefundException("test");
        
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }
    
    @Test
    public void testUnauthorizedRefundException_CanBeThrown() {
        String message = "Test unauthorized refund";
        
        assertThrows(UnauthorizedRefundException.class, () -> {
            throw new UnauthorizedRefundException(message);
        });
    }

    // ================= Exception Message Consistency Tests =================
    
    @Test
    public void testAllExceptions_MessagePreservation() {
        String testMessage = "Test exception message";
        
        assertEquals(testMessage, new TransactionNotFoundException(testMessage).getMessage());
        assertEquals(testMessage, new UnauthorizedAccessException(testMessage).getMessage());
        assertEquals(testMessage, new RefundTooLateException(testMessage).getMessage());
        assertEquals(testMessage, new RefundAlreadyRequestedException(testMessage).getMessage());
        assertEquals(testMessage, new InvalidRefundReasonException(testMessage).getMessage());
        assertEquals(testMessage, new UnauthorizedRefundException(testMessage).getMessage());
    }
    
    @Test
    public void testAllExceptions_RuntimeExceptionHierarchy() {
        assertTrue(new TransactionNotFoundException("test") instanceof RuntimeException);
        assertTrue(new UnauthorizedAccessException("test") instanceof RuntimeException);
        assertTrue(new RefundTooLateException("test") instanceof RuntimeException);
        assertTrue(new RefundAlreadyRequestedException("test") instanceof RuntimeException);
        assertTrue(new InvalidRefundReasonException("test") instanceof RuntimeException);
        assertTrue(new UnauthorizedRefundException("test") instanceof RuntimeException);
    }
    
    @Test
    public void testAllExceptions_UniqueTypes() {
        Exception transaction = new TransactionNotFoundException("test");
        Exception unauthorized = new UnauthorizedAccessException("test");
        Exception tooLate = new RefundTooLateException("test");
        Exception alreadyRequested = new RefundAlreadyRequestedException("test");
        Exception invalidReason = new InvalidRefundReasonException("test");
        Exception unauthorizedRefund = new UnauthorizedRefundException("test");
        
        // Ensure each exception type is distinct
        assertNotEquals(transaction.getClass(), unauthorized.getClass());
        assertNotEquals(transaction.getClass(), tooLate.getClass());
        assertNotEquals(transaction.getClass(), alreadyRequested.getClass());
        assertNotEquals(transaction.getClass(), invalidReason.getClass());
        assertNotEquals(transaction.getClass(), unauthorizedRefund.getClass());
        
        assertNotEquals(unauthorized.getClass(), tooLate.getClass());
        assertNotEquals(unauthorized.getClass(), alreadyRequested.getClass());
        assertNotEquals(unauthorized.getClass(), invalidReason.getClass());
        assertNotEquals(unauthorized.getClass(), unauthorizedRefund.getClass());
    }
}
