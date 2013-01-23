package com.nvarghese.funtoo.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleQueueListener implements MessageListener {

	private final Logger logger = LoggerFactory.getLogger(SampleQueueListener.class);

	@Override
	public void onMessage(Message message) {

		try {

			logger.info("New Message received in the request queue listner with Jms Message Id {}",
					message.getJMSMessageID());

			String operation = message.getStringProperty(JmsQueueManager.MESSAGE_OPERATION);
			String messagePayload = message.getStringProperty(JmsQueueManager.MESSAGE_PAYLOAD);
			routeMessage(operation, messagePayload);

		} catch (JMSException e) {
			logger.error("Unable to get the detail of the new message received in Library Request Queue. ", e);
		}
	}

	private void routeMessage(String operation, String messagePayload) {
		
		logger.info("Received message - Operation: {}, message: {}", operation, messagePayload);

	}

}
