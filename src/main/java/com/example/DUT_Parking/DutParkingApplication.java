package com.example.DUT_Parking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.example.DUT_Parking.repository")
public class DutParkingApplication {
	public static void main(String[] args) {
		SpringApplication.run(DutParkingApplication.class, args);
	}
}

