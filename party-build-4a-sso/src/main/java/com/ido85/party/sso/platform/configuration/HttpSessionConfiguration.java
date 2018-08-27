/**
 *
 */
package com.ido85.party.sso.platform.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ido85.party.sso.security.core.session.SpringSessionBackedSessionRegistry;
import com.ido85.party.sso.security.jakson2.SecurityJacksonModules;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HttpSessionStrategy;

import javax.inject.Inject;

/**
 * @author rongxj
 *
 */
@Configuration
@EnableRedisHttpSession(redisNamespace="iw-sso")
public class HttpSessionConfiguration implements BeanClassLoaderAware, EnvironmentAware {

	private ClassLoader loader;

	@Inject
	private RedisTemplate<String, String> redisTemplate;

	private RelaxedPropertyResolver propertyResolver;

//	@Value("${spring.session.cookie.domain}")
//	private String cookieDomain;

	@Value("${spring.session.cookie.path}")
	private String cookiePath;

	@Value("${spring.session.cookie.name:X-SESSION}")
	private String cookieName;



	@Bean
	public RedisTemplate<Object, Object> objectRedisTemplate(RedisConnectionFactory factory){
		RedisTemplate template = new RedisTemplate();
		template.setConnectionFactory(factory);
		template.setValueSerializer(new JdkSerializationRedisSerializer());
		template.afterPropertiesSet();
		return template;
	}
	
	@Bean
	public HttpSessionStrategy httpSessionStrategy(){
		CookieHttpSessionStrategy sessionStrategy = new CookieHttpSessionStrategy();
		sessionStrategy.setCookieSerializer(cookieSerializer());
		return sessionStrategy;
	}

	@Bean
	public CookieSerializer cookieSerializer(){
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();
		return serializer;
	}



	@Bean(name = "cacheManager")
	@Primary
	public CacheManager cacheManager(
			@SuppressWarnings("rawtypes") RedisTemplate redisTemplate) {
		return new RedisCacheManager(redisTemplate);
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate(
			RedisConnectionFactory factory) {
		StringRedisTemplate template = new StringRedisTemplate(factory);
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(
				Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.afterPropertiesSet();
		return template;
	}

//	@Bean
//	public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
//		return new GenericJackson2JsonRedisSerializer(objectMapper());
//	}

	/**
	 * Customized {@link ObjectMapper} to add mix-in for class that doesn't have default
	 * constructors
	 *
	 * @return the {@link ObjectMapper} to use
	 */
	ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModules(SecurityJacksonModules.getModules(this.loader));
		return mapper;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.loader = classLoader;
	}


	/**
	 * session 注册器
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public SessionRegistry sessionRegistry(FindByIndexNameSessionRepository sessionRepository){
		return new SpringSessionBackedSessionRegistry(sessionRepository, redisTemplate);
	}

	/**
	 * oauth session策略
	 * @return
	 */
	@Bean
	public SessionAuthenticationStrategy sessoinAuthenticationStrategy(@Qualifier("sessionRepository") FindByIndexNameSessionRepository sessionRepository){
		return new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry(sessionRepository));
	}

	private final static String SESSION_SERIALIZATION_ID = "party-build";

	@Inject
	private ApplicationContext appContext;

	@Bean
	public String overwriteSerializationId() {
		BeanFactory beanFactory = appContext.getAutowireCapableBeanFactory();
		((DefaultListableBeanFactory) beanFactory).setSerializationId(SESSION_SERIALIZATION_ID);
		return "overwritten";
	}

	@Override
	public void setEnvironment(Environment environment) {
		propertyResolver = new RelaxedPropertyResolver(environment);
		DefaultCookieSerializer cookieSerializer = (DefaultCookieSerializer)cookieSerializer();
//		cookieSerializer.setDomainName(cookieDomain);
		cookieSerializer.setCookiePath(cookiePath);
		cookieSerializer.setCookieName(cookieName);
	}
}
