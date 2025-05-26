package id.ac.ui.cs.advprog.udehnihpayment.model;

import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.enums.RefundStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RefundTest {

    @Test
    public void testRefundBuilder_ShouldBuildCorrectly() {
        UUID transactionId = UUID.fromString("6e51f16b-eba9-493f-9e97-fba59f421e48");
        UUID refundId = UUID.fromString("d4f5e6b7-8c9d-0e1f-2a3b-4c5d6e7f8g9h");
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .courseId(123L)
                .userId(456L)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("50000"))
                .build();
        
        Refund refund = Refund.builder()
                .id(refundId)
                .payment(payment)
                .refundStatus(RefundStatus.PENDING)
                .build();

        assertEquals(UUID.fromString("d4f5e6b7-8c9d-0e1f-2a3b-4c5d6e7f8g9h"), refund.getId());
        assertEquals(payment.getTransactionId(), refund.getPayment().getTransactionId());
        assertEquals("PENDING", refund.getRefundStatus().toString());
    }

    @Test
    public void testRefundSetterGetter_ShouldWorkProperly() {
        Payment payment = new Payment();
        payment.setTransactionId(UUID.fromString("d4f5e6b7-8c9d-0e1f-2a3b-4c5d6e7f8g9h"));
        
        Refund refund = new Refund();
        refund.setId(UUID.fromString("6e51f16b-eba9-493f-9e97-fba59f421e48"));
        refund.setPayment(payment);
        refund.setRefundStatus(RefundStatus.PENDING);

        assertEquals(UUID.fromString("6e51f16b-eba9-493f-9e97-fba59f421e48"), refund.getId());
        assertEquals(payment.getTransactionId(), refund.getPayment().getTransactionId());
        assertEquals("PENDING", refund.getRefundStatus().toString());
    }
}