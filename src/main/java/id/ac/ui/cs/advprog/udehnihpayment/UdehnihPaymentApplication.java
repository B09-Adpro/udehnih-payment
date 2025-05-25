package id.ac.ui.cs.advprog.udehnihpayment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UdehnihPaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(UdehnihPaymentApplication.class, args);
	}

}
