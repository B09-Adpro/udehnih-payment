package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.enums.RefundStatus;
import id.ac.ui.cs.advprog.udehnihpayment.exception.InvalidRefundReasonException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RefundServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RefundRepository refundRepository;

    @InjectMocks
    private RefundServiceImpl refundService;

    private Payment payment;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        payment = createTestPayment();
    }

    private Payment createTestPayment() {
        return Payment.builder()
                .transactionId(1L)
                .userId(456L)
                .courseId(123L)
                .amount(new BigDecimal("50000"))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void testRequestRefund_Success() {
        when(paymentRepository.findByTransactionId(payment.getTransactionId())).thenReturn(payment);
        when(refundRepository.save(any(Refund.class))).thenReturn(Refund.builder()
                .payment(payment)
                .reason("Not satisfied")
                .refundStatus(RefundStatus.PENDING)
                .details("Too basic")
                .build());

        Refund result = refundService.requestRefund(payment.getTransactionId(), "Not satisfied", "Too basic");

        assertNotNull(result);
        assertEquals("PENDING", result.getRefundStatus().toString());
        assertEquals("Not satisfied", result.getReason());
    }

    @Test
    public void testRequestRefund_TransactionNotFound() {
        Long fakeId = 999L;
        when(paymentRepository.findByTransactionId(fakeId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            refundService.requestRefund(fakeId, "reason", "details");
        });
    }

    @Test
    public void testRequestRefund_EmptyReason() {
        Long id = 2L;
        Long userId = 456L;
        Payment payment = Payment.builder()
                .transactionId(id)
                .userId(userId)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        when(paymentRepository.findByTransactionId(id)).thenReturn(payment);

        assertThrows(InvalidRefundReasonException.class, () -> {
            refundService.requestRefund(id, "", "some detail");
        });
    }
}