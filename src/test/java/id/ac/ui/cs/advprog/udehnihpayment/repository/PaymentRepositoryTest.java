package id.ac.ui.cs.advprog.udehnihpayment.repository;

import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    public void testSaveAndFindByIdTransaksi() {
        Payment payment = Payment.builder()
                .courseId(42L)
                .userId("user123")
                .coursePrice(new BigDecimal("50000"))
                .paymentMethod("BankTransfer")
                .paymentStatus("PENDING")
                .build();

        Payment saved = paymentRepository.save(payment);
        Payment found = paymentRepository.findByIdTransaksi(saved.getIdTransaksi());

        assertNotNull(found);
        assertEquals(saved.getIdTransaksi(), found.getIdTransaksi());
        assertEquals("user123", found.getUserId());
    }

    @Test
    public void testFindAllByUserId() {
        Payment p1 = Payment.builder().courseId(1L).userId("u1").paymentStatus("PAID").paymentMethod("BankTransfer").coursePrice(new BigDecimal("10000")).build();
        Payment p2 = Payment.builder().courseId(2L).userId("u1").paymentStatus("PENDING").paymentMethod("CreditCard").coursePrice(new BigDecimal("20000")).build();
        Payment p3 = Payment.builder().courseId(3L).userId("u2").paymentStatus("PAID").paymentMethod("BankTransfer").coursePrice(new BigDecimal("30000")).build();

        paymentRepository.save(p1);
        paymentRepository.save(p2);
        paymentRepository.save(p3);

        List<Payment> result = paymentRepository.findAllByUserId("u1");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getUserId().equals("u1")));
    }

    @Test
    public void testFindByIdTransaksi_NotFound() {
        Payment result = paymentRepository.findByIdTransaksi(9999L);
        assertNull(result);
    }
}
