package br.com.victorolinasc.configuration;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Apache Shiro Programmatic configuration using its integration with Spring.
 * This configuration is loaded in the Web App Initializer.
 * <p>
 * It uses an in memory Basic authentication mechanism.
 * <p>
 * 
 * @author victor
 */
@Configuration
public class SecurityConfiguration {

	private static final Logger LOG = LoggerFactory
			.getLogger(SecurityConfiguration.class);

	public SecurityConfiguration() {
		LOG.info("Initializing Apache Shiro Security configuration");
	}

	/**
	 * Constructs a {@link BeanPostProcessor} for security related checks.
	 * 
	 * @return LifecycleBeanPostProcessor
	 */
	@Bean
	public BeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	/**
	 * Constructs a default web security manager
	 * 
	 * @return DefaultWebSecurityManager
	 */
	@Bean
	public org.apache.shiro.mgt.SecurityManager securityManager() {
		return new DefaultWebSecurityManager(myRealm());
	}

	/**
	 * Constructs a Shiro filter that matches the name of the fitler declared in
	 * the web configuration.
	 * <p>
	 * 
	 * @return ShiroFilterFactoryBean
	 */
	@Bean(name = ApplicationConfiguration.SHIRO_FILTER_NAME)
	public ShiroFilterFactoryBean shiroFilter() {

		final ShiroFilterFactoryBean filter = new ShiroFilterFactoryBean();
		final Map<String, String> filterDefs = new LinkedHashMap<>();

		final DefaultFilter filterType = DefaultFilter.authcBasic;
		final String filterPath = "/**";

		LOG.info(String.format("Configuring filter %s with path %s", filterType
				.getFilterClass().getName(), filterPath));
		filterDefs.put(filterPath, filterType.name());

		filter.setFilterChainDefinitionMap(filterDefs);
		filter.setSecurityManager(securityManager());
		return filter;
	}

	/**
	 * Constructs a DefaultAdvisorAutoProxyCreator that depends on
	 * lifecycleBeanPostProcessor. This is important to set the order on which
	 * interception occurs. Security must come before any other interception.
	 * <p>
	 * 
	 * @return DefaultAdvisorAutoProxyCreator
	 */
	@Bean
	@DependsOn("lifecycleBeanPostProcessor")
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		return new DefaultAdvisorAutoProxyCreator();
	}

	/**
	 * Constructs an AuthorizationAttributeSourceAdvisor.
	 * 
	 * @return AuthorizationAttributeSourceAdvisor
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
		final AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
		aasa.setSecurityManager(securityManager());
		return aasa;
	}

	// simple InMemory Realm with one user that has two roles.
	private Realm myRealm() {

		LOG.warn("Creating in memory simple realm");

		final SimpleAccountRealm simpleAccountRealm = new SimpleAccountRealm(
				"myRealm");
		simpleAccountRealm.addAccount("admin", "admin", "ROLE_USER",
				"ROLE_ADMIN");
		return simpleAccountRealm;
	}
}