package id.ac.ui.cs.advprog.udehnihpayment.service;

import id.ac.ui.cs.advprog.udehnihpayment.model.Refund;

import java.util.List;

public interface RefundService {
    Refund requestRefund(Long transactionId, String reason, String details);
    List<Refund> getAllRefunds();
}