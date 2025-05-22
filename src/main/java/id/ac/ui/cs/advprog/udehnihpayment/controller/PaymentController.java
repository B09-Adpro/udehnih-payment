package id.ac.ui.cs.advprog.udehnihpayment.controller;

import id.ac.ui.cs.advprog.udehnihpayment.dto.PaymentResponseDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.PaymentDetailDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.RefundResponseDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.PaymentRequestDTO;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.mapper.PaymentMapper;
import id.ac.ui.cs.advprog.udehnihpayment.mapper.RefundMapper;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.model.Refund;
import id.ac.ui.cs.advprog.udehnihpayment.service.PaymentService;
import id.ac.ui.cs.advprog.udehnihpayment.service.RefundService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// Implementasi HLN pada Controller
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final RefundService refundService;
    private final PaymentMapper paymentMapper;
    private final RefundMapper refundMapper;  

    public PaymentController(
            PaymentService paymentService, 
            RefundService refundService,
            PaymentMapper paymentMapper,
            RefundMapper refundMapper) {
        this.paymentService = paymentService;
        this.refundService = refundService;
        this.paymentMapper = paymentMapper;
        this.refundMapper = refundMapper;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(
            @RequestBody PaymentRequestDTO request,
            @RequestHeader("X-User-Id") Long userId) {
        
        Payment payment = paymentMapper.toEntity(request);
        Payment result = paymentService.createPayment(payment);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMapper.toResponseDto(result));
    }

    @GetMapping("/history")
    public ResponseEntity<List<PaymentResponseDTO>> getTransactionHistory(@RequestHeader("X-User-Id") Long userId) {
        List<Payment> payments = paymentService.getAllPayments(userId);
        List<PaymentResponseDTO> dtos = payments.stream().map(paymentMapper::toResponseDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
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
    public ResponseEntity<PaymentResponseDTO> processBankTransferPayment(@PathVariable("transactionId") UUID transactionId) {
        try {
            Payment processedPayment = paymentService.processPayment(
                    transactionId,
                    PaymentMethod.BANK_TRANSFER.getValue());
            return ResponseEntity.ok(paymentMapper.toResponseDto(processedPayment));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error processing payment: " + e.getMessage());
        }
    }

    @PostMapping("/{transactionId}/credit-card")
    public ResponseEntity<PaymentResponseDTO> processCreditCardPayment(@PathVariable("transactionId") UUID transactionId) {
        try {
            Payment processedPayment = paymentService.processPayment(
                    transactionId,
                    PaymentMethod.CREDIT_CARD.getValue());
            return ResponseEntity.ok(paymentMapper.toResponseDto(processedPayment));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error processing payment: " + e.getMessage());
        }
    }

    @PostMapping("/{transactionId}/refund")
    public ResponseEntity<RefundResponseDTO> requestRefund(@PathVariable("transactionId") UUID transactionId,
                                                           @RequestParam("reason") String reason,
                                                           @RequestParam(value = "details", required = false) String details) {
        try {
            Payment payment = paymentService.findByTransactionId(transactionId);
            if (payment == null) {
                RefundResponseDTO errorResponse = RefundResponseDTO.builder()
                        .status("ERROR")
                        .message("Payment not found for transactionId: " + transactionId)
                        .errorMessage("Payment not found for transactionId: " + transactionId)
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            Refund refund = refundService.requestRefund(transactionId, reason, details);
            return ResponseEntity.ok(refundMapper.toResponseDto(refund));

        } catch (Exception e) {
            RefundResponseDTO errorResponse = RefundResponseDTO.builder()
                    .status("ERROR")
                    .message("Error processing refund: " + e.getMessage())
                    .errorMessage(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<PaymentDetailDTO> getTransactionDetails(@PathVariable("transactionId") UUID transactionId) {
        Payment payment = paymentService.findByTransactionId(transactionId);

        if (payment == null) {
            PaymentDetailDTO errorDTO = new PaymentDetailDTO();
            errorDTO.setErrorMessage("Transaction Not Found");
            errorDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
        }

        PaymentDetailDTO paymentDetailDTO = paymentMapper.toDetailDto(payment);
        return ResponseEntity.ok(paymentDetailDTO);
    }
}