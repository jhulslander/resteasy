package com.restfully.shop.test;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.core.Response;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CustomerResourceTest
{
   @Test
   public void testCustomerResource() throws Exception
   {
	   javax.ws.rs.client.Client client = javax.ws.rs.client.ClientBuilder.newClient();
      try {
         System.out.println("*** Create a new Customer ***");

         

         javax.ws.rs.core.Response response = client.target("http://localhost:8080/services/customers")
                 .request().post(javax.ws.rs.client.Entity.xml(buildCuxtomerUploadXML()));
         if (response.getStatus() != 201) throw new RuntimeException("Failed to create");
         String location = response.getLocation().toString();
         System.out.println("Location: " + location);
         response.close();

         System.out.println("*** GET Created Customer **");
         String customer = client.target(location).request().get(String.class);
         System.out.println("Customer output resteasy: " + customer);

         
         response = client.target(location).request().put(javax.ws.rs.client.Entity.xml(buildCustomerUpdateUploadXML()));
         if (response.getStatus() != 204) throw new RuntimeException("Failed to update");
         response.close();
         System.out.println("**** After Update ***  statusL: " + response.getStatus());
         customer = client.target(location).request().get(String.class);
         System.out.println("Customer output resteasy: " + customer);
      } finally {
         client.close();
      }
   }
   
   private String buildCuxtomerUploadXML() {
	   String xml = "<customer>"
               + "<first-name>Bill</first-name>"
               + "<last-name>Burke</last-name>"
               + "<street>256 Clarendon Street</street>"
               + "<city>Boston</city>"
               + "<state>MA</state>"
               + "<zip>02115</zip>"
               + "<country>USA</country>"
               + "</customer>";
	   return xml;
   }
   
   private String buildCustomerUpdateUploadXML() {
	   String updateCustomer = "<customer>"
               + "<first-name>William</first-name>"
               + "<last-name>Burke</last-name>"
               + "<street>256 Clarendon Street</street>"
               + "<city>Boston</city>"
               + "<state>MA</state>"
               + "<zip>02115</zip>"
               + "<country>USA</country>"
               + "</customer>";
	   return updateCustomer;
   }
   
   @Test
   public void testCustomerResourceJersey() throws Exception {
	   ClientConfig clientConfig = new DefaultClientConfig();
       Client client = Client.create(clientConfig);
       
       ClientResponse response = client.handle(buildClientRequest("http://localhost:8080/services/customers", buildCustomerUpdateUploadXML(), HttpMethod.POST));
       
       if (response.getStatus() == HttpURLConnection.HTTP_CREATED) {
    	   System.out.println("Jersey post worked fine");
       } else {
    	   throw new RuntimeException("Jersey there was an error in the post, with a status of " + response.getStatus());
       }
       System.out.println("response " + response);
       String results = response.getEntity(String.class);
       System.err.println("Jersey POST results: " + results);
       
       
       ClientResponse response2 = client.handle(buildClientRequest("http://localhost:8080/services/customers", buildCustomerUpdateUploadXML(), HttpMethod.PUT));
       
       if (response2.getStatus() == HttpURLConnection.HTTP_CREATED) {
    	   System.out.println("Jersey put worked fine");
       } else {
    	   throw new RuntimeException("Jersey there was an error in the put, with a status of " + response.getStatus());
       }
       
       //String results = response.getEntity(String.class);
       results = response2.getEntity(String.class);
       System.err.println("Jersey PUT results: " + results);
	   
   }
   
   protected ClientRequest buildClientRequest(String url, String xml, String method) {
		ClientRequest.Builder builder = new ClientRequest.Builder();
		builder.accept(MediaType.APPLICATION_XML);
		//builder.header(PaymentWorksConstants.AUTHORIZATION_HEADER_KEY, buildAuthorizationHeaderString());
		builder.entity(xml, MediaType.APPLICATION_XML);

		return builder.build(buildURI(url), HttpMethod.POST);
	}
   
   protected URI buildURI(String url) {
		URI uri;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			throw new RuntimeException("Unable to build URI for the URL: " + url, e);
		}
		return uri;
	}
   
}
