package integration.funtoo.resources;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.testng.annotations.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


public class TestMessageResource {
	
	@Test(groups = "Funtoo_integration_test")
	public void testPostMessage() {
		
		ClientResponse clientResponse = null;
		
		Client client = Client.create();
		WebResource r = client.resource("http://localhost:2222/message/new");
		clientResponse = r.accept(MediaType.TEXT_PLAIN)
				.entity("message_text=Sample%20message%20from%20testng", MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class);
		
		int responseStatus = clientResponse.getStatus();
		Assert.assertEquals(202, responseStatus);
		
	}

}
