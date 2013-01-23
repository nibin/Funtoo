package com.nvarghese.funtoo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nvarghese.funtoo.data.Database;
import com.nvarghese.funtoo.models.ArticleModel;

public class ArticleDAO {

	static Logger logger = LoggerFactory.getLogger(UserDAO.class);

	public boolean saveArticle(ArticleModel articleModel) throws SQLException {

		Database database = new Database();
		DataSource datasource = database.getDatasource();
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean saved = false;

		String insertQuery = "INSERT INTO articles (title, article_text, belongs_to, id) " + " VALUES (?, ?, ?, ?)";

		try {

			conn = datasource.getConnection();
			stmt = conn.prepareStatement(insertQuery);
			stmt.setString(1, articleModel.getTitle());
			stmt.setString(2, articleModel.getText());
			stmt.setInt(3, articleModel.getBelongsTo());
			stmt.setInt(4, articleModel.getId());

			saved = stmt.execute();

		} catch (SQLException e) {
			logger.error("SQLException while saving article", e);
			throw e;
		}

		return saved;

	}

	public ArticleModel getArticle(int articleId) throws SQLException {

		ArticleModel articleModel = null;
		Database database = new Database();
		DataSource datasource = database.getDatasource();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;

		String selectQuery = "SELECT title, article_text, belongs_to FROM articles WHERE id = ?";

		try {

			conn = datasource.getConnection();
			stmt = conn.prepareStatement(selectQuery);
			stmt.setInt(1, articleId);

			stmt.execute();

			resultSet = stmt.getResultSet();
			if (resultSet.next()) {
				articleModel = new ArticleModel();
				articleModel.setId(articleId);
				articleModel.setTitle(resultSet.getString(1));
				articleModel.setText(resultSet.getString(2));
				articleModel.setBelongsTo(resultSet.getInt(3));
			}

		} catch (SQLException e) {
			logger.error("SQLException while reading article", e);
			throw e;
		}

		return articleModel;

	}
	
	public int getMaxArticles() throws SQLException {

		int max = 0;
		Database database = new Database();
		DataSource datasource = database.getDatasource();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;

		String selectQuery = "SELECT COUNT(*) FROM articles";

		try {

			conn = datasource.getConnection();
			stmt = conn.prepareStatement(selectQuery);			

			stmt.execute();

			resultSet = stmt.getResultSet();
			if (resultSet.next()) {
				max = resultSet.getInt(1);		
			}

		} catch (SQLException e) {
			logger.error("SQLException while reading user", e);
			throw e;
		}

		return max;

	}
}
