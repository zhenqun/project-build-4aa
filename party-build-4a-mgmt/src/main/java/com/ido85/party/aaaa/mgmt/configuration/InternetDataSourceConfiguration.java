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
//@ComponentScan
@EnableTransactionManagement 
@EnableJpaRepositories(
		entityManagerFactoryRef="entityManagerFactoryInternet",  
    transactionManagerRef="transactionManagerInternet",  
    basePackages={"com.ido85.party.aaaa.mgmt.internet"}) 
public class InternetDataSourceConfiguration {

	@Value("${spring.InternetDataSource.username}")
	private String user;

	@Value("${spring.InternetDataSource.password}")
	private String password;

	@Value("${spring.InternetDataSource.url}")
	private String dataSourceUrl;

	@Value("${spring.InternetDataSource.dataSourceClassName}")
	private String dataSourceClassName;

	@Value("${spring.InternetDataSource.poolName}")
	private String poolName;

	@Value("${spring.InternetDataSource.connectionTimeout}")
	private int connectionTimeout;

	@Value("${spring.InternetDataSource.maxLifetime}")
	private int maxLifetime;

	@Value("${spring.InternetDataSource.maximumPoolSize}")
	private int maximumPoolSize;

	@Value("${spring.InternetDataSource.minimumIdle}")
	private int minimumIdle;

	@Value("${spring.InternetDataSource.idleTimeout}")
	private int idleTimeout;

	@Bean(name = "internetDataSource")
	public DataSource internetDataSource() {
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

	@Bean(name="entityManagerFactoryInternet")  
    public LocalContainerEntityManagerFactoryBean internetEntityManagerFactory(@Qualifier("internetDataSource")DataSource dataSource, JpaProperties jpaProperties) {  
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
//		jpaProperties.getProperties().put("hibernate." + "ejb.naming_strategy_delegator", "none");
		jpaProperties.getProperties().put("hibernate.dialect",
				"org.hibernate.dialect.PostgreSQLDialect");
		factory.setJpaPropertyMap(jpaProperties.getProperties());
		factory.setPackagesToScan("com.ido85.party.aaaa.mgmt.internet");
		factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		factory.setPersistenceUnitName("internet");
		factory.afterPropertiesSet();
		return factory;
    }
	
	@Bean(name = "transactionManagerInternet")
	@DependsOn("entityManagerFactoryInternet")
    PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactoryInternet") LocalContainerEntityManagerFactoryBean entityManagerFactoryInternet) {  
        return new JpaTransactionManager(entityManagerFactoryInternet.getObject());  
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
