package id.ac.ui.cs.advprog.udehnihpayment.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
    BANK_TRANSFER("BankTransfer"),
    CREDIT_CARD("CreditCard");

    private final String value;

    private PaymentMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }

    public static boolean contains(String text) {
        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
            if (paymentMethod.value.equalsIgnoreCase(text)) {
                return true;
            }
        }
        return false;
    }

    public static PaymentMethod fromString(String text) {
        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
            if (paymentMethod.value.equalsIgnoreCase(text)) {
                return paymentMethod;
            }
        }
        throw new IllegalArgumentException("No payment method with value " + text + " found");
    }
}