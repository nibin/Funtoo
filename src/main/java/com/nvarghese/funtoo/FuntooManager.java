package com.nvarghese.funtoo;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nvarghese.funtoo.dao.ArticleDAO;
import com.nvarghese.funtoo.dao.UserDAO;

public class FuntooManager implements ServletContextListener {

	private static AtomicInteger nextUserId = new AtomicInteger(1);
	private static AtomicInteger nextArticleId = new AtomicInteger(1);
	
	static Logger logger = LoggerFactory.getLogger(FuntooManager.class);

	public static AtomicInteger getNextUserId() {

		return nextUserId;
	}

	public static AtomicInteger getNextArticleId() {

		return nextArticleId;
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

		// TODO Auto-generated method stub

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

		try {
			nextUserId.set(new UserDAO().getMaxUsers() + 1);
			logger.info("Initialized nextUserId to be: " + nextUserId.get());
			nextArticleId.set(new ArticleDAO().getMaxArticles() + 1);
			logger.info("Initialized nextArticleId to be: " + nextArticleId.get());
		} catch (SQLException e) {
			
			logger.error("Initialization of userId/ArticleId failed");
		}

	}

}
