package com.nvarghese.funtoo.data;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {

	private DataSource dataSource;

	static Logger logger = LoggerFactory.getLogger(Database.class);

	public DataSource getDatasource() {

		if (dataSource == null) {
			dataSource = getDataSourceFromJNDI("jdbc/funtooDB");
		}

		return dataSource;
	}

	private DataSource getDataSourceFromJNDI(String jndiDataSourceName) {

		try {
			InitialContext ic = new InitialContext();
			return (DataSource) ic.lookup(jndiDataSourceName);
		} catch (NamingException e) {
			logger.error("JNDI error while retrieving " + jndiDataSourceName, e);
		}
		return null;
	}

}
