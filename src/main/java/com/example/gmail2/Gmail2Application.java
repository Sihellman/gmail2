package com.example.gmail2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Gmail2Application {
	@Bean
	public RestTemplate getRestTemplate() {
		return  new RestTemplate();
	}
	public static void main(String[] args) {
		SpringApplication.run(Gmail2Application.class, args);
	}

}
