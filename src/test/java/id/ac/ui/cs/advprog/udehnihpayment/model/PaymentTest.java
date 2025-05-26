package id.ac.ui.cs.advprog.udehnihpayment.model;

import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {
    @Test
    public void testPaymentBuilder_ShouldBuildCorrectly() {
        UUID transactionId = UUID.fromString("6e51f16b-eba9-493f-9e97-fba59f421e48");
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .courseId(123L)
                .userId(456L)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .build();

        assertEquals(UUID.fromString("6e51f16b-eba9-493f-9e97-fba59f421e48"), payment.getTransactionId());
        assertEquals(123L, payment.getCourseId());
        assertEquals(456L, payment.getUserId());
        assertEquals("Bank Transfer", payment.getPaymentMethod().toString());
        assertEquals("PENDING", payment.getPaymentStatus().toString());
        assertEquals(new BigDecimal("50000"), payment.getAmount());
    }

    @Test
    public void testPaymentSetterGetter_ShouldWorkProperly() {
        Payment payment = new Payment();
        payment.setTransactionId(UUID.fromString("6e51f16b-eba9-493f-9e97-fba59f421e48"));
        payment.setCourseId(123L);
        payment.setUserId(456L);
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(new BigDecimal("75000"));

        assertEquals(UUID.fromString("6e51f16b-eba9-493f-9e97-fba59f421e48"), payment.getTransactionId());
        assertEquals(123L, payment.getCourseId());
        assertEquals(456L, payment.getUserId());
        assertEquals("Credit Card", payment.getPaymentMethod().toString());
        assertEquals("PENDING", payment.getPaymentStatus().toString());
        assertEquals(new BigDecimal("75000"), payment.getAmount());
    }

    @Test
    public void testPaymentDefaultConstructor_ShouldNotThrowException() {
        assertDoesNotThrow(() -> new Payment());
    }
}
