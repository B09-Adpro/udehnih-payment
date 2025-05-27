package id.ac.ui.cs.advprog.udehnihpayment.mapper;

import id.ac.ui.cs.advprog.udehnihpayment.dto.request.RefundRequestDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.response.RefundResponseDTO;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.enums.RefundStatus;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.model.Refund;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RefundMapperTest {

    private RefundMapper refundMapper;
    private Payment samplePayment;
    private RefundRequestDTO sampleRequestDTO;
    private Refund sampleRefund;

    @BeforeEach
    public void setUp() {
        refundMapper = new RefundMapper();
        
        // Setup sample Payment
        samplePayment = Payment.builder()
                .transactionId(UUID.randomUUID())
                .courseId(123L)
                .userId(456L)
                .courseTitle("Sample Course")
                .tutorName("John Doe")
                .amount(new BigDecimal("100000"))
                .paymentStatus(PaymentStatus.PAID)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Setup sample RefundRequestDTO
        sampleRequestDTO = RefundRequestDTO.builder()
                .reason("Course was cancelled")
                .details("The instructor cancelled the course due to technical issues")
                .build();
        
        // Setup sample Refund
        sampleRefund = Refund.builder()
                .id(UUID.randomUUID())
                .payment(samplePayment)
                .reason("Course was cancelled")
                .details("The instructor cancelled the course due to technical issues")
                .refundStatus(RefundStatus.PENDING)
                .note("Your refund request is being processed by admin.")
                .requestedAt(LocalDateTime.now().minusHours(2))
                .build();
    }

    // ================= TO ENTITY TESTS (WITH DTO AND PAYMENT) =================

    @Test
    public void toEntity_ValidDTOAndPayment_ReturnsCorrectEntity() {
        // Act
        Refund result = refundMapper.toEntity(sampleRequestDTO, samplePayment);

        // Assert
        assertNotNull(result);
        assertEquals(samplePayment, result.getPayment());
        assertEquals(sampleRequestDTO.getReason(), result.getReason());
        assertEquals(sampleRequestDTO.getDetails(), result.getDetails());
        assertEquals(RefundStatus.PENDING, result.getRefundStatus());
        assertNotNull(result.getRequestedAt());
        
        // Verify that requestedAt is recent (within last minute)
        assertTrue(result.getRequestedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
        assertTrue(result.getRequestedAt().isBefore(LocalDateTime.now().plusMinutes(1)));
    }

    @Test
    public void toEntity_NullDTO_ReturnsNull() {
        // Act
        Refund result = refundMapper.toEntity(null, samplePayment);

        // Assert
        assertNull(result);
    }

    @Test
    public void toEntity_NullPayment_ReturnsNull() {
        // Act
        Refund result = refundMapper.toEntity(sampleRequestDTO, null);

        // Assert
        assertNull(result);
    }

    @Test
    public void toEntity_BothNull_ReturnsNull() {
        // Act
        Refund result = refundMapper.toEntity(null, null);

        // Assert
        assertNull(result);
    }

    @Test
    public void toEntity_DTOWithNullDetails_HandlesGracefully() {
        // Arrange
        RefundRequestDTO dtoWithNullDetails = RefundRequestDTO.builder()
                .reason("Course was cancelled")
                .details(null)
                .build();

        // Act
        Refund result = refundMapper.toEntity(dtoWithNullDetails, samplePayment);

        // Assert
        assertNotNull(result);
        assertEquals(dtoWithNullDetails.getReason(), result.getReason());
        assertNull(result.getDetails()); // Should preserve null
        assertEquals(RefundStatus.PENDING, result.getRefundStatus());
    }

    // ================= CREATE REFUND TESTS =================

    @Test
    public void createRefund_ValidParametersWithDetails_ReturnsCorrectEntity() {
        // Arrange
        String reason = "Technical issues";
        String details = "Course platform was down for 3 days";

        // Act
        Refund result = refundMapper.createRefund(samplePayment, reason, details);

        // Assert
        assertNotNull(result);
        assertEquals(samplePayment, result.getPayment());
        assertEquals(reason, result.getReason());
        assertEquals(details, result.getDetails());
        assertEquals(RefundStatus.PENDING, result.getRefundStatus());
        assertNotNull(result.getRequestedAt());
        
        // Verify that requestedAt is recent
        assertTrue(result.getRequestedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    public void createRefund_ValidParametersWithNullDetails_UsesEmptyString() {
        // Arrange
        String reason = "Technical issues";
        String details = null;

        // Act
        Refund result = refundMapper.createRefund(samplePayment, reason, details);

        // Assert
        assertNotNull(result);
        assertEquals(samplePayment, result.getPayment());
        assertEquals(reason, result.getReason());
        assertEquals("", result.getDetails()); // Should default to empty string
        assertEquals(RefundStatus.PENDING, result.getRefundStatus());
    }

    @Test
    public void createRefund_NullPayment_ReturnsNull() {
        // Act
        Refund result = refundMapper.createRefund(null, "reason", "details");

        // Assert
        assertNull(result);
    }

    @Test
    public void createRefund_NullReason_HandlesGracefully() {
        // Act
        Refund result = refundMapper.createRefund(samplePayment, null, "details");

        // Assert
        assertNotNull(result);
        assertNull(result.getReason());
        assertEquals("details", result.getDetails());
    }

    @Test
    public void createRefund_EmptyStrings_HandlesGracefully() {
        // Act
        Refund result = refundMapper.createRefund(samplePayment, "", "");

        // Assert
        assertNotNull(result);
        assertEquals("", result.getReason());
        assertEquals("", result.getDetails());
    }

    // ================= TO RESPONSE DTO TESTS =================

    @Test
    public void toResponseDto_ValidRefund_ReturnsCorrectDTO() {
        // Act
        RefundResponseDTO result = refundMapper.toResponseDto(sampleRefund);

        // Assert
        assertNotNull(result);
        assertEquals(sampleRefund.getId(), result.getRefundId());
        assertEquals(sampleRefund.getPayment().getTransactionId(), result.getTransactionId());
        assertEquals(sampleRefund.getReason(), result.getReason());
        assertEquals(sampleRefund.getDetails(), result.getDetails());
        assertEquals(sampleRefund.getRefundStatus().getValue(), result.getStatus());
        assertEquals(sampleRefund.getNote(), result.getNote());
        assertEquals("Refund request has been submitted successfully.", result.getMessage());
    }

    @Test
    public void toResponseDto_NullRefund_ReturnsNull() {
        // Act
        RefundResponseDTO result = refundMapper.toResponseDto(null);

        // Assert
        assertNull(result);
    }    @Test
    public void toResponseDto_RefundWithNullNote_UsesEmptyString() {
        // Arrange
        Refund refundWithNullNote = Refund.builder()
                .id(sampleRefund.getId())
                .payment(sampleRefund.getPayment())
                .refundStatus(sampleRefund.getRefundStatus())
                .reason(sampleRefund.getReason())
                .details(sampleRefund.getDetails())
                .note(null)
                .requestedAt(sampleRefund.getRequestedAt())
                .updatedAt(sampleRefund.getUpdatedAt())
                .build();

        // Act
        RefundResponseDTO result = refundMapper.toResponseDto(refundWithNullNote);

        // Assert
        assertNotNull(result);
        assertEquals("", result.getNote());
        assertEquals("Refund request has been submitted successfully.", result.getMessage());
    }    @Test
    public void toResponseDto_ApprovedRefund_ReturnsCorrectMessage() {
        // Arrange
        Refund approvedRefund = Refund.builder()
                .id(sampleRefund.getId())
                .payment(sampleRefund.getPayment())
                .refundStatus(RefundStatus.APPROVED)
                .reason(sampleRefund.getReason())
                .details(sampleRefund.getDetails())
                .note("Refund has been processed to your account")
                .requestedAt(sampleRefund.getRequestedAt())
                .updatedAt(sampleRefund.getUpdatedAt())
                .build();

        // Act
        RefundResponseDTO result = refundMapper.toResponseDto(approvedRefund);

        // Assert
        assertNotNull(result);
        assertEquals(RefundStatus.APPROVED.getValue(), result.getStatus());
        assertEquals("Your refund request has been approved.", result.getMessage());
        assertEquals("Refund has been processed to your account", result.getNote());
    }    @Test
    public void toResponseDto_RejectedRefund_ReturnsCorrectMessage() {
        // Arrange
        Refund rejectedRefund = Refund.builder()
                .id(sampleRefund.getId())
                .payment(sampleRefund.getPayment())
                .refundStatus(RefundStatus.REJECTED)
                .reason(sampleRefund.getReason())
                .details(sampleRefund.getDetails())
                .note("Refund request does not meet our policy requirements")
                .requestedAt(sampleRefund.getRequestedAt())
                .updatedAt(sampleRefund.getUpdatedAt())
                .build();

        // Act
        RefundResponseDTO result = refundMapper.toResponseDto(rejectedRefund);

        // Assert
        assertNotNull(result);
        assertEquals(RefundStatus.REJECTED.getValue(), result.getStatus());
        assertEquals("Your refund request has been rejected.", result.getMessage());
        assertEquals("Refund request does not meet our policy requirements", result.getNote());
    }

    // ================= TO DTO LIST TESTS =================

    @Test
    public void toDtoList_ValidRefundList_ReturnsCorrectDTOList() {
        // Arrange
        Refund secondRefund = Refund.builder()
                .id(UUID.randomUUID())
                .payment(samplePayment)
                .reason("Different reason")
                .details("Different details")
                .refundStatus(RefundStatus.APPROVED)
                .note("Different note")
                .requestedAt(LocalDateTime.now().minusHours(3))
                .build();

        List<Refund> refundList = Arrays.asList(sampleRefund, secondRefund);

        // Act
        List<RefundResponseDTO> result = refundMapper.toDtoList(refundList);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Verify first refund
        RefundResponseDTO firstDTO = result.get(0);
        assertEquals(sampleRefund.getId(), firstDTO.getRefundId());
        assertEquals(sampleRefund.getReason(), firstDTO.getReason());
        assertEquals("PENDING", firstDTO.getStatus());
        
        // Verify second refund
        RefundResponseDTO secondDTO = result.get(1);
        assertEquals(secondRefund.getId(), secondDTO.getRefundId());
        assertEquals(secondRefund.getReason(), secondDTO.getReason());
        assertEquals("APPROVED", secondDTO.getStatus());
    }

    @Test
    public void toDtoList_EmptyList_ReturnsEmptyList() {
        // Arrange
        List<Refund> emptyList = Arrays.asList();

        // Act
        List<RefundResponseDTO> result = refundMapper.toDtoList(emptyList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void toDtoList_ListWithNullElements_HandlesGracefully() {
        // Arrange
        List<Refund> listWithNull = Arrays.asList(sampleRefund, null);

        // Act
        List<RefundResponseDTO> result = refundMapper.toDtoList(listWithNull);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertNotNull(result.get(0)); // First element should be mapped correctly
        assertNull(result.get(1));    // Null element should remain null
    }

    // ================= MESSAGE FOR STATUS TESTS (PRIVATE METHOD BEHAVIOR) =================    @Test
    public void toResponseDto_VariousStatuses_GeneratesCorrectMessages() {
        // Test all RefundStatus values to ensure correct message generation
        
        // PENDING
        Refund pendingRefund = Refund.builder()
                .id(sampleRefund.getId())
                .payment(sampleRefund.getPayment())
                .refundStatus(RefundStatus.PENDING)
                .reason(sampleRefund.getReason())
                .details(sampleRefund.getDetails())
                .note(sampleRefund.getNote())
                .requestedAt(sampleRefund.getRequestedAt())
                .updatedAt(sampleRefund.getUpdatedAt())
                .build();
        RefundResponseDTO pendingResult = refundMapper.toResponseDto(pendingRefund);
        assertEquals("Refund request has been submitted successfully.", pendingResult.getMessage());
        
        // APPROVED
        Refund approvedRefund = Refund.builder()
                .id(sampleRefund.getId())
                .payment(sampleRefund.getPayment())
                .refundStatus(RefundStatus.APPROVED)
                .reason(sampleRefund.getReason())
                .details(sampleRefund.getDetails())
                .note(sampleRefund.getNote())
                .requestedAt(sampleRefund.getRequestedAt())
                .updatedAt(sampleRefund.getUpdatedAt())
                .build();
        RefundResponseDTO approvedResult = refundMapper.toResponseDto(approvedRefund);
        assertEquals("Your refund request has been approved.", approvedResult.getMessage());
        
        // REJECTED
        Refund rejectedRefund = Refund.builder()
                .id(sampleRefund.getId())
                .payment(sampleRefund.getPayment())
                .refundStatus(RefundStatus.REJECTED)
                .reason(sampleRefund.getReason())
                .details(sampleRefund.getDetails())
                .note(sampleRefund.getNote())
                .requestedAt(sampleRefund.getRequestedAt())
                .updatedAt(sampleRefund.getUpdatedAt())
                .build();
        RefundResponseDTO rejectedResult = refundMapper.toResponseDto(rejectedRefund);
        assertEquals("Your refund request has been rejected.", rejectedResult.getMessage());
    }

    // ================= EDGE CASE TESTS =================

    @Test
    public void toEntity_DTOWithEmptyStrings_HandlesCorrectly() {
        // Arrange
        RefundRequestDTO emptyDTO = RefundRequestDTO.builder()
                .reason("")
                .details("")
                .build();

        // Act
        Refund result = refundMapper.toEntity(emptyDTO, samplePayment);

        // Assert
        assertNotNull(result);
        assertEquals("", result.getReason());
        assertEquals("", result.getDetails());
    }

    @Test
    public void toEntity_DTOWithVeryLongStrings_HandlesCorrectly() {
        // Arrange
        String longReason = "A".repeat(1000);
        String longDetails = "B".repeat(2000);
        
        RefundRequestDTO longDTO = RefundRequestDTO.builder()
                .reason(longReason)
                .details(longDetails)
                .build();

        // Act
        Refund result = refundMapper.toEntity(longDTO, samplePayment);

        // Assert
        assertNotNull(result);
        assertEquals(longReason, result.getReason());
        assertEquals(longDetails, result.getDetails());
    }

    @Test
    public void createRefund_WithSpecialCharacters_HandlesCorrectly() {
        // Arrange
        String specialReason = "Reason with special chars: !@#$%^&*()";
        String specialDetails = "Details with unicode: ñáéíóúü";

        // Act
        Refund result = refundMapper.createRefund(samplePayment, specialReason, specialDetails);

        // Assert
        assertNotNull(result);
        assertEquals(specialReason, result.getReason());
        assertEquals(specialDetails, result.getDetails());
    }

    // ================= INTEGRATION TESTS =================    @Test
    public void integrationTest_CompleteRefundWorkflow() {
        // Test complete workflow: DTO -> Entity -> ResponseDTO
        
        // 1. DTO to Entity
        Refund entity = refundMapper.toEntity(sampleRequestDTO, samplePayment);
        assertNotNull(entity);
        assertEquals(sampleRequestDTO.getReason(), entity.getReason());
        
        // 2. Simulate status change (would happen in service layer)
        entity = Refund.builder()
                .id(UUID.randomUUID())
                .payment(entity.getPayment())
                .refundStatus(RefundStatus.APPROVED)
                .reason(entity.getReason())
                .details(entity.getDetails())
                .note("Refund approved and processed")
                .requestedAt(entity.getRequestedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
        
        // 3. Entity to ResponseDTO
        RefundResponseDTO responseDTO = refundMapper.toResponseDto(entity);
        assertNotNull(responseDTO);
        assertEquals(entity.getId(), responseDTO.getRefundId());
        assertEquals(entity.getReason(), responseDTO.getReason());
        assertEquals("APPROVED", responseDTO.getStatus());
        assertEquals("Your refund request has been approved.", responseDTO.getMessage());
    }    @Test
    public void integrationTest_CreateRefundAndConvertToDTO() {
        // Test createRefund -> toResponseDto workflow
        
        // 1. Create refund
        Refund created = refundMapper.createRefund(samplePayment, "Test reason", "Test details");
        assertNotNull(created);
        
        // 2. Set ID (would be set by database)
        created = Refund.builder()
                .id(UUID.randomUUID())
                .payment(created.getPayment())
                .refundStatus(created.getRefundStatus())
                .reason(created.getReason())
                .details(created.getDetails())
                .note(created.getNote())
                .requestedAt(created.getRequestedAt())
                .updatedAt(created.getUpdatedAt())
                .build();
        
        // 3. Convert to DTO
        RefundResponseDTO dto = refundMapper.toResponseDto(created);
        assertNotNull(dto);
        assertEquals(created.getId(), dto.getRefundId());
        assertEquals("Test reason", dto.getReason());
        assertEquals("Test details", dto.getDetails());
        assertEquals("PENDING", dto.getStatus());
    }    @Test
    public void integrationTest_ListConversion() {
        // Test creating multiple refunds and converting to DTO list
        
        // 1. Create multiple refunds using different methods
        Refund refund1 = refundMapper.toEntity(sampleRequestDTO, samplePayment);
        Refund refund2 = refundMapper.createRefund(samplePayment, "Second reason", "Second details");
        
        // 2. Set IDs
        refund1 = Refund.builder()
                .id(UUID.randomUUID())
                .payment(refund1.getPayment())
                .refundStatus(refund1.getRefundStatus())
                .reason(refund1.getReason())
                .details(refund1.getDetails())
                .note(refund1.getNote())
                .requestedAt(refund1.getRequestedAt())
                .updatedAt(refund1.getUpdatedAt())
                .build();
        refund2 = Refund.builder()
                .id(UUID.randomUUID())
                .payment(refund2.getPayment())
                .refundStatus(refund2.getRefundStatus())
                .reason(refund2.getReason())
                .details(refund2.getDetails())
                .note(refund2.getNote())
                .requestedAt(refund2.getRequestedAt())
                .updatedAt(refund2.getUpdatedAt())
                .build();
        
        List<Refund> refundList = Arrays.asList(refund1, refund2);
        
        // 3. Convert to DTO list
        List<RefundResponseDTO> dtoList = refundMapper.toDtoList(refundList);
        
        // 4. Verify conversion
        assertNotNull(dtoList);
        assertEquals(2, dtoList.size());
        assertEquals(refund1.getId(), dtoList.get(0).getRefundId());
        assertEquals(refund2.getId(), dtoList.get(1).getRefundId());
    }
}
