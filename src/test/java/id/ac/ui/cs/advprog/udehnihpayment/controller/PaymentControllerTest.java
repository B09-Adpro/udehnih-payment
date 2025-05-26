package id.ac.ui.cs.advprog.udehnihpayment.controller;

import id.ac.ui.cs.advprog.udehnihpayment.dto.response.PaymentDetailDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.response.PaymentResponseDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.response.RefundResponseDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.request.PaymentRequestDTO;
import id.ac.ui.cs.advprog.udehnihpayment.enums.*;
import id.ac.ui.cs.advprog.udehnihpayment.mapper.*;
import id.ac.ui.cs.advprog.udehnihpayment.model.*;
import id.ac.ui.cs.advprog.udehnihpayment.security.AppUserDetails;
import id.ac.ui.cs.advprog.udehnihpayment.service.*;
import id.ac.ui.cs.advprog.udehnihpayment.config.TestSecurityConfig;
import id.ac.ui.cs.advprog.udehnihpayment.strategy.CreditCardStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

@WebMvcTest(PaymentController.class)
@Import({TestSecurityConfig.class, PaymentControllerTest.MockConfig.class})
@TestPropertySource(locations = "classpath:application-test.properties")
public class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private RefundMapper refundMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public PaymentService paymentService() {
            return org.mockito.Mockito.mock(PaymentService.class);
        }
        @Bean
        public RefundService refundService() {
            return org.mockito.Mockito.mock(RefundService.class);
        }
        @Bean
        public PaymentMapper paymentMapper() {
            return org.mockito.Mockito.mock(PaymentMapper.class);
        }
        @Bean
        public RefundMapper refundMapper() {
            return org.mockito.Mockito.mock(RefundMapper.class);
        }
        @Bean
        public CreditCardStrategy creditCardStrategy() {
            return org.mockito.Mockito.mock(CreditCardStrategy.class);
        }
    }

    private Long transactionId;
    private Long courseId;
    private Long userId;
    private Payment payment;    
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionId = 1001L;
        courseId = 123L;
        userId = 456L;
        payment = Payment.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .userId(userId)                .build();
                
        // Set up PaymentMapper mock behaviors
        PaymentDetailDTO detailDTO = PaymentDetailDTO.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .amount(new BigDecimal("50000"))
                .paymentStatus("PENDING")
                .paymentMethod("BANK_TRANSFER")
                .build();

        PaymentResponseDTO responseDTO = PaymentResponseDTO.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .amount(new BigDecimal("50000"))
                .paymentStatus("PENDING")
                .paymentMethod("BANK_TRANSFER")
                .build();

        lenient().when(paymentMapper.toDetailDto(any(Payment.class))).thenReturn(detailDTO);
        lenient().when(paymentMapper.toResponseDto(any(Payment.class))).thenReturn(responseDTO);

        // Set up RefundMapper mock behavior
        RefundResponseDTO refundResponseDTO = RefundResponseDTO.builder()
                .refundId(678L)
                .status("PENDING")
                .message("Refund request has been submitted successfully.")
                .note("Your refund request is being processed by admin.")
                .build();

        lenient().when(refundMapper.toResponseDto(any(Refund.class))).thenReturn(refundResponseDTO);
    }

    @Test
    public void testGetPaymentMethods_Success() throws Exception {
        List<String> methods = Arrays.asList("Bank Transfer", "Credit Card");
        when(paymentService.getPaymentMethods()).thenReturn(methods);

        mockMvc.perform(get("/api/payments/methods")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Bank Transfer"))
                .andExpect(jsonPath("$[1]").value("Credit Card"));
    }

    @Test
    public void testGetPaymentMethods_ServiceThrowsException() throws Exception {
        when(paymentService.getPaymentMethods()).thenThrow(new RuntimeException("Service failure"));

        mockMvc.perform(get("/api/payments/methods")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

        @Test
        public void testProcessPayment_Success() throws Exception {
        String paymentMethod = "Bank Transfer";
        String requestJson = "{\"studentId\":" + userId + ",\"courseId\":" + courseId + ",\"amount\":50000,\"paymentMethod\":\"" + paymentMethod + "\"}";

        Payment savedPayment = Payment.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .build();

        when(paymentMapper.toEntity(any(PaymentRequestDTO.class))).thenReturn(savedPayment);
        when(paymentService.createPayment(any(Payment.class))).thenReturn(savedPayment);
        when(paymentService.processPayment(eq(transactionId), eq(paymentMethod))).thenReturn(savedPayment);

        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.transactionId").value(transactionId))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"));
    }

    @Test
    public void testProcessPayment_MissingFields() throws Exception {
        String requestJson = "{\"studentId\":null,\"courseId\":null,\"amount\":null,\"paymentMethod\":null}";
        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Missing required fields"));
    }

    @Test
    public void testGetTransactionDetails_Success() throws Exception {
        when(paymentService.findByTransactionId(eq(transactionId))).thenReturn(payment);
        PaymentDetailDTO detailDTO = PaymentDetailDTO.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .amount(new BigDecimal("50000"))
                .paymentStatus("PENDING")
                .paymentMethod("BANK_TRANSFER")
                .build();
        when(paymentMapper.toDetailDto(payment)).thenReturn(detailDTO);
        mockMvc.perform(get("/api/payments/{transactionId}", transactionId)
                .header("X-API-Key", "test-dashboard-api-key")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(transactionId))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"));
    }

    @Test
    public void testGetTransactionDetails_Unauthorized() throws Exception {
        when(paymentService.findByTransactionId(eq(transactionId))).thenReturn(payment);
        mockMvc.perform(get("/api/payments/{transactionId}", transactionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Authentication required"));
    }

    @Test
    public void testGetTransactionDetails_NotFound() throws Exception {
        when(paymentService.findByTransactionId(eq(transactionId))).thenReturn(null);
        mockMvc.perform(get("/api/payments/{transactionId}", transactionId)
                .header("X-API-Key", "test-dashboard-api-key")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Transaction not found with id: " + transactionId));
    }

    @Test
    public void testCreatePayment_Success() throws Exception {
        String requestJson = "{\"courseId\":" + courseId + ",\"paymentMethod\":\"BANK_TRANSFER\"}";
        Payment newPayment = Payment.builder()
                .transactionId(4001L)
                .courseId(courseId)
                .userId(userId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .build();
        PaymentResponseDTO responseDTO = PaymentResponseDTO.builder()
                .transactionId(newPayment.getTransactionId())
                .courseId(courseId)
                .userId(userId)
                .amount(new BigDecimal("50000"))
                .paymentMethod("BANK_TRANSFER")
                .paymentStatus("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        lenient().when(paymentMapper.toEntity(any(PaymentRequestDTO.class))).thenReturn(newPayment);
        lenient().when(paymentService.createPayment(any(Payment.class))).thenReturn(newPayment);
        lenient().when(paymentMapper.toResponseDto(any(Payment.class))).thenReturn(responseDTO);
        mockMvc.perform(post("/api/payments")
                .header("X-API-Key", "test-courses-api-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").exists());
    }

    @Test
    public void testCreatePayment_UnauthorizedAccess() throws Exception {
        String requestJson = "{\"courseId\":" + courseId + ",\"paymentMethod\":\"BANK_TRANSFER\"}";
        mockMvc.perform(post("/api/payments")
                .header("X-API-Key", "invalid-api-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Unauthorized access"));
    }

    @Test
    public void testRequestRefund_Success() throws Exception {
        Refund refund = Refund.builder()
                .id(3001L)
                .payment(payment)
                .reason("Course not as expected")
                .details("Content too basic")
                .build();
        RefundResponseDTO refundResponseDTO = RefundResponseDTO.builder()
                .refundId(refund.getId())
                .status("PENDING")
                .message("Refund request has been submitted successfully.")
                .note("Your refund request is being processed by admin.")
                .build();
        when(paymentService.findByTransactionId(eq(transactionId))).thenReturn(payment);
        when(refundService.requestRefund(eq(transactionId), eq("Course not as expected"), eq("Content too basic")))
                .thenReturn(refund);
        when(refundMapper.toResponseDto(refund)).thenReturn(refundResponseDTO);
        AppUserDetails authenticatedUser = new AppUserDetails(
                userId,
                "test@example.com",
                List.of(new SimpleGrantedAuthority("ROLE_STUDENT"))
        );
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        authenticatedUser,
                        null,
                        authenticatedUser.getAuthorities()
                );
        String requestJson = "{\"reason\":\"Course not as expected\",\"details\":\"Content too basic\"}";
        mockMvc.perform(post("/api/payments/{transactionId}/refund", transactionId)
                .with(authentication(authToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refundId").value(3001L))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.message").value("Refund request has been submitted successfully."));
    }
    
    @Test
    public void testGetTransactionHistory_Success() throws Exception {
        AppUserDetails userDetails = mock(AppUserDetails.class);
        when(userDetails.getId()).thenReturn(userId);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, List.of());
        List<Payment> userPayments = new ArrayList<>();
        userPayments.add(payment);
        List<PaymentResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(PaymentResponseDTO.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .amount(new BigDecimal("50000"))
                .paymentStatus("PENDING")
                .paymentMethod("BANK_TRANSFER")
                .build());
        lenient().when(paymentService.getAllPayments(userId)).thenReturn(userPayments);
        when(paymentMapper.toResponseDto(payment)).thenReturn(responseDTOs.get(0));
        mockMvc.perform(get("/api/payments/history")
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value(transactionId))
                .andExpect(jsonPath("$[0].userId").value(userId));
    }
    
    @Test
    public void testGetAllPayments_Success() throws Exception {
        List<Payment> allPayments = new ArrayList<>();
        allPayments.add(payment);
        List<PaymentResponseDTO> responseDTOs = new ArrayList<>();
        responseDTOs.add(PaymentResponseDTO.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .build());
        when(paymentService.getAllPayments()).thenReturn(allPayments);
        when(paymentMapper.toResponseDto(payment)).thenReturn(responseDTOs.get(0));
        mockMvc.perform(get("/api/payments/transactions")
                .header("X-API-Key", "test-dashboard-api-key")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions[0].transactionId").value(transactionId));
    }
    
    @Test
    public void testGetAllPayments_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/payments/transactions")
                .header("X-API-Key", "invalid-api-key")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Unauthorized access"));
    }
    
    @Test
    public void testUpdatePaymentStatus_Success() throws Exception {
        PaymentDetailDTO.Details updateRequest = new PaymentDetailDTO.Details();
        updateRequest.setAdminApproval(true);
        updateRequest.setApprovedBy("Admin User");
        
        String requestJson = "{\"adminApproval\":true,\"approvedBy\":\"Admin User\"}";
        
        when(paymentService.updatePaymentStatus(eq(transactionId), any(PaymentDetailDTO.Details.class)))
                .thenReturn(payment);
                
        PaymentResponseDTO responseDTO = PaymentResponseDTO.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .paymentStatus("PAID")
                .build();
                
        when(paymentMapper.toResponseDto(payment)).thenReturn(responseDTO);
        
        mockMvc.perform(put("/api/payments/{transactionId}/status", transactionId)
                .header("X-API-Key", "test-dashboard-api-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(transactionId))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));
    }
    
    @Test
    public void testUpdatePaymentStatus_Unauthorized() throws Exception {
        String requestJson = "{\"adminApproval\":true,\"approvedBy\":\"Admin User\"}";
        
        mockMvc.perform(put("/api/payments/{transactionId}/status", transactionId)
                .header("X-API-Key", "invalid-api-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized());
    }
}