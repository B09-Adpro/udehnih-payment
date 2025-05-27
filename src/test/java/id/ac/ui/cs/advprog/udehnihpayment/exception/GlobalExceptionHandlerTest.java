package id.ac.ui.cs.advprog.udehnihpayment.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    // ================= TRANSACTION NOT FOUND EXCEPTION TESTS =================

    @Test
    public void handleTransactionNotFoundException_ShouldReturnNotFound() {
        // Arrange
        TransactionNotFoundException exception = new TransactionNotFoundException("Transaction with ID 123 not found");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleTransactionNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Transaksi tidak ditemukan", response.getBody().get("error"));
        assertEquals("Transaction with ID 123 not found", response.getBody().get("message"));
    }

    @Test
    public void handleTransactionNotFoundException_WithNullMessage_ShouldHandleGracefully() {
        // Arrange
        TransactionNotFoundException exception = new TransactionNotFoundException(null);

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleTransactionNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Transaksi tidak ditemukan", response.getBody().get("error"));
        assertNull(response.getBody().get("message"));
    }

    @Test
    public void handleTransactionNotFoundException_WithEmptyMessage_ShouldHandleGracefully() {
        // Arrange
        TransactionNotFoundException exception = new TransactionNotFoundException("");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleTransactionNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Transaksi tidak ditemukan", response.getBody().get("error"));
        assertEquals("", response.getBody().get("message"));
    }

    // ================= COURSE NOT FOUND EXCEPTION TESTS =================

    @Test
    public void handleCourseNotFoundException_ShouldReturnNotFound() {
        // Arrange
        CourseNotFoundException exception = new CourseNotFoundException("Course with ID 456 not found");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleCourseNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Kursus tidak ditemukan", response.getBody().get("error"));
        assertEquals("Course with ID 456 not found", response.getBody().get("message"));
    }

    @Test
    public void handleCourseNotFoundException_WithLongMessage_ShouldReturnCorrectly() {
        // Arrange
        String longMessage = "This is a very long error message that describes in detail why the course could not be found in the database";
        CourseNotFoundException exception = new CourseNotFoundException(longMessage);

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleCourseNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Kursus tidak ditemukan", response.getBody().get("error"));
        assertEquals(longMessage, response.getBody().get("message"));
    }

    // ================= UNAUTHORIZED ACCESS EXCEPTION TESTS =================

    @Test
    public void handleUnauthorizedAccessException_ShouldReturnForbidden() {
        // Arrange
        UnauthorizedAccessException exception = new UnauthorizedAccessException("User does not have access to this resource");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleUnauthorizedAccessException(exception);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Akses tidak diizinkan", response.getBody().get("error"));
        assertEquals("User does not have access to this resource", response.getBody().get("message"));
    }

    @Test
    public void handleUnauthorizedAccessException_WithSpecialCharacters_ShouldHandleCorrectly() {
        // Arrange
        UnauthorizedAccessException exception = new UnauthorizedAccessException("Access denied: ñáéíóú & special chars @#$%");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleUnauthorizedAccessException(exception);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Akses tidak diizinkan", response.getBody().get("error"));
        assertEquals("Access denied: ñáéíóú & special chars @#$%", response.getBody().get("message"));
    }

    // ================= ALREADY ENROLLED EXCEPTION TESTS =================

    @Test
    public void handleAlreadyEnrolledException_ShouldReturnConflict() {
        // Arrange
        AlreadyEnrolledException exception = new AlreadyEnrolledException("Student is already enrolled in this course");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleAlreadyEnrolledException(exception);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Sudah terdaftar", response.getBody().get("error"));
        assertEquals("Student is already enrolled in this course", response.getBody().get("message"));
    }

    // ================= REFUND ALREADY REQUESTED EXCEPTION TESTS =================

    @Test
    public void handleRefundAlreadyRequestedException_ShouldReturnConflict() {
        // Arrange
        RefundAlreadyRequestedException exception = new RefundAlreadyRequestedException("Refund has already been requested for this transaction");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRefundAlreadyRequestedException(exception);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Sudah mengajukan refund", response.getBody().get("error"));
        assertEquals("Refund has already been requested for this transaction", response.getBody().get("message"));
    }

    @Test
    public void handleRefundAlreadyRequestedException_WithMultilineMessage_ShouldHandleCorrectly() {
        // Arrange
        String multilineMessage = "Refund request failed:\n- Transaction already has pending refund\n- Contact support for assistance";
        RefundAlreadyRequestedException exception = new RefundAlreadyRequestedException(multilineMessage);

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRefundAlreadyRequestedException(exception);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Sudah mengajukan refund", response.getBody().get("error"));
        assertEquals(multilineMessage, response.getBody().get("message"));
    }

    // ================= INVALID REFUND REASON EXCEPTION TESTS =================

    @Test
    public void handleInvalidRefundReasonException_ShouldReturnBadRequest() {
        // Arrange
        InvalidRefundReasonException exception = new InvalidRefundReasonException("The provided refund reason is not valid");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleInvalidRefundReasonException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Alasan Refund tidak valid!", response.getBody().get("error"));
        assertEquals("The provided refund reason is not valid", response.getBody().get("message"));
    }

    @Test
    public void handleInvalidRefundReasonException_WithValidationDetails_ShouldReturnCorrectly() {
        // Arrange
        InvalidRefundReasonException exception = new InvalidRefundReasonException("Reason must be between 10-500 characters");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleInvalidRefundReasonException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Alasan Refund tidak valid!", response.getBody().get("error"));
        assertEquals("Reason must be between 10-500 characters", response.getBody().get("message"));
    }

    // ================= REFUND TOO LATE EXCEPTION TESTS =================

    @Test
    public void handleRefundTooLateException_ShouldReturnForbiddenWithStatusCode() {
        // Arrange
        RefundTooLateException exception = new RefundTooLateException("Refund period has expired");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRefundTooLateException(exception);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Refund no longer available", response.getBody().get("error"));
        assertEquals("Refund period has expired", response.getBody().get("message"));
        assertEquals(403, response.getBody().get("status_code"));
    }

    @Test
    public void handleRefundTooLateException_WithTimeDetails_ShouldIncludeAllFields() {
        // Arrange
        RefundTooLateException exception = new RefundTooLateException("Refund deadline was 30 days ago");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleRefundTooLateException(exception);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size()); // Should have exactly 3 fields
        assertEquals("Refund no longer available", response.getBody().get("error"));
        assertEquals("Refund deadline was 30 days ago", response.getBody().get("message"));
        assertEquals(403, response.getBody().get("status_code"));
    }

    // ================= UNAUTHORIZED REFUND EXCEPTION TESTS =================

    @Test
    public void handleUnauthorizedRefundException_ShouldReturnUnauthorizedWithStatusCode() {
        // Arrange
        UnauthorizedRefundException exception = new UnauthorizedRefundException("User not authorized to request refund");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleUnauthorizedRefundException(exception);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unauthorized", response.getBody().get("error"));
        assertEquals("User not authorized to request refund", response.getBody().get("message"));
        assertEquals(401, response.getBody().get("status_code"));
    }

    @Test
    public void handleUnauthorizedRefundException_WithAuthenticationDetails_ShouldIncludeAllFields() {
        // Arrange
        UnauthorizedRefundException exception = new UnauthorizedRefundException("Token expired or invalid");

        // Act
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleUnauthorizedRefundException(exception);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size()); // Should have exactly 3 fields
        assertEquals("Unauthorized", response.getBody().get("error"));
        assertEquals("Token expired or invalid", response.getBody().get("message"));
        assertEquals(401, response.getBody().get("status_code"));
    }

    // ================= RESPONSE STRUCTURE VALIDATION TESTS =================

    @Test
    public void allExceptionHandlers_ShouldReturnConsistentResponseStructure() {
        // Test that all handlers return responses with consistent structure
        
        // Test TransactionNotFoundException
        TransactionNotFoundException transactionException = new TransactionNotFoundException("test");
        ResponseEntity<Map<String, Object>> transactionResponse = globalExceptionHandler.handleTransactionNotFoundException(transactionException);
        assertTrue(transactionResponse.getBody().containsKey("error"));
        assertTrue(transactionResponse.getBody().containsKey("message"));

        // Test CourseNotFoundException
        CourseNotFoundException courseException = new CourseNotFoundException("test");
        ResponseEntity<Map<String, Object>> courseResponse = globalExceptionHandler.handleCourseNotFoundException(courseException);
        assertTrue(courseResponse.getBody().containsKey("error"));
        assertTrue(courseResponse.getBody().containsKey("message"));

        // Test UnauthorizedAccessException
        UnauthorizedAccessException unauthorizedException = new UnauthorizedAccessException("test");
        ResponseEntity<Map<String, Object>> unauthorizedResponse = globalExceptionHandler.handleUnauthorizedAccessException(unauthorizedException);
        assertTrue(unauthorizedResponse.getBody().containsKey("error"));
        assertTrue(unauthorizedResponse.getBody().containsKey("message"));

        // All responses should have non-null bodies
        assertNotNull(transactionResponse.getBody());
        assertNotNull(courseResponse.getBody());
        assertNotNull(unauthorizedResponse.getBody());
    }

    @Test
    public void exceptionsWithStatusCode_ShouldIncludeStatusCodeField() {
        // Test RefundTooLateException
        RefundTooLateException refundTooLateException = new RefundTooLateException("test");
        ResponseEntity<Map<String, Object>> refundTooLateResponse = globalExceptionHandler.handleRefundTooLateException(refundTooLateException);
        assertTrue(refundTooLateResponse.getBody().containsKey("status_code"));
        assertEquals(403, refundTooLateResponse.getBody().get("status_code"));

        // Test UnauthorizedRefundException
        UnauthorizedRefundException unauthorizedRefundException = new UnauthorizedRefundException("test");
        ResponseEntity<Map<String, Object>> unauthorizedRefundResponse = globalExceptionHandler.handleUnauthorizedRefundException(unauthorizedRefundException);
        assertTrue(unauthorizedRefundResponse.getBody().containsKey("status_code"));
        assertEquals(401, unauthorizedRefundResponse.getBody().get("status_code"));
    }

    // ================= HTTP STATUS CODE MAPPING TESTS =================

    @Test
    public void httpStatusCodes_ShouldMatchExpectedValues() {
        // Test all exception handlers return correct HTTP status codes
        
        // 404 NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, 
            globalExceptionHandler.handleTransactionNotFoundException(new TransactionNotFoundException("test")).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, 
            globalExceptionHandler.handleCourseNotFoundException(new CourseNotFoundException("test")).getStatusCode());

        // 403 FORBIDDEN
        assertEquals(HttpStatus.FORBIDDEN, 
            globalExceptionHandler.handleUnauthorizedAccessException(new UnauthorizedAccessException("test")).getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, 
            globalExceptionHandler.handleRefundTooLateException(new RefundTooLateException("test")).getStatusCode());

        // 409 CONFLICT
        assertEquals(HttpStatus.CONFLICT, 
            globalExceptionHandler.handleAlreadyEnrolledException(new AlreadyEnrolledException("test")).getStatusCode());
        assertEquals(HttpStatus.CONFLICT, 
            globalExceptionHandler.handleRefundAlreadyRequestedException(new RefundAlreadyRequestedException("test")).getStatusCode());

        // 400 BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST, 
            globalExceptionHandler.handleInvalidRefundReasonException(new InvalidRefundReasonException("test")).getStatusCode());

        // 401 UNAUTHORIZED
        assertEquals(HttpStatus.UNAUTHORIZED, 
            globalExceptionHandler.handleUnauthorizedRefundException(new UnauthorizedRefundException("test")).getStatusCode());
    }

    // ================= EDGE CASE TESTS =================

    @Test
    public void exceptionHandlers_WithVeryLongMessages_ShouldHandleCorrectly() {
        // Create a very long message (over 1000 characters)
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longMessage.append("This is a very long error message part ").append(i).append(". ");
        }

        TransactionNotFoundException exception = new TransactionNotFoundException(longMessage.toString());
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleTransactionNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(longMessage.toString(), response.getBody().get("message"));
    }

    @Test
    public void exceptionHandlers_WithUnicodeCharacters_ShouldHandleCorrectly() {
        // Test with various Unicode characters
        String unicodeMessage = "Error: 测试错误 🚨 ñáéíóú αβγδε приче 🔥💰";
        
        InvalidRefundReasonException exception = new InvalidRefundReasonException(unicodeMessage);
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleInvalidRefundReasonException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(unicodeMessage, response.getBody().get("message"));
    }
}
