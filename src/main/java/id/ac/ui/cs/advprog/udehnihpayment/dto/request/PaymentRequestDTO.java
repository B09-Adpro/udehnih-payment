package id.ac.ui.cs.advprog.udehnihpayment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {
    private Long enrollmentId;
    private Long studentId;
    private Long courseId;
    private BigDecimal amount;
    private String paymentMethod;
    private Long timestamp;
}
