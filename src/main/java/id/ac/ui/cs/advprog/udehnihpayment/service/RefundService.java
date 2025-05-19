package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.model.Refund;

import java.util.UUID;

public interface RefundService {
    Refund requestRefund(UUID transactionId, String reason, String details);
}