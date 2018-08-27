package com.ido85.party.sso.platform.configuration;

import com.ido85.party.sso.platform.configuration.config.DataSourceConfig;
import com.ido85.party.sso.platform.data.datasource.DynamicDataSource;
import com.ido85.party.sso.platform.data.datasource.DynamicDataSourceContextHolder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ido85.party.sso.distribute.generator.DistributedIdGenerator;
import com.ido85.party.sso.distribute.generator.IdGenerator;

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
    basePackages={"com.ido85.party.sso.security"}) 
public class DataSourceConfiguration {

	@Inject
	DataSourceConfig dataSourceConfig;

	public DataSource dataSource() {

		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		dynamicDataSource.setDefaultTargetDataSource(dataSourceConfig.getMasterDataSource());
		Map<String, DataSource> customerDataSources = dataSourceConfig.getCustomerDataSource();
		Map<Object, Object> targetDataSource = new HashMap<>();
		customerDataSources.keySet().forEach(key->{
			targetDataSource.put(key, customerDataSources.get(key));
			DynamicDataSourceContextHolder.dataSourceIds.add(key);
		});
		targetDataSource.put("master", dataSourceConfig.getMasterDataSource());
		DynamicDataSourceContextHolder.dataSourceIds.add("master");
		dynamicDataSource.setTargetDataSources(targetDataSource);
		return dynamicDataSource;
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
		factory.setPackagesToScan("com.ido85.party.sso.security");
		factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		factory.setPersistenceUnitName("system");
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
