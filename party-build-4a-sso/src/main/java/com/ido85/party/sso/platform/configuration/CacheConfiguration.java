/**
 *
 */
package com.ido85.party.sso.platform.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * 缓存配置（ehcache）
 *
 * @author rongxj
 *
 */
@Configuration
public class CacheConfiguration extends CachingConfigurerSupport {


	/**
	 * 默认 缓存
	 *
	 * @param bean
	 * @return
	 */
	@Bean(name = "ehCacheManager")
	public CacheManager cacheManager(EhCacheManagerFactoryBean bean) {
		return new EhCacheCacheManager(bean.getObject());
	}

	/**
	 * ehcache 管理器
	 */
	@Bean(name = "appEhCacheCacheManager")
	public EhCacheCacheManager ehCacheCacheManager(
			EhCacheManagerFactoryBean bean) {
		return new EhCacheCacheManager(bean.getObject());
	}

	/**
	 * 据shared与否的设置,Spring分别通过CacheManager.create()或new
	 * CacheManager()方式来创建一个ehcache基地.
	 */
	@Bean(name = "ehCacheManagerFactory")
	public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
		EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
		cacheManagerFactoryBean.setConfigLocation(new ClassPathResource(
				"conf/ehcache-local.xml"));
		cacheManagerFactoryBean.setShared(true);
		return cacheManagerFactoryBean;
	}

	/*
	 * @Bean
	 *
	 * @DependsOn("keywordService") public Object getKwywords(KeywordService
	 * keywordService){ List<Keyword> list = keywordService.getKeyWords();
	 * if(null!=list&&list.size()>0){ for(Keyword k : list){
	 * CacheUtils.put(k.getKeywordName().hashCode()+"", k.getKeywordId()); } }
	 *
	 * System.out.println("-------------------------注入完成------------------");
	 * return new Object(); }
	 */
}
