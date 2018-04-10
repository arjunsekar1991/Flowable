package org.flowable.app.extension.conf;

import java.util.Properties;
import javax.sql.DataSource;
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
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.history.async.AsyncHistoryListener;
import org.flowable.job.service.impl.asyncexecutor.JobManager;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;

@Configuration
@Component
public class BPMEngineConfig {
	@Autowired
	JobManager jobManager;
	
	@Autowired
	AsyncHistoryListener asyncHistoryListener;
	
	@Autowired
	PlatformTransactionManager platformTransactionManager;
	
	@Bean
	public DataSource dataSource() {
		AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
		dataSource.setUniqueResourceName("postgres");
		dataSource.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
		Properties p = new Properties();
		p.setProperty("user", "postgres");
		p.setProperty("password", "");
		p.setProperty("serverName", "localhost");
		p.setProperty("portNumber", "5432");
		p.setProperty("databaseName", "postgres");
		dataSource.setXaProperties(p);
		// ds.setConnectionPoolSize(5);
		return dataSource;
	}
	
	@Bean
	public ProcessEngine processEngine() {
		return processEngineConfiguration().buildProcessEngine();
	}
	
	@Bean
	public ProcessEngineConfigurationImpl processEngineConfiguration() {
		SpringProcessEngineConfiguration springProcessEngineConfiguration = new SpringProcessEngineConfiguration();
		springProcessEngineConfiguration.setDataSource(dataSource());
		springProcessEngineConfiguration.setTransactionManager(platformTransactionManager);
		springProcessEngineConfiguration
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_DROP_CREATE);
		springProcessEngineConfiguration.setAsyncHistoryEnabled(true);
		//springProcessEngineConfiguration.setAsyncHistoryEnabled(false);
		springProcessEngineConfiguration.setAsyncHistoryExecutorActivate(true);
		//springProcessEngineConfiguration.setAsyncHistoryExecutorActivate(false);
	    springProcessEngineConfiguration.setAsyncHistoryExecutorMessageQueueMode(true);
		//springProcessEngineConfiguration.setAsyncHistoryExecutorMessageQueueMode(false);
		springProcessEngineConfiguration.setEngineName("my flowable engine");

		/*
		 * // Optional settings config.setAsyncHistoryJsonGroupingEnabled(true);
		 * config.setAsyncHistoryJsonGzipCompressionEnabled(false);
		 * config.setAsyncHistoryJsonGroupingThreshold(10);
		 */

		springProcessEngineConfiguration.setAsyncHistoryListener(asyncHistoryListener);

        springProcessEngineConfiguration.setJobManager(jobManager);
		return springProcessEngineConfiguration;
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
