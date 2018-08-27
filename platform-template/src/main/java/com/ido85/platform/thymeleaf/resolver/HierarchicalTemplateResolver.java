package com.ido85.platform.thymeleaf.resolver;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class HierarchicalTemplateResolver extends SpringResourceTemplateResolver {

    private ResolveMode resolveMode;

    public HierarchicalTemplateResolver() {
        this(ResolveMode.DEFAULT);
    }

    public HierarchicalTemplateResolver(ResolveMode resolveMode) {
        this.resolveMode = resolveMode;
    }

    public ResolveMode getResolveMode() {
        return resolveMode;
    }

    public void setResolveMode(ResolveMode resolveMode) {
        this.resolveMode = resolveMode;
    }

    @Override
    protected String computeResourceName(IEngineConfiguration configuration, String ownerTemplate, String template, String prefix, String suffix, boolean forceSuffix, Map<String, String> templateAliases, Map<String, Object> templateResolutionAttributes) {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attrs).getRequest();
        String area = request.getHeader("X-Area");
        if ((!template.startsWith("redirect:")) && (!template.startsWith("forward:"))) {
            if (!StringUtils.isEmptyOrWhitespace(area)) {
                if (resolveMode == ResolveMode.CUSTOMIZED) {
                    template = area + "/" + template;
                }
            }
        }

        return super.computeResourceName(configuration, ownerTemplate, template, prefix, suffix, forceSuffix, templateAliases, templateResolutionAttributes);
    }
}
