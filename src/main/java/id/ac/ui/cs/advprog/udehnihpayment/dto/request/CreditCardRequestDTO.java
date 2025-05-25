package id.ac.ui.cs.advprog.udehnihpayment.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditCardRequestDTO {
    private Long transactionId;
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvc;
    private BigDecimal amount;
}