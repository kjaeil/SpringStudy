package com.example.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HwBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(HwBlogApplication.class, args);
	}
}
