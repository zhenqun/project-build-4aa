package com.ido85.party.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.ido85.party.sso.security.servlet.ValidateCodeServlet;

import java.sql.SQLOutput;

/**
 * 统一登录server
 *
 */
@SpringBootApplication(scanBasePackages = {"com.ido85"}, exclude = ThymeleafAutoConfiguration.class)
//@EnableResourceServer
//@SessionAttributes("authorization")
//@EnableAuthorizationServer
@SessionAttributes("authorizationRequest")
@EnableEurekaClient
@ServletComponentScan(basePackageClasses = { ValidateCodeServlet.class })
@EnableAsync
public class SsoServerApp 
{


    public static void main( String[] args )
    {
        // SpringApplication aap=new SpringApplication(SsoServerApp.class);

        SpringApplication.run(SsoServerApp.class, args);


    }
}