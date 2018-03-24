package org.flowable.app.extension.conf;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.activemq.ActiveMQXAConnectionFactory;
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
import org.flowable.engine.impl.history.async.AsyncHistoryListener;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.executor.jms.MessageBasedJobManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.nm.listeners.JmsAsyncHistoryListener;

@Configuration
@Component
public class BPMEngineConfig {

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
	public UserTransactionManager userTransactionManager() {
		UserTransactionManager userTransactionManager = new UserTransactionManager();
		userTransactionManager.setForceShutdown(false);
		userTransactionManager.setStartupTransactionService(false);
		return userTransactionManager;
	}

	@Bean
	public UserTransaction userTransaction() {
		UserTransactionImp userTransactionImp = new UserTransactionImp();
		try {
			userTransactionImp.setTransactionTimeout(1000);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return userTransactionImp;
	}

	@Bean
	public UserTransactionService userTransactionService() {
		Properties properties = new Properties();
		properties.put("com.atomikos.icatch.service", "com.atomikos.icatch.standalone.UserTransactionServiceFactory");
		UserTransactionServiceImp userTransactionServiceImp = new UserTransactionServiceImp(properties);
		return userTransactionServiceImp;
	}

	@Bean
	@DependsOn//(value = { "userTransactionManager", "userTransaction" })
	public PlatformTransactionManager transactionManager() {
		JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
		jtaTransactionManager.setTransactionManager(userTransactionManager());
		jtaTransactionManager.setUserTransaction(userTransaction());
		return jtaTransactionManager;
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

		springProcessEngineConfiguration.setAsyncHistoryListener(jmsAsyncHistoryListener());

		springProcessEngineConfiguration.setJobManager(jobManager());
		springProcessEngineConfiguration.setAsyncHistoryExecutorMessageQueueMode(true);
		return springProcessEngineConfiguration;
	}

	@Bean
	public AsyncHistoryListener jmsAsyncHistoryListener() {
		JmsAsyncHistoryListener jmsAsyncHistoryListener = new JmsAsyncHistoryListener();
		jmsAsyncHistoryListener.setJmsTemplate(jmsTemplate());
		return jmsAsyncHistoryListener;
	}

	@Bean
	public ProcessEngine processEngine() {
		return processEngineConfiguration().buildProcessEngine();
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		ActiveMQXAConnectionFactory activeMQXAConnectionFactory = new ActiveMQXAConnectionFactory();
		activeMQXAConnectionFactory.setUseAsyncSend(true);
		activeMQXAConnectionFactory.setAlwaysSessionAsync(true);
		activeMQXAConnectionFactory.setStatsEnabled(true);
		activeMQXAConnectionFactory.setBrokerURL("tcp://127.0.0.1:61616");

		AtomikosConnectionFactoryBean atomikosConnectionFactoryBean = new AtomikosConnectionFactoryBean();
		atomikosConnectionFactoryBean.setUniqueResourceName("xamq");
		atomikosConnectionFactoryBean.setLocalTransactionMode(false);
		atomikosConnectionFactoryBean.setMaxPoolSize(100);
		atomikosConnectionFactoryBean.setBorrowConnectionTimeout(30000);
		atomikosConnectionFactoryBean.setXaConnectionFactory(activeMQXAConnectionFactory);
		return atomikosConnectionFactoryBean;
	}

	@Bean
	public JmsTemplate jmsTemplate() {
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setDefaultDestination(new ActiveMQQueue("flowable-history-jobs"));
		jmsTemplate.setConnectionFactory(connectionFactory());
		return jmsTemplate;
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
