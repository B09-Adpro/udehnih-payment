package id.ac.ui.cs.advprog.udehnihpayment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

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
