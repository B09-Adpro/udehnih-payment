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
    private Long courseId;
    private Long userId;
    private String courseTitle;
    private String tutorName;
    private BigDecimal amount;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private PaymentDetailDTO paymentDetails;
}
