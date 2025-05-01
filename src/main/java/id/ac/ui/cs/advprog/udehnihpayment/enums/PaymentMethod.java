package id.ac.ui.cs.advprog.udehnihpayment.enums;

public enum PaymentMethod {
    BANK_TRANSFER("BankTransfer"),
    CREDIT_CARD("CreditCard");
    
    private final String value;
    
    private PaymentMethod(String value) {
        this.value = value;
    }
    
    public static boolean contains(String text) {
        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
            if (paymentMethod.value.equalsIgnoreCase(text)) {
                return true;
            }
        }
        return false;
    }
}