package com.tarrific.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.tarrific.backend.model")
@EnableJpaRepositories(basePackages = "com.tarrific.backend.repository")
public class TarrificBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TarrificBackendApplication.class, args);
	}

}
