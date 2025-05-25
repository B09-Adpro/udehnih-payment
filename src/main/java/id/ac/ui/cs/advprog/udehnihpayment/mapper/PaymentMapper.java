package id.ac.ui.cs.advprog.udehnihpayment.mapper;

import id.ac.ui.cs.advprog.udehnihpayment.dto.request.PaymentRequestDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.response.PaymentDetailDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.response.PaymentResponseDTO;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.model.PaymentDetails;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    /**
     * Convert Payment entity to PaymentDetailDTO
     */
    public PaymentDetailDTO toDetailDto(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentDetailDTO.Details detailsDto = payment.getPaymentDetails() == null ? null : toDetailsDto(payment.getPaymentDetails());

        return PaymentDetailDTO.builder()
                .transactionId(payment.getTransactionId())
                .courseId(payment.getCourseId())
                .userId(payment.getUserId())
                .courseTitle(payment.getCourseTitle())
                .tutorName(payment.getTutorName())
                .amount(payment.getAmount())
                .paymentStatus(payment.getPaymentStatus().getValue())
                .paymentMethod(payment.getPaymentMethod().getValue())
                .bankName(getBankNameForPaymentMethod(payment.getPaymentMethod()))
                .paymentDetails(detailsDto)
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    /**
     * Convert Payment entity to PaymentResponseDTO
     */
    public PaymentResponseDTO toResponseDto(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentResponseDTO.builder()
                .transactionId(payment.getTransactionId())
                .courseId(payment.getCourseId())
                .userId(payment.getUserId())
                .courseTitle(payment.getCourseTitle())
                .tutorName(payment.getTutorName())
                .amount(payment.getAmount())
                .paymentStatus(payment.getPaymentStatus().getValue())
                .paymentMethod(payment.getPaymentMethod().getValue())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    /**
     * Convert PaymentRequestDTO to Payment entity
     */
    public Payment toEntity(PaymentRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Payment.builder()
                .enrollmentId(dto.getEnrollmentId())
                .courseId(dto.getCourseId())
                .userId(dto.getStudentId())
                .tutorName(dto.getTutorName())
                .courseTitle(dto.getCourseTitle())
                .amount(dto.getAmount())
                .paymentMethod(PaymentMethod.fromString(dto.getPaymentMethod()))
                .build();
    }

    /**
     * Convert PaymentDetails entity to PaymentDetailDTO.Details
     */
    public PaymentDetailDTO.Details toDetailsDto(PaymentDetails details) {
        if (details == null) {
            return null;
        }
        
        return PaymentDetailDTO.Details.builder()
                .confirmation(details.isConfirmation())
                .confirmedAt(details.getConfirmedAt())
                .adminApproval(details.isAdminApproval())
                .approvedAt(details.getApprovedAt())
                .approvedBy(details.getApprovedBy())
                .build();
    }

    /**
     * Convert PaymentDetailDTO.Details to PaymentDetails entity
     */
    public PaymentDetails toDetailsEntity(PaymentDetailDTO.Details dto) {
        if (dto == null) {
            return null;
        }
        
        PaymentDetails details = new PaymentDetails();
        details.setConfirmation(dto.isConfirmation());
        details.setConfirmedAt(dto.getConfirmedAt());
        details.setAdminApproval(dto.isAdminApproval());
        details.setApprovedAt(dto.getApprovedAt());
        details.setApprovedBy(dto.getApprovedBy());
        
        return details;
    }
    
    // Helper methods
    private String getBankNameForPaymentMethod(PaymentMethod method) {
        if (method == PaymentMethod.BANK_TRANSFER) {
            return "Bank BCA";
        }
        return null;
    }
}