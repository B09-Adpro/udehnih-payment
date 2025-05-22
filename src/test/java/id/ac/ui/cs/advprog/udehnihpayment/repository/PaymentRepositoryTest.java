package id.ac.ui.cs.advprog.udehnihpayment.repository;

import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentRepositoryTest {

    @Mock
    private PaymentRepository paymentRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveAndFindByIdTransaksi() {
        Payment payment = Payment.builder()
                .transactionId(UUID.fromString("b6968765-7268-4604-8f32-2b21236ab1d9"))
                .courseId(123L)
                .userId(456L)
                .amount(new BigDecimal("50000"))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentRepository.findByTransactionId(UUID.fromString("b6968765-7268-4604-8f32-2b21236ab1d9"))).thenReturn(payment);

        Payment saved = paymentRepository.save(payment);
        Payment found = paymentRepository.findByTransactionId(saved.getTransactionId());

        assertNotNull(found);
        assertEquals(saved.getTransactionId(), found.getTransactionId());
        assertEquals(456L, found.getUserId());
    }

    @Test
    public void testFindAllByUserId() {
        Payment p1 = Payment.builder().courseId(123L).userId(456L).paymentStatus(PaymentStatus.PAID).paymentMethod(PaymentMethod.BANK_TRANSFER).amount(new BigDecimal("10000")).build();
        Payment p2 = Payment.builder().courseId(123L).userId(456L).paymentStatus(PaymentStatus.PENDING).paymentMethod(PaymentMethod.CREDIT_CARD).amount(new BigDecimal("20000")).build();

        when(paymentRepository.findAllByUserId(456L)).thenReturn(List.of(p1, p2));

        List<Payment> result = paymentRepository.findAllByUserId(456L);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getUserId().equals(456L)));
    }

    @Test
    public void testFindByIdTransaksi_NotFound() {
        when(paymentRepository.findByTransactionId(any(UUID.class))).thenReturn(null);

        Payment result = paymentRepository.findByTransactionId(UUID.randomUUID());
        assertNull(result);
    }

    @Test
    public void testFindAndUpdatePayment() {
        UUID id = UUID.randomUUID();
        Long courseId = 123L;
        Long userId = 456L;
        Payment payment = Payment.builder()
                .transactionId(id)
                .courseId(courseId)
                .userId(userId)
                .amount(new BigDecimal("50000"))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        Payment updatedPayment = payment.toBuilder()
                .paymentStatus(PaymentStatus.PAID)
                .build();

        when(paymentRepository.findByTransactionId(id)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);

        Payment found = paymentRepository.findByTransactionId(id);
        assertNotNull(found);
        assertEquals("PENDING", found.getPaymentStatus().toString());

        found.setPaymentStatus(PaymentStatus.PAID);
        Payment saved = paymentRepository.save(found);

        assertEquals("PAID", saved.getPaymentStatus().toString());
    }
}