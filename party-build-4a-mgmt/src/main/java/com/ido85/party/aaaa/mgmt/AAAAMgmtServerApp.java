package com.ido85.party.aaaa.mgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.ido85.party.aaaa.mgmt.security.servlet.ValidateCodeServlet;


/**
 * 统一登录server
 *
 */
@SpringBootApplication
//@EnableResourceServer
//@SessionAttributes("authorization")
//@EnableAuthorizationServer
@SessionAttributes("authorizationRequest")
@EnableEurekaClient
@ServletComponentScan(basePackageClasses = { ValidateCodeServlet.class })
@EnableAsync
public class AAAAMgmtServerApp 
{
    public static void main( String[] args )
    {
        SpringApplication.run(AAAAMgmtServerApp.class, args);
    }
    
//    @Bean
//    public EmbeddedServletContainerCustomizer containerCustomizer() {
//
//       return (container -> {
//            ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401");
//            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404");
//            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500");
//            container.addErrorPages(error401Page, error404Page, error500Page);
//       });
//    }
}