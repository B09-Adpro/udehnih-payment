package id.ac.ui.cs.advprog.udehnihpayment.controller;

import id.ac.ui.cs.advprog.udehnihpayment.dto.response.PaymentResponseDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.response.PaymentDetailDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.response.RefundResponseDTO;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.RefundStatus;
import id.ac.ui.cs.advprog.udehnihpayment.dto.request.PaymentRequestDTO;
import id.ac.ui.cs.advprog.udehnihpayment.exception.InvalidRefundReasonException;
import id.ac.ui.cs.advprog.udehnihpayment.exception.RefundAlreadyRequestedException;
import id.ac.ui.cs.advprog.udehnihpayment.exception.RefundTooLateException;
import id.ac.ui.cs.advprog.udehnihpayment.exception.TransactionNotFoundException;
import id.ac.ui.cs.advprog.udehnihpayment.exception.UnauthorizedRefundException;
import id.ac.ui.cs.advprog.udehnihpayment.mapper.PaymentMapper;
import id.ac.ui.cs.advprog.udehnihpayment.mapper.RefundMapper;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.model.Refund;
import id.ac.ui.cs.advprog.udehnihpayment.security.AppUserDetails;
import id.ac.ui.cs.advprog.udehnihpayment.service.PaymentService;
import id.ac.ui.cs.advprog.udehnihpayment.service.RefundService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Implementasi HLN pada Controller
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final RefundService refundService;
    private final PaymentMapper paymentMapper;
    private final RefundMapper refundMapper;

    @Value("${services.course.api-key}")
    private String courseApiKey;

    @Value("${services.dashboard.api-key}")
    private String dashboardApiKey;

    @Value("${jwt.secret-key}")
    private String secretKey;

    public PaymentController(
            PaymentService paymentService, 
            RefundService refundService,
            PaymentMapper paymentMapper,
            RefundMapper refundMapper) {
        this.paymentService = paymentService;
        this.refundService = refundService;
        this.paymentMapper = paymentMapper;
        this.refundMapper = refundMapper;
    }

    private boolean validateCourseApiKey(String apiKey) {
        return courseApiKey != null && courseApiKey.equals(apiKey);
    }

    private boolean validateDashboardApiKey(String apiKey) {
        return dashboardApiKey != null && dashboardApiKey.equals(apiKey);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPayment(
            @RequestBody PaymentRequestDTO request,
            @RequestHeader (value = "x-api-key", required=true) String CourseApiKey) {

        // Validate API key
        if (!validateCourseApiKey(CourseApiKey)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Unauthorized access");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        Payment payment = paymentMapper.toEntity(request);
        Payment result = paymentService.createPayment(payment);

        PaymentResponseDTO responseDTO = paymentMapper.toResponseDto(result);
        Map<String, Object> response = new HashMap<>();
        response.put("transactionId", responseDTO.getTransactionId());
        response.put("courseId", responseDTO.getCourseId());
        response.put("userId", responseDTO.getUserId());
        response.put("amount", responseDTO.getAmount());
        response.put("paymentStatus", responseDTO.getPaymentStatus());
        response.put("paymentMethod", responseDTO.getPaymentMethod());
        response.put("createdAt", responseDTO.getCreatedAt());
        response.put("updatedAt", responseDTO.getUpdatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<PaymentResponseDTO>> getTransactionHistory(@AuthenticationPrincipal AppUserDetails userDetails) {
        Long userId = userDetails.getId();
        List<Payment> payments = paymentService.getAllPayments(userId);
        List<PaymentResponseDTO> dtos = payments.stream().map(paymentMapper::toResponseDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransactionDetails(
        @PathVariable("transactionId") Long transactionId,
        @RequestHeader(value = "X-API-Key", required = false) String apiKey,
        @AuthenticationPrincipal AppUserDetails userDetails) {
        
        // Cek metode autentikasi
        boolean isServiceCall = apiKey != null && validateDashboardApiKey(apiKey);
        boolean isAuthenticated = userDetails != null;
        
        // Tolak jika tidak ada autentikasi sama sekali
        if (!isServiceCall && !isAuthenticated) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Authentication required");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        
        // Ambil data payment
        Payment payment = paymentService.findByTransactionId(transactionId);
        
        if (payment == null) {
            throw new TransactionNotFoundException("Transaction not found with id: " + transactionId);
        }
        
        if (!isServiceCall && isAuthenticated) {
            if (!payment.getUserId().equals(userDetails.getId())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "Unauthorized access");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
        }
        
        PaymentDetailDTO paymentDetailDTO = paymentMapper.toDetailDto(payment);
        return ResponseEntity.ok(paymentDetailDTO);
    }

    @GetMapping("/methods")
    public ResponseEntity<List<String>> getPaymentMethods() {
        try {
            List<String> methods = paymentService.getPaymentMethods();
            return ResponseEntity.ok(methods);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{transactionId}/bank-transfer")
    public ResponseEntity<PaymentResponseDTO> processBankTransferPayment(@PathVariable("transactionId") Long transactionId) {
        try {
            Payment processedPayment = paymentService.processPayment(
                    transactionId,
                    PaymentMethod.BANK_TRANSFER.getValue());
            return ResponseEntity.ok(paymentMapper.toResponseDto(processedPayment));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error processing payment: " + e.getMessage());
        }
    }

    @PostMapping("/{transactionId}/credit-card")
    public ResponseEntity<PaymentResponseDTO> processCreditCardPayment(@PathVariable("transactionId") Long transactionId) {
        try {
            Payment processedPayment = paymentService.processPayment(
                    transactionId,
                    PaymentMethod.CREDIT_CARD.getValue());
            return ResponseEntity.ok(paymentMapper.toResponseDto(processedPayment));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error processing payment: " + e.getMessage());
        }
    }

    @PostMapping("/{transactionId}/refund")
    public ResponseEntity<RefundResponseDTO> requestRefund(
            @PathVariable("transactionId") Long transactionId,
            @RequestParam String reason,
            @RequestParam(required = false) String details,
            @AuthenticationPrincipal AppUserDetails userDetails) {

        // Error 401 - Unauthorized (Invalid token)
        boolean isAuthenticated = userDetails != null;
        
        try {
            Payment payment = paymentService.findByTransactionId(transactionId);

            if (payment == null) {
                throw new TransactionNotFoundException("Payment not found for transactionId: " + transactionId);
            }

            // Validate the user is requesting refund for their own payment
            if (isAuthenticated && !payment.getUserId().equals(userDetails.getId())) {
                throw new UnauthorizedRefundException("You are not authorized to request a refund for this transaction");
            }

            Refund refund = refundService.requestRefund(transactionId, reason, details);
            return ResponseEntity.ok(refundMapper.toResponseDto(refund));

        } catch (TransactionNotFoundException e) {
            throw e;
        } catch (InvalidRefundReasonException e) {
            throw e;
        } catch (RefundAlreadyRequestedException e) {
            throw e;
        } catch (RefundTooLateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error processing refund: " + e.getMessage(), e);
        }
    }

    @PutMapping("/{transactionId}/status")
    public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(
            @PathVariable("transactionId") Long transactionId,
            @RequestBody PaymentDetailDTO.Details updateRequest,
            @RequestHeader(value = "X-API-Key", required = true) String apiKey) {

        if (!validateDashboardApiKey(apiKey)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Unauthorized access");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        try {
            Payment updatedPayment = paymentService.updatePaymentStatus(transactionId, updateRequest);
            return ResponseEntity.ok(paymentMapper.toResponseDto(updatedPayment));

        } catch (TransactionNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error updating payment status: " + e.getMessage());
        }
    }

    @PutMapping("refunds/{refundId}/status")
    public ResponseEntity<RefundResponseDTO> updateRefundStatus(
            @PathVariable("refundId") Long refundId,
            @RequestParam("status") String status,
            @RequestParam("approvedBy") String approvedBy,
            @RequestHeader(value = "X-API-Key", required = true) String apiKey) {

        // Validate API key
        if (!validateDashboardApiKey(apiKey)) {
            throw new UnauthorizedRefundException("Invalid API key");
        }

        try {
            RefundStatus refundStatus = RefundStatus.fromString(status);
            Refund updatedRefund = refundService.updateRefundStatus(refundId, refundStatus, approvedBy);
            return ResponseEntity.ok(refundMapper.toResponseDto(updatedRefund));
        } catch (IllegalArgumentException e) {
            throw new InvalidRefundReasonException("Invalid refund status: " + status);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Refund not found")) {
                throw new TransactionNotFoundException(e.getMessage());
            }
            throw new RuntimeException("Error updating refund status: " + e.getMessage());
        }
    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody PaymentRequestDTO paymentRequest) {
        try {
            // Validasi request
            if (paymentRequest.getStudentId() == null || paymentRequest.getCourseId() == null ||
                paymentRequest.getAmount() == null || paymentRequest.getPaymentMethod() == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Missing required fields");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Buat payment baru
            Payment payment = paymentMapper.toEntity(paymentRequest);
            Payment savedPayment = paymentService.createPayment(payment);
            
            // Proses payment menggunakan payment method yang sesuai
            try {
                paymentService.processPayment(
                    savedPayment.getTransactionId(), 
                    paymentRequest.getPaymentMethod()
                );
            } catch (Exception e) {
                // Error handling tetapi payment tetap dibuat
                System.err.println("Error processing payment: " + e.getMessage());
            }
            
            // Menyiapkan response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transactionId", savedPayment.getTransactionId());
            response.put("paymentStatus", savedPayment.getPaymentStatus().getValue());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error processing payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getAllPayments(
            @RequestHeader(value = "X-API-Key", required = true) String apiKey) {
        
        // Validate API key
        if (!validateDashboardApiKey(apiKey)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Unauthorized access");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        
        List<Payment> payments = paymentService.getAllPayments();
        List<PaymentResponseDTO> dtos = payments.stream()
                .map(paymentMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/refunds")
    public ResponseEntity<?> getAllRefunds(
            @RequestHeader(value = "X-API-Key", required = true) String apiKey) {
        
        // Validate API key
        if (!validateDashboardApiKey(apiKey)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Unauthorized access");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        
        List<Refund> refunds = refundService.getAllRefunds();
        List<RefundResponseDTO> dtos = refunds.stream()
                .map(refundMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/refunds/{refundId}")
    public ResponseEntity<?> getRefundDetails(
            @PathVariable("refundId") Long refundId,
            @RequestHeader(value = "X-API-Key", required = true) String apiKey) {
        
        // Validate API key
        if (!validateDashboardApiKey(apiKey)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Unauthorized access");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        
        try {
            Refund refund = refundService.findById(refundId);
            return ResponseEntity.ok(refundMapper.toResponseDto(refund));
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving refund details: " + e.getMessage());
        }
    }

    @PostMapping("/{transactionId}/confirm-transfer")
    public ResponseEntity<PaymentResponseDTO> confirmBankTransfer(
            @PathVariable("transactionId") Long transactionId,
            @AuthenticationPrincipal AppUserDetails userDetails) {
        Payment payment = paymentService.confirmBankTransfer(transactionId, userDetails.getId());
        return ResponseEntity.ok(paymentMapper.toResponseDto(payment));
    }

    public boolean isAuthenticated(AppUserDetails userDetails) {
        return userDetails != null && userDetails.getId() != null;
    }
}