package id.ac.ui.cs.advprog.udehnihpayment.controller;

import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePaymentRequest {
        private String courseId;
    }
}
