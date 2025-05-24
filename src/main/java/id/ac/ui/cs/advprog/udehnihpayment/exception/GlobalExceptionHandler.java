package id.ac.ui.cs.advprog.udehnihpayment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTransactionNotFoundException(TransactionNotFoundException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Transaksi tidak ditemukan");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCourseNotFoundException(CourseNotFoundException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Kursus tidak ditemukan");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedAccessException(UnauthorizedAccessException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Akses tidak diizinkan");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(AlreadyEnrolledException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyEnrolledException(AlreadyEnrolledException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Sudah terdaftar");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(RefundAlreadyRequestedException.class)
    public ResponseEntity<Map<String, Object>> handleRefundAlreadyRequestedException(RefundAlreadyRequestedException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Sudah mengajukan refund");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InvalidRefundReasonException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRefundReasonException(InvalidRefundReasonException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Alasan Refund tidak valid!");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RefundTooLateException.class)
    public ResponseEntity<Map<String, Object>> handleRefundTooLateException(RefundTooLateException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Refund no longer available");
        response.put("message", e.getMessage());
        response.put("status_code", 403);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    
    @ExceptionHandler(UnauthorizedRefundException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedRefundException(UnauthorizedRefundException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Unauthorized");
        response.put("message", e.getMessage());
        response.put("status_code", 401);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}