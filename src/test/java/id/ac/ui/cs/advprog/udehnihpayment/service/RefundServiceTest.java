package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.enums.RefundStatus;
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
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RefundServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RefundRepository refundRepository;

    // Gunakan implementasi RefundServiceImpl di sini
    @InjectMocks
    private RefundServiceImpl refundService;

    private Payment payment;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        payment = createTestPayment();
    }

    // Helper method untuk membuat Payment object
    private Payment createTestPayment() {
        return Payment.builder()
                .transactionId(UUID.randomUUID())
                .user(UUID.randomUUID())
                .course(UUID.randomUUID())
                .coursePrice(new BigDecimal("50000"))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }

    @Test
    public void testRequestRefund_Success() {
        when(paymentRepository.findByIdTransaksi(payment.getTransactionId())).thenReturn(payment);
        when(refundRepository.save(any(Refund.class))).thenReturn(Refund.builder()
                .payment(payment)
                .reason("Not satisfied")
                .refundStatus(RefundStatus.PENDING)
                .build());

        Refund result = refundService.requestRefund(payment.getTransactionId(), "Not satisfied", "Too basic");

        assertNotNull(result);
        assertEquals("PENDING", result.getRefundStatus().toString());
        assertEquals("Not satisfied", result.getReason());
    }

    @Test
    public void testRequestRefund_TransactionNotFound() {
        UUID fakeId = UUID.randomUUID();
        when(paymentRepository.findByIdTransaksi(fakeId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            refundService.requestRefund(fakeId, "reason", "details");
        });
    }

    @Test
    public void testRequestRefund_EmptyReason() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .transactionId(id)
                .user(userId)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        when(paymentRepository.findByIdTransaksi(id)).thenReturn(payment);

        assertThrows(IllegalArgumentException.class, () -> {
            refundService.requestRefund(id, "", "some detail");
        });
    }
}