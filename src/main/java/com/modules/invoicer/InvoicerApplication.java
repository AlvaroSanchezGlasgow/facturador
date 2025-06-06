package com.modules.invoicer;

import org.springframework.boot.SpringApplication;
import org.springframework.modulith.Modulith;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Modulith(systemName = "Invoicer")
@EnableJpaAuditing
public class InvoicerApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoicerApplication.class, args);
	}

}
