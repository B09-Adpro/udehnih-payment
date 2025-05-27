package id.ac.ui.cs.advprog.udehnihpayment.mapper;

import id.ac.ui.cs.advprog.udehnihpayment.dto.request.PaymentRequestDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.response.PaymentDetailDTO;
import id.ac.ui.cs.advprog.udehnihpayment.dto.response.PaymentResponseDTO;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentMethod;
import id.ac.ui.cs.advprog.udehnihpayment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.udehnihpayment.model.Payment;
import id.ac.ui.cs.advprog.udehnihpayment.model.PaymentDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PaymentMapperTest {

    private PaymentMapper paymentMapper;
    private Payment samplePayment;
    private PaymentDetails samplePaymentDetails;
    private PaymentRequestDTO sampleRequestDTO;
    private PaymentDetailDTO.Details sampleDetailsDTO;

    @BeforeEach
    public void setUp() {
        paymentMapper = new PaymentMapper();
        
        // Setup sample PaymentDetails
        samplePaymentDetails = new PaymentDetails();
        samplePaymentDetails.setConfirmation(true);
        samplePaymentDetails.setConfirmedAt(LocalDateTime.now().minusHours(1));
        samplePaymentDetails.setAdminApproval(true);
        samplePaymentDetails.setApprovedAt(LocalDateTime.now());
        samplePaymentDetails.setApprovedBy("admin@test.com");
        
        // Setup sample Payment
        samplePayment = Payment.builder()
                .transactionId(UUID.randomUUID())
                .courseId(123L)
                .userId(456L)
                .enrollmentId(789L)
                .courseTitle("Sample Course")
                .tutorName("John Doe")
                .amount(new BigDecimal("100000"))
                .paymentStatus(PaymentStatus.PAID)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .paymentDetails(samplePaymentDetails)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Setup sample PaymentRequestDTO
        sampleRequestDTO = PaymentRequestDTO.builder()
                .enrollmentId(789L)
                .courseId(123L)
                .studentId(456L)
                .tutorName("John Doe")
                .courseTitle("Sample Course")
                .amount(new BigDecimal("100000"))
                .paymentMethod("Credit Card")
                .build();
        
        // Setup sample PaymentDetailDTO.Details
        sampleDetailsDTO = PaymentDetailDTO.Details.builder()
                .confirmation(true)
                .confirmedAt(LocalDateTime.now().minusHours(1))
                .adminApproval(true)
                .approvedAt(LocalDateTime.now())
                .approvedBy("admin@test.com")
                .build();
    }

    // ================= TO DETAIL DTO TESTS =================

    @Test
    public void toDetailDto_ValidPayment_ReturnsCorrectDTO() {
        // Act
        PaymentDetailDTO result = paymentMapper.toDetailDto(samplePayment);

        // Assert
        assertNotNull(result);
        assertEquals(samplePayment.getTransactionId(), result.getTransactionId());
        assertEquals(samplePayment.getCourseId(), result.getCourseId());
        assertEquals(samplePayment.getUserId(), result.getUserId());
        assertEquals(samplePayment.getCourseTitle(), result.getCourseTitle());
        assertEquals(samplePayment.getTutorName(), result.getTutorName());
        assertEquals(samplePayment.getAmount(), result.getAmount());
        assertEquals(samplePayment.getPaymentStatus().getValue(), result.getPaymentStatus());
        assertEquals(samplePayment.getPaymentMethod().getValue(), result.getPaymentMethod());
        assertEquals(samplePayment.getCreatedAt(), result.getCreatedAt());
        assertEquals(samplePayment.getUpdatedAt(), result.getUpdatedAt());
        
        // Verify payment details mapping
        assertNotNull(result.getPaymentDetails());
        assertEquals(samplePaymentDetails.isConfirmation(), result.getPaymentDetails().isConfirmation());
        assertEquals(samplePaymentDetails.getConfirmedAt(), result.getPaymentDetails().getConfirmedAt());
        assertEquals(samplePaymentDetails.isAdminApproval(), result.getPaymentDetails().isAdminApproval());
        assertEquals(samplePaymentDetails.getApprovedAt(), result.getPaymentDetails().getApprovedAt());
        assertEquals(samplePaymentDetails.getApprovedBy(), result.getPaymentDetails().getApprovedBy());
    }

    @Test
    public void toDetailDto_NullPayment_ReturnsNull() {
        // Act
        PaymentDetailDTO result = paymentMapper.toDetailDto(null);

        // Assert
        assertNull(result);
    }

    @Test
    public void toDetailDto_PaymentWithNullDetails_MapsCorrectly() {
        // Arrange
        Payment paymentWithoutDetails = samplePayment.toBuilder()
                .paymentDetails(null)
                .build();

        // Act
        PaymentDetailDTO result = paymentMapper.toDetailDto(paymentWithoutDetails);

        // Assert
        assertNotNull(result);
        assertNull(result.getPaymentDetails());
        assertEquals(paymentWithoutDetails.getTransactionId(), result.getTransactionId());
    }

    // ================= TO RESPONSE DTO TESTS =================

    @Test
    public void toResponseDto_ValidPayment_ReturnsCorrectDTO() {
        // Act
        PaymentResponseDTO result = paymentMapper.toResponseDto(samplePayment);

        // Assert
        assertNotNull(result);
        assertEquals(samplePayment.getTransactionId(), result.getTransactionId());
        assertEquals(samplePayment.getCourseId(), result.getCourseId());
        assertEquals(samplePayment.getUserId(), result.getUserId());
        assertEquals(samplePayment.getCourseTitle(), result.getCourseTitle());
        assertEquals(samplePayment.getTutorName(), result.getTutorName());
        assertEquals(samplePayment.getAmount(), result.getAmount());
        assertEquals(samplePayment.getPaymentStatus().getValue(), result.getPaymentStatus());
        assertEquals(samplePayment.getPaymentMethod().getValue(), result.getPaymentMethod());
        assertEquals(samplePayment.getCreatedAt(), result.getCreatedAt());
        assertEquals(samplePayment.getUpdatedAt(), result.getUpdatedAt());
    }

    @Test
    public void toResponseDto_NullPayment_ReturnsNull() {
        // Act
        PaymentResponseDTO result = paymentMapper.toResponseDto(null);

        // Assert
        assertNull(result);
    }

    @Test
    public void toResponseDto_PaymentWithMinimalData_MapsCorrectly() {
        // Arrange
        Payment minimalPayment = Payment.builder()
                .transactionId(UUID.randomUUID())
                .courseId(123L)
                .userId(456L)
                .amount(new BigDecimal("50000"))
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .build();

        // Act
        PaymentResponseDTO result = paymentMapper.toResponseDto(minimalPayment);

        // Assert
        assertNotNull(result);
        assertEquals(minimalPayment.getTransactionId(), result.getTransactionId());
        assertEquals(minimalPayment.getCourseId(), result.getCourseId());
        assertEquals(minimalPayment.getUserId(), result.getUserId());
        assertNull(result.getCourseTitle());
        assertNull(result.getTutorName());
    }

    // ================= TO ENTITY TESTS =================

    @Test
    public void toEntity_ValidRequestDTO_ReturnsCorrectEntity() {
        // Act
        Payment result = paymentMapper.toEntity(sampleRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(sampleRequestDTO.getEnrollmentId(), result.getEnrollmentId());
        assertEquals(sampleRequestDTO.getCourseId(), result.getCourseId());
        assertEquals(sampleRequestDTO.getStudentId(), result.getUserId());
        assertEquals(sampleRequestDTO.getTutorName(), result.getTutorName());
        assertEquals(sampleRequestDTO.getCourseTitle(), result.getCourseTitle());
        assertEquals(sampleRequestDTO.getAmount(), result.getAmount());
        assertEquals(PaymentMethod.fromString(sampleRequestDTO.getPaymentMethod()), result.getPaymentMethod());
    }

    @Test
    public void toEntity_NullRequestDTO_ReturnsNull() {
        // Act
        Payment result = paymentMapper.toEntity(null);

        // Assert
        assertNull(result);
    }    @Test
    public void toEntity_RequestDTOWithDifferentPaymentMethods_MapsCorrectly() {
        // Test Bank Transfer
        PaymentRequestDTO bankTransferDTO = PaymentRequestDTO.builder()
                .enrollmentId(sampleRequestDTO.getEnrollmentId())
                .studentId(sampleRequestDTO.getStudentId())
                .courseId(sampleRequestDTO.getCourseId())
                .courseTitle(sampleRequestDTO.getCourseTitle())
                .tutorName(sampleRequestDTO.getTutorName())
                .amount(sampleRequestDTO.getAmount())
                .paymentMethod("Bank Transfer")
                .timestamp(sampleRequestDTO.getTimestamp())
                .build();
        
        Payment bankTransferResult = paymentMapper.toEntity(bankTransferDTO);
        assertEquals(PaymentMethod.BANK_TRANSFER, bankTransferResult.getPaymentMethod());
        
        // Test Credit Card
        PaymentRequestDTO creditCardDTO = PaymentRequestDTO.builder()
                .enrollmentId(sampleRequestDTO.getEnrollmentId())
                .studentId(sampleRequestDTO.getStudentId())
                .courseId(sampleRequestDTO.getCourseId())
                .courseTitle(sampleRequestDTO.getCourseTitle())
                .tutorName(sampleRequestDTO.getTutorName())
                .amount(sampleRequestDTO.getAmount())
                .paymentMethod("Credit Card")
                .timestamp(sampleRequestDTO.getTimestamp())
                .build();
        
        Payment creditCardResult = paymentMapper.toEntity(creditCardDTO);
        assertEquals(PaymentMethod.CREDIT_CARD, creditCardResult.getPaymentMethod());
    }

    @Test
    public void toEntity_RequestDTOWithMinimalData_MapsCorrectly() {
        // Arrange
        PaymentRequestDTO minimalDTO = PaymentRequestDTO.builder()
                .courseId(123L)
                .studentId(456L)
                .amount(new BigDecimal("50000"))
                .paymentMethod("Bank Transfer")
                .build();

        // Act
        Payment result = paymentMapper.toEntity(minimalDTO);

        // Assert
        assertNotNull(result);
        assertEquals(minimalDTO.getCourseId(), result.getCourseId());
        assertEquals(minimalDTO.getStudentId(), result.getUserId());
        assertEquals(minimalDTO.getAmount(), result.getAmount());
        assertNull(result.getEnrollmentId());
        assertNull(result.getTutorName());
        assertNull(result.getCourseTitle());
    }

    // ================= TO DETAILS DTO TESTS =================

    @Test
    public void toDetailsDto_ValidPaymentDetails_ReturnsCorrectDTO() {
        // Act
        PaymentDetailDTO.Details result = paymentMapper.toDetailsDto(samplePaymentDetails);

        // Assert
        assertNotNull(result);
        assertEquals(samplePaymentDetails.isConfirmation(), result.isConfirmation());
        assertEquals(samplePaymentDetails.getConfirmedAt(), result.getConfirmedAt());
        assertEquals(samplePaymentDetails.isAdminApproval(), result.isAdminApproval());
        assertEquals(samplePaymentDetails.getApprovedAt(), result.getApprovedAt());
        assertEquals(samplePaymentDetails.getApprovedBy(), result.getApprovedBy());
    }

    @Test
    public void toDetailsDto_NullPaymentDetails_ReturnsNull() {
        // Act
        PaymentDetailDTO.Details result = paymentMapper.toDetailsDto(null);

        // Assert
        assertNull(result);
    }

    @Test
    public void toDetailsDto_PaymentDetailsWithDefaults_MapsCorrectly() {
        // Arrange
        PaymentDetails defaultDetails = new PaymentDetails();
        // Default values: confirmation=false, adminApproval=false, others=null

        // Act
        PaymentDetailDTO.Details result = paymentMapper.toDetailsDto(defaultDetails);

        // Assert
        assertNotNull(result);
        assertFalse(result.isConfirmation());
        assertFalse(result.isAdminApproval());
        assertNull(result.getConfirmedAt());
        assertNull(result.getApprovedAt());
        assertNull(result.getApprovedBy());
    }

    // ================= TO DETAILS ENTITY TESTS =================

    @Test
    public void toDetailsEntity_ValidDetailsDTO_ReturnsCorrectEntity() {
        // Act
        PaymentDetails result = paymentMapper.toDetailsEntity(sampleDetailsDTO);

        // Assert
        assertNotNull(result);
        assertEquals(sampleDetailsDTO.isConfirmation(), result.isConfirmation());
        assertEquals(sampleDetailsDTO.getConfirmedAt(), result.getConfirmedAt());
        assertEquals(sampleDetailsDTO.isAdminApproval(), result.isAdminApproval());
        assertEquals(sampleDetailsDTO.getApprovedAt(), result.getApprovedAt());
        assertEquals(sampleDetailsDTO.getApprovedBy(), result.getApprovedBy());
    }

    @Test
    public void toDetailsEntity_NullDetailsDTO_ReturnsNull() {
        // Act
        PaymentDetails result = paymentMapper.toDetailsEntity(null);

        // Assert
        assertNull(result);
    }

    @Test
    public void toDetailsEntity_DetailsDTOWithDefaults_MapsCorrectly() {
        // Arrange
        PaymentDetailDTO.Details defaultDetailsDTO = PaymentDetailDTO.Details.builder()
                .confirmation(false)
                .adminApproval(false)
                .build();

        // Act
        PaymentDetails result = paymentMapper.toDetailsEntity(defaultDetailsDTO);

        // Assert
        assertNotNull(result);
        assertFalse(result.isConfirmation());
        assertFalse(result.isAdminApproval());
        assertNull(result.getConfirmedAt());
        assertNull(result.getApprovedAt());
        assertNull(result.getApprovedBy());
    }

    // ================= EDGE CASE TESTS =================

    @Test
    public void toDetailDto_PaymentWithZeroAmount_MapsCorrectly() {
        // Arrange
        Payment zeroAmountPayment = samplePayment.toBuilder()
                .amount(BigDecimal.ZERO)
                .build();

        // Act
        PaymentDetailDTO result = paymentMapper.toDetailDto(zeroAmountPayment);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getAmount());
    }    @Test
    public void toEntity_RequestDTOWithZeroAmount_MapsCorrectly() {
        // Arrange
        PaymentRequestDTO zeroAmountDTO = PaymentRequestDTO.builder()
                .enrollmentId(sampleRequestDTO.getEnrollmentId())
                .studentId(sampleRequestDTO.getStudentId())
                .courseId(sampleRequestDTO.getCourseId())
                .courseTitle(sampleRequestDTO.getCourseTitle())
                .tutorName(sampleRequestDTO.getTutorName())
                .amount(BigDecimal.ZERO)
                .paymentMethod(sampleRequestDTO.getPaymentMethod())
                .timestamp(sampleRequestDTO.getTimestamp())
                .build();

        // Act
        Payment result = paymentMapper.toEntity(zeroAmountDTO);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getAmount());
    }    @Test
    public void toEntity_RequestDTOWithNegativeAmount_MapsCorrectly() {
        // Arrange
        PaymentRequestDTO negativeAmountDTO = PaymentRequestDTO.builder()
                .enrollmentId(sampleRequestDTO.getEnrollmentId())
                .studentId(sampleRequestDTO.getStudentId())
                .courseId(sampleRequestDTO.getCourseId())
                .courseTitle(sampleRequestDTO.getCourseTitle())
                .tutorName(sampleRequestDTO.getTutorName())
                .amount(new BigDecimal("-1000"))
                .paymentMethod(sampleRequestDTO.getPaymentMethod())
                .timestamp(sampleRequestDTO.getTimestamp())
                .build();

        // Act
        Payment result = paymentMapper.toEntity(negativeAmountDTO);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("-1000"), result.getAmount());
    }

    // ================= INTEGRATION TESTS =================

    @Test
    public void integrationTest_FullMappingCycle() {
        // Test: DTO -> Entity -> DTO conversion preserves data
        
        // 1. RequestDTO to Entity
        Payment entity = paymentMapper.toEntity(sampleRequestDTO);
        assertNotNull(entity);
        
        // 2. Add some fields that would be set by the service layer
        entity = entity.toBuilder()
                .transactionId(UUID.randomUUID())
                .paymentStatus(PaymentStatus.PENDING)
                .paymentDetails(samplePaymentDetails)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // 3. Entity to DetailDTO
        PaymentDetailDTO detailDTO = paymentMapper.toDetailDto(entity);
        assertNotNull(detailDTO);
        assertEquals(entity.getCourseId(), detailDTO.getCourseId());
        assertEquals(entity.getUserId(), detailDTO.getUserId());
        assertEquals(entity.getAmount(), detailDTO.getAmount());
        
        // 4. Entity to ResponseDTO
        PaymentResponseDTO responseDTO = paymentMapper.toResponseDto(entity);
        assertNotNull(responseDTO);
        assertEquals(entity.getCourseId(), responseDTO.getCourseId());
        assertEquals(entity.getUserId(), responseDTO.getUserId());
        assertEquals(entity.getAmount(), responseDTO.getAmount());
    }

    @Test
    public void integrationTest_PaymentDetailsRoundTrip() {
        // Test: PaymentDetails -> DTO -> PaymentDetails preserves data
        
        // 1. Entity to DTO
        PaymentDetailDTO.Details dto = paymentMapper.toDetailsDto(samplePaymentDetails);
        assertNotNull(dto);
        
        // 2. DTO to Entity
        PaymentDetails roundTripEntity = paymentMapper.toDetailsEntity(dto);
        assertNotNull(roundTripEntity);
        
        // 3. Verify data preservation
        assertEquals(samplePaymentDetails.isConfirmation(), roundTripEntity.isConfirmation());
        assertEquals(samplePaymentDetails.getConfirmedAt(), roundTripEntity.getConfirmedAt());
        assertEquals(samplePaymentDetails.isAdminApproval(), roundTripEntity.isAdminApproval());
        assertEquals(samplePaymentDetails.getApprovedAt(), roundTripEntity.getApprovedAt());
        assertEquals(samplePaymentDetails.getApprovedBy(), roundTripEntity.getApprovedBy());
    }
}
