package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.model.Refund;

import java.util.UUID;

public interface RefundService {
    // Method untuk request refund
    Refund requestRefund(UUID transactionId, String reason, String details);
}