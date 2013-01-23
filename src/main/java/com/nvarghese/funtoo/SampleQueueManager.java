package com.nvarghese.funtoo;

import java.io.IOException;
import java.net.URL;

import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nvarghese.funtoo.jms.JmsQueueManager;
import com.nvarghese.funtoo.jms.SampleQueueListener;


public class SampleQueueManager implements ServletContextListener {

	private static PropertiesConfiguration propertiesConfiguration;

	private static String QUEUES__JNDI_FILE_NAME = "queues.jndi_file";
	private static final String QUEUES__REQUEST_QUEUE_NAME = "queues.queue_name";
	private static final String QUEUES__CONNECTION_FACTORY_NAME = "queues.conn_factory_name";

	// create queue manager
	private static JmsQueueManager queueManager;

	static Logger logger = LoggerFactory.getLogger(SampleQueueManager.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

		try {
			queueManager.shutDownAll();
		} catch (JMSException e) {
			logger.error("Failed to shutdown queue manager: " + e.getMessage(), e);
		} catch (NamingException e) {
			logger.error("Failed to shutdown queue manager: " + e.getMessage(), e);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {

		ServletContext ctx = event.getServletContext();
		String settingsFile = ctx.getInitParameter("queue-conf-filename");
		try {
			if (settingsFile != null)
				initialize(settingsFile);
			else
				initialize();

			SampleQueueListener messageListenerImpl = new SampleQueueListener();
			queueManager = new JmsQueueManager(getQueueName(), getConnectionFactoryName());
			queueManager.initialize(SampleQueueManager.class.getClassLoader().getResourceAsStream(getJndiFileName()),
					100, messageListenerImpl);

			// start listeners
			queueManager.startJmsQueueReceivers();
			queueManager.startJmsQueueSender();
			logger.info("RequestQueueManager for sample app started receivers and senders");

		} catch (ConfigurationException e) {
			logger.error("Failed to initialize queue settings: " + e.getMessage(), e);
		} catch (JMSException e) {
			logger.error("Failed to initialize queue settings: " + e.getMessage(), e);
		} catch (NamingException e) {
			logger.error("Failed to initialize queue settings: " + e.getMessage(), e);
		} catch (IOException e) {
			logger.error("Failed to initialize queue settings: " + e.getMessage(), e);
		}

	}

	private String getConnectionFactoryName() {

		if (propertiesConfiguration == null) {
			try {
				initialize();
			} catch (ConfigurationException e) {
				logger.error("Failed to initialize security queue settings: " + e.getMessage(), e);
			}
		}
		return propertiesConfiguration.getString(QUEUES__CONNECTION_FACTORY_NAME);
	}

	public static void initialize(String filename) throws ConfigurationException {

		URL u1 = SampleQueueManager.class.getClassLoader().getResource(filename);
		propertiesConfiguration = new PropertiesConfiguration(u1);

	}

	public static void initialize() throws ConfigurationException {

		initialize("security_queues.conf");
	}

	public static String getQueueName() {

		if (propertiesConfiguration == null) {
			try {
				initialize();
			} catch (ConfigurationException e) {
				logger.error("Failed to initialize queue settings: " + e.getMessage(), e);
			}
		}
		return propertiesConfiguration.getString(QUEUES__REQUEST_QUEUE_NAME);
	}

	public static String getUserName() {

		if (propertiesConfiguration == null) {
			try {
				initialize();
			} catch (ConfigurationException e) {
				logger.error("Failed to initialize queue settings: " + e.getMessage(), e);
			}
		}
		return propertiesConfiguration.getString(Context.SECURITY_PRINCIPAL);
	}

	public static String getPassword() {

		if (propertiesConfiguration == null) {
			try {
				initialize();
			} catch (ConfigurationException e) {
				logger.error("Failed to initialize queue settings: " + e.getMessage(), e);
			}
		}
		return propertiesConfiguration.getString(Context.SECURITY_CREDENTIALS);
	}

	public static String getJndiFileName() {

		if (propertiesConfiguration == null) {
			try {
				initialize();
			} catch (ConfigurationException e) {
				logger.error("Failed to initialize queue settings: " + e.getMessage(), e);
			}
		}
		return propertiesConfiguration.getString(QUEUES__JNDI_FILE_NAME);
	}

	/**
	 * @return the sdpRequestQueueManager
	 */
	public static JmsQueueManager getRequestQueueManager() {

		return queueManager;
	}
}
