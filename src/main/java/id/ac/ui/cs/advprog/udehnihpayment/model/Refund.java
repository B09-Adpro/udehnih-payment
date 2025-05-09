package id.ac.ui.cs.advprog.udehnihpayment.model;

import id.ac.ui.cs.advprog.udehnihpayment.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "refund")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @OneToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "transactionId", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private RefundStatus refundStatus;

    private String reason;
    private String details;
}