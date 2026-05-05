package com.apiFutbol.futbolapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FutbolApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FutbolApiApplication.class, args);
	}

}
