package com.ido85.party.sso.platform.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.*;

import com.ido85.party.sso.distribute.generator.DistributedIdGenerator;
import com.ido85.party.sso.distribute.generator.IdGenerator;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * 数据源设置
 * @author rongxj
 *
 */
@Configuration
@ComponentScan
public class DataSourceConfiguration {

	@Value("${spring.datasource.username}")
	private String user;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${spring.datasource.url}")
	private String dataSourceUrl;

	@Value("${spring.datasource.dataSourceClassName}")
	private String dataSourceClassName;

	@Value("${spring.datasource.poolName}")
	private String poolName;

	@Value("${spring.datasource.connectionTimeout}")
	private int connectionTimeout;

	@Value("${spring.datasource.maxLifetime}")
	private int maxLifetime;

	@Value("${spring.datasource.maximumPoolSize}")
	private int maximumPoolSize;

	@Value("${spring.datasource.minimumIdle}")
	private int minimumIdle;

	@Value("${spring.datasource.idleTimeout}")
	private int idleTimeout;

	@Bean
	public DataSource primaryDataSource() {
		Properties dsProps = new Properties();
		dsProps.put("url", dataSourceUrl);
		dsProps.put("user", user);
		dsProps.put("password", password);
//		dsProps.put("prepStmtCacheSize", 250);
//		dsProps.put("prepStmtCacheSqlLimit", 2048);
//		dsProps.put("cachePrepStmts", Boolean.TRUE);
//		dsProps.put("useServerPrepStmts", Boolean.TRUE);

		Properties configProps = new Properties();
		configProps.put("dataSourceClassName", dataSourceClassName);
		configProps.put("poolName", poolName);
		configProps.put("maximumPoolSize", maximumPoolSize);
		configProps.put("minimumIdle", minimumIdle);
		configProps.put("minimumIdle", minimumIdle);
		configProps.put("connectionTimeout", connectionTimeout);
		configProps.put("idleTimeout", idleTimeout);
		configProps.put("dataSourceProperties", dsProps);

		HikariConfig hc = new HikariConfig(configProps);
		HikariDataSource ds = new HikariDataSource(hc);
		return ds;
	}
	
	/**
	 * 分布式ID生成器
	 * @return
	 */
	@Bean
	public IdGenerator idGenerator(){
		return new DistributedIdGenerator(4);
	}

	@Primary
	public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(DataSource dataSource, JpaProperties jpaProperties) {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
//		jpaProperties.getProperties().put("hibernate." + "ejb.naming_strategy_delegator", "none");
		jpaProperties.getProperties().put("hibernate.dialect",
				"org.hibernate.dialect.PostgreSQLDialect");
		factory.setJpaPropertyMap(jpaProperties.getProperties());
		factory.setPackagesToScan("com.ido85.party.sso");
		factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		factory.setPersistenceUnitName("admin");
		factory.afterPropertiesSet();
		return factory;
	}
}
