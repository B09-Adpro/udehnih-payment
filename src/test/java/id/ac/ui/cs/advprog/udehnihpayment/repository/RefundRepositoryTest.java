package id.ac.ui.cs.advprog.udehnihpayment.repository;

import id.ac.ui.cs.advprog.udehnihpayment.config.TestConfig;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.model.Refund;
import id.ac.ui.cs.advprog.udehnihpayment.enums.RefundStatus; // Perbaikan import RefundStatus
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class RefundRepositoryTest {

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private Payment payment;    
    
    @BeforeEach
    public void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        payment = Payment.builder()
                .courseId(123L)
                .enrollmentId(789L)
                .courseTitle("Java Programming")
                .tutorName("John Doe")
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PAID)
                .amount(new BigDecimal("50000"))
                .userId(456L)
                .expiresAt(now.plusDays(1))
                .createdAt(now)
                .updatedAt(now)
                .build();

        paymentRepository.save(payment);
    }
    
    @Test
    public void testSaveRefund() {
        Refund refund = Refund.builder()
                .payment(payment)
                .reason("Not satisfied")
                .details("The course was too basic")
                .refundStatus(RefundStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Refund savedRefund = refundRepository.save(refund);

        assertNotNull(savedRefund);
        assertNotNull(savedRefund.getId());
        assertEquals("Not satisfied", savedRefund.getReason());
        assertEquals(RefundStatus.PENDING, savedRefund.getRefundStatus());
    }    
    
    @Test
    public void testFindRefundByTransactionId() {
        // Save refund first with all required fields
        Refund refund = Refund.builder()
                .payment(payment)
                .reason("Not satisfied")
                .details("The course was too basic")
                .refundStatus(RefundStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Refund savedRefund = refundRepository.save(refund);

        Refund foundRefund = refundRepository.findByPayment_TransactionId(payment.getTransactionId());

        assertNotNull(foundRefund);
        assertEquals(savedRefund.getId(), foundRefund.getId());
        assertEquals(payment.getTransactionId(), foundRefund.getPayment().getTransactionId());
    }    
    
    @Test
    public void testDeleteRefund() {
        Refund refund = Refund.builder()
                .payment(payment)
                .reason("Not satisfied")
                .details("The course was too basic")
                .refundStatus(RefundStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Refund savedRefund = refundRepository.save(refund);

        refundRepository.delete(savedRefund);

        Refund foundRefund = refundRepository.findById(savedRefund.getId()).orElse(null);
        assertNull(foundRefund);
    }
}