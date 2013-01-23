package com.nvarghese.funtoo.jms;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nvarghese.funtoo.SampleQueueManager;

/**
 * 
 * Jms Queue Manager to manage a single queue which consists of one producer
 * instance and multiple consumer instances
 * 
 * @author nibin
 * 
 */
public class JmsQueueManager {

	private InitialContext ic;
	private String queueName;
	private String connectionFactoryName;
	private JmsQueueSender queueSender;
	private List<JmsQueueReceiver> queueReceivers;
	private AtomicBoolean initialized = new AtomicBoolean(false);

	static Logger logger = LoggerFactory.getLogger(JmsQueueManager.class);

	public static final String MESSAGE_OPERATION = "messageOperation";
	public static final String MESSAGE_PAYLOAD = "messagePayload";

	public void sendTextMapMessage(String operation, String message) throws JMSException {

		Message mapMessage = this.getJmsQueueSender().getSession().createMapMessage();
		mapMessage.setStringProperty(MESSAGE_OPERATION, operation);
		mapMessage.setStringProperty(MESSAGE_PAYLOAD, message);
		
		this.getJmsQueueSender().getProducer().send(mapMessage);
	}

	public JmsQueueManager(String queueName, String connectionFactoryName) {

		this.queueName = queueName;
		this.connectionFactoryName = connectionFactoryName;
		queueReceivers = new ArrayList<JmsQueueReceiver>();
	}

	public void initialize(InputStream jndiFileInputStream, int countOfReceivers, MessageListener jmsMessageListener)
			throws JMSException, IOException, NamingException {

		if (initialized.get() == false) {

			ic = getContext(jndiFileInputStream);
			Queue queue = (Queue) ic.lookup(queueName);
			// connection factory lookup is hard coded at this moment
			ConnectionFactory cf = (ConnectionFactory) ic.lookup(connectionFactoryName);
			queueSender = new JmsQueueSender(queue);
			queueSender.initialize(cf, SampleQueueManager.getUserName(), SampleQueueManager.getPassword());

			for (int i = 0; i < countOfReceivers; i++) {
				JmsQueueReceiver receiver = new JmsQueueReceiver(queue);
				receiver.initialize(cf, jmsMessageListener, SampleQueueManager.getUserName(),
						SampleQueueManager.getPassword());
				queueReceivers.add(receiver);
			}
			initialized.set(true);
		}

	}

	public JmsQueueSender getJmsQueueSender() {

		return queueSender;
	}

	public void startJmsQueueSender() throws JMSException {

		queueSender.startConnection();
	}

	public void startJmsQueueReceivers() throws JMSException {

		for (JmsQueueReceiver r : queueReceivers) {
			r.startConnection();
		}
	}

	public void shutDownAll() throws JMSException, NamingException {

		ic.close();
		queueSender.shutdownAll();
		for (JmsQueueReceiver r : queueReceivers) {
			r.shutdownAll();
		}
	}

	private InitialContext getContext(InputStream jndiFileInputStream) throws IOException, NamingException {

		Properties props = new Properties();

		try {
			props.load(jndiFileInputStream);
		} finally {
			if (jndiFileInputStream != null) {
				jndiFileInputStream.close();
			}
		}

		return new InitialContext(props);
	}

}
