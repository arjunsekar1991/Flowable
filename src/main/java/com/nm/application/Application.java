package com.nm.application;

import org.flowable.app.extension.conf.BPMEngineConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "org.flowable.app.extension.conf","org.flowable.app.extension.bean" ,"com.nm.services"})
public class Application {

	
	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}
}
