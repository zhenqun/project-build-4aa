package com.ido85.party.sso.platform.data.datasource;
/**
 * 在方法上使用，用于指定使用哪个数据源
 * @Author kiky
 * @Date 2017/7/18
 */

import java.lang.annotation.*;


@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    String name();
}
