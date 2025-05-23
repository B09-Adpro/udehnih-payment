package id.ac.ui.cs.advprog.udehnihpayment.exception;

public class RefundAlreadyRequestedException extends RuntimeException {
    public RefundAlreadyRequestedException(String message) {
        super(message);
    }
}
