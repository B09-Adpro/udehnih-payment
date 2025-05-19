package id.ac.ui.cs.advprog.udehnihpayment.controller;

import id.ac.ui.cs.advprog.udehnihpayment.dto.*;
import id.ac.ui.cs.advprog.udehnihpayment.enums.*;
import id.ac.ui.cs.advprog.udehnihpayment.mapper.*;
import id.ac.ui.cs.advprog.udehnihpayment.model.*;
import id.ac.ui.cs.advprog.udehnihpayment.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {
    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;
    
    @Mock
    private RefundService refundService;
    
    @Mock
    private PaymentMapper paymentMapper;
    
    @Mock
    private RefundMapper refundMapper;
    
    @InjectMocks
    private PaymentController paymentController;

    private UUID transactionId;
    private UUID courseId;
    private UUID userId;
    private Payment payment;

    @BeforeEach
    public void setUp() {
        transactionId = UUID.randomUUID();
        courseId = UUID.randomUUID();
        userId = UUID.randomUUID();
        payment = Payment.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PAID)
                .amount(new BigDecimal("50000"))
                .userId(userId)
                .build();
                
        // Set up PaymentMapper mock behaviors
        PaymentDetailDTO detailDTO = PaymentDetailDTO.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .amount(new BigDecimal("50000"))
                .paymentStatus("PAID")
                .paymentMethod("BANK_TRANSFER")
                .build();
        
        PaymentResponseDTO responseDTO = PaymentResponseDTO.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .coursePrice(new BigDecimal("50000"))
                .paymentStatus("PAID")
                .paymentMethod("BANK_TRANSFER")
                .build();
                
        when(paymentMapper.toDetailDto(any(Payment.class))).thenReturn(detailDTO);
        when(paymentMapper.toResponseDto(any(Payment.class))).thenReturn(responseDTO);
        
        // Set up RefundMapper mock behavior
        RefundResponseDTO refundResponseDTO = RefundResponseDTO.builder()
                .refundId(UUID.randomUUID())
                .status("PENDING")
                .message("Refund request has been submitted successfully.")
                .note("Your refund request is being processed by admin.")
                .build();
                
        when(refundMapper.toResponseDto(any(Refund.class))).thenReturn(refundResponseDTO);
    }

    @Test
    public void getPaymentMethods_HappyPath_ReturnsAllMethods() throws Exception {
        List<String> methods = Arrays.asList("BankTransfer", "CreditCard");
        when(paymentService.getPaymentMethods()).thenReturn(methods);

        mockMvc.perform(get("/api/payments/methods")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("BankTransfer"))
                .andExpect(jsonPath("$[1]").value("CreditCard"));
    }
    
    // UNHAPPY PATH: Service throws exception
    @Test
    public void getPaymentMethods_UnhappyPath_ServiceThrowsException() throws Exception {
        when(paymentService.getPaymentMethods()).thenThrow(new RuntimeException("Service failure"));
        
        mockMvc.perform(get("/api/payments/methods")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void processBankTransferPayment_HappyPath_ReturnsSuccess() throws Exception {
        UUID transactionId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Payment updatedPayment = Payment.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .build();
                
        when(paymentService.processPayment(eq(transactionId), eq("BankTransfer")))
            .thenReturn(updatedPayment);
        
        mockMvc.perform(post("/api/payments/{transactionId}/bank-transfer", transactionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"))
                .andExpect(jsonPath("$.paymentMethod").value(PaymentMethod.BANK_TRANSFER.toString()));
    }

    @Test
    public void processCreditCardPayment_HappyPath_ReturnsSuccess() throws Exception {
        UUID transactionId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Payment updatedPayment = Payment.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .build();
                
        when(paymentService.processPayment(eq(transactionId), eq("CreditCard")))
            .thenReturn(updatedPayment);
        
        mockMvc.perform(post("/api/payments/{transactionId}/credit-card", transactionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"))
                .andExpect(jsonPath("$.paymentMethod").value(PaymentMethod.CREDIT_CARD.toString()));
    }

    @Test
    public void processBankTransferPayment_NotFound_Returns404() throws Exception {
        UUID transactionId = UUID.randomUUID();
        
        when(paymentService.processPayment(eq(transactionId), eq("BankTransfer")))
            .thenThrow(new IllegalArgumentException("Payment not found"));
        
        mockMvc.perform(post("/api/payments/{transactionId}/bank-transfer", transactionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetTransactionDetails_Success() throws Exception {
        // Mock the service to return the sample payment
        when(paymentService.findByTransactionId(eq(transactionId))).thenReturn(payment);
        
        // Create DTO that will be returned by mapper
        PaymentDetailDTO detailDTO = PaymentDetailDTO.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .amount(new BigDecimal("50000"))
                .paymentStatus("PAID")
                .paymentMethod("BANK_TRANSFER")
                .build();
                
        // Mock the mapper to return the DTO
        when(paymentMapper.toDetailDto(payment)).thenReturn(detailDTO);

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/payments/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // 200 OK
                .andExpect(jsonPath("$.transactionId").value(transactionId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));
    }

    @Test
    public void testGetTransactionDetails_NotFound() throws Exception {
        // Mock the service to return null for non-existing transaction
        when(paymentService.findByTransactionId(eq(transactionId))).thenReturn(null);

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/payments/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())  // 404 Not Found
                .andExpect(content().string("Transaction Not Found"));
    }

    @Test
    public void testGetTransactionDetails_InternalServerError() throws Exception {
        // Mock the service to throw an exception
        when(paymentService.findByTransactionId(eq(transactionId))).thenThrow(new RuntimeException("Unexpected error"));

        // Perform the GET request and verify the response
        mockMvc.perform(get("/api/payments/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error processing payment: Unexpected error"));
    }

    @Test
    public void testRequestRefund_Success() throws Exception {
        // Mock findByTransactionId to return the sample payment
        when(paymentService.findByTransactionId(eq(transactionId))).thenReturn(payment);
        
        // Create a sample refund
        Refund refund = Refund.builder()
                .id(UUID.randomUUID())
                .transactionId(transactionId)
                .reason("Course not as expected")
                .details("Content too basic")
                .build();
                
        // Mock refundService to return the refund
        when(refundService.requestRefund(eq(transactionId), eq("Course not as expected"), eq("Content too basic")))
                .thenReturn(refund);
                
        // Create RefundResponseDTO that mapper will return
        RefundResponseDTO refundResponseDTO = RefundResponseDTO.builder()
                .refundId(refund.getId())
                .status("PENDING")
                .message("Refund request has been submitted successfully.")
                .note("Your refund request is being processed by admin.")
                .build();
                
        // Mock refundMapper to return the DTO
        when(refundMapper.toResponseDto(refund)).thenReturn(refundResponseDTO);

        // Perform the request
        mockMvc.perform(post("/api/payments/{transactionId}/refund", transactionId)
                .param("reason", "Course not as expected")
                .param("details", "Content too basic")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refundId").exists())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.message").value("Refund request has been submitted successfully."))
                .andExpect(jsonPath("$.note").value("Your refund request is being processed by admin."));
    }
    
    @Test
    public void testRequestRefund_PaymentNotFound() throws Exception {
        // Mock findByTransactionId to return null
        when(paymentService.findByTransactionId(eq(transactionId))).thenReturn(null);
        
        // Perform the request
        mockMvc.perform(post("/api/payments/{transactionId}/refund", transactionId)
                .param("reason", "Course not as expected")
                .param("details", "Content too basic")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Payment not found for transactionId: " + transactionId));
    }
    
    @Test
    public void testRequestRefund_ServiceThrowsException() throws Exception {
        // Mock findByTransactionId to return the sample payment
        when(paymentService.findByTransactionId(eq(transactionId))).thenReturn(payment);
        
        // Mock refundService to throw exception
        when(refundService.requestRefund(eq(transactionId), eq("Course not as expected"), eq("Content too basic")))
                .thenThrow(new RuntimeException("Error processing refund"));
        
        // Perform the request
        mockMvc.perform(post("/api/payments/{transactionId}/refund", transactionId)
                .param("reason", "Course not as expected")
                .param("details", "Content too basic")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error processing refund: Error processing refund"));
    }

    @Test
    public void testCreatePayment_Success() throws Exception {
        // Sample request data
        // Create JSON request
        String requestJson = "{\"courseId\":\"" + courseId + "\",\"paymentMethod\":\"BANK_TRANSFER\"}";
        
        // Create a sample payment that will be returned by service
        Payment newPayment = Payment.builder()
                .transactionId(UUID.randomUUID())
                .courseId(courseId)
                .userId(userId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .build();
        
        // Create response DTO that mapper will return
        PaymentResponseDTO responseDTO = PaymentResponseDTO.builder()
                .transactionId(newPayment.getTransactionId())
                .courseId(courseId)
                .userId(userId)
                .coursePrice(new BigDecimal("50000"))
                .paymentMethod("BANK_TRANSFER")
                .paymentStatus("PENDING")
                .build();
                
        // Mock behavior of mapper to return a Payment when converting from DTO
        when(paymentMapper.toEntity(any(), eq(userId), eq(PaymentMethod.BANK_TRANSFER.getValue())))
                .thenReturn(newPayment);
                
        // Mock behavior of service to return the payment
        when(paymentService.createPayment(any(Payment.class))).thenReturn(newPayment);
        
        // Mock behavior of mapper to return DTO when converting from Payment
        when(paymentMapper.toResponseDto(newPayment)).thenReturn(responseDTO);
        
        // Perform the request
        mockMvc.perform(post("/api/payments")
                .header("X-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").exists())
                .andExpect(jsonPath("$.courseId").value(courseId.toString()))                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"))
                .andExpect(jsonPath("$.paymentMethod").value("BANK_TRANSFER"));
    }
    
    // Exception handler to be used in tests
    private static class PaymentExceptionHandler {
        @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
        public org.springframework.http.ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        
        @org.springframework.web.bind.annotation.ExceptionHandler(RuntimeException.class)
        public org.springframework.http.ResponseEntity<String> handleRuntimeException(RuntimeException e) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}