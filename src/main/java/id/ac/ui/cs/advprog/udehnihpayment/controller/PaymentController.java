package id.ac.ui.cs.advprog.udehnihpayment.controller;

import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.model.Refund;
import id.ac.ui.cs.advprog.udehnihpayment.service.PaymentService;
import id.ac.ui.cs.advprog.udehnihpayment.service.RefundService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final RefundService refundService;

    public PaymentController(PaymentService paymentService, RefundService refundService) {
        this.paymentService = paymentService;
        this.refundService = refundService;
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody CreatePaymentRequest request,
                                                 @RequestHeader("X-User-Id") UUID userId) {
        Payment payment = Payment.builder()
                .course(UUID.fromString("a8e376a9-3754-47f9-9dd1-3191a67828d7"))
                .userId(userId)
                .coursePrice(new BigDecimal("50000"))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        Payment result = paymentService.createPayment(payment);
        return ResponseEntity.status(201).body(result);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Payment>> getTransactionHistory(@RequestHeader("X-User-Id") UUID userId) {
        List<Payment> payments = paymentService.getPaymentsByUser(userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/methods")
    public ResponseEntity<List<String>> getPaymentMethods() {
        try {
            List<String> methods = paymentService.getPaymentMethods();
            return ResponseEntity.ok(methods);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{transactionId}/bank-transfer")
    public ResponseEntity<?> processBankTransferPayment(@PathVariable UUID transactionId) {
        try {
            Payment processedPayment = paymentService.processPayment(
                    transactionId,
                    PaymentMethod.BANK_TRANSFER.getValue());
            return ResponseEntity.ok(processedPayment);  // Return the processed payment
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing payment: " + e.getMessage());
        }
    }

    @PostMapping("/{transactionId}/credit-card")
    public ResponseEntity<?> processCreditCardPayment(@PathVariable UUID transactionId) {
        try {
            Payment processedPayment = paymentService.processPayment(
                    transactionId,
                    PaymentMethod.CREDIT_CARD.getValue());
            return ResponseEntity.ok(processedPayment);  // Return the processed payment
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing payment: " + e.getMessage());
        }
    }

    @PostMapping("/{transactionId}/refund")
    public ResponseEntity<?> requestRefund(@PathVariable UUID transactionId,
                                           @RequestParam String reason,
                                           @RequestParam(required = false) String details) {
        try {
            Payment payment = paymentService.findByIdTransaksi(transactionId);
            if (payment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Payment not found for transactionId: " + transactionId);
            }

            Refund refund = refundService.requestRefund(transactionId, reason, details);
            return ResponseEntity.ok(refund);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing refund: " + e.getMessage());
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransactionDetails(@PathVariable UUID transactionId) {
        try {
            Payment payment = paymentService.findByIdTransaksi(transactionId);
            if (payment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing payment: " + e.getMessage());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePaymentRequest {
        private String courseId;
    }
}
