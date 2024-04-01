package com.app.apptier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AppTierApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppTierApplication.class, args);
	}

}
