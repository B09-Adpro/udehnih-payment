package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void getPaymentMethods_DoesNotContainInvalidMethods() {
        List<String> result = paymentService.getPaymentMethods();
        
        assertFalse(result.contains("InvalidMethod"));
        assertFalse(result.contains("Cash"));
    }

    @Test
    public void processPayment_BankTransfer_HappyPath() {
        // Arrange
        Long transactionId = 1L;
        Payment existingPayment = Payment.builder()
                .idTransaksi(transactionId)
                .courseId(42L)
                .userId("user123")
                .paymentMethod("BankTransfer")
                .paymentStatus("PENDING")
                .coursePrice(new BigDecimal("50000"))
                .build();
                
        Payment updatedPayment = Payment.builder()
                .idTransaksi(transactionId)
                .courseId(42L)
                .userId("user123")
                .paymentMethod("BankTransfer")
                .paymentStatus("PAID")
                .coursePrice(new BigDecimal("50000"))
                .build();
        
        when(paymentRepository.findByIdTransaksi(transactionId)).thenReturn(existingPayment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);
        
        // Act
        Payment result = paymentService.processPayment(transactionId, "BankTransfer");
        
        // Assert
        assertEquals("PAID", result.getPaymentStatus());
        verify(paymentRepository).findByIdTransaksi(transactionId);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    public void processPayment_CreditCard_HappyPath() {
        // Arrange
        Long transactionId = 2L;
        Payment existingPayment = Payment.builder()
                .idTransaksi(transactionId)
                .courseId(43L)
                .userId("user456")
                .paymentMethod("CreditCard")
                .paymentStatus("PENDING")
                .coursePrice(new BigDecimal("75000"))
                .build();
                
        Payment updatedPayment = Payment.builder()
                .idTransaksi(transactionId)
                .courseId(43L)
                .userId("user456")
                .paymentMethod("CreditCard")
                .paymentStatus("PAID")
                .coursePrice(new BigDecimal("75000"))
                .build();
        
        when(paymentRepository.findByIdTransaksi(transactionId)).thenReturn(existingPayment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);
        
        // Act
        Payment result = paymentService.processPayment(transactionId, "CreditCard");
        
        // Assert
        assertEquals("PAID", result.getPaymentStatus());
        verify(paymentRepository).findByIdTransaksi(transactionId);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    public void processPayment_NotFound_ThrowsException() {
        // Arrange
        Long transactionId = 999L;
        when(paymentRepository.findByIdTransaksi(transactionId)).thenReturn(null);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(transactionId, "BankTransfer");
        });
        
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    public void processPayment_MethodMismatch_ThrowsException() {
        // Arrange
        Long transactionId = 1L;
        Payment existingPayment = Payment.builder()
                .idTransaksi(transactionId)
                .courseId(42L)
                .userId("user123")
                .paymentMethod("BankTransfer") 
                .paymentStatus("PENDING")
                .coursePrice(new BigDecimal("50000"))
                .build();
        
        when(paymentRepository.findByIdTransaksi(transactionId)).thenReturn(existingPayment);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(transactionId, "CreditCard");
        });
        
        assertTrue(exception.getMessage().contains("mismatch"));
    }
}