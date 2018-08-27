package com.ido85.party.sso.security.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class ListUntils {
	/***
	 * list转Map
	 * 
	 * @param <E>
	 * @param list
	 *            需要转换的集合 不能有重复的元素
	 * @param filedName（多个以,分割）
	 *            相当于Map中的key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E> Map<String, E> listToMap(List<E> list, String filedName) throws Exception {
		if (null == filedName || "".equals(filedName)) {
			throw new Exception("入参不能为空");
		}
		if (null == list) {
			throw new Exception("list入参不能为空");
		}
		String[] filedNameArr = filedName.split(",");// 字段名称
		List<String> nameListtemp = new ArrayList<String>();// 方法名称
		for (String s : filedNameArr) {
			if (null == s || "".equals(s)) {
				throw new Exception("入参不能为空");
			}
			String name = "get" + StringUtils.toUpperCase(s);
			nameListtemp.add(name);
		}
		final List<String> nameList = nameListtemp;
		Map<String, E> returnMap = new HashMap<String, E>();
		if (list.get(0) instanceof Map) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> ma = (Map<String, Object>) list.get(i);
				String returnStr = "";
				for (int j = 0; j < filedNameArr.length; j++) {
					/*
					 * System.out.println(list.get(i).getClass()); Class<?> c =
					 * list.get(i).getClass(); Method m = c.getMethod("get");
					 */
					returnStr = returnStr + ma.get(filedNameArr[j]).toString();
					if (j < filedNameArr.length - 1) {
						returnStr = returnStr + ",";
					}
				}
				returnMap.put(returnStr, list.get(i));
			}
		} else {
			returnMap = Maps.uniqueIndex(list, new Function<E, String>() {
				public String apply(E site) {
					String returnStr = "";
					List<Method> methodList = new ArrayList<Method>();
					try {
						for (int i = 0; i < nameList.size(); i++) {
							methodList.add(site.getClass().getMethod(nameList.get(i)));
						}
						if (methodList.size() > 0) {
							for (int i = 0; i < methodList.size(); i++) {
								returnStr = returnStr + (methodList.get(i).invoke(site) == null ? ""
										: methodList.get(i).invoke(site));
								if (i < methodList.size() - 1) {
									returnStr = returnStr + ",";
								}
							}
						}
						return returnStr;
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return returnStr;
				}
			});
		}
		if (null == returnMap) {
			return null;
		}
		return returnMap;
	}

	/***
	 * list 内部去重
	 * 
	 * @param list
	 * @param filedName
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <E> List<E> deWeight(List<E> list, String filedName) throws Exception {
		if (null == list || list.size() <= 0) {
			throw new Exception("入参不能空");
		}
		if (null == filedName || "".equals(filedName)) {
			throw new Exception("入参不能空");
		}
		Map<String, E> map = new HashMap<String, E>();
		for (E s : list) {
			String key = "";
			String[] filedNameArr = filedName.split(",");
			/**
			 * 合并key
			 */
			for (int i = 0; i < filedNameArr.length; i++) {
				String methodName = "get" + filedNameArr[i];
				Class<?> cl = s.getClass();
				Method me = cl.getMethod(methodName);
				if (null == me.invoke(s)) {
					key = key + "";
				} else {
					key = key + me.invoke(s).toString();
				}
				if (i < filedNameArr.length - 1) {
					key = key + ",";
				}
			}
			map.put(key, s);
		}
		List<E> returnList = new ArrayList<E>();
		for (Object o : map.values()) {
			returnList.add((E) o);
		}
		return returnList;
	}

	/***
	 * @param list
	 * @param filedName
	 *            去重的字段
	 * @param type
	 *            0 保存第一个 1保存最后一个
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <E> List<E> deWeight1(List<E> list, String filedName, String type) throws Exception {
		if (null == list || list.size() <= 0) {
			throw new Exception("入参不能空");
		}
		if (null == filedName || "".equals(filedName)) {
			throw new Exception("入参不能空");
		}
		Map<String, E> map = new HashMap<String, E>();
		boolean isMap = (list.get(0) instanceof Map);
		for (E s : list) {
			String key = "";
			String[] filedNameArr = filedName.split(",");
			/**
			 * 合并key
			 */
			for (int i = 0; i < filedNameArr.length; i++) {
				if (isMap) {// 是Map类型
					Map<String, Object> m = (Map<String, Object>) s;
					key = key + m.get(filedNameArr[i]).toString();
				} else {
					String methodName = "get" + filedNameArr[i];
					Class<?> cl = s.getClass();
					Method me = cl.getMethod(methodName);
					if (null == me.invoke(s)) {
						key = key + "";
					} else {
						key = key + me.invoke(s).toString();
					}
				}
				if (i < filedNameArr.length - 1) {
					key = key + ",";
				}
			}
			if ("0".equals(type)) {// 保存第一个
				if (!map.containsKey(key)) {// 有这个key
					map.put(key, s);
				}
			} else {
				map.put(key, s);
			}
		}
		List<E> returnList = new ArrayList<E>();
		for (Object o : map.values()) {
			returnList.add((E) o);
		}
		return returnList;
	}

	/**
	 * 去重list<String>中重复的数据
	 * 
	 * @param list
	 * @return
	 */
	public static List<String> duplicateRemoval(List<String> list) {
		if (null == list || list.size() <= 0) {
			return null;
		}
		List<String> ListTemp = new ArrayList<String>();
		for (String s : list) {
			s = s.trim();
			if (!ListTemp.contains(s)) {
				ListTemp.add(s);
			}
		}
		return ListTemp;
	}

	/**
	 * 判断list<String>有无重复的数据，没有则返回true 有则返回false
	 * 
	 * @param list
	 * @return
	 */
	public static boolean duplicateCheck(List<String> list) {
		if (null == list || list.size() <= 0) {
			return true;
		}
		List<String> listTemp = new ArrayList<>();
		for (String s : list) {
			s = s.trim();
			if (!listTemp.contains(s)) {
				listTemp.add(s);
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 去重list<object>中重复的数据 对象中需要实现equals 和hashcode两个方法
	 * 
	 * @param list
	 * @return
	 */
	public static <E> List<E> duplicateList(List<E> list) {
		if (null == list || list.size() <= 0) {
			return null;
		}
		List<E> ListTemp = new ArrayList<E>(new HashSet<E>(list));
		return ListTemp;
	}

	/**
	 * 判断 list<String> 是否存在重复数据 存在重复返回true
	 * 
	 * @param list
	 * @return
	 */
	public static <E> boolean isObjectRepeat(List<E> list) {
		if (null == list || list.size() <= 0) {
			return false;
		}
		List<E> temp = new ArrayList<E>();
		for (E s : list) {
			if (temp.contains(s)) {
				return true;
			} else {
				temp.add(s);
			}
		}
		return false;
	}

	/***
	 * 将List<String> 转成字符串 以str分割
	 * 
	 * @param list
	 * @param Str
	 *            分隔符
	 * @return
	 */
	public static String listToStr(List<String> list, String str) {
		if (null == list || list.size() <= 0) {
			return null;
		}
		String returnStr = "";
		String s = (str == null || "".equals(str)) ? "," : str;
		for (int i = 0; i < list.size(); i++) {
			returnStr = returnStr + list.get(i);
			if (i < list.size() - 1) {
				returnStr = returnStr + s;
			}
		}
		return returnStr;
	}

	/***
	 * 将List<String> 转成字符串 以str分割
	 * 
	 * @param list
	 * @param Str
	 *            分隔符
	 * @return
	 */
	public static String intlistToStr(List<Integer> list, String str) {
		if (null == list || list.size() <= 0) {
			return null;
		}
		String returnStr = "";
		String s = (str == null || "".equals(str)) ? "," : str;
		for (int i = 0; i < list.size(); i++) {
			returnStr = returnStr + list.get(i) + "";
			if (i < list.size() - 1) {
				returnStr = returnStr + s;
			}
		}
		return returnStr;
	}

	/***
	 * 将List<String> 转成字符串 以str分割
	 * 
	 * @param list
	 * @param Str
	 *            分隔符
	 * @return
	 */
	public static String datelistToStr(List<Date> list, String str) {
		if (null == list || list.size() <= 0) {
			return null;
		}
		String returnStr = "";
		String s = (str == null || "".equals(str)) ? "," : str;
		for (int i = 0; i < list.size(); i++) {
			returnStr = returnStr + "'" + DateUtils.formatDate(list.get(i), "yyyy-MM-dd") + "'";
			if (i < list.size() - 1) {
				returnStr = returnStr + s;
			}
		}
		return returnStr;
	}

	/**
	 * @param <E>
	 * @param targetList
	 *            目标排序List
	 * @param sortField
	 *            排序字段(实体类属性名) 必须是数字
	 * @param sortMode
	 *            排序方式（asc or desc）
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <E> void sort(List<E> targetList, final String sortField, final String sortMode) {
		Collections.sort(targetList, new Comparator() {
			public int compare(Object obj1, Object obj2) {
				int retVal = 0;
				if (obj1 instanceof Map || obj2 instanceof Map) {
					try {
						int str1 = Integer.parseInt(((Map) obj1).get(sortField).toString());
						int str2 = Integer.parseInt(((Map) obj2).get(sortField).toString());
						if (sortMode != null && "desc".equals(sortMode)) {
							retVal = str2 - str1;
						} else { // asc
							retVal = str1 - str2;
						}
					} catch (Exception e) {
						throw new RuntimeException();
					}
				} else {
					try {
						// 首字母转大写
						String newStr = sortField.substring(0, 1).toUpperCase() + sortField.replaceFirst("\\w", "");
						String methodStr = "get" + newStr;
						Method method1 = ((E) obj1).getClass().getMethod(methodStr, null);
						Method method2 = ((E) obj2).getClass().getMethod(methodStr, null);
						int str1 = Integer.parseInt(method1.invoke(((E) obj1), null).toString());
						int str2 = Integer.parseInt(method2.invoke(((E) obj2), null).toString());
						if (sortMode != null && "desc".equals(sortMode)) {
							retVal = str2 - str1;
						} else { // asc
							retVal = str1 - str2;
						}
					} catch (Exception e) {
						throw new RuntimeException();
					}
				}
				return retVal;
			}
		});
	}

	/**
	 * 分割List
	 * 
	 * @param list
	 *            待分割的list
	 * @param pageSize
	 *            每段list的大小
	 * @return List<<List<T>>
	 */
	public static <T> List<List<T>> splitList(List<T> list, int pageSize) {
		if (null == list || list.size() <= 0) {
			return null;
		}
		int listSize = list.size();// list的大小
		int page = (listSize + (pageSize - 1)) / pageSize;// 页数
		List<List<T>> listArray = new ArrayList<List<T>>();// 创建list数组
															// ,用来保存分割后的list
		for (int i = 0; i < page; i++) {// 按照数组大小遍历
			List<T> subList = new ArrayList<T>();// 数组每一位放入一个分割后的list
			for (int j = 0; j < listSize; j++) {// 遍历待分割的list
				int pageIndex = ((j + 1) + (pageSize - 1)) / pageSize; // 当前记录的页码(第几页)
				if (pageIndex == (i + 1)) { // 当前记录的页码等于要放入的页码时
					subList.add(list.get(j)); // 放入list中的元素到分割后的list(subList)
				}
				if ((j + 1) == ((j + 1) * pageSize)) {// 当放满一页时退出当前循环
					break;
				}
			}
			listArray.add(subList);// 将分割后的list放入对应的数组的位中
		}
		return listArray;
	}

	/**
	 * 判断 list<String> 是否存在重复数据 存在重复返回true
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isRepeat(List<String> list) {
		if (null == list || list.size() <= 0) {
			return false;
		}
		List<String> temp = new ArrayList<String>();
		for (String s : list) {
			if (temp.contains(s)) {
				return true;
			} else {
				temp.add(s);
			}
		}
		return false;
	}

	/**
	 * @param list
	 * @return
	 */
	public static Map<String, List<Map<String, Object>>> mergeListMap(List<Map<String, Object>> list) {
		if (null == list || list.size() <= 0) {
			return null;
		}
		Map<String, List<Map<String, Object>>> retrunMap = new HashMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> m : list) {
			if (m.get("site_id") != null) {
				if (retrunMap.containsKey(m.get("site_id").toString())) {// 已经存在
																			// key
					retrunMap.get(m.get("site_id").toString()).add(m);
				} else {
					List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
					temp.add(m);
					retrunMap.put(m.get("site_id").toString(), temp);
				}
			}
		}
		return retrunMap;
	}

	/**
	 * 将List泛型为string类型的集合转化为List泛型为Integer类型
	 * 
	 * @param list
	 * @return
	 */
	public static List<Integer> listStrToInteger(List<String> list) throws Exception {
		if (null == list || list.size() <= 0) {
			return null;
		}
		List<Integer> res = new ArrayList<Integer>();
		for (String string : list) {
			if (null != string && !"".equals(string)) {
				res.add(StringUtils.toInteger(string));
			}
		}
		return res;
	}

	/**
	 * 将List泛型为Integer类型的集合转化为List泛型为String类型
	 * 
	 * @param list
	 * @return
	 */
	public static List<String> listIntegerToStr(List<Integer> list) throws Exception {
		if (null == list || list.size() <= 0) {
			return null;
		}
		List<String> res = new ArrayList<String>();
		for (Integer item : list) {
			if (null != item) {
				res.add(item + "");
			}
		}
		return res;
	}

	/***
	 * 判断是否为空 为空返回真
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isNull(List<?> list) {
		return null == list || "".equals(list) || list.size() <= 0;
	}

	/***
	 * 判断是否为空,不为空返回真
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isNotNull(List<?> list) {
		return null != list && !"".equals(list) && list.size() > 0;
	}

	/***
	 * 去重重复的时间
	 * 
	 * @param list
	 * @return
	 */
	public static List<Date> deWeight(List<Date> list) {
		List<Date> temp = new ArrayList<>();
		int i = 0;
		if (ListUntils.isNotNull(list)) {
			for (Date date : list) {
				i = 0;
				for (Date date1 : temp) {
					if (DateUtils.isSameDate(date, date1)) {
						i++;
						break;
					}
				}
				if(i==0){
					temp.add(date);
				}
			}
		}
		return temp;

	}

}
