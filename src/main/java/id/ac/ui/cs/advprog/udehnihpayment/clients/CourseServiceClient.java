package id.ac.ui.cs.advprog.udehnihpayment.clients;
import id.ac.ui.cs.advprog.udehnihpayment.config.FeignConfig;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "course-service", 
    url = "${services.course.baseurl}", 
    configuration = FeignConfig.class
)

public interface CourseServiceClient {
    @PostMapping("/api/enrollment/payment-callback")
    ResponseEntity<Map<String, Object>> updateEnrollmentStatus(
        @RequestBody Map<String, Object> paymentData);
}