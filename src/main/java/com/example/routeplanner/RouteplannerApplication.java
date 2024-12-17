package com.example.routeplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class RouteplannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RouteplannerApplication.class, args);
	}

}
