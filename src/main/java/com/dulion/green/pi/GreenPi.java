package com.dulion.green.pi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GreenPi {

	public static void main(String[] args) {
		SpringApplication.run(GreenPi.class, args);
	}

}
