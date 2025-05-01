package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    // HAPPY PATH: Get payment methods returns expected values
    @Test
    public void getPaymentMethods_ReturnsAllAvailableMethods() {
        List<String> result = paymentService.getPaymentMethods();
        
        assertEquals(2, result.size());
        assertTrue(result.contains("BankTransfer"));
        assertTrue(result.contains("CreditCard"));
    }

    // UNHAPPY PATH: No payment methods available
    @Test
    public void getPaymentMethods_NoAvailableMethods() {
        List<String> result = paymentService.getPaymentMethods();
        
        assertEquals(0, result.size());
    }
}