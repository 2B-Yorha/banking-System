package com.Banking.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class SystemApplication {

	public static void main(String[] args) {

//		Dotenv dotenv = Dotenv.load();
//
//		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
//		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
//		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
//		System.setProperty("JWT_EXPIRATION_MS", dotenv.get("JWT_SECRET"));

		SpringApplication.run(SystemApplication.class, args);
	}

}
