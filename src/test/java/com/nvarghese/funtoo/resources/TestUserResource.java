package com.nvarghese.funtoo.resources;

import java.sql.SQLException;

import javax.ws.rs.core.Response;

import junit.framework.Assert;
import static org.mockito.Mockito.*;

import org.testng.annotations.Test;

import com.nvarghese.funtoo.dao.UserDAO;
import com.nvarghese.funtoo.models.UserModel;


public class TestUserResource {
	
//	@Test
//	public void testGetUser() throws SQLException {
//		
//		UserResource userResource = new UserResource();
//		UserModel userModel = new UserModel();
//		userModel.setEmail("nibin012@gmail.com");
//		userModel.setId(1);
//		userModel.setUsername("nibin");
//		UserDAO userDAO = mock(UserDAO.class);
//		when(userDAO.getUser(1)).thenReturn(userModel);
//		Response response = userResource.getUser(1);
//		Assert.assertNotNull(response);
//		
//	}

}
