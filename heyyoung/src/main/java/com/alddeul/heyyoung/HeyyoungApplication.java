package com.alddeul.heyyoung;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing(dateTimeProviderRef = "offSetDateTimeProvider")
@SpringBootApplication
@EnableScheduling
public class HeyyoungApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeyyoungApplication.class, args);
	}

}
