package id.ac.ui.cs.advprog.udehnihpayment.model;

import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.enums.RefundStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class RefundTest {

    @Test
    public void testRefundBuilder_ShouldBuildCorrectly() {
        Payment payment = Payment.builder()
                .transactionId(1L)
                .courseId(123L)
                .userId(456L)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .build();
        
        Refund refund = Refund.builder()
                .id(100L)
                .payment(payment)
                .refundStatus(RefundStatus.PENDING)
                .build();

        assertEquals(100L, refund.getId());
        assertEquals(payment.getTransactionId(), refund.getPayment().getTransactionId());
        assertEquals("PENDING", refund.getRefundStatus().toString());
    }

    @Test
    public void testRefundSetterGetter_ShouldWorkProperly() {
        Payment payment = new Payment();
        payment.setTransactionId(1L);
        
        Refund refund = new Refund();
        refund.setId(101L);
        refund.setPayment(payment);
        refund.setRefundStatus(RefundStatus.PENDING);

        assertEquals(101L, refund.getId());
        assertEquals(payment.getTransactionId(), refund.getPayment().getTransactionId());
        assertEquals("PENDING", refund.getRefundStatus().toString());
    }
}