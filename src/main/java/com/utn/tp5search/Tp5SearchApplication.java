package com.utn.tp5search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Tp5SearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(Tp5SearchApplication.class, args);
	}
	
}
