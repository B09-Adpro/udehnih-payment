package id.ac.ui.cs.advprog.udehnihpayment.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTransaksi;

    private Long courseId;

    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal coursePrice;

    private String userId;
}
