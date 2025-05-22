package id.ac.ui.cs.advprog.udehnihpayment.mapper;

import id.ac.ui.cs.advprog.udehnihpayment.dto.RefundRequestDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.RefundResponseDTO;
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
        
        return RefundResponseDTO.builder()
                .refundId(refund.getId())
                .status(refund.getRefundStatus().getValue())
                .message("Refund request has been submitted successfully.")
                .note("Your refund request is being processed by admin.")
                .build();
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