package br.com.victorolinasc.configuration;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Repository module configuration. It basically enables transaction management
 * on a standard JPA EntityManager session.
 * <p>
 * This configuration uses a properties file that must be place in the root
 * classpath.
 * 
 * @author victor
 * 
 * @see Configuration
 * @see EnableTransactionManagement
 * @see PropertySource
 */
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:jdbc.properties")
public class RepositoryConfiguration {

	@Autowired
	private Environment env;

	/**
	 * Configures an Apache Commons Basic Data Source according to the
	 * properties appointed down in jdbc.properties file
	 * 
	 * 
	 * @return the basic data source configured by the jdbc.properties file.
	 */
	@Bean
	public DataSource dataSource() {

		final BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(env.getProperty("jdbc.driverClassName"));
		bds.setUrl(env.getProperty("jdbc.url"));
		bds.setUsername(env.getProperty("jdbc.username"));
		bds.setPassword(env.getProperty("jdbc.password"));

		final Resource schema = new ClassPathResource("schema.sql");
		final Resource data = new ClassPathResource("data.sql");

		if (!schema.exists() && !data.exists())
			return bds;

		final ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();

		if (schema.exists())
			rdp.addScript(schema);

		if (data.exists())
			rdp.addScript(data);

		DatabasePopulatorUtils.execute(rdp, bds);
		return bds;
	}

	/**
	 * Configures a local EntityManager Factory container using the properties
	 * set on jdbc.properties.
	 * 
	 * @return A local EntityManager Factory container
	 */
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

		final LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(dataSource());
		emf.setPersistenceUnitName(env.getProperty("persistence.unit.name"));
		emf.setPackagesToScan(env.getProperty("jpa.scan.packages"));

		final HibernateJpaVendorAdapter hjva = new HibernateJpaVendorAdapter();
		hjva.setDatabase(Database.valueOf(env.getProperty("spring.database")));

		emf.setJpaVendorAdapter(hjva);
		return emf;
	}

	/**
	 * Configures a local EntityManager Factory container.
	 * 
	 * @return A local EntityManager Factory container
	 */
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new JpaTransactionManager(entityManagerFactory().getObject());
	}
}