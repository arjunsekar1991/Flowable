package com.nm.listeners;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.flowable.engine.impl.history.async.AsyncHistoryListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JmsAsyncHistoryListener implements AsyncHistoryListener {
	
	@Autowired
	protected JmsTemplate jmsTemplate;
    
    protected ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void historyDataGenerated(List<ObjectNode> historyObjectNodes) {
        try {
            final String msg = objectMapper.writeValueAsString(historyObjectNodes);
            jmsTemplate.send(new MessageCreator() {
                
                @Override
                public Message createMessage(Session session) throws JMSException {
                	
                    return session.createTextMessage(msg);
                }
                
            });
            
            
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

   /* public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }*/
    
}