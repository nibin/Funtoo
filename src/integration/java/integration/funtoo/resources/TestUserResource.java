package integration.funtoo.resources;

import javax.ws.rs.core.MediaType;

import org.testng.annotations.Test;

import junit.framework.Assert;

import com.nvarghese.funtoo.dto.UserDTO;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


public class TestUserResource {
	
	
	
	@Test(groups = "Funtoo_integration_test")
	public void testCreateUser() {
		
		ClientResponse clientResponse = null;
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername("nibin");
		userDTO.setEmail("nibin012@gmail.com");
		Client client = Client.create();
		WebResource r = client.resource("http://localhost:2222/user/new");
		clientResponse = r.accept(MediaType.APPLICATION_XML)
				.entity(userDTO, MediaType.APPLICATION_XML).post(ClientResponse.class);
		
		int responseStatus = clientResponse.getStatus();
		Assert.assertEquals(201, responseStatus);
		
	}

}
