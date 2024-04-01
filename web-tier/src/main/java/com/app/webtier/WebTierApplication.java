package com.app.webtier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebTierApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebTierApplication.class, args);
	}

}
