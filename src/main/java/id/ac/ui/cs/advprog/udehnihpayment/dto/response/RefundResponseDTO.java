package id.ac.ui.cs.advprog.udehnihpayment.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundResponseDTO {
    private UUID refundId;
    private UUID transactionId;
    private String status;
    private String message;
    private String note;
    private String reason;
    private String details; 
}