package id.ac.ui.cs.advprog.udehnihpayment.repository;

import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.model.Refund;
import id.ac.ui.cs.advprog.udehnihpayment.enums.RefundStatus; // Perbaikan import RefundStatus
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RefundRepositoryTest {

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private Payment payment;

    @BeforeEach
    public void setUp() {
        payment = Payment.builder()
                .courseId(UUID.fromString("8bb0d883-f09f-43ab-8471-2114066313b6"))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PAID)
                .amount(new BigDecimal("50000"))
                .userId(UUID.fromString("a2e08dac-c13b-4a2f-9605-5e6840f91ef7"))
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        paymentRepository.save(payment);
    }

    @Test
    public void testSaveRefund() {
        Refund refund = Refund.builder()
                .transactionId(payment.getTransactionId())
                .reason("Not satisfied")
                .details("The course was too basic")
                .refundStatus(RefundStatus.PENDING)  // Use enum RefundStatus from enums package
                .build();

        // Save refund and check if it's persisted in the database
        Refund savedRefund = refundRepository.save(refund);

        assertNotNull(savedRefund);
        assertNotNull(savedRefund.getId());  // Ensure the ID is generated
        assertEquals("Not satisfied", savedRefund.getReason());
        assertEquals(RefundStatus.PENDING, savedRefund.getRefundStatus()); // Validate status is set
    }

    @Test
    public void testFindRefundByTransactionId() {
        // Save refund first
        Refund refund = Refund.builder()
                .transactionId(payment.getTransactionId())
                .reason("Not satisfied")
                .details("The course was too basic")
                .refundStatus(RefundStatus.PENDING)
                .build();
        Refund savedRefund = refundRepository.save(refund);

        Refund foundRefund = refundRepository.findByTransactionId(payment.getTransactionId());

        assertNotNull(foundRefund);
        assertEquals(savedRefund.getId(), foundRefund.getId());
        assertEquals(payment.getTransactionId(), foundRefund.getTransactionId());
    }

    @Test
    public void testDeleteRefund() {
        // Save refund first
        Refund refund = Refund.builder()
                .transactionId(payment.getTransactionId())
                .reason("Not satisfied")
                .details("The course was too basic")
                .refundStatus(RefundStatus.PENDING)
                .build();
        Refund savedRefund = refundRepository.save(refund);

        // Delete the refund
        refundRepository.delete(savedRefund);

        // Verify the refund was deleted
        Refund foundRefund = refundRepository.findById(savedRefund.getId()).orElse(null);
        assertNull(foundRefund);
    }
}