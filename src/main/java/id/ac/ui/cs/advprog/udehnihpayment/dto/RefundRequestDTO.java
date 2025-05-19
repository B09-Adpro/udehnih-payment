package id.ac.ui.cs.advprog.udehnihpayment.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequestDTO {
    private String reason;
    private String details;
}