package com.revisen.football.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class FootballAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(FootballAdminApplication.class, args);
	}

}
