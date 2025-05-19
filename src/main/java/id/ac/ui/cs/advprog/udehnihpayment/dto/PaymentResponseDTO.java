// src/main/java/id/ac/ui/cs/advprog/udehnihpayment/dto/PaymentResponseDTO.java
package id.ac.ui.cs.advprog.udehnihpayment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {
    private UUID transactionId;
    private UUID courseId;
    private UUID userId;
    private BigDecimal coursePrice;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private PaymentDetailDTO paymentDetails;
}
