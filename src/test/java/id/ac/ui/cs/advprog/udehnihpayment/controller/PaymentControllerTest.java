package id.ac.ui.cs.advprog.udehnihpayment.controller;

import id.ac.ui.cs.advprog.udehnihpayment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}