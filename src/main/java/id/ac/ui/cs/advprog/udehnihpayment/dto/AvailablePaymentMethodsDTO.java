package id.ac.ui.cs.advprog.udehnihpayment.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailablePaymentMethodsDTO {
    private List<String> methods;
}
