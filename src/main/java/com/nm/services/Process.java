package com.nm.services;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@RestController
public class Process {

	@Autowired
	RepositoryService repositoryService;
	@Autowired
	ProcessEngine processEngine;
	
	@RequestMapping("/test")
	String home() {
		repositoryService.createDeployment()
        .addClasspathResource("src/main/resources/MyProcess.bpmn20.xml").deploy();
		//System.out.println(processEngine.getName());
		return "Hello World!";
	}

	

}