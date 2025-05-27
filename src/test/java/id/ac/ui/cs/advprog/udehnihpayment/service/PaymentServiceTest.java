package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.clients.CourseServiceClient;
import id.ac.ui.cs.advprog.udehnihpayment.clients.DashboardServiceClient;
import id.ac.ui.cs.advprog.udehnihpayment.dto.response.PaymentDetailDTO;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.exception.TransactionNotFoundException;
import id.ac.ui.cs.advprog.udehnihpayment.exception.UnauthorizedAccessException;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.model.PaymentDetails;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CourseServiceClient courseServiceClient;

    @Mock
    private DashboardServiceClient dashboardServiceClient;

    @Mock
    private CreditCardStrategy creditCardStrategy;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private BankTransferStrategy bankTransferStrategy;

    private final String dashboardApiKey = "test-api-key";    @BeforeEach
    public void setUp() {
        // Inject mock strategies ke PaymentServiceImpl
        paymentService = new PaymentServiceImpl(
            paymentRepository,
            courseServiceClient,
            dashboardServiceClient,
            dashboardApiKey,
            Arrays.asList(bankTransferStrategy, creditCardStrategy)
        );
        
        // Use reflection to inject all @Autowired fields
        try {
            java.lang.reflect.Field creditCardField = PaymentServiceImpl.class.getDeclaredField("creditCardStrategy");
            creditCardField.setAccessible(true);
            creditCardField.set(paymentService, creditCardStrategy);
            
            java.lang.reflect.Field repositoryField = PaymentServiceImpl.class.getDeclaredField("paymentRepository");
            repositoryField.setAccessible(true);
            repositoryField.set(paymentService, paymentRepository);
            
            java.lang.reflect.Field courseServiceField = PaymentServiceImpl.class.getDeclaredField("courseServiceClient");
            courseServiceField.setAccessible(true);
            courseServiceField.set(paymentService, courseServiceClient);
            
            java.lang.reflect.Field dashboardServiceField = PaymentServiceImpl.class.getDeclaredField("dashboardServiceClient");
            dashboardServiceField.setAccessible(true);
            dashboardServiceField.set(paymentService, dashboardServiceClient);
            
            java.lang.reflect.Field apiKeyField = PaymentServiceImpl.class.getDeclaredField("dashboardApiKey");
            apiKeyField.setAccessible(true);
            apiKeyField.set(paymentService, dashboardApiKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject fields", e);
        }
    }

    // ================= EXISTING TESTS =================

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
    }

    @Test
    public void processPayment_BankTransfer_HappyPath() {
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

    // ================= NEW TESTS FOR CREATEPAYMENT =================

    @Test
    public void createPayment_HappyPath_SetsDefaultStatusAndExpiry() {
        // Arrange
        Payment payment = Payment.builder()
                .transactionId(UUID.randomUUID())
                .courseId(123L)
                .userId(456L)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .amount(new BigDecimal("50000"))
                .build();

        Payment savedPayment = payment.toBuilder()
                .paymentStatus(PaymentStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        when(bankTransferStrategy.supports("Bank Transfer")).thenReturn(true);
        when(bankTransferStrategy.processPayment(any(Payment.class))).thenReturn("Payment instructions");

        // Act
        Payment result = paymentService.createPayment(payment);

        // Assert
        assertNotNull(result);
        assertEquals(PaymentStatus.PENDING, result.getPaymentStatus());
        assertNotNull(result.getExpiresAt());
        verify(paymentRepository).save(any(Payment.class));
        verify(bankTransferStrategy).processPayment(any(Payment.class));
    }

    @Test
    public void createPayment_WithExistingStatusAndExpiry_DoesNotOverride() {
        // Arrange
        LocalDateTime customExpiry = LocalDateTime.now().plusHours(12);
        Payment payment = Payment.builder()
                .transactionId(UUID.randomUUID())
                .courseId(123L)
                .userId(456L)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .amount(new BigDecimal("75000"))
                .paymentStatus(PaymentStatus.WAITING_PAYMENT)
                .expiresAt(customExpiry)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(creditCardStrategy.supports("Credit Card")).thenReturn(true);
        when(creditCardStrategy.processPayment(any(Payment.class))).thenReturn("Card processing");

        // Act
        Payment result = paymentService.createPayment(payment);

        // Assert
        assertEquals(PaymentStatus.WAITING_PAYMENT, result.getPaymentStatus());
        assertEquals(customExpiry, result.getExpiresAt());
    }

    @Test
    public void createPayment_NoStrategyFound_ThrowsException() {
        // Arrange
        Payment payment = Payment.builder()
                .transactionId(UUID.randomUUID())
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(bankTransferStrategy.supports("Bank Transfer")).thenReturn(false);
        when(creditCardStrategy.supports("Bank Transfer")).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.createPayment(payment);
        });
        
        assertTrue(exception.getMessage().contains("No strategy found"));
    }

    // ================= NEW TESTS FOR GETALLPAYMENTS =================

    @Test
    public void getAllPayments_WithUserId_ReturnsUserPayments() {
        // Arrange
        Long userId = 456L;
        List<Payment> userPayments = Arrays.asList(
            Payment.builder().userId(userId).transactionId(UUID.randomUUID()).build(),
            Payment.builder().userId(userId).transactionId(UUID.randomUUID()).build()
        );

        when(paymentRepository.findAllByUserId(userId)).thenReturn(userPayments);

        // Act
        List<Payment> result = paymentService.getAllPayments(userId);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getUserId().equals(userId)));
        verify(paymentRepository).findAllByUserId(userId);
    }

    @Test
    public void getAllPayments_WithUserId_EmptyResult() {
        // Arrange
        Long userId = 999L;
        when(paymentRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        List<Payment> result = paymentService.getAllPayments(userId);

        // Assert
        assertTrue(result.isEmpty());
        verify(paymentRepository).findAllByUserId(userId);
    }

    @Test
    public void getAllPayments_NoUserId_ReturnsAllPayments() {
        // Arrange
        List<Payment> allPayments = Arrays.asList(
            Payment.builder().userId(123L).transactionId(UUID.randomUUID()).build(),
            Payment.builder().userId(456L).transactionId(UUID.randomUUID()).build(),
            Payment.builder().userId(789L).transactionId(UUID.randomUUID()).build()
        );

        when(paymentRepository.findAll()).thenReturn(allPayments);

        // Act
        List<Payment> result = paymentService.getAllPayments();

        // Assert
        assertEquals(3, result.size());
        verify(paymentRepository).findAll();
    }

    @Test
    public void getAllPayments_NoUserId_EmptyResult() {
        // Arrange
        when(paymentRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Payment> result = paymentService.getAllPayments();

        // Assert
        assertTrue(result.isEmpty());
        verify(paymentRepository).findAll();
    }

    // ================= NEW TESTS FOR FINDBYTRANSACTIONID =================

    @Test
    public void findByTransactionId_PaymentExists_ReturnsPayment() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .userId(123L)
                .build();

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(payment);

        // Act
        Payment result = paymentService.findByTransactionId(transactionId);

        // Assert
        assertNotNull(result);
        assertEquals(transactionId, result.getTransactionId());
        verify(paymentRepository).findByTransactionId(transactionId);
    }

    @Test
    public void findByTransactionId_PaymentNotExists_ReturnsNull() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(null);

        // Act
        Payment result = paymentService.findByTransactionId(transactionId);

        // Assert
        assertNull(result);
        verify(paymentRepository).findByTransactionId(transactionId);
    }

    // ================= NEW TESTS FOR UPDATEPAYMENTSTATUS =================

    @Test
    public void updatePaymentStatus_PaymentNotFound_ThrowsException() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        PaymentDetailDTO.Details updateRequest = new PaymentDetailDTO.Details();
        
        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(null);

        // Act & Assert
        TransactionNotFoundException exception = assertThrows(TransactionNotFoundException.class, () -> {
            paymentService.updatePaymentStatus(transactionId, updateRequest);
        });
        
        assertTrue(exception.getMessage().contains("Payment not found"));
    }    @Test
    public void updatePaymentStatus_AdminApproval_UpdatesStatusAndNotifiesServices() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .userId(123L)
                .courseId(456L)
                .enrollmentId(789L)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .amount(new BigDecimal("50000"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PaymentDetailDTO.Details updateRequest = new PaymentDetailDTO.Details();
        updateRequest.setAdminApproval(true);
        updateRequest.setApprovedBy("admin123");

        Payment updatedPayment = payment.toBuilder()
                .paymentStatus(PaymentStatus.PAID)
                .paymentDetails(new PaymentDetails())
                .build();
        updatedPayment.getPaymentDetails().setAdminApproval(true);
        updatedPayment.getPaymentDetails().setApprovedBy("admin123");

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);

        // Act
        Payment result = paymentService.updatePaymentStatus(transactionId, updateRequest);

        // Assert
        assertEquals(PaymentStatus.PAID, result.getPaymentStatus());
        assertTrue(result.getPaymentDetails().isAdminApproval());
        assertEquals("admin123", result.getPaymentDetails().getApprovedBy());
        verify(courseServiceClient).updateEnrollmentStatus(anyMap());
        verify(dashboardServiceClient).notifyPaymentUpdate(eq(dashboardApiKey), anyMap());
    }

    @Test
    public void updatePaymentStatus_ConfirmationOnly_UpdatesDetailsWithoutNotification() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .userId(123L)
                .paymentStatus(PaymentStatus.WAITING_PAYMENT)
                .build();

        PaymentDetailDTO.Details updateRequest = new PaymentDetailDTO.Details();
        updateRequest.setConfirmation(true);

        Payment updatedPayment = payment.toBuilder()
                .paymentDetails(new PaymentDetails())
                .build();

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);        // Act
        Payment result = paymentService.updatePaymentStatus(transactionId, updateRequest);

        // Assert
        assertNotNull(result.getPaymentDetails());
        assertFalse(result.getPaymentDetails().isConfirmation()); // Should remain false since not set
        verify(courseServiceClient, never()).updateEnrollmentStatus(anyMap());
        verify(dashboardServiceClient, never()).notifyPaymentUpdate(anyString(), anyMap());
    }

    @Test
    public void updatePaymentStatus_AlreadyApproved_DoesNotNotifyAgain() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        PaymentDetails existingDetails = new PaymentDetails();
        existingDetails.setAdminApproval(true);
        
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .userId(123L)
                .paymentStatus(PaymentStatus.PAID)
                .paymentDetails(existingDetails)
                .build();

        PaymentDetailDTO.Details updateRequest = new PaymentDetailDTO.Details();
        updateRequest.setAdminApproval(true);

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        Payment result = paymentService.updatePaymentStatus(transactionId, updateRequest);

        // Assert
        verify(courseServiceClient, never()).updateEnrollmentStatus(anyMap());
        verify(dashboardServiceClient, never()).notifyPaymentUpdate(anyString(), anyMap());
    }

    @Test
    public void updatePaymentStatus_ServiceNotificationFails_ContinuesExecution() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .userId(123L)
                .courseId(456L)
                .enrollmentId(789L)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        PaymentDetailDTO.Details updateRequest = new PaymentDetailDTO.Details();
        updateRequest.setAdminApproval(true);

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        doThrow(new RuntimeException("Service error")).when(courseServiceClient).updateEnrollmentStatus(anyMap());        // Act & Assert (should not throw exception)
        assertDoesNotThrow(() -> {
            Payment result = paymentService.updatePaymentStatus(transactionId, updateRequest);
            assertNotNull(result); // Verify result is not null
        });
    }

    // ================= NEW TESTS FOR CONFIRMBANKTRANSFER =================

    @Test
    public void confirmBankTransfer_HappyPath_UpdatesStatusToPending() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        Long userId = 123L;
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .userId(userId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.WAITING_PAYMENT)
                .expiresAt(LocalDateTime.now().plusHours(12))
                .build();        Payment updatedPayment = payment.toBuilder()
                .paymentStatus(PaymentStatus.PENDING)
                .paymentDetails(new PaymentDetails())
                .build();
        updatedPayment.getPaymentDetails().setConfirmation(true);
        updatedPayment.getPaymentDetails().setConfirmedAt(LocalDateTime.now());

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(payment);
        when(paymentRepository.saveAndFlush(any(Payment.class))).thenReturn(updatedPayment);

        // Act
        Payment result = paymentService.confirmBankTransfer(transactionId, userId);        // Assert
        assertEquals(PaymentStatus.PENDING, result.getPaymentStatus());
        assertTrue(result.getPaymentDetails().isConfirmation());
        assertNotNull(result.getPaymentDetails().getConfirmedAt());
        verify(paymentRepository).saveAndFlush(any(Payment.class));
    }

    @Test
    public void confirmBankTransfer_PaymentNotFound_ThrowsException() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        Long userId = 123L;
        
        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(null);

        // Act & Assert
        TransactionNotFoundException exception = assertThrows(TransactionNotFoundException.class, () -> {
            paymentService.confirmBankTransfer(transactionId, userId);
        });
        
        assertTrue(exception.getMessage().contains("Payment not found"));
    }

    @Test
    public void confirmBankTransfer_UnauthorizedUser_ThrowsException() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        Long userId = 123L;
        Long differentUserId = 456L;
        
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .userId(differentUserId)
                .build();

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(payment);

        // Act & Assert
        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () -> {
            paymentService.confirmBankTransfer(transactionId, userId);
        });
        
        assertTrue(exception.getMessage().contains("Not your payment"));
    }

    @Test
    public void confirmBankTransfer_NotBankTransfer_ThrowsException() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        Long userId = 123L;
        
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .userId(userId)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(payment);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.confirmBankTransfer(transactionId, userId);
        });
        
        assertTrue(exception.getMessage().contains("Confirmation only for bank transfer"));
    }

    @Test
    public void confirmBankTransfer_NotWaitingPayment_ThrowsException() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        Long userId = 123L;
        
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .userId(userId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(payment);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.confirmBankTransfer(transactionId, userId);
        });
        
        assertTrue(exception.getMessage().contains("not in WAITING_PAYMENT state"));
    }

    @Test
    public void confirmBankTransfer_AlreadyConfirmed_ThrowsException() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        Long userId = 123L;
        
        PaymentDetails details = new PaymentDetails();
        details.setConfirmation(true);
        
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .userId(userId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.WAITING_PAYMENT)
                .paymentDetails(details)
                .build();

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(payment);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.confirmBankTransfer(transactionId, userId);
        });
        
        assertTrue(exception.getMessage().contains("Transfer already accepted"));
    }

    @Test
    public void confirmBankTransfer_ExpiredPayment_ThrowsException() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        Long userId = 123L;
        
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .userId(userId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.WAITING_PAYMENT)
                .expiresAt(LocalDateTime.now().minusHours(1)) // Expired 1 hour ago
                .build();

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(payment);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.confirmBankTransfer(transactionId, userId);
        });
        
        assertTrue(exception.getMessage().contains("Payment has expired"));
    }

    // ================= NEW TESTS FOR PROCESSCREDITCARDPAYMENT =================

    @Test
    public void processCreditCardPayment_HappyPath_UpdatesStatusToPaid() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        String cardNumber = "1234567890123456";
        String cvc = "123";
        String expiryDate = "12/25";
        
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        Payment updatedPayment = payment.toBuilder()
                .paymentStatus(PaymentStatus.PAID)
                .build();

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(payment);
        when(creditCardStrategy.validateCardDetails(cardNumber, cvc)).thenReturn(true);
        when(creditCardStrategy.validateExpiryDate(expiryDate)).thenReturn(true);
        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);

        // Act
        Payment result = paymentService.processCreditCardPayment(transactionId, cardNumber, cvc, expiryDate);

        // Assert
        assertEquals(PaymentStatus.PAID, result.getPaymentStatus());
        verify(creditCardStrategy).validateCardDetails(cardNumber, cvc);
        verify(creditCardStrategy).validateExpiryDate(expiryDate);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    public void processCreditCardPayment_PaymentNotFound_ThrowsException() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        
        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processCreditCardPayment(transactionId, "1234", "123", "12/25");
        });
        
        assertTrue(exception.getMessage().contains("Payment not found"));
    }

    @Test
    public void processCreditCardPayment_InvalidCardDetails_ThrowsException() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        String cardNumber = "invalid";
        String cvc = "123";
        String expiryDate = "12/25";
        
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .build();

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(payment);
        when(creditCardStrategy.validateCardDetails(cardNumber, cvc)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processCreditCardPayment(transactionId, cardNumber, cvc, expiryDate);
        });
        
        assertTrue(exception.getMessage().contains("Nomor kartu atau CVC tidak valid"));
    }

    @Test
    public void processCreditCardPayment_InvalidExpiryDate_ThrowsException() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        String cardNumber = "1234567890123456";
        String cvc = "123";
        String expiryDate = "13/20"; // Invalid month/year
        
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .build();

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(payment);
        when(creditCardStrategy.validateCardDetails(cardNumber, cvc)).thenReturn(true);
        when(creditCardStrategy.validateExpiryDate(expiryDate)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processCreditCardPayment(transactionId, cardNumber, cvc, expiryDate);
        });
        
        assertTrue(exception.getMessage().contains("Tanggal kadaluarsa tidak valid"));
    }

    // ================= EDGE CASE TESTS =================

    @Test
    public void processPayment_NoStrategyFound_ThrowsException() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .build();

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(payment);
        when(bankTransferStrategy.supports("Bank Transfer")).thenReturn(false);
        when(creditCardStrategy.supports("Bank Transfer")).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(transactionId, "Bank Transfer");
        });
        
        assertTrue(exception.getMessage().contains("No strategy found"));
    }

    @Test
    public void updatePaymentStatus_WithExistingPaymentDetails_UpdatesCorrectly() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        PaymentDetails existingDetails = new PaymentDetails();
        existingDetails.setConfirmation(false);
        existingDetails.setAdminApproval(false);
        
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .userId(123L)
                .paymentDetails(existingDetails)
                .build();

        PaymentDetailDTO.Details updateRequest = new PaymentDetailDTO.Details();
        updateRequest.setConfirmation(true);
        updateRequest.setConfirmedAt(LocalDateTime.now());

        when(paymentRepository.findByTransactionId(transactionId)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        Payment result = paymentService.updatePaymentStatus(transactionId, updateRequest);

        // Assert
        assertNotNull(result.getPaymentDetails());
        assertTrue(result.getPaymentDetails().isConfirmation());
        assertNotNull(result.getPaymentDetails().getConfirmedAt());
    }
}