package com.nvarghese.funtoo.resources;

import javax.jms.JMSException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nvarghese.funtoo.SampleQueueManager;

@Path("message")
public class MessageResource {

	static Logger logger = LoggerFactory.getLogger(MessageResource.class);

	@POST
	@Path("new")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response postMessage(@FormParam("message_text") String messageText) {

		Response response = null;
		if (messageText == null || messageText.isEmpty()) {
			return Response.status(400).build();
		}
		try {

			SampleQueueManager.getRequestQueueManager().sendTextMapMessage("message_post", messageText);
			response = Response.status(202).entity("Message posted").build();

		} catch (JMSException e) {
			logger.error("JMSException while posting message", e);
			response = Response.status(500).entity("JMSException while posting message").build();
		}

		return response;

	}

}
