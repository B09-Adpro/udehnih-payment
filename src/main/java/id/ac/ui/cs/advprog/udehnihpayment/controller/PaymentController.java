package id.ac.ui.cs.advprog.udehnihpayment.controller;

import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // POST /api/payments
    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody CreatePaymentRequest request,
                                                 @RequestHeader("X-User-Id") String userId) {
        Payment payment = Payment.builder()
                .courseId(Long.parseLong(request.getCourseId()))
                .userId(userId)
                .coursePrice(new BigDecimal("50000")) // hardcoded sementara, bisa di-fetch dari Course
                .paymentMethod("BankTransfer")   // nanti bisa pakai Strategy
                .paymentStatus("PENDING")
                .build();

        Payment result = paymentService.createPayment(payment);
        return ResponseEntity.status(201).body(result);
    }

    // GET /api/payments/history
    @GetMapping("/history")
    public ResponseEntity<List<Payment>> getTransactionHistory(@RequestHeader("X-User-Id") String userId) {
        List<Payment> payments = paymentService.getPaymentsByUser(userId);
        return ResponseEntity.ok(payments);
    }

    // GET /api/payments/methods
    @GetMapping("/methods")
    public ResponseEntity<?> getPaymentMethods() {
        try {
            List<String> methods = paymentService.getPaymentMethods();
            return ResponseEntity.ok(methods);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{transactionId}/bank-transfer")
    public ResponseEntity<?> processBankTransferPayment(@PathVariable Long transactionId) {
        try {
            Payment processedPayment = paymentService.processPayment(
                    transactionId, 
                    PaymentMethod.BANK_TRANSFER.getValue());
            return ResponseEntity.ok(processedPayment);
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
    public ResponseEntity<?> processCreditCardPayment(@PathVariable Long transactionId) {
        try {
            Payment processedPayment = paymentService.processPayment(
                    transactionId, 
                    PaymentMethod.CREDIT_CARD.getValue());
            return ResponseEntity.ok(processedPayment);
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePaymentRequest {
        private String courseId;
    }
}
