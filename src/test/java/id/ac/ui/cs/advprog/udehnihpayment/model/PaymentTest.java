package id.ac.ui.cs.advprog.udehnihpayment.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {
    @Test
    public void testPaymentBuilder_ShouldBuildCorrectly() {
        Payment payment = Payment.builder()
                .idTransaksi(1L)
                .courseId(42L)
                .userId("user123")
                .paymentMethod("BankTransfer")
                .paymentStatus("PENDING")
                .coursePrice(new BigDecimal("50000"))
                .build();

        assertEquals(1L, payment.getIdTransaksi());
        assertEquals(42L, payment.getCourseId());
        assertEquals("user123", payment.getUserId());
        assertEquals("BankTransfer", payment.getPaymentMethod());
        assertEquals("PENDING", payment.getPaymentStatus());
        assertEquals(new BigDecimal("50000"), payment.getCoursePrice());
    }

    @Test
    public void testPaymentSetterGetter_ShouldWorkProperly() {
        Payment payment = new Payment();
        payment.setIdTransaksi(99L);
        payment.setCourseId(88L);
        payment.setUserId("abc123");
        payment.setPaymentMethod("CreditCard");
        payment.setPaymentStatus("PAID");
        payment.setCoursePrice(new BigDecimal("75000"));

        assertEquals(99L, payment.getIdTransaksi());
        assertEquals(88L, payment.getCourseId());
        assertEquals("abc123", payment.getUserId());
        assertEquals("CreditCard", payment.getPaymentMethod());
        assertEquals("PAID", payment.getPaymentStatus());
        assertEquals(new BigDecimal("75000"), payment.getCoursePrice());
    }

    @Test
    public void testPaymentDefaultConstructor_ShouldNotThrowException() {
        assertDoesNotThrow(() -> new Payment());
    }
}
