package com.ido85.platform.thymeleaf.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(
        prefix = "spring.thymeleaf"
)
public class ThymeleafResolverProperties {
    private List<ThymeleafResolverProperty> resolvers;
}
