package com.nvarghese.funtoo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nvarghese.funtoo.data.Database;
import com.nvarghese.funtoo.models.UserModel;

public class UserDAO {

	static Logger logger = LoggerFactory.getLogger(UserDAO.class);

	public boolean saveUser(UserModel userModel) throws SQLException {

		Database database = new Database();
		DataSource datasource = database.getDatasource();
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean saved = false;

		String insertQuery = "INSERT INTO users (name, email, user_key, id) " + " VALUES (?, ?, ?, ?)";

		try {

			conn = datasource.getConnection();
			stmt = conn.prepareStatement(insertQuery);
			stmt.setString(1, userModel.getUsername());
			stmt.setString(2, userModel.getEmail());
			stmt.setString(3, userModel.getUserKey());
			stmt.setInt(4, userModel.getId());

			saved = stmt.execute();

		} catch (SQLException e) {
			logger.error("SQLException while saving user", e);
			throw e;
		}

		return saved;

	}

	public UserModel getUser(int userId) throws SQLException {

		UserModel userModel = null;
		Database database = new Database();
		DataSource datasource = database.getDatasource();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;

		String selectQuery = "SELECT name, email, user_key FROM users WHERE id = ?";

		try {

			conn = datasource.getConnection();
			stmt = conn.prepareStatement(selectQuery);
			stmt.setInt(1, userId);

			stmt.execute();

			resultSet = stmt.getResultSet();
			if (resultSet.next()) {
				userModel = new UserModel();
				userModel.setId(userId);
				userModel.setUsername(resultSet.getString(1));
				userModel.setEmail(resultSet.getString(2));
				userModel.setUserKey(resultSet.getString(3));				
			}

		} catch (SQLException e) {
			logger.error("SQLException while reading user", e);
			throw e;
		}

		return userModel;

	}
	
	public int getMaxUsers() throws SQLException {

		int max = 0;
		Database database = new Database();
		DataSource datasource = database.getDatasource();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;

		String selectQuery = "SELECT COUNT(*) FROM users";

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
