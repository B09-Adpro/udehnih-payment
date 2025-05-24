package id.ac.ui.cs.advprog.udehnihpayment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundResponseDTO {
    private Long refundId;
    private String status;
    private String message;
    private String note;
}