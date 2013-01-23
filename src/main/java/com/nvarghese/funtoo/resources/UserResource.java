package com.nvarghese.funtoo.resources;

import java.security.SecureRandom;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nvarghese.funtoo.FuntooManager;
import com.nvarghese.funtoo.dao.UserDAO;
import com.nvarghese.funtoo.dto.UserDTO;
import com.nvarghese.funtoo.models.UserModel;
import com.nvarghese.funtoo.services.CryptoService;
import com.nvarghese.funtoo.services.CryptoServiceException;
import com.nvarghese.funtoo.utils.ByteUtils;

@Path("user")
public class UserResource {

	static Logger logger = LoggerFactory.getLogger(UserResource.class);

	@POST
	@Path("new")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response createUser(UserDTO userDTO) {

		Response response = null;
		if (userDTO == null || userDTO.getUsername() == null) {
			return Response.status(400).build();
		}
		try {
			UserModel userModel = getUserModelFromDTO(userDTO);
			userModel.setId(FuntooManager.getNextUserId().getAndIncrement());
			UserDAO userDAO = new UserDAO();
			userDAO.saveUser(userModel);
			userDTO.setId(userModel.getId());
			response = Response.status(201).entity(userDTO).build();
		} catch (CryptoServiceException cse) {
			logger.error("Crypto exception while creating user", cse);
			response = Response.status(500).entity("User create failed due to crypto exception").build();
		} catch (SQLException e) {
			logger.error("SQLException while creating user", e);
			response = Response.status(500).entity("User create failed due to sql exception").build();
		}

		return response;

	}

	@GET
	@Path("{user_id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getUser(@PathParam("user_id") int userId) {

		Response response = null;
		UserDAO userDao = new UserDAO();
		try {
			UserModel userModel = userDao.getUser(userId);
			if (userModel != null) {

				UserDTO userDTO = new UserDTO();
				userDTO.setId(userId);
				userDTO.setUsername(userModel.getUsername());
				userDTO.setEmail(userModel.getEmail());

				response = Response.ok(userDTO).build();

			} else {
				response = Response.status(404).entity("User model doesnt exist").build();
			}
		} catch (SQLException e) {
			logger.error("SQLException while reading user", e);
			response = Response.status(500).entity("User read failed due to sql exception").build();
		}

		return response;
	}

	private UserModel getUserModelFromDTO(UserDTO userDTO) throws CryptoServiceException {

		SecureRandom random = new SecureRandom();
		CryptoService cryptoService = new CryptoService();
		byte[] keyBytes = cryptoService.generateKey(
				cryptoService.createTempPassPhraseInBytes(userDTO.getUsername(), userDTO.getEmail()),
				random.generateSeed(32), 256);

		UserModel userModel = new UserModel();
		userModel.setUsername(userDTO.getUsername());
		userModel.setEmail(userDTO.getEmail());
		userModel.setUserKey(ByteUtils.toHex(keyBytes));

		return userModel;
	}
}
