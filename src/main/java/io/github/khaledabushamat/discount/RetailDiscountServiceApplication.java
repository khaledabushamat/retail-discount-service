package io.github.khaledabushamat.discount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class RetailDiscountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetailDiscountServiceApplication.class, args);
	}

}
