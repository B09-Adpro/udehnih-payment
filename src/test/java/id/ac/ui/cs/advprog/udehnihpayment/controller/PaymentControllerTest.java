package id.ac.ui.cs.advprog.udehnihpayment.controller;

import id.ac.ui.cs.advprog.udehnihpayment.dto.response.PaymentDetailDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.response.PaymentResponseDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.response.RefundResponseDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.request.PaymentRequestDTO;
import id.ac.ui.cs.advprog.udehnihpayment.enums.*;
import id.ac.ui.cs.advprog.udehnihpayment.exception.GlobalExceptionHandler;
import id.ac.ui.cs.advprog.udehnihpayment.mapper.*;
import id.ac.ui.cs.advprog.udehnihpayment.model.*;
import id.ac.ui.cs.advprog.udehnihpayment.security.AppUserDetails;
import id.ac.ui.cs.advprog.udehnihpayment.service.*;
import jakarta.servlet.Filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.bind.support.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

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
    private UUID refundId;
    private Long courseId;
    private Long userId;
    private Payment payment;    @BeforeEach
    public void setUp() {
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        
        // Initialize test data first
        transactionId = UUID.fromString("6e51f16b-eba9-493f-9e97-fba59f421e48");
        refundId = UUID.fromString("7ed33dfe-cff7-41e4-b083-79822eddd600");
        courseId = 123L;
        userId = 456L;
        
        // Create a real AppUserDetails instance instead of mocking it
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
        AppUserDetails userDetails = new AppUserDetails(userId, "test@example.com", authorities);

        UsernamePasswordAuthenticationToken auth = 
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(auth);
        
        // Set up MockMvc with proper AuthenticationPrincipalArgumentResolver
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setControllerAdvice(exceptionHandler)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        payment = Payment.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .userId(userId)
                .build();
                
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
                .refundId(refundId)
                .status("PENDING")
                .message("Refund request has been submitted successfully.")
                .note("Your refund request is being processed by admin.")
                .build();

        lenient().when(refundMapper.toResponseDto(any(Refund.class))).thenReturn(refundResponseDTO);
        
        // Set API keys for testing
        ReflectionTestUtils.setField(paymentController, "courseApiKey", "test-course-api-key");
        ReflectionTestUtils.setField(paymentController, "dashboardApiKey", "test-dashboard-api-key");
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
        // Setup
        String paymentMethod = "Bank Transfer";

        // Prepare request JSON
        String requestJson = "{\"studentId\":" + userId + ",\"courseId\":" + courseId + 
                ",\"amount\":50000,\"paymentMethod\":\"" + paymentMethod + "\"}";

        // Create the payment that will be returned by createPayment
        Payment savedPayment = Payment.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .build();

        // Create the payment that will be returned by processPayment
        Payment processedPayment = Payment.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .build();

        // Mock payment creation and processing
        when(paymentMapper.toEntity(any(PaymentRequestDTO.class))).thenReturn(savedPayment);
        when(paymentService.createPayment(any(Payment.class))).thenReturn(savedPayment);
        when(paymentService.processPayment(eq(transactionId), eq(paymentMethod))).thenReturn(processedPayment);

        // Execute & Verify
        mockMvc.perform(post("/api/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.transactionId").exists())
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"));
        }

    @Test
    public void testProcessPayment_InvalidMethod() throws Exception {
        // Setup
        String paymentMethod = "InvalidMethod";
        mockMvc.perform(post("/api/payments/process", transactionId)
                .param("paymentMethod", paymentMethod)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
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
                .andExpect(jsonPath("$.transactionId").value(transactionId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"));
    }

    @Test
    public void testGetTransactionDetails_Unauthorized() throws Exception {
        when(paymentService.findByTransactionId(eq(transactionId))).thenReturn(payment);

        mockMvc.perform(get("/api/payments/{transactionId}", transactionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Unauthorized access"));
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
        String requestJson = "{\"courseId\":\"" + courseId + "\",\"paymentMethod\":\"BANK_TRANSFER\"}";
        UUID newTransactionId = UUID.fromString("67e46ff1-cfff-4ac0-a406-fd1c5923c62d");

        // Mock Payment entity
        Payment newPayment = Payment.builder()
                .transactionId(newTransactionId)
                .courseId(courseId)
                .userId(userId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .build();

        // Mock PaymentResponseDTO
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
                .header("X-API-Key", "test-course-api-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").exists());
    }

    @Test
    public void testCreatePayment_UnauthorizedAccess() throws Exception {
        String requestJson = "{\"courseId\":\"" + courseId + "\",\"paymentMethod\":\"BANK_TRANSFER\"}";

        mockMvc.perform(post("/api/payments")
                .header("X-API-Key", "invalid-api-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Unauthorized access"));
    }
    
    @Test
    public void testGetTransactionHistory_Success() throws Exception {
        // Mock user authentication
        AppUserDetails userDetails = mock(AppUserDetails.class);
        
        UsernamePasswordAuthenticationToken auth = 
                new UsernamePasswordAuthenticationToken(userDetails, null, List.of());
        
        // Prepare test data
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
        lenient().when(paymentService.getAllPayments(any())).thenReturn(userPayments);
        when(paymentMapper.toResponseDto(payment)).thenReturn(responseDTOs.get(0));
        
        mockMvc.perform(get("/api/payments/history")
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value(transactionId.toString()))
                .andExpect(jsonPath("$[0].userId").value(userId));
    }
    
    @Test
    public void testGetAllPayments_Success() throws Exception {
        // Prepare test data
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
        .andExpect(jsonPath("$.transactions[0].transactionId").value(transactionId.toString()));
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
                .andExpect(jsonPath("$.transactionId").value(transactionId.toString()))
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