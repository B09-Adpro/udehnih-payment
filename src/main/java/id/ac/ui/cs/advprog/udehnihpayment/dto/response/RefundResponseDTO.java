package id.ac.ui.cs.advprog.udehnihpayment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundResponseDTO {
    private UUID refundId;
    private String status;
    private String message;
    private String note;
    private String errorMessage;
}