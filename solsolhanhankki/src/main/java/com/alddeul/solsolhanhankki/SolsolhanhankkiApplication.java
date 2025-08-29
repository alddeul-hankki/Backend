package com.alddeul.solsolhanhankki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing(dateTimeProviderRef = "offSetDateTimeProvider")
@SpringBootApplication
@EnableScheduling
public class SolsolhanhankkiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SolsolhanhankkiApplication.class, args);
	}

}
