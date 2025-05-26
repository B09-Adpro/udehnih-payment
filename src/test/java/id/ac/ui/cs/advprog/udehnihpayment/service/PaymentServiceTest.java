package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.exception.TransactionNotFoundException;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.repository.PaymentRepository;
import id.ac.ui.cs.advprog.udehnihpayment.strategy.BankTransferStrategy;
import id.ac.ui.cs.advprog.udehnihpayment.strategy.CreditCardStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

    @Mock
    private BankTransferStrategy bankTransferStrategy;

    @Mock
    private CreditCardStrategy creditCardStrategy;

    @BeforeEach
    public void setUp() {
        // Inject mock strategies ke PaymentServiceImpl
        paymentService = new PaymentServiceImpl(
            paymentRepository,
            null,
            null,
            "",
            Arrays.asList(bankTransferStrategy, creditCardStrategy)
        );
    }

    // HAPPY PATH: Get payment methods returns expected values
    @Test
    public void getPaymentMethods_ReturnsAllAvailableMethods() {
        List<String> result = paymentService.getPaymentMethods();
        
        assertEquals(2, result.size());
        assertTrue(result.contains("Bank Transfer"));
        assertTrue(result.contains("Credit Card"));
    }

    // UNHAPPY PATH: No payment methods available
    @Test
    public void getPaymentMethods_DoesNotContainInvalidMethods() {
        List<String> result = paymentService.getPaymentMethods();
        
        assertFalse(result.contains("InvalidMethod"));
        assertFalse(result.contains("Cash"));
    }    @Test
    public void processPayment_BankTransfer_HappyPath() {
        UUID transactionId = UUID.fromString("6e51f16b-eba9-493f-9e97-fba59f421e48");
        Long courseId = 123L;
        Long userId = 456L;        Payment existingPayment = Payment.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .expiresAt(java.time.LocalDateTime.now().plusDays(1))
                .build();

        Payment savedPayment = existingPayment.toBuilder()
                .paymentStatus(PaymentStatus.WAITING_PAYMENT)
                .build();

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(existingPayment);
        when(bankTransferStrategy.supports("Bank Transfer")).thenReturn(true);
        when(bankTransferStrategy.processPayment(any(Payment.class))).thenReturn("OK");
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // Act
        Payment result = paymentService.processPayment(transactionId, PaymentMethod.BANK_TRANSFER.toString());

        // Assert
        assertEquals("WAITING_PAYMENT", result.getPaymentStatus().toString());
        verify(paymentRepository).findByTransactionId(transactionId);
        verify(paymentRepository).save(any(Payment.class));
    }    
    
    @Test
    public void processPayment_CreditCard_HappyPath() {
        UUID transactionId = UUID.fromString("6e51f16b-eba9-493f-9e97-fba59f421e48");
        Long courseId = 123L;
        Long userId = 456L;        
        Payment existingPayment = Payment.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .expiresAt(java.time.LocalDateTime.now().plusDays(1))
                .build();

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(existingPayment);
        when(creditCardStrategy.supports("Credit Card")).thenReturn(true);
        when(creditCardStrategy.processPayment(any(Payment.class))).thenReturn("OK");

        // Act
        Payment result = paymentService.processPayment(transactionId, PaymentMethod.CREDIT_CARD.toString());

        // Assert
        assertEquals("PENDING", result.getPaymentStatus().toString());
        verify(paymentRepository).findByTransactionId(transactionId);
    }

    @Test
    public void processPayment_NotFound_ThrowsException() {
        // Arrange
        UUID transactionId = UUID.fromString("6e51f16b-eba9-493f-9e97-fba59f421e48");
        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(null);
        
        // Act & Assert
        TransactionNotFoundException exception = assertThrows(TransactionNotFoundException.class, () -> {
            paymentService.processPayment(transactionId, "Bank Transfer");
        });
        
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    public void processPayment_MethodMismatch_ThrowsException() {
        // Arrange
        UUID transactionId = UUID.fromString("6e51f16b-eba9-493f-9e97-fba59f421e48");
        Long courseId = 123L;
        Long userId = 456L;        
        Payment existingPayment = Payment.builder()
                .transactionId(transactionId)
                .courseId(courseId)
                .userId(userId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .expiresAt(java.time.LocalDateTime.now().plusDays(1))
                .build();
        
        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(existingPayment);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(transactionId, "Credit Card");
        });
        
        assertTrue(exception.getMessage().contains("mismatch"));
    }
}