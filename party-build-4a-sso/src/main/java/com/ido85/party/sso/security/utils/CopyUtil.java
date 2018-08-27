package com.ido85.party.sso.security.utils;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CopyUtil {
	/**   */
	/**
	 * Copy properties of orig to dest Exception the Entity and Collection Type
	 * 
	 * @param dest
	 * @param orig
	 * @return the dest bean
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Object copyProperties(Object dest, Object orig) throws Exception {
		if (dest == null || orig == null) {
			return dest;
		}

		PropertyDescriptor[] destDesc = PropertyUtils.getPropertyDescriptors(dest);
		for (int i = 0; i < destDesc.length; i++) {
			Class destType = destDesc[i].getPropertyType();
			Class origType = PropertyUtils.getPropertyType(orig, destDesc[i].getName());
			if (destType != null && destType.equals(origType) && !destType.equals(Class.class)) {
				if (!Collection.class.isAssignableFrom(origType)) {
					try {
						Object value = PropertyUtils.getProperty(orig, destDesc[i].getName());
						PropertyUtils.setProperty(dest, destDesc[i].getName(), value);
					} catch (Exception ex) {
					}
				}
			}
		}

		return dest;

	}

	/**   */
	/**
	 * Copy properties of orig to dest Exception the Entity and Collection Type
	 * 
	 * @param dest
	 * @param orig
	 * @param ignores
	 * @return the dest bean
	 */
	@SuppressWarnings("rawtypes")
	public static Object copyProperties(Object dest, Object orig, String[] ignores) throws Exception {
		if (dest == null || orig == null) {
			return dest;
		}

		PropertyDescriptor[] destDesc = PropertyUtils.getPropertyDescriptors(dest);
		for (int i = 0; i < destDesc.length; i++) {
			if (contains(ignores, destDesc[i].getName())) {
				continue;
			}

			Class destType = destDesc[i].getPropertyType();
			Class origType = PropertyUtils.getPropertyType(orig, destDesc[i].getName());
			if (destType != null && destType.equals(origType) && !destType.equals(Class.class)) {
				if (!Collection.class.isAssignableFrom(origType)) {
					Object value = PropertyUtils.getProperty(orig, destDesc[i].getName());
					PropertyUtils.setProperty(dest, destDesc[i].getName(), value);
				}
			}
		}

		return dest;
	}

	static boolean contains(String[] ignores, String name) {
		boolean ignored = false;
		for (int j = 0; ignores != null && j < ignores.length; j++) {
			if (ignores[j].equals(name)) {
				ignored = true;
				break;
			}
		}
		return ignored;
	}

	/**
	 * 复制集合
	 * 
	 * @param <E>
	 * @param source
	 * @param destinationClass
	 * @return
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static <E> List<E> copyTo(List<?> source, Class<E> destinationClass)
			throws IllegalAccessException, InvocationTargetException, InstantiationException {
		if (source.size() == 0)
			return Collections.emptyList();
		List<E> res = new ArrayList<E>(source.size());
		for (Object o : source) {
			E e = destinationClass.newInstance();
			BeanUtils.copyProperties(e, o);
			res.add(e);
		}
		return res;
	}
	
	
	/**
	 * Copy properties of orig to dest Exception the Entity and Collection Type
	 * 
	 * @param dest
	 * @param orig
	 * @return the dest bean
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Object copyPropertiesNull(Object dest, Object orig) throws Exception {
		if (dest == null || orig == null) {
			return dest;
		}

		PropertyDescriptor[] destDesc = PropertyUtils.getPropertyDescriptors(dest);
		for (int i = 0; i < destDesc.length; i++) {
			Class destType = destDesc[i].getPropertyType();
			Class origType = PropertyUtils.getPropertyType(orig, destDesc[i].getName());
			if (destType != null && destType.equals(origType) && !destType.equals(Class.class)) {
				if (!Collection.class.isAssignableFrom(origType)) {
					try {
						Object destValue = PropertyUtils.getProperty(dest, destDesc[i].getName());
						if(destValue != null){
							continue;
						}
						Object value = PropertyUtils.getProperty(orig, destDesc[i].getName());
						if(value == null){
							continue;
						}
						PropertyUtils.setProperty(dest, destDesc[i].getName(), value);
					} catch (Exception ex) {
					}
				}
			}
		}

		return dest;

	}
}
