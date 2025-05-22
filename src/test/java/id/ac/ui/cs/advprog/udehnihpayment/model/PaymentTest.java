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
        Payment payment = Payment.builder()
                .transactionId(UUID.fromString("17e18d66-4974-49cb-a3d2-f33ee33ebdd1"))
                .courseId(123L)
                .userId(456L)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .build();

        assertEquals("17e18d66-4974-49cb-a3d2-f33ee33ebdd1", payment.getTransactionId().toString());
        assertEquals(123L, payment.getCourseId());
        assertEquals(456L, payment.getUserId());
        assertEquals("BankTransfer", payment.getPaymentMethod().toString());
        assertEquals("PENDING", payment.getPaymentStatus().toString());
        assertEquals(new BigDecimal("50000"), payment.getAmount());
    }

    @Test
    public void testPaymentSetterGetter_ShouldWorkProperly() {
        Payment payment = new Payment();
        payment.setTransactionId(UUID.fromString("98ffe7e1-481e-4100-9321-5aa57dec5e06"));
        payment.setCourseId(123L);
        payment.setUserId(456L);
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(new BigDecimal("75000"));

        assertEquals("98ffe7e1-481e-4100-9321-5aa57dec5e06", payment.getTransactionId().toString());
        assertEquals(123L, payment.getCourseId());
        assertEquals(456L, payment.getUserId());
        assertEquals("CreditCard", payment.getPaymentMethod().toString());
        assertEquals("PENDING", payment.getPaymentStatus().toString());
        assertEquals(new BigDecimal("75000"), payment.getAmount());
    }

    @Test
    public void testPaymentDefaultConstructor_ShouldNotThrowException() {
        assertDoesNotThrow(() -> new Payment());
    }
}
