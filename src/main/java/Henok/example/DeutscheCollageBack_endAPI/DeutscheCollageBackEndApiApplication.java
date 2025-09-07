package Henok.example.DeutscheCollageBack_endAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class DeutscheCollageBackEndApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeutscheCollageBackEndApiApplication.class, args);
	}

}
