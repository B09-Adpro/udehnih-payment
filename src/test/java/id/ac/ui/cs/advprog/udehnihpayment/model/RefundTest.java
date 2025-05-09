package id.ac.ui.cs.advprog.udehnihpayment.model;

import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.enums.RefundStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RefundTest {

    @Test
    public void testRefundBuilder_ShouldBuildCorrectly() {
        Payment payment = Payment.builder()
                .transactionId(UUID.fromString("17e18d66-4974-49cb-a3d2-f33ee33ebdd1"))
                .course(UUID.fromString("36c35311-73af-47c1-9bfc-1a3d13683723"))
                .user(UUID.fromString("8eed0cfb-b550-49e7-a1bd-bee838c08ba7"))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .coursePrice(new BigDecimal("50000"))
                .build();
        
        Refund refund = Refund.builder()
                .id(UUID.fromString("f7d65f1d-4521-4c3f-9106-3cc11976f954"))
                .payment(payment)
                .refundStatus(RefundStatus.PENDING)
                .build();

        assertEquals("f7d65f1d-4521-4c3f-9106-3cc11976f954", refund.getId().toString());
        assertEquals(payment, refund.getPayment());
        assertEquals("PENDING", refund.getRefundStatus().toString());
    }

    @Test
    public void testRefundSetterGetter_ShouldWorkProperly() {
        Payment payment = new Payment();
        payment.setTransactionId(UUID.fromString("17e18d66-4974-49cb-a3d2-f33ee33ebdd1"));
        
        Refund refund = new Refund();
        refund.setId(UUID.fromString("7f77b8cc-47a9-40a3-bc8a-fef1ec7182e9"));
        refund.setPayment(payment);
        refund.setRefundStatus(RefundStatus.PENDING);

        assertEquals("7f77b8cc-47a9-40a3-bc8a-fef1ec7182e9", refund.getId().toString());
        assertEquals(payment, refund.getPayment());
        assertEquals("PENDING", refund.getRefundStatus().toString());
    }
}