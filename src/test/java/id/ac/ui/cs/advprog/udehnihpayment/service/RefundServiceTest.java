package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.clients.CourseServiceClient;
import id.ac.ui.cs.advprog.udehnihpayment.clients.DashboardServiceClient;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.enums.RefundStatus;
import id.ac.ui.cs.advprog.udehnihpayment.exception.InvalidRefundReasonException;
import id.ac.ui.cs.advprog.udehnihpayment.exception.RefundAlreadyRequestedException;
import id.ac.ui.cs.advprog.udehnihpayment.exception.RefundTooLateException;
import id.ac.ui.cs.advprog.udehnihpayment.exception.TransactionNotFoundException;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.model.Refund;
import id.ac.ui.cs.advprog.udehnihpayment.repository.PaymentRepository;
import id.ac.ui.cs.advprog.udehnihpayment.repository.RefundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class RefundServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RefundRepository refundRepository;

    @Mock
    private CourseServiceClient courseServiceClient;

    @Mock
    private DashboardServiceClient dashboardServiceClient;

    @InjectMocks
    private RefundServiceImpl refundService;

    private Payment payment;
    private Refund refund;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        payment = createTestPayment();
        refund = createTestRefund();
    }

    private Payment createTestPayment() {
        return Payment.builder()
                .transactionId(UUID.fromString("6e51f16b-eba9-493f-9e97-fba59f421e48"))
                .userId(456L)
                .courseId(123L)
                .enrollmentId(789L)
                .amount(new BigDecimal("50000"))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PAID)
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();
    }

    private Refund createTestRefund() {
        return Refund.builder()
                .id(UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef"))
                .payment(payment)
                .reason("Not satisfied")
                .details("Course content too basic")
                .refundStatus(RefundStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void testRequestRefund_Success() {
        when(paymentRepository.findByTransactionId(payment.getTransactionId())).thenReturn(payment);
        when(refundRepository.findByPaymentTransactionId(payment.getTransactionId())).thenReturn(Collections.emptyList());
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        Refund result = refundService.requestRefund(payment.getTransactionId(), "Not satisfied", "Too basic");

        assertNotNull(result);
        assertEquals("PENDING", result.getRefundStatus().toString());
        assertEquals("Not satisfied", result.getReason());
        assertEquals("Course content too basic", result.getDetails());
        verify(paymentRepository).findByTransactionId(payment.getTransactionId());
        verify(refundRepository).findByPaymentTransactionId(payment.getTransactionId());
        verify(refundRepository).save(any(Refund.class));
    }

    @Test
    public void testRequestRefund_TransactionNotFound() {
        UUID fakeId = UUID.fromString("6e51f16b-eba9-493f-9e97-fba59f421e48");
        when(paymentRepository.findByTransactionId(fakeId)).thenReturn(null);

        TransactionNotFoundException exception = assertThrows(TransactionNotFoundException.class, () -> {
            refundService.requestRefund(fakeId, "reason", "details");
        });

        assertTrue(exception.getMessage().contains("Payment with ID"));
        assertTrue(exception.getMessage().contains("not found"));
        verify(paymentRepository).findByTransactionId(fakeId);
        verify(refundRepository, never()).save(any());
    }

    @Test
    public void testRequestRefund_EmptyReason() {
        when(paymentRepository.findByTransactionId(payment.getTransactionId())).thenReturn(payment);

        InvalidRefundReasonException exception = assertThrows(InvalidRefundReasonException.class, () -> {
            refundService.requestRefund(payment.getTransactionId(), "", "some detail");
        });

        assertEquals("Refund reason cannot be empty", exception.getMessage());
        verify(paymentRepository).findByTransactionId(payment.getTransactionId());
        verify(refundRepository, never()).save(any());
    }

    @Test
    public void testRequestRefund_NullReason() {
        when(paymentRepository.findByTransactionId(payment.getTransactionId())).thenReturn(payment);

        InvalidRefundReasonException exception = assertThrows(InvalidRefundReasonException.class, () -> {
            refundService.requestRefund(payment.getTransactionId(), null, "some detail");
        });

        assertEquals("Refund reason cannot be empty", exception.getMessage());
        verify(paymentRepository).findByTransactionId(payment.getTransactionId());
        verify(refundRepository, never()).save(any());
    }

    @Test
    public void testRequestRefund_WhitespaceOnlyReason() {
        when(paymentRepository.findByTransactionId(payment.getTransactionId())).thenReturn(payment);

        InvalidRefundReasonException exception = assertThrows(InvalidRefundReasonException.class, () -> {
            refundService.requestRefund(payment.getTransactionId(), "   ", "some detail");
        });

        assertEquals("Refund reason cannot be empty", exception.getMessage());
        verify(paymentRepository).findByTransactionId(payment.getTransactionId());
        verify(refundRepository, never()).save(any());
    }

    @Test
    public void testRequestRefund_AlreadyRequested() {
        List<Refund> existingRefunds = Arrays.asList(refund);
        
        when(paymentRepository.findByTransactionId(payment.getTransactionId())).thenReturn(payment);
        when(refundRepository.findByPaymentTransactionId(payment.getTransactionId())).thenReturn(existingRefunds);

        RefundAlreadyRequestedException exception = assertThrows(RefundAlreadyRequestedException.class, () -> {
            refundService.requestRefund(payment.getTransactionId(), "reason", "details");
        });

        assertTrue(exception.getMessage().contains("A refund has already been requested"));
        assertTrue(exception.getMessage().contains(payment.getTransactionId().toString()));
        verify(paymentRepository).findByTransactionId(payment.getTransactionId());
        verify(refundRepository).findByPaymentTransactionId(payment.getTransactionId());
        verify(refundRepository, never()).save(any());
    }

    @Test
    public void testRequestRefund_TooLate() {
        Payment oldPayment = Payment.builder()
                .transactionId(UUID.fromString("6e51f16b-eba9-493f-9e97-fba59f421e48"))
                .userId(456L)
                .courseId(123L)
                .amount(new BigDecimal("50000"))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PAID)
                .createdAt(LocalDateTime.now().minusDays(35)) // 35 days ago, beyond 30-day limit
                .build();

        when(paymentRepository.findByTransactionId(oldPayment.getTransactionId())).thenReturn(oldPayment);
        when(refundRepository.findByPaymentTransactionId(oldPayment.getTransactionId())).thenReturn(Collections.emptyList());

        RefundTooLateException exception = assertThrows(RefundTooLateException.class, () -> {
            refundService.requestRefund(oldPayment.getTransactionId(), "reason", "details");
        });

        assertTrue(exception.getMessage().contains("Refund is no longer available"));
        assertTrue(exception.getMessage().contains("30 days ago"));
        verify(paymentRepository).findByTransactionId(oldPayment.getTransactionId());
        verify(refundRepository).findByPaymentTransactionId(oldPayment.getTransactionId());
        verify(refundRepository, never()).save(any());
    }

    @Test
    public void testRequestRefund_WithNullDetails() {
        when(paymentRepository.findByTransactionId(payment.getTransactionId())).thenReturn(payment);
        when(refundRepository.findByPaymentTransactionId(payment.getTransactionId())).thenReturn(Collections.emptyList());
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        Refund result = refundService.requestRefund(payment.getTransactionId(), "Not satisfied", null);

        assertNotNull(result);
        verify(refundRepository).save(argThat(savedRefund -> 
            savedRefund.getDetails().equals("") && // null details should be converted to empty string
            savedRefund.getReason().equals("Not satisfied")
        ));
    }

    @Test
    public void testRequestRefund_WithEmptyDetails() {
        when(paymentRepository.findByTransactionId(payment.getTransactionId())).thenReturn(payment);
        when(refundRepository.findByPaymentTransactionId(payment.getTransactionId())).thenReturn(Collections.emptyList());
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        Refund result = refundService.requestRefund(payment.getTransactionId(), "Not satisfied", "");

        assertNotNull(result);
        verify(refundRepository).save(argThat(savedRefund -> 
            savedRefund.getDetails().equals("") &&
            savedRefund.getReason().equals("Not satisfied")
        ));
    }

    @Test
    public void testGetAllRefunds_EmptyList() {
        when(refundRepository.findAll()).thenReturn(Collections.emptyList());

        List<Refund> result = refundService.getAllRefunds();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(refundRepository).findAll();
    }

    @Test
    public void testGetAllRefunds_WithMultipleRefunds() {
        Refund refund1 = createTestRefund();        Refund refund2 = Refund.builder()
                .id(UUID.fromString("b2c3d4e5-f6a7-8901-2345-678901bcdef0"))
                .payment(payment)
                .reason("Different reason")
                .details("Different details")
                .refundStatus(RefundStatus.APPROVED)
                .build();
        
        List<Refund> refunds = Arrays.asList(refund1, refund2);
        when(refundRepository.findAll()).thenReturn(refunds);

        List<Refund> result = refundService.getAllRefunds();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(refund1.getId(), result.get(0).getId());
        assertEquals(refund2.getId(), result.get(1).getId());
        verify(refundRepository).findAll();
    }

    // ================= UPDATEREFUNDSTATUS TESTS =================    @Test
    public void testUpdateRefundStatus_Success_Approved() {
        UUID refundId = refund.getId();
        when(refundRepository.findById(refundId)).thenReturn(Optional.of(refund));
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        Refund result = refundService.updateRefundStatus(refundId, RefundStatus.APPROVED, "admin123");

        assertNotNull(result);
        verify(refundRepository).findById(refundId);
        verify(refundRepository).save(any(Refund.class));
        verify(courseServiceClient).updateEnrollmentStatus(anyMap());
        verify(dashboardServiceClient).notifyRefundUpdate(isNull(), anyMap());
    }    @Test
    public void testUpdateRefundStatus_Success_Rejected() {
        UUID refundId = refund.getId();
        when(refundRepository.findById(refundId)).thenReturn(Optional.of(refund));
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        Refund result = refundService.updateRefundStatus(refundId, RefundStatus.REJECTED, "admin123");

        assertNotNull(result);
        verify(refundRepository).findById(refundId);
        verify(refundRepository).save(any(Refund.class));
        verify(courseServiceClient).updateEnrollmentStatus(anyMap());
        verify(dashboardServiceClient).notifyRefundUpdate(isNull(), anyMap());
    }@Test
    public void testUpdateRefundStatus_RefundNotFound() {
        UUID fakeRefundId = UUID.fromString("12345678-1234-1234-1234-123456789abc");
        when(refundRepository.findById(fakeRefundId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            refundService.updateRefundStatus(fakeRefundId, RefundStatus.APPROVED, "admin123");
        });

        assertTrue(exception.getMessage().contains("Refund not found with ID"));
        assertTrue(exception.getMessage().contains(fakeRefundId.toString()));
        verify(refundRepository).findById(fakeRefundId);
        verify(refundRepository, never()).save(any());
        verify(courseServiceClient, never()).updateEnrollmentStatus(anyMap());
        verify(dashboardServiceClient, never()).notifyRefundUpdate(isNull(), anyMap());
    }

    @Test
    public void testUpdateRefundStatus_NoStatusChange() {
        // Refund already has PENDING status, updating to PENDING again
        UUID refundId = refund.getId();
        refund.setRefundStatus(RefundStatus.PENDING);
        
        when(refundRepository.findById(refundId)).thenReturn(Optional.of(refund));
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        Refund result = refundService.updateRefundStatus(refundId, RefundStatus.PENDING, "admin123");

        assertNotNull(result);
        verify(refundRepository).findById(refundId);
        verify(refundRepository).save(any(Refund.class));
        // No notifications should be sent when status doesn't change
        verify(courseServiceClient, never()).updateEnrollmentStatus(anyMap());
        verify(dashboardServiceClient, never()).notifyRefundUpdate(anyString(), anyMap());
    }

    @Test
    public void testUpdateRefundStatus_ServiceNotificationFails() {
        UUID refundId = refund.getId();
        when(refundRepository.findById(refundId)).thenReturn(Optional.of(refund));
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);
        doThrow(new RuntimeException("Service error")).when(courseServiceClient).updateEnrollmentStatus(anyMap());

        // Should not throw exception even if notification fails
        assertDoesNotThrow(() -> {
            Refund result = refundService.updateRefundStatus(refundId, RefundStatus.APPROVED, "admin123");
            assertNotNull(result);
        });

        verify(refundRepository).findById(refundId);
        verify(refundRepository).save(any(Refund.class));
        verify(courseServiceClient).updateEnrollmentStatus(anyMap());
    }

    @Test
    public void testUpdateRefundStatus_DashboardNotificationFails() {
        UUID refundId = refund.getId();
        when(refundRepository.findById(refundId)).thenReturn(Optional.of(refund));
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);
        doThrow(new RuntimeException("Dashboard service error")).when(dashboardServiceClient).notifyRefundUpdate(anyString(), anyMap());

        // Should not throw exception even if notification fails
        assertDoesNotThrow(() -> {
            Refund result = refundService.updateRefundStatus(refundId, RefundStatus.APPROVED, "admin123");
            assertNotNull(result);
        });        verify(refundRepository).findById(refundId);
        verify(refundRepository).save(any(Refund.class));
        verify(courseServiceClient).updateEnrollmentStatus(anyMap());
        verify(dashboardServiceClient).notifyRefundUpdate(isNull(), anyMap());
    }

    @Test
    public void testFindById_Success() {
        UUID refundId = refund.getId();
        when(refundRepository.findById(refundId)).thenReturn(Optional.of(refund));

        Refund result = refundService.findById(refundId);

        assertNotNull(result);
        assertEquals(refund.getId(), result.getId());
        assertEquals(refund.getReason(), result.getReason());
        assertEquals(refund.getRefundStatus(), result.getRefundStatus());
        verify(refundRepository).findById(refundId);
    }    
    
    @Test
    public void testFindById_NotFound() {
        UUID fakeRefundId = UUID.fromString("12345678-1234-1234-1234-123456789abc");
        when(refundRepository.findById(fakeRefundId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            refundService.findById(fakeRefundId);
        });

        assertTrue(exception.getMessage().contains("Refund not found with ID"));
        assertTrue(exception.getMessage().contains(fakeRefundId.toString()));
        verify(refundRepository).findById(fakeRefundId);
    }

    @Test
    public void testUpdateRefundStatus_VerifyApprovedNotificationData() {
        UUID refundId = refund.getId();
        when(refundRepository.findById(refundId)).thenReturn(Optional.of(refund));
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        refundService.updateRefundStatus(refundId, RefundStatus.APPROVED, "admin123");

        // Verify course service notification
        verify(courseServiceClient).updateEnrollmentStatus(argThat(data -> {
            Map<String, Object> map = (Map<String, Object>) data;
            return map.get("enrollmentId").equals(payment.getEnrollmentId()) &&
                   map.get("studentId").equals(payment.getUserId()) &&
                   map.get("courseId").equals(payment.getCourseId()) &&
                   map.get("approved").equals(false) && // false for APPROVED (cancel enrollment)
                   map.get("refundId").equals(refund.getId());
        }));        // Verify dashboard service notification
        verify(dashboardServiceClient).notifyRefundUpdate(isNull(), argThat(data -> {
            Map<String, Object> map = (Map<String, Object>) data;
            return map.get("refundId").equals(refund.getId()) &&
                   map.get("transactionId").equals(payment.getTransactionId()) &&
                   map.get("status").equals(RefundStatus.APPROVED.getValue()) &&
                   map.get("approvedBy").equals("admin123");
        }));
    }

    @Test
    public void testUpdateRefundStatus_VerifyRejectedNotificationData() {
        UUID refundId = refund.getId();
        when(refundRepository.findById(refundId)).thenReturn(Optional.of(refund));
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        refundService.updateRefundStatus(refundId, RefundStatus.REJECTED, "admin456");

        // Verify course service notification
        verify(courseServiceClient).updateEnrollmentStatus(argThat(data -> {
            Map<String, Object> map = (Map<String, Object>) data;
            return map.get("enrollmentId").equals(payment.getEnrollmentId()) &&
                   map.get("studentId").equals(payment.getUserId()) &&
                   map.get("courseId").equals(payment.getCourseId()) &&
                   map.get("approved").equals(true) && // true for REJECTED (keep enrollment active)
                   map.get("refundId").equals(refund.getId());
        }));        // Verify dashboard service notification
        verify(dashboardServiceClient).notifyRefundUpdate(isNull(), argThat(data -> {
            Map<String, Object> map = (Map<String, Object>) data;
            return map.get("refundId").equals(refund.getId()) &&
                   map.get("transactionId").equals(payment.getTransactionId()) &&
                   map.get("status").equals(RefundStatus.REJECTED.getValue()) &&
                   map.get("approvedBy").equals("admin456");
        }));
    }

    @Test
    public void testRequestRefund_ExactlyThirtyDaysOld() {
        Payment thirtyDayOldPayment = Payment.builder()
                .transactionId(UUID.fromString("6e51f16b-eba9-493f-9e97-fba59f421e48"))
                .userId(456L)
                .courseId(123L)
                .amount(new BigDecimal("50000"))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PAID)
                .createdAt(LocalDateTime.now().minusDays(30).minusMinutes(1)) // Just over 30 days
                .build();

        when(paymentRepository.findByTransactionId(thirtyDayOldPayment.getTransactionId())).thenReturn(thirtyDayOldPayment);
        when(refundRepository.findByPaymentTransactionId(thirtyDayOldPayment.getTransactionId())).thenReturn(Collections.emptyList());

        RefundTooLateException exception = assertThrows(RefundTooLateException.class, () -> {
            refundService.requestRefund(thirtyDayOldPayment.getTransactionId(), "reason", "details");
        });

        assertTrue(exception.getMessage().contains("Refund is no longer available"));
    }

    @Test
    public void testRequestRefund_WithinThirtyDayLimit() {
        Payment recentPayment = Payment.builder()
                .transactionId(UUID.fromString("6e51f16b-eba9-493f-9e97-fba59f421e48"))
                .userId(456L)
                .courseId(123L)
                .amount(new BigDecimal("50000"))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PAID)
                .createdAt(LocalDateTime.now().minusDays(29)) // Within 30 days
                .build();

        when(paymentRepository.findByTransactionId(recentPayment.getTransactionId())).thenReturn(recentPayment);
        when(refundRepository.findByPaymentTransactionId(recentPayment.getTransactionId())).thenReturn(Collections.emptyList());
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        assertDoesNotThrow(() -> {
            Refund result = refundService.requestRefund(recentPayment.getTransactionId(), "reason", "details");
            assertNotNull(result);
        });

        verify(refundRepository).save(any(Refund.class));
    }    
    
    @Test
    public void testUpdateRefundStatus_PreUpdateMethodCalled() {
        UUID refundId = refund.getId();
        
        when(refundRepository.findById(refundId)).thenReturn(Optional.of(refund));
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        refundService.updateRefundStatus(refundId, RefundStatus.APPROVED, "admin123");

        verify(refundRepository).save(argThat(savedRefund -> {
            // Verify that preUpdate was called by checking the status was set
            return savedRefund.getRefundStatus() == RefundStatus.APPROVED;
        }));
    }
}