package com.ido85.platform.thymeleaf.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;

@Data
public class ThymeleafResolverProperty extends ThymeleafProperties {
    private String name;

    private String clazz;

    private String resolveMode;
}
