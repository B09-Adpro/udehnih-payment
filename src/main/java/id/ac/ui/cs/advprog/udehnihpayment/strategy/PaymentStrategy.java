package id.ac.ui.cs.advprog.udehnihpayment.strategy;

import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;

public interface PaymentStrategy {
    /**
     * Memproses pembayaran
     * @param payment Payment yang akan diproses
     * @return Status pemrosesan
     */
    String processPayment(Payment payment);
    
    /**
     * Menghasilkan instruksi pembayaran
     * @param payment Payment yang membutuhkan instruksi
     * @return String berisi instruksi pembayaran
     */
    String generateInstructions(Payment payment);
    
    /**
     * Memvalidasi pembayaran
     * @param payment Payment yang akan divalidasi
     * @return true jika valid, false jika tidak
     */
    boolean validatePayment(Payment payment);
    
    /**
     * Mengecek apakah strategi mendukung metode pembayaran tertentu
     * @param paymentMethodName Nama metode pembayaran
     * @return true jika didukung
     */
    boolean supports(String paymentMethodName);
}