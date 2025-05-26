package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.enums.RefundStatus;
import id.ac.ui.cs.advprog.udehnihpayment.model.Refund;

import java.util.List;
import java.util.UUID;

public interface RefundService {
    Refund requestRefund(UUID transactionId, String reason, String details);
    List<Refund> getAllRefunds();
    Refund updateRefundStatus(UUID refundId, RefundStatus status, String approvedBy);
    Refund findById(UUID refundId);
}