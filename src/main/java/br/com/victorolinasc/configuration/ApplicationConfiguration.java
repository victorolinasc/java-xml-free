package br.com.victorolinasc.configuration;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.spring.container.SpringComponentProviderFactory;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;

/**
 * This class implements all the configuration that would otherwise be placed
 * inside web.xml. the steps taken in the configuration are:
 * <ul>
 * <li>Bootstrap Spring Container
 * <li>Register Shiro Security Filter
 * <li>Register Jersey REST servlet
 * </ul>
 * 
 * @author victor
 * @see WebApplicationInitializer
 */
public class ApplicationConfiguration implements WebApplicationInitializer {

	/**
	 * The filter name must be the same in web configuration AND spring
	 * configuration. Thus this static public field. Otherwise it
	 */
	public static final String SHIRO_FILTER_NAME = "shiroFilter";

	// REST services are mapped only with this root
	private static final String JERSEY_SERVLET_MAPPING = "/services/*";

	@Override
	public void onStartup(final ServletContext servletContext)
			throws ServletException {

		AnnotationConfigWebApplicationContext root = null;

		// Bootstrap
		root = bootstrapSpring(servletContext);
		// Security
		registerSecurityFilter(servletContext, root);
		// REST
		registerJerseyServlet(servletContext, root);
	}

	/**
	 * This inner class holds all Spring configuration.
	 * <p>
	 * Warning: this class can be moved to a top level class if it grows in
	 * size.
	 * 
	 * @author victor
	 * @see RepositoryConfiguration
	 * @see SecurityConfiguration
	 */
	@Configuration
	@ComponentScan("br.com.victorolinasc")
	@Import({ SecurityConfiguration.class, //
			BeanValidationConfiguration.class, //
			RepositoryConfiguration.class })
	public static class SpringConfiguration {
	}

	/**
	 * This is a slightly modified Jersey Spring Servlet. The actual
	 * implementation searches for the Spring context using
	 * {@link WebApplicationContextUtils} which is not ready at this point (this
	 * can be fixed in a later release). So, we pass the context in the
	 * constructor and overwrites the method that searches for the Spring
	 * context.
	 * 
	 * @author victor
	 */
	private class JerseyConfiguration extends SpringServlet {

		private static final long serialVersionUID = 1L;
		private final ConfigurableApplicationContext cac;

		public JerseyConfiguration(ConfigurableApplicationContext cac) {
			this.cac = cac;
		}

		@Override
		protected void initiate(ResourceConfig rc, WebApplication wa) {
			// May throw RuntimeException
			wa.initiate(rc, new SpringComponentProviderFactory(rc, cac));
		}
	}

	// Initializes a Configurable Bean Context
	private AnnotationConfigWebApplicationContext bootstrapSpring(
			ServletContext servletContext) {

		final AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
		root.setServletContext(servletContext); // registers the servlet context
		root.register(SpringConfiguration.class); // registers our configuration
		root.refresh(); // refreshes all beans

		return root;
	}

	private void registerJerseyServlet(final ServletContext servletContext,
			final AnnotationConfigWebApplicationContext root) {

		// Adds the Slightly modified Jersey Spring Servlet
		final ServletRegistration.Dynamic jerseyServlet = servletContext
				.addServlet(SpringServlet.class.getName(),
						new JerseyConfiguration(root));
		jerseyServlet.setLoadOnStartup(1);
		jerseyServlet.addMapping(JERSEY_SERVLET_MAPPING);

		jerseyServlet.setInitParameter(
				"com.sun.jersey.api.json.POJOMappingFeature",
				Boolean.TRUE.toString());
	}

	private void registerSecurityFilter(final ServletContext servletContext,
			final AnnotationConfigWebApplicationContext root) {

		final FilterRegistration.Dynamic shiroFilter = servletContext
				.addFilter("shiroFilter", new DelegatingFilterProxy(
						"shiroFilter", root));
		shiroFilter.setInitParameter("targetFilterLifecycle",
				Boolean.TRUE.toString());
		shiroFilter.addMappingForUrlPatterns(null, false,
				JERSEY_SERVLET_MAPPING);
	}
}