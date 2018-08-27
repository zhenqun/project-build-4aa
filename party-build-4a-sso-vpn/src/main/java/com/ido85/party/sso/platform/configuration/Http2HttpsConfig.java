package com.ido85.party.sso.platform.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.undertow.UndertowOptions;
import io.undertow.servlet.api.SecurityConstraint;
import io.undertow.servlet.api.SecurityInfo;
import io.undertow.servlet.api.TransportGuaranteeType;
import io.undertow.servlet.api.WebResourceCollection;

//@Configuration
public class Http2HttpsConfig {
	
	@Value("${server.http.port:9999}")
	private int httpPort;
	
	@Value("${server.https.port:9443}")
	private int httpsPort;
	

	@Bean
	UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory() {
		UndertowEmbeddedServletContainerFactory factory = new UndertowEmbeddedServletContainerFactory();
		factory.addBuilderCustomizers(
				builder -> {
					builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true);
					builder.addHttpListener(httpPort, "0.0.0.0");
				});
		factory.addDeploymentInfoCustomizers(deploymentInfo -> {
			deploymentInfo.addSecurityConstraint(new SecurityConstraint()
					.addWebResourceCollection(new WebResourceCollection()
							.addUrlPattern("/*"))
					.setTransportGuaranteeType(TransportGuaranteeType.CONFIDENTIAL)
					.setEmptyRoleSemantic(SecurityInfo.EmptyRoleSemantic.PERMIT))
					.setConfidentialPortManager(exchange -> httpsPort);
		});
		return factory;
	}
}
