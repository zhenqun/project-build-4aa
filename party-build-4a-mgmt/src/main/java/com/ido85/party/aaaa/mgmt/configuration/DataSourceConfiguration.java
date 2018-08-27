package com.ido85.party.aaaa.mgmt.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.ido85.party.aaaa.mgmt.distribute.generator.DistributedIdGenerator;
import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 数据源设置
 * @author rongxj
 *
 */
@Configuration
@ComponentScan
@EnableTransactionManagement 
@EnableJpaRepositories(
		entityManagerFactoryRef="entityManagerFactoryPrimary",  
    transactionManagerRef="transactionManagerPrimary",  
    basePackages={"com.ido85.party.aaaa.mgmt.security"}) 
public class DataSourceConfiguration {

	@Value("${spring.primaryDataSource.username}")
	private String user;

	@Value("${spring.primaryDataSource.password}")
	private String password;

	@Value("${spring.primaryDataSource.url}")
	private String dataSourceUrl;

	@Value("${spring.primaryDataSource.dataSourceClassName}")
	private String dataSourceClassName;

	@Value("${spring.primaryDataSource.poolName}")
	private String poolName;

	@Value("${spring.primaryDataSource.connectionTimeout}")
	private int connectionTimeout;

	@Value("${spring.primaryDataSource.maxLifetime}")
	private int maxLifetime;

	@Value("${spring.primaryDataSource.maximumPoolSize}")
	private int maximumPoolSize;

	@Value("${spring.primaryDataSource.minimumIdle}")
	private int minimumIdle;

	@Value("${spring.primaryDataSource.idleTimeout}")
	private int idleTimeout;

	@Bean
	@Primary 
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
	
	@Bean(name="entityManagerFactoryPrimary")  
    @Primary 
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(DataSource dataSource, JpaProperties jpaProperties) {  
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
//		jpaProperties.getProperties().put("hibernate." + "ejb.naming_strategy_delegator", "none");
		jpaProperties.getProperties().put("hibernate.dialect",
				"org.hibernate.dialect.PostgreSQLDialect");
		factory.setJpaPropertyMap(jpaProperties.getProperties());
		factory.setPackagesToScan("com.ido85.party.aaaa.mgmt.security");
		factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		factory.setPersistenceUnitName("admin");
		factory.afterPropertiesSet();
		return factory;
    }
	
	@Bean(name = "transactionManagerPrimary")
	@DependsOn("entityManagerFactoryPrimary")
	@Primary
    PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactoryPrimary") LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary) {  
        return new JpaTransactionManager(entityManagerFactoryPrimary.getObject());  
    }
	
	@Bean
    public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
	    jpaVendorAdapter.setDatabase(Database.POSTGRESQL);
//	    jpaVendorAdapter.setGenerateDdl(true);
	    return jpaVendorAdapter;
//        return new HibernateJpaVendorAdapter();
    }
}
