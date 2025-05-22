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
    private Long courseId;
    private Long userId;
    private BigDecimal coursePrice;
    private String paymentMethod;
}
