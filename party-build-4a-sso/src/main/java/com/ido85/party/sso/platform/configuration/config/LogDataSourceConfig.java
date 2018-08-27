package com.ido85.party.sso.platform.configuration.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Author kiky
 * @Date 2017/7/18
 */
@Configuration
public class LogDataSourceConfig {



    @Value("${spring.logdatasource.username}")
    private String user;

    @Value("${spring.logdatasource.password}")
    private String password;

    @Value("${spring.logdatasource.url}")
    private String dataSourceUrl;
//	@Value("${spring.primaryDataSource.databasename}")
//	private String databaseame;

    @Value("${spring.logdatasource.dataSourceClassName}")
    private String dataSourceClassName;

    @Value("${spring.logdatasource.poolName}")
    private String poolName;

    @Value("${spring.logdatasource.connectionTimeout}")
    private int connectionTimeout;

    @Value("${spring.logdatasource.maxLifetime}")
    private int maxLifetime;

    @Value("${spring.logdatasource.maximumPoolSize}")
    private int maximumPoolSize;

    @Value("${spring.logdatasource.minimumIdle}")
    private int minimumIdle;

    @Value("${spring.logdatasource.idleTimeout}")
    private int idleTimeout;

//    @Value("${spring.primaryDatasource.sqlScriptEncoding}")
//    private String encode;

//    @Value("${spring.jpa.multitenant.default-id}")
//    private String defaultTenantId;

    private DataSource masterDataSource;
    private Map<String, DataSource> customerDataSource = new HashMap<>();


    /**
     * 创建主数据源
     * @return
     */
    public DataSource getMasterDataSource(){
        if(masterDataSource != null){
            return masterDataSource;
        }
        Properties dsProps = new Properties();
        dsProps.put("url", dataSourceUrl);
        dsProps.put("user", user);
        dsProps.put("password", password);
//		dsProps.put("databaseName",databaseame);
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


    @Inject
    CustomerDataSourceProperties customerDataSourceProperties;

    /**
     * 创建数据源
     * @return
     */
    public Map<String, DataSource> getCustomerDataSource(){
        if(customerDataSource.isEmpty()){
            Map<String, Map<String, String>> props = customerDataSourceProperties.getDatasources();
            props.keySet().forEach(key->{
                customerDataSource.put(key, buildDataSource(props.get(key)));
            });
        }
        return customerDataSource;
    }

    private DataSource buildDataSource(Map<String, String> dsMap){
        Properties dsProps = new Properties();
        dsProps.put("url", dsMap.get("url"));
        dsProps.put("user", dsMap.get("username"));
        dsProps.put("password", dsMap.get("password"));

        Properties configProps = new Properties();
        configProps.put("dataSourceClassName", dsMap.get("dataSourceClassName"));
        configProps.put("poolName", dsMap.get("poolName"));
        configProps.put("maximumPoolSize", dsMap.get("maximumPoolSize"));
        configProps.put("minimumIdle", dsMap.get("minimumIdle"));
        configProps.put("maxLifetime", dsMap.get("maxLifetime"));
        configProps.put("connectionTimeout", dsMap.get("connectionTimeout"));
        configProps.put("idleTimeout", dsMap.get("idleTimeout"));
        configProps.put("dataSourceProperties", dsProps);

        HikariConfig hc = new HikariConfig(configProps);
        HikariDataSource ds = new HikariDataSource(hc);
        return ds;
    }


    @Configuration
    @ConfigurationProperties(prefix = "customer")
    @Data
    public static class CustomerDataSourceProperties{
        private Map<String, Map<String, String>> datasources = new HashMap<>();
    }
}
