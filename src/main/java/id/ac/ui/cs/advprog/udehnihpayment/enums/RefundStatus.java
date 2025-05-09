package id.ac.ui.cs.advprog.udehnihpayment.enums;

public enum RefundStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String value;

    private RefundStatus(String value) {
        this.value = value;
    }

    // Method untuk mengambil nilai status refund
    public String getValue() {
        return value;
    }

    // Override toString untuk mengembalikan value
    @Override
    public String toString() {
        return value;
    }

    // Cek apakah text sesuai dengan salah satu status refund
    public static boolean contains(String text) {
        for (RefundStatus status : RefundStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return true;
            }
        }
        return false;
    }

    public static RefundStatus fromString(String text) {
        for (RefundStatus status : RefundStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No refund status with value " + text + " found");
    }
}
