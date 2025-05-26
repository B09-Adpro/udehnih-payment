package id.ac.ui.cs.advprog.udehnihpayment.config;

import id.ac.ui.cs.advprog.udehnihpayment.clients.AuthServiceClient;
import id.ac.ui.cs.advprog.udehnihpayment.clients.CourseServiceClient;
import id.ac.ui.cs.advprog.udehnihpayment.clients.DashboardServiceClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public AuthServiceClient authServiceClient() {
        return mock(AuthServiceClient.class);
    }

    @Bean
    @Primary
    public CourseServiceClient courseServiceClient() {
        return mock(CourseServiceClient.class);
    }

    @Bean
    @Primary
    public DashboardServiceClient dashboardServiceClient() {
        return mock(DashboardServiceClient.class);
    }
}
