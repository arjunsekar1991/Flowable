package com.nm.services;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
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
	@Autowired
	RuntimeService runtimeService;

	@RequestMapping("/test")
	String home() {
		repositoryService.createDeployment().addClasspathResource("myprocess.bpmn20.xml").deploy();
		runtimeService.startProcessInstanceByKey("myprocess");
		return processEngine.getName();
	}

}