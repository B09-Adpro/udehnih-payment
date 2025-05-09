package id.ac.ui.cs.advprog.udehnihpayment.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetails {

    private boolean confirmed;

    private LocalDateTime confirmedAt;

    private String confirmationBy;
}
