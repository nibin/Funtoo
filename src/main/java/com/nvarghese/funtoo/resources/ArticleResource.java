package com.nvarghese.funtoo.resources;

import java.security.InvalidKeyException;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nvarghese.funtoo.FuntooManager;
import com.nvarghese.funtoo.dao.ArticleDAO;
import com.nvarghese.funtoo.dao.UserDAO;
import com.nvarghese.funtoo.dto.ArticleDTO;
import com.nvarghese.funtoo.models.ArticleModel;
import com.nvarghese.funtoo.models.UserModel;
import com.nvarghese.funtoo.services.CryptoService;
import com.nvarghese.funtoo.services.CryptoServiceException;
import com.nvarghese.funtoo.utils.ByteUtils;

@Path("article")
public class ArticleResource {

	static Logger logger = LoggerFactory.getLogger(ArticleResource.class);
	final byte[] ivBytes = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x02, 0x03, 0x00,
			0x00, 0x00, 0x01 };

	@POST
	@Path("new")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response createArticle(ArticleDTO articleDTO) {

		Response response = null;
		if (articleDTO == null || articleDTO.getText() == null) {
			return Response.status(400).build();
		}
		try {

			UserModel userModel = new UserDAO().getUser(articleDTO.getBelongsTo());
			if (userModel != null) {
				CryptoService service = new CryptoService();
				byte[] cypherBytes = service.symmetricEncrypt(articleDTO.getText().getBytes(),
						ByteUtils.toBytes(userModel.getUserKey()), ivBytes);

				ArticleModel articleModel = getArticleModelFromDTO(articleDTO);
				articleModel.setId(FuntooManager.getNextArticleId().getAndIncrement());				
				articleModel.setText(ByteUtils.toHex(cypherBytes));
				articleModel.setBelongsTo(userModel.getId());
				ArticleDAO articleDAO = new ArticleDAO();
				articleDAO.saveArticle(articleModel);
				articleDTO.setId(articleModel.getId());
				response = Response.status(201).entity(articleDTO).build();
			} else {
				response = Response.status(400).entity("User model doesn't exist to create article").build();
			}

		} catch (CryptoServiceException cse) {
			logger.error("Crypto exception while creating article", cse);
			response = Response.status(500).entity("Article create failed due to crypto exception").build();
		} catch (SQLException e) {
			logger.error("SQLException while creating article", e);
			response = Response.status(500).entity("Article create failed due to sql exception").build();
		} catch (InvalidKeyException e) {
			logger.error("InvalidKeyException while creating article", e);
			response = Response.status(500).entity("Article create failed due to sql exception").build();
		}

		return response;

	}

	@GET
	@Path("{article_id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getArticle(@PathParam("article_id") int articleId,
			@DefaultValue("true") @QueryParam("decrypt_text") boolean shouldDecryptText) {

		Response response = null;
		ArticleDAO articleDAO = new ArticleDAO();
		UserDAO userDao = new UserDAO();
		try {

			ArticleModel articleModel = articleDAO.getArticle(articleId);

			if (articleModel != null) {

				UserModel userModel = userDao.getUser(articleModel.getBelongsTo());
				if (userModel != null) {

					ArticleDTO articleDTO = new ArticleDTO();
					articleDTO.setId(articleModel.getId());
					articleDTO.setTitle(articleModel.getTitle());
					articleDTO.setBelongsTo(articleModel.getBelongsTo());
					if (shouldDecryptText) {
						byte[] plainBytes = new CryptoService().symmetricDecrypt(ByteUtils.toBytes(articleModel.getText()), ByteUtils.toBytes(userModel.getUserKey()), ivBytes);
						articleDTO.setText(new String(plainBytes));
					} else {
						articleDTO.setText(articleModel.getText());
					}
					response = Response.ok(articleDTO).build();

				} else {
					response = Response.status(404).entity("User model doesnt exist").build();
				}
			} else {
				response = Response.status(404).entity("Article model doesnt exist").build();
			}
		} catch (SQLException e) {
			logger.error("SQLException while reading article", e);
			response = Response.status(500).entity("Article read failed due to sql exception").build();
		} catch (InvalidKeyException e) {
			logger.error("InvalidKeyException while reading article", e);
			response = Response.status(500).entity("Article read failed due to InvalidKeyException").build();
		} catch (CryptoServiceException e) {
			logger.error("CryptoServiceException while reading article", e);
			response = Response.status(500).entity("Article read failed due to CryptoServiceException").build();
		}

		return response;
	}

	private ArticleModel getArticleModelFromDTO(ArticleDTO articleDTO) throws CryptoServiceException {

		ArticleModel articleModel = new ArticleModel();
		articleModel.setTitle(articleDTO.getTitle());
		articleModel.setText(articleDTO.getText());
		

		return articleModel;
	}

}
