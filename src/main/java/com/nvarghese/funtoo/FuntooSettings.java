package com.nvarghese.funtoo;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FuntooSettings {

	private PropertiesConfiguration propertiesConfiguration;

	private String funtooRootPath;
	private String defaultConfDir;

	/* jetty settings */
	private String jettyResourceFileName;
	private String jettyContextDescriptor;
	private String jettyContextResourceBase;
	private String jettyContextRootPath;

	private static final String FUNTOO__DEFAULT_CONF_DIR = "funtoo.conf.dir";
	private static final String FUNTOO__JETTY__RESOURCE_FILE_NAME = "funtoo.jetty.resource_file_name";
	private static final String FUNTOO__JETTY__CONTEXT__DESCRIPTOR = "funtoo.jetty.context.descriptor";
	private static final String FUNTOO__JETTY__CONTEXT__RESOURCE_BASE = "funtoo.jetty.context.resourcebase";
	private static final String FUNTOO__JETTY__CONTEXT__ROOT_PATH = "funtoo.jetty.context.rootpath";

	static Logger logger = LoggerFactory.getLogger(FuntooSettings.class);

	public FuntooSettings() throws ConfigurationException, URISyntaxException {

		URL u = FuntooSettings.class.getClassLoader().getResource("funtoo.conf");
		propertiesConfiguration = new PropertiesConfiguration(u);
		initialize();
	}

	public FuntooSettings(String funtooServerConfigFileName) throws ConfigurationException, URISyntaxException {

		propertiesConfiguration = new PropertiesConfiguration(FuntooSettings.class.getClassLoader().getResource(
				funtooServerConfigFileName));
		initialize();
	}

	public FuntooSettings(File funtooServerConfigFilePath) throws ConfigurationException, URISyntaxException {

		propertiesConfiguration = new PropertiesConfiguration(funtooServerConfigFilePath);
		initialize();
	}

	private void initialize() throws URISyntaxException {

		{

			PropertyConfigurator.configure("log4j.properties");
			funtooRootPath = "";

		}

		{
			defaultConfDir = propertiesConfiguration.getString(FUNTOO__DEFAULT_CONF_DIR, "conf");
		}
		{
			jettyResourceFileName = propertiesConfiguration.getString(FUNTOO__JETTY__RESOURCE_FILE_NAME,
					"jetty-web.xml");
			jettyContextDescriptor = propertiesConfiguration.getString(FUNTOO__JETTY__CONTEXT__DESCRIPTOR);
			jettyContextResourceBase = propertiesConfiguration.getString(FUNTOO__JETTY__CONTEXT__RESOURCE_BASE);
			jettyContextRootPath = propertiesConfiguration.getString(FUNTOO__JETTY__CONTEXT__ROOT_PATH);
		}
	}

	public String getFuntooRootPath() {

		return funtooRootPath;
	}

	public String getDefaultConfDir() {

		return defaultConfDir;
	}

	public void setDefaultConfDir(String defaultConfDir) {

		this.defaultConfDir = defaultConfDir;
	}

	public String getJettyResourceFileName() {

		return jettyResourceFileName;
	}

	public String getJettyContextDescriptor() {

		return jettyContextDescriptor;
	}

	public String getJettyContextResourceBase() {

		return jettyContextResourceBase;
	}

	public String getJettyContextRootPath() {

		return jettyContextRootPath;
	}

}
