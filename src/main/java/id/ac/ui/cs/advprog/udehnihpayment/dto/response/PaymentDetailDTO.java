package id.ac.ui.cs.advprog.udehnihpayment.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetailDTO {
    private UUID transactionId;
    private Long courseId;
    private Long userId;
    private String courseTitle;
    private String tutorName;
    private BigDecimal amount;
    private String paymentStatus;
    private String paymentMethod;
    private Details paymentDetails;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Details {
        private boolean confirmation;
        private LocalDateTime confirmedAt;
        private boolean adminApproval;
        private LocalDateTime approvedAt;
        private String approvedBy;
    }
}

