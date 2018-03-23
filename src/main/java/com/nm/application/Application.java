package com.nm.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "org.flowable.app.extension.conf" })
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}
}
