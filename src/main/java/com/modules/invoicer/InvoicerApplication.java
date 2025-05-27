package com.modules.invoicer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class InvoicerApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoicerApplication.class, args);
	}

}
