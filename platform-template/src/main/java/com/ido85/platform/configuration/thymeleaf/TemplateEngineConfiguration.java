package com.ido85.platform.configuration.thymeleaf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;
import com.ido85.platform.thymeleaf.cache.HierarchicalCacheManager;

import java.util.Collection;
import java.util.Collections;

@Configuration
@ConditionalOnMissingBean(SpringTemplateEngine.class)
public class TemplateEngineConfiguration {

    @Autowired(required = false)
    private final Collection<IDialect> dialects = Collections.emptySet();

    private HierarchicalCacheManager hierarchicalCacheManager() {
        return new HierarchicalCacheManager();
    }

    @Bean
    @DependsOn(value = "templateResolvers")
    public SpringTemplateEngine templateEngine(Collection<ITemplateResolver> templateResolvers) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        for (ITemplateResolver templateResolver : templateResolvers) {
            engine.addTemplateResolver(templateResolver);
        }
        for (IDialect dialect : this.dialects) {
            engine.addDialect(dialect);
        }
        engine.setCacheManager(hierarchicalCacheManager());
        return engine;
    }
}
