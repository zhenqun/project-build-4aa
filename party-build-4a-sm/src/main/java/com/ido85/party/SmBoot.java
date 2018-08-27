package com.ido85.party;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;



/**
 * 
 * 短信服务 启动类
 *
 */
@SpringBootApplication
@EnableEurekaClient
public class SmBoot 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(SmBoot.class, args);
    }
}
