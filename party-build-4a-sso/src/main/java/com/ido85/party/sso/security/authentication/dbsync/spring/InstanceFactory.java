/**
 * 
 */
package com.ido85.party.sso.security.authentication.dbsync.spring;

import javax.inject.Named;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

/**
 * spring 托管bean管理
 * @author rongxj
 *
 */
@Named
public class InstanceFactory implements ApplicationContextAware{// 在这里定义了事件的触发
	/**
	 *     这里只要实现了 ApplicationContextAware 就会自动注入spring 的上下文。
	 */
	private static ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext context) {
		applicationContext = context;
	}
	
	/**
	 * 获取托管bean
	 * @param beanType
	 * @return
	 */
	public static <T> T getInstance(Class<T> beanType){
		return applicationContext.getBean(beanType);
	}
	
	/**
	 * 根据beanName获取托管bean
	 * @param beanName
	 * @return
	 */
	public static Object getInstance(String beanName){
		return applicationContext.getBean(beanName);
	}
	
	/**
	 * 根据bean类型和beanname获取托管bean
	 * @param beanType
	 * @param beanName
	 * @return
	 */
	public static <T> T getInstance(Class<T> beanType, String beanName){
		return applicationContext.getBean(beanName, beanType);
	}
	
	/**
	 * 获取spring上下文
	 * @return ApplicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	/**
	 * 获取环境信息
	 * @return Environment
	 */
	public static Environment getEnvironment() {
		return applicationContext.getEnvironment();
	}
	
	/** 
	 * 
	 * @Title: 发布事件 
	 * @param event  void    返回类型
	 *   https://blog.csdn.net/wgw335363240/article/details/7202320 定义了
	 *   Spring的ApplicationEvent的使用
	 */
	public static void publishEvent(Object event){
		applicationContext.publishEvent(event);
	}
}
