package com.ibrahim.DBPulse;

import org.springframework.boot.SpringApplication;

public class TestDbPulseApplication {

	public static void main(String[] args) {
		SpringApplication.from(DbPulseApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
