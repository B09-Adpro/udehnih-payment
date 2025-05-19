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
                .courseId(UUID.fromString("a8e376a9-3754-47f9-9dd1-3191a67828d7"))
                .userId(UUID.fromString("a2e08dac-c13b-4a2f-9605-5e6840f91ef7"))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .build();

        assertEquals("17e18d66-4974-49cb-a3d2-f33ee33ebdd1", payment.getTransactionId().toString());
        assertEquals("a8e376a9-3754-47f9-9dd1-3191a67828d7", payment.getCourseId().toString());
        assertEquals("a2e08dac-c13b-4a2f-9605-5e6840f91ef7", payment.getUserId().toString());
        assertEquals("BankTransfer", payment.getPaymentMethod().toString());
        assertEquals("PENDING", payment.getPaymentStatus().toString());
        assertEquals(new BigDecimal("50000"), payment.getAmount());
    }

    @Test
    public void testPaymentSetterGetter_ShouldWorkProperly() {
        Payment payment = new Payment();
        payment.setTransactionId(UUID.fromString("98ffe7e1-481e-4100-9321-5aa57dec5e06"));
        payment.setCourseId(UUID.fromString("79e1e12f-1188-4833-bcff-70979899d0a3"));
        payment.setUserId(UUID.fromString("f0749b83-c13a-48b8-9af3-ea841441e22d"));
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(new BigDecimal("75000"));

        assertEquals("98ffe7e1-481e-4100-9321-5aa57dec5e06", payment.getTransactionId().toString());
        assertEquals("79e1e12f-1188-4833-bcff-70979899d0a3", payment.getCourseId().toString());
        assertEquals("f0749b83-c13a-48b8-9af3-ea841441e22d", payment.getUserId().toString());
        assertEquals("CreditCard", payment.getPaymentMethod().toString());
        assertEquals("PENDING", payment.getPaymentStatus().toString());
        assertEquals(new BigDecimal("75000"), payment.getAmount());
    }

    @Test
    public void testPaymentDefaultConstructor_ShouldNotThrowException() {
        assertDoesNotThrow(() -> new Payment());
    }
}
