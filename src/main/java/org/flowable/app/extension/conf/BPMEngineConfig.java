package org.flowable.app.extension.conf;

import org.flowable.engine.DynamicBpmnService;
import org.flowable.engine.FormService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.common.AbstractEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BPMEngineConfig {

	@Bean
	public ProcessEngine processEngine() {
		ProcessEngine processEngine = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
				.setDatabaseType(AbstractEngineConfiguration.DATABASE_TYPE_POSTGRES)
				.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres")
				.setJdbcDriver("org.postgresql.Driver")
				.setJdbcUsername("postgres")
				.setJdbcPassword("")
				.setDatabaseSchema("flowable")
				.setDatabaseSchemaUpdate(AbstractEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
				.setEngineName("myengine")
				.buildProcessEngine();
		// processEngine
		return processEngine;
	}

	@Bean
	public RepositoryService repositoryService() {

		return processEngine().getRepositoryService();
	};

	@Bean
	public RuntimeService runtimeService() {

		return processEngine().getRuntimeService();
	};

	@Bean
	public FormService formService() {
		return processEngine().getFormService();
	};

	@Bean
	public TaskService taskService() {
		return processEngine().getTaskService();
	};
	
	@Bean
	public HistoryService historyService() {
		return processEngine().getHistoryService();
	};
	
	@Bean
	public IdentityService identityService() {
		return processEngine().getIdentityService();
	};
	
	@Bean
	public ManagementService managementService() {
		return processEngine().getManagementService();
	};
	@Bean
	public DynamicBpmnService dynamicBpmnService() {
		return processEngine().getDynamicBpmnService();
	};



}
