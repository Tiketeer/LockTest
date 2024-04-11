package com.tiketeer.Tiketeer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
@EnableJpaAuditing
public class TiketeerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiketeerApplication.class, args);
	}

}
