package com.ido85.platform.configuration.thymeleaf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.util.StringUtils;
import com.ido85.platform.thymeleaf.properties.ThymeleafResolverProperties;
import com.ido85.platform.thymeleaf.properties.ThymeleafResolverProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@ConditionalOnMissingBean(name = "defaultTemplateResolver")
public class TemplateResolverConfiguration {
    @Autowired
    private ThymeleafResolverProperties properties;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public Collection<ITemplateResolver> templateResolvers() throws BeansException {
        Collection<ITemplateResolver> resolverBeans = new ArrayList<>();
        // 获取BeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        List<ThymeleafResolverProperty> resolvers = properties.getResolvers();
        if (null != resolvers) {
            for (ThymeleafResolverProperty resolver : resolvers) {
                String className = resolver.getClazz();
                if (StringUtils.isEmptyOrWhitespace(className)) {
                    continue;
                }
                Class clazz = null;
                try {
                    clazz = applicationContext.getClassLoader().loadClass(className);
                } catch (ClassNotFoundException e) {
                    log.error("Cannot find template resolver definition with class name: " + className);
                }
                if (clazz == null) {
                    continue;
                }
                // 创建bean信息
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
                builder.addPropertyValue("applicationContext", applicationContext);
                builder.addPropertyValue("resolveMode", resolver.getResolveMode());
                builder.addPropertyValue("prefix", resolver.getPrefix());
                builder.addPropertyValue("suffix", resolver.getSuffix());
                builder.addPropertyValue("templateMode", resolver.getMode());
                if (resolver.getEncoding() != null) {
                    builder.addPropertyValue("characterEncoding", resolver.getEncoding().name());
                }
                builder.addPropertyValue("cacheable", resolver.isCache());
                if (resolver.getTemplateResolverOrder() != null) {
                    builder.addPropertyValue("order", resolver.getTemplateResolverOrder());
                }
                builder.addPropertyValue("checkExistence", resolver.isCheckTemplateLocation());

                // 动态注册bean
                defaultListableBeanFactory.registerBeanDefinition(resolver.getName(), builder.getBeanDefinition());
            }
        }

        AnnotationConfigEmbeddedWebApplicationContext context = (AnnotationConfigEmbeddedWebApplicationContext) applicationContext;

        context.register(ITemplateResolver.class);
        if (!context.isActive()) {
            context.refresh();
        }
        return applicationContext.getBeansOfType(ITemplateResolver.class).values();
    }
}
