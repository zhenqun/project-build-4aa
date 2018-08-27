package com.ido85.party;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;



/**
 * 
 * ldap sync 启动类
 *
 */
@SpringBootApplication
@EnableEurekaClient
public class AuthenticationBoot 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(AuthenticationBoot.class, args);
    }
}
