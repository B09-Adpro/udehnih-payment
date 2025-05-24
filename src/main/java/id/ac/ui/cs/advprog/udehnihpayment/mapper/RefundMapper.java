package id.ac.ui.cs.advprog.udehnihpayment.mapper;

import id.ac.ui.cs.advprog.udehnihpayment.dto.request.RefundRequestDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.response.RefundResponseDTO;
import id.ac.ui.cs.advprog.udehnihpayment.enums.RefundStatus;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.model.Refund;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RefundMapper {

    /**
     * Convert RefundRequestDTO to Refund entity
     */
    public Refund toEntity(RefundRequestDTO dto, Payment payment) {
        if (dto == null || payment == null) {
            return null;
        }
        
        return Refund.builder()
                .payment(payment)
                .reason(dto.getReason())
                .details(dto.getDetails())
                .refundStatus(RefundStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create Refund entity from parameters
     */
    public Refund createRefund(Payment payment, String reason, String details) {
        if (payment == null) {
            return null;
        }
        
        return Refund.builder()
                .payment(payment)
                .reason(reason)
                .details(details != null ? details : "")
                .refundStatus(RefundStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();
    }
    
    /**
     * Convert Refund entity to RefundResponseDTO
     */
    public RefundResponseDTO toResponseDto(Refund refund) {
        if (refund == null) {
            return null;
        }
        
        // Gunakan note dari refund jika ada
        String noteText = refund.getNote(); 
        
        // Jika note kosong, gunakan default berdasarkan status
        if (noteText == null) {
            noteText = "";
        }
        
        return RefundResponseDTO.builder()
                .refundId(refund.getId())
                .transactionId(refund.getPayment().getTransactionId())
                .reason(refund.getReason())
                .details(refund.getDetails())
                .status(refund.getRefundStatus().getValue())
                .message(getMessageForStatus(refund.getRefundStatus()))
                .note(noteText)
                .build();
    }

    private String getMessageForStatus(RefundStatus status) {
        switch (status) {
            case PENDING:
                return "Refund request has been submitted successfully.";
            case APPROVED:
                return "Your refund request has been approved.";
            case REJECTED:
                return "Your refund request has been rejected.";
            default:
                return "Unknown refund status: " + status.getValue();
        }
    }
    
    /**
     * Convert list of Refund entities to list of RefundResponseDTO
     */
    public List<RefundResponseDTO> toDtoList(List<Refund> refunds) {
        return refunds.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}