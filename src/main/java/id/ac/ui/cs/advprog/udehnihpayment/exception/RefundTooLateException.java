package id.ac.ui.cs.advprog.udehnihpayment.exception;

public class RefundTooLateException extends RuntimeException {
    public RefundTooLateException(String message) {
        super(message);
    }
}