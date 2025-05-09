package id.ac.ui.cs.advprog.udehnihpayment.controller;

import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

    private PaymentController paymentController;

    private UUID transactionId;
    private UUID courseId;
    private UUID userId;
    private Payment payment;

    @BeforeEach
    public void setUp() {
        paymentController = new PaymentController(paymentService, null);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();

        // Set up a sample payment object
        transactionId = UUID.randomUUID();
        courseId = UUID.randomUUID();
        userId = UUID.randomUUID();
        payment = Payment.builder()
                .transactionId(transactionId)
                .course(courseId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PAID)
                .coursePrice(new BigDecimal("50000"))
                .userId(userId)
                .build();
    }

    // HAPPY PATH: Get payment methods successfully
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
                .course(courseId)
                .userId(userId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .coursePrice(new BigDecimal("50000"))
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
                .course(courseId)
                .userId(userId)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .coursePrice(new BigDecimal("50000"))
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
}