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
        UUID courseId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .transactionId(UUID.fromString("b6968765-7268-4604-8f32-2b21236ab1d9"))
                .course(courseId)
                .userId(UUID.fromString("2d0243bd-1e5a-4bbc-ac60-ae4636666ef5"))
                .coursePrice(new BigDecimal("50000"))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentRepository.findByTransactionId(UUID.fromString("b6968765-7268-4604-8f32-2b21236ab1d9"))).thenReturn(payment);

        Payment saved = paymentRepository.save(payment);
        Payment found = paymentRepository.findByTransactionId(saved.getTransactionId());

        assertNotNull(found);
        assertEquals(saved.getTransactionId(), found.getTransactionId());
        assertEquals("2d0243bd-1e5a-4bbc-ac60-ae4636666ef5", found.getUserId().toString());
    }

    @Test
    public void testFindAllByUserId() {
        Payment p1 = Payment.builder().course(UUID.fromString("56bd8339-267f-4a52-b144-7a6bab0290ae")).userId(UUID.fromString("70f5cdb2-ea8f-488e-aeed-13768e7dbd7b")).paymentStatus(PaymentStatus.PAID).paymentMethod(PaymentMethod.BANK_TRANSFER).coursePrice(new BigDecimal("10000")).build();
        Payment p2 = Payment.builder().course(UUID.fromString("948e9c9f-c976-4a12-9479-7008f50aaf7c")).userId(UUID.fromString("70f5cdb2-ea8f-488e-aeed-13768e7dbd7b")).paymentStatus(PaymentStatus.PENDING).paymentMethod(PaymentMethod.CREDIT_CARD).coursePrice(new BigDecimal("20000")).build();

        when(paymentRepository.findAllByUserId(UUID.fromString("70f5cdb2-ea8f-488e-aeed-13768e7dbd7b"))).thenReturn(List.of(p1, p2));

        List<Payment> result = paymentRepository.findAllByUserId(UUID.fromString("70f5cdb2-ea8f-488e-aeed-13768e7dbd7b"));

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getUserId().equals(UUID.fromString("70f5cdb2-ea8f-488e-aeed-13768e7dbd7b"))));
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
        UUID courseId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .transactionId(id)
                .course(courseId)
                .userId(userId)
                .coursePrice(new BigDecimal("50000"))
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