package id.ac.ui.cs.advprog.udehnihpayment.enums;

public enum PaymentStatus {
    PENDING("PENDING"),
    PAID("PAID"),
    FAILED("FAILED");
    
    private final String value;
    
    private PaymentStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    public static boolean contains(String text) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return true;
            }
        }
        return false;
    }
    
    public static PaymentStatus fromString(String text) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No payment status with value " + text + " found");
    }
}