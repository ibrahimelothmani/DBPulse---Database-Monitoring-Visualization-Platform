package com.ibrahim.DBPulse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class DbPulseApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbPulseApplication.class, args);
		log.info("DBPulse application started successfully.");
	}
}
