package id.ac.ui.cs.advprog.udehnihpayment.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor @AllArgsConstructor
public class PaymentDetails {
    private boolean confirmation;
    private LocalDateTime confirmedAt;
    private boolean adminApproval;
    private LocalDateTime approvedAt;
    private String approvedBy;
}