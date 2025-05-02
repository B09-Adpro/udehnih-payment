package id.ac.ui.cs.advprog.udehnihpayment.controller;

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

    @BeforeEach
    public void setUp() {
        paymentController = new PaymentController(paymentService);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
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
        Long transactionId = 1L;
        Payment updatedPayment = Payment.builder()
                .idTransaksi(transactionId)
                .courseId(42L)
                .userId("user123")
                .paymentMethod("BankTransfer")
                .paymentStatus("PAID")
                .coursePrice(new BigDecimal("50000"))
                .build();
                
        when(paymentService.processPayment(eq(transactionId), eq("BankTransfer")))
            .thenReturn(updatedPayment);
        
        mockMvc.perform(post("/api/payments/{transactionId}/bank-transfer", transactionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("PAID"))
                .andExpect(jsonPath("$.paymentMethod").value("BankTransfer"));
    }

    @Test
    public void processCreditCardPayment_HappyPath_ReturnsSuccess() throws Exception {
        Long transactionId = 2L;
        Payment updatedPayment = Payment.builder()
                .idTransaksi(transactionId)
                .courseId(43L)
                .userId("user456")
                .paymentMethod("CreditCard")
                .paymentStatus("PAID")
                .coursePrice(new BigDecimal("75000"))
                .build();
                
        when(paymentService.processPayment(eq(transactionId), eq("CreditCard")))
            .thenReturn(updatedPayment);
        
        mockMvc.perform(post("/api/payments/{transactionId}/credit-card", transactionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("PAID"))
                .andExpect(jsonPath("$.paymentMethod").value("CreditCard"));
    }

    @Test
    public void processBankTransferPayment_NotFound_Returns404() throws Exception {
        Long transactionId = 999L;
        
        when(paymentService.processPayment(eq(transactionId), eq("BankTransfer")))
            .thenThrow(new IllegalArgumentException("Payment not found"));
        
        mockMvc.perform(post("/api/payments/{transactionId}/bank-transfer", transactionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}