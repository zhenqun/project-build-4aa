package com.ido85.platform.configuration.thymeleaf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.MimeType;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.Servlet;
import java.util.LinkedHashMap;

@Configuration
@ConditionalOnClass({ Servlet.class })
@ConditionalOnWebApplication
@EnableConfigurationProperties({ThymeleafProperties.class})
public class ViewResolverConfiguration {
    @Autowired
    private ThymeleafProperties properties;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Bean
    @ConditionalOnMissingBean(name = "thymeleafViewResolver")
    @ConditionalOnProperty(name = "spring.thymeleaf.enabled", matchIfMissing = true)
    public ThymeleafViewResolver thymeleafViewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(this.templateEngine);
        resolver.setCharacterEncoding(this.properties.getEncoding().name());
        resolver.setContentType(appendCharset(this.properties.getContentType(),
                resolver.getCharacterEncoding()));
        resolver.setExcludedViewNames(this.properties.getExcludedViewNames());
        resolver.setViewNames(this.properties.getViewNames());
        // This resolver acts as a fallback resolver (e.g. like a
        // InternalResourceViewResolver) so it needs to have low precedence
        resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 5);
        resolver.setCache(this.properties.isCache());
        return resolver;
    }

    private String appendCharset(MimeType type, String charset) {
        if (type.getCharSet() != null) {
            return type.toString();
        }
        LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
        parameters.put("charset", charset);
        parameters.putAll(type.getParameters());
        return new MimeType(type, parameters).toString();
    }
}
