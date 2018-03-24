package org.flowable.app.extension.conf;

import javax.jms.ConnectionFactory;
import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
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
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.executor.jms.HistoryJobMessageListener;
import org.flowable.spring.executor.jms.MessageBasedJobManager;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@Component
public class BPMEngineConfig {

	@Bean
	public DataSource dataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUsername("postgres");
		dataSource.setPassword("org.postgresql.Driver");
		// dataSource.setMaximumPoolSize(50);
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource());
		return transactionManager;
	}

	@Bean
	public ProcessEngineConfigurationImpl processEngineConfiguration() {
		SpringProcessEngineConfiguration springProcessEngineConfiguration = new SpringProcessEngineConfiguration();
		springProcessEngineConfiguration.setDataSource(dataSource());
		springProcessEngineConfiguration.setTransactionManager(transactionManager());
		springProcessEngineConfiguration
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_DROP_CREATE);
		springProcessEngineConfiguration.setAsyncHistoryEnabled(true);
		springProcessEngineConfiguration.setAsyncHistoryExecutorActivate(true);
		springProcessEngineConfiguration.setEngineName("my flowable engine");

		/*
		 * // Optional settings config.setAsyncHistoryJsonGroupingEnabled(true);
		 * config.setAsyncHistoryJsonGzipCompressionEnabled(false);
		 * config.setAsyncHistoryJsonGroupingThreshold(10);
		 */
		springProcessEngineConfiguration.setJobManager(jobManager());
		springProcessEngineConfiguration.setAsyncHistoryExecutorMessageQueueMode(true);
		return springProcessEngineConfiguration;
	}

	@Bean
	public ProcessEngine processEngine() {
		return processEngineConfiguration().buildProcessEngine();
	}

	@Bean
	public JmsTemplate jmsTemplate() {
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setDefaultDestination(new ActiveMQQueue("flowable-history-jobs"));
		jmsTemplate.setConnectionFactory(connectionFactory());
		return jmsTemplate;
	}

	

	@Bean
	public ConnectionFactory connectionFactory() {

		//Using an in memory ms queue for easy of demonstration
		// ActiveMQConnectionFactory activeMQConnectionFactory = new
		// ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");

		// Uncomment the following line if a real, standalone ActiveMQ JMS queue should
		// be used
		// ActiveMQConnectionFactory activeMQConnectionFactory = new
		// ActiveMQConnectionFactory("tcp://127.0.0.1:61616");
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

		activeMQConnectionFactory.setUseAsyncSend(true);
		activeMQConnectionFactory.setAlwaysSessionAsync(true);
		activeMQConnectionFactory.setStatsEnabled(true);
		return new CachingConnectionFactory(activeMQConnectionFactory);
	}

	@Bean
	public MessageBasedJobManager jobManager() {
		MessageBasedJobManager jobManager = new MessageBasedJobManager();
		jobManager.setHistoryJmsTemplate(jmsTemplate());
		return jobManager;
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
