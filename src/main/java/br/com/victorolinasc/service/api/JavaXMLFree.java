package br.com.victorolinasc.service.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.authz.annotation.RequiresRoles;

/**
 * This is the entry point for the LRS. According to the specification, the
 * services work only through a JSON RESTful service.
 * 
 * @author victor
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface JavaXMLFree {

	/**
	 * Convenience method to test configuration
	 * 
	 * @return A sentence assuring the method was executed
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	@RequiresRoles("ROLE_USER")
	String test();
}
