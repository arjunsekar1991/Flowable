package com.nm.listeners;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

public class ExampleJmsListener implements javax.jms.MessageListener {

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                System.out.println("Received historical data : " + textMessage.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

}