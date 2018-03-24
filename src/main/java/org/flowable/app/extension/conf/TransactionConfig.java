package org.flowable.app.extension.conf;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.flowable.engine.impl.history.async.AsyncHistoryListener;
import org.flowable.spring.executor.jms.MessageBasedJobManager;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.nm.listeners.JmsAsyncHistoryListener;

@Component
public class TransactionConfig {
	

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
	//@DependsOn//(value = { "userTransactionManager", "userTransaction" })
	public PlatformTransactionManager platformTransactionManager() {
		JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
		jtaTransactionManager.setTransactionManager(userTransactionManager());
		jtaTransactionManager.setUserTransaction(userTransaction());
		return jtaTransactionManager;
	}

	

	@Bean
	public AsyncHistoryListener jmsAsyncHistoryListener() {
		JmsAsyncHistoryListener jmsAsyncHistoryListener = new JmsAsyncHistoryListener();
	//	jmsAsyncHistoryListener.setJmsTemplate(jmsTemplate());
		return jmsAsyncHistoryListener;
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

}
