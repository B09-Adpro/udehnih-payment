package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.exception.TransactionNotFoundException;
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
        Long transactionId = 789L;
        Long courseId = 123L;
        Long userId = 456L;
        Payment existingPayment = Payment.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .build();
        
        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(existingPayment);
        
        // Act
        Payment result = paymentService.processPayment(transactionId, PaymentMethod.BANK_TRANSFER.toString());
        
        // Assert
        assertEquals("PENDING", result.getPaymentStatus().toString());
        verify(paymentRepository).findByTransactionId(transactionId);
    }

    @Test
    public void processPayment_CreditCard_HappyPath() {
        // Arrange
        Long transactionId = 789L;
        Long courseId = 123L;
        Long userId = 456L;
        Payment existingPayment = Payment.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .build();
        
        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(existingPayment);

        Payment result = paymentService.processPayment(transactionId, PaymentMethod.CREDIT_CARD.toString());

        assertEquals("PENDING", result.getPaymentStatus().toString());
        verify(paymentRepository).findByTransactionId(transactionId);
    }

    @Test
    public void processPayment_NotFound_ThrowsException() {
        // Arrange
        Long transactionId = 789L;
        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(null);
        
        // Act & Assert
        TransactionNotFoundException exception = assertThrows(TransactionNotFoundException.class, () -> {
            paymentService.processPayment(transactionId, "BankTransfer");
        });
        
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    public void processPayment_MethodMismatch_ThrowsException() {
        // Arrange
        Long transactionId = 789L;
        Long courseId = 123L;
        Long userId = 456L;
        Payment existingPayment = Payment.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .build();
        
        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(existingPayment);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(transactionId, "CreditCard");
        });
        
        assertTrue(exception.getMessage().contains("mismatch"));
    }
}