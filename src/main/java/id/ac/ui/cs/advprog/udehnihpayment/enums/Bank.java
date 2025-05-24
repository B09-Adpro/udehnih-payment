package id.ac.ui.cs.advprog.udehnihpayment.enums;

public enum Bank {
    BCA("Bank Central Asia", "123-456-7890", "Udehnih"),
    BNI("Bank Negara Indonesia", "987-654-3210", "Udehnih"),
    MANDIRI("Bank Mandiri", "456-789-0123", "Udehnih"),
    BRI("Bank Rakyat Indonesia", "789-012-3456", "Udehnih");

    private final String bankName;
    private final String accountNumber;
    private final String accountName;

    private Bank(String bankName, String accountNumber, String accountName) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }
}