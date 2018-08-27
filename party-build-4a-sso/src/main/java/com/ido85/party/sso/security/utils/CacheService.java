package com.ido85.party.sso.security.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.context.annotation.DependsOn;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Cache工具类
 * 应用启动的时候可以用来加载数据=
 * @author ThinkGem
 */
@Named
@DependsOn("ehCacheManager")
public class CacheService {
	@Inject
	private  CacheManager cacheManager ;//= InstanceFactory.getInstance(CacheManager.class);//((CacheManager)SpringContextHolder.getBean("cacheManager"));

	private static final String SYS_CACHE = "sysCache";

	public static final String REST_SESSION_KEY = "_curr_rest_session_devices_";

	/**
	 * 获取SYS_CACHE缓存
	 * @param key
	 * @return
	 */
	public  Object get(String key) {
		return get(SYS_CACHE, key);
	}

	/**
	 * 写入SYS_CACHE缓存
	 * @param key
	 * @return
	 */
	public  void put(String key, Object value) {
		put(SYS_CACHE, key, value);
	}

	/**
	 * 从SYS_CACHE缓存中移除
	 * @param key
	 * @return
	 */
	public  void remove(String key) {
		remove(SYS_CACHE, key);
	}

	/**
	 * 获取缓存
	 * @param cacheName
	 * @param key
	 * @return
	 */
	public  Object get(String cacheName, String key) {
		Element element = getCache(cacheName).get(key);
		return element==null?null:element.getObjectValue();
	}

	/**
	 * 写入缓存
	 * @param cacheName
	 * @param key
	 * @param value
	 */
	public  void put(String cacheName, String key, Object value) {
		Element element = new Element(key, value);
		getCache(cacheName).put(element);
	}

	/**
	 * 从缓存中移除
	 * @param cacheName
	 * @param key
	 */
	public  void remove(String cacheName, String key) {
		getCache(cacheName).remove(key);
	}

	/**
	 * 从缓存中移除所有
	 * @param cacheName
	 */
	public void removeAll(String cacheName){
		getCache(cacheName).removeAll();
	}

	/**
	 * 获得一个Cache，没有则创建一个。
	 * @param cacheName
	 * @return
	 */
	private  Cache getCache(String cacheName){
		Cache cache = cacheManager.getCache(cacheName);
		if (cache == null){
			cacheManager.addCache(cacheName);
			cache = cacheManager.getCache(cacheName);
			cache.getCacheConfiguration().setEternal(true);
		}
		return cache;
	}

	public  CacheManager getCacheManager() {
		return cacheManager;
	}

}
