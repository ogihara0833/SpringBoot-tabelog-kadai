package com.example.nagoyameshi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.example.nagoyameshi.entity")
@EnableJpaRepositories("com.example.nagoyameshi.repository")
public class NagoyameshiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NagoyameshiApplication.class, args);
	}

}
