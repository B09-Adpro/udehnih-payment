package id.ac.ui.cs.advprog.udehnihpayment.clients;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import id.ac.ui.cs.advprog.udehnihpayment.config.FeignConfig;

@FeignClient(
    name = "dashboard-service", 
    url = "${services.dashboard.baseurl}", 
    configuration = FeignConfig.class
)
public interface DashboardServiceClient {
    @PostMapping("/api/dashboard/processTransactionStatus")
    ResponseEntity<Map<String, Object>> notifyPaymentUpdate(
        @RequestHeader("X-API-Key") String apiKey,
        @RequestBody Map<String, Object> paymentData);

    @PostMapping("/api/dashboard/processRefundStatus")
    ResponseEntity<Map<String, Object>> notifyRefundUpdate(
        @RequestHeader("X-API-Key") String apiKey,
        @RequestBody Map<String, Object> refundData);
}