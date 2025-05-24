package id.ac.ui.cs.advprog.udehnihpayment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import id.ac.ui.cs.advprog.udehnihpayment.clients.CourseServiceClient;
import id.ac.ui.cs.advprog.udehnihpayment.clients.DashboardServiceClient;

@SpringBootTest
@ActiveProfiles("test")
class UdehnihPaymentApplicationTests {

	@MockBean
	private CourseServiceClient courseServiceClient;

	@MockBean
	private DashboardServiceClient dashboardServiceClient;

	@Test
	void contextLoads() {
	}

}
