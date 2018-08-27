package com.ido85.platform.configuration.thymeleaf;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "nz.net.ultraq.thymeleaf.LayoutDialect")
public class LayoutConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }
}
