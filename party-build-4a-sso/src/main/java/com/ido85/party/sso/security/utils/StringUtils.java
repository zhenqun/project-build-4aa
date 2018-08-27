/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.ido85.party.sso.security.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//import org.springframework.web.servlet.LocaleResolver;

import com.google.common.collect.Lists;


/**
 * 字符串工具类, 继承org.apache.commons.lang3.StringUtils类
 * @author ThinkGem
 * @version 2013-05-22
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

	private static final char SEPARATOR = '_';
	private static final String CHARSET_NAME = "UTF-8";

	/**
	 * 转换为字节数组
	 * @param str
	 * @return
	 */
	public static byte[] getBytes(String str){
		if (str != null){
			try {
				return str.getBytes(CHARSET_NAME);
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}else{
			return null;
		}
	}

	/**
	 * 转换为字节数组
	 * @param str
	 * @return
	 */
	public static String toString(byte[] bytes){
		try {
			return new String(bytes, CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			return EMPTY;
		}
	}

	/**
	 * 是否包含字符串
	 * @param str 验证字符串
	 * @param strs 字符串组
	 * @return 包含返回true
	 */
	public static boolean inString(String str, String... strs){
		if (str != null){
			for (String s : strs){
				if (str.equals(trim(s))){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 替换掉HTML标签方法
	 */
	public static String replaceHtml(String html) {
		if (isBlank(html)){
			return "";
		}
		String regEx = "<.+?>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("");
		return s;
	}

	/**
	 * 替换为手机识别的HTML，去掉样式及属性，保留回车。
	 * @param html
	 * @return
	 */
	public static String replaceMobileHtml(String html){
		if (html == null){
			return "";
		}
		return html.replaceAll("<([a-z]+?)\\s+?.*?>", "<$1>");
	}

	/**
	 * 替换为手机识别的HTML，去掉样式及属性，保留回车。
	 * @param txt
	 * @return
	 */
	public static String toHtml(String txt){
		if (txt == null){
			return "";
		}
		return replace(replace(Encodes.escapeHtml(txt), "\n", "<br/>"), "\t", "&nbsp; &nbsp; ");
	}

	/**
	 * 缩略字符串（不区分中英文字符）
	 * @param str 目标字符串
	 * @param length 截取长度
	 * @return
	 */
	public static String abbr(String str, int length) {
		if (str == null) {
			return "";
		}
		try {
			StringBuilder sb = new StringBuilder();
			int currentLength = 0;
			for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
				currentLength += String.valueOf(c).getBytes("GBK").length;
				if (currentLength <= length - 3) {
					sb.append(c);
				} else {
					sb.append("...");
					break;
				}
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String abbr2(String param, int length) {
		if (param == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		int n = 0;
		char temp;
		boolean isCode = false; // 是不是HTML代码
		boolean isHTML = false; // 是不是HTML特殊字符,如&nbsp;
		for (int i = 0; i < param.length(); i++) {
			temp = param.charAt(i);
			if (temp == '<') {
				isCode = true;
			} else if (temp == '&') {
				isHTML = true;
			} else if (temp == '>' && isCode) {
				n = n - 1;
				isCode = false;
			} else if (temp == ';' && isHTML) {
				isHTML = false;
			}
			try {
				if (!isCode && !isHTML) {
					n += String.valueOf(temp).getBytes("GBK").length;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			if (n <= length - 3) {
				result.append(temp);
			} else {
				result.append("...");
				break;
			}
		}
		// 取出截取字符串中的HTML标记
		String temp_result = result.toString().replaceAll("(>)[^<>]*(<?)",
				"$1$2");
		// 去掉不需要结素标记的HTML标记
		temp_result = temp_result
				.replaceAll(
						"</?(AREA|BASE|BASEFONT|BODY|BR|COL|COLGROUP|DD|DT|FRAME|HEAD|HR|HTML|IMG|INPUT|ISINDEX|LI|LINK|META|OPTION|P|PARAM|TBODY|TD|TFOOT|TH|THEAD|TR|area|base|basefont|body|br|col|colgroup|dd|dt|frame|head|hr|html|img|input|isindex|li|link|meta|option|p|param|tbody|td|tfoot|th|thead|tr)[^<>]*/?>",
						"");
		// 去掉成对的HTML标记
		temp_result = temp_result.replaceAll("<([a-zA-Z]+)[^<>]*>(.*?)</\\1>",
				"$2");
		// 用正则表达式取出标记
		Pattern p = Pattern.compile("<([a-zA-Z]+)[^<>]*>");
		Matcher m = p.matcher(temp_result);
		List<String> endHTML = Lists.newArrayList();
		while (m.find()) {
			endHTML.add(m.group(1));
		}
		// 补全不成对的HTML标记
		for (int i = endHTML.size() - 1; i >= 0; i--) {
			result.append("</");
			result.append(endHTML.get(i));
			result.append(">");
		}
		return result.toString();
	}

	/**
	 * 转换为Double类型
	 */
	public static Double toDouble(Object val){
		if (val == null || "".equals(val)){
			return 0D;
		}
		try {
			return Double.valueOf(trim(val.toString()));
		} catch (Exception e) {
			return 0D;
		}
	}

	/**
	 * 转换为Float类型
	 */
	public static Float toFloat(Object val){
		return toDouble(val).floatValue();
	}

	/**
	 * 转换为Long类型
	 */
	public static Long toLong(Object val){
		return toDouble(val).longValue();
	}

	/**
	 * 转换为String类型
	 */
	public static String toString(Object val){
		if (val == null){
			return "";
		}
		return val.toString();
	}

	/**
	 * 转换为Integer类型
	 */
	public static Integer toInteger(Object val){
		return toLong(val).intValue();
	}

	/**
	 * 获得i18n字符串

	public static String getMessage(String code, Object[] args) {
		LocaleResolver localLocaleResolver = (LocaleResolver) SpringContextHolder.getBean(LocaleResolver.class);
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
		Locale localLocale = localLocaleResolver.resolveLocale(request);
		return SpringContextHolder.getApplicationContext().getMessage(code, args, localLocale);
	}*/

	/**
	 * 获得用户远程地址
	 */
	public static String getRemoteAddr(HttpServletRequest request){
		String remoteAddr = request.getHeader("X-Real-IP");
		if (isNotBlank(remoteAddr)) {
			remoteAddr = request.getHeader("X-Forwarded-For");
		}else if (isNotBlank(remoteAddr)) {
			remoteAddr = request.getHeader("Proxy-Client-IP");
		}else if (isNotBlank(remoteAddr)) {
			remoteAddr = request.getHeader("WL-Proxy-Client-IP");
		}
		return remoteAddr != null ? remoteAddr : request.getRemoteAddr();
	}

	/**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld" 
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
	public static String toCamelCase(String s) {
		if (s == null) {
			return null;
		}

		s = s.toLowerCase();

		StringBuilder sb = new StringBuilder(s.length());
		boolean upperCase = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (c == SEPARATOR) {
				upperCase = true;
			} else if (upperCase) {
				sb.append(Character.toUpperCase(c));
				upperCase = false;
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	/**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld" 
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
	public static String toCapitalizeCamelCase(String s) {
		if (s == null) {
			return null;
		}
		s = toCamelCase(s);
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	/**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld" 
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
	public static String toUnderScoreCase(String s) {
		if (s == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		boolean upperCase = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			boolean nextUpperCase = true;

			if (i < (s.length() - 1)) {
				nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
			}

			if ((i > 0) && Character.isUpperCase(c)) {
				if (!upperCase || !nextUpperCase) {
					sb.append(SEPARATOR);
				}
				upperCase = true;
			} else {
				upperCase = false;
			}

			sb.append(Character.toLowerCase(c));
		}

		return sb.toString();
	}

	/**
	 * 如果不为空，则设置值
	 * @param target
	 * @param source
	 */
	public static void setValueIfNotBlank(String target, String source) {
		if (isNotBlank(source)){
			target = source;
		}
	}


	/**
	 * 转换为JS获取对象值，生成三目运算返回结果
	 * @param objectString 对象串
	 *   例如：row.user.id
	 *   返回：!row?'':!row.user?'':!row.user.id?'':row.user.id
	 */
	public static String jsGetVal(String objectString){
		StringBuilder result = new StringBuilder();
		StringBuilder val = new StringBuilder();
		String[] vals = split(objectString, ".");
		for (int i=0; i<vals.length; i++){
			val.append("." + vals[i]);
			result.append("!"+(val.substring(1))+"?'':");
		}
		result.append(val.substring(1));
		return result.toString();
	}
	/***
	 * 首字母转换成大写
	 * @param str
	 * @return
	 */
	public static String toUpperCase(String str){
		return str.replaceFirst(str.substring(0, 1),str.substring(0, 1).toUpperCase())  ; 
	}
	/***
	 * 首字母转成小写
	 * @param str
	 * @return
	 */
	public static String toLowerCase(String str){
		return str.replaceFirst(str.substring(0, 1),str.substring(0, 1).toLowerCase())  ; 
	}
	/***
	 * 判断子字符串是不是为空
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str){
		return null==str||"".equals(str);
	}
	/**
	 * 去掉 字符串尾部的/
	 * @param str
	 * @return 不以/结尾的新字符创
	 */
	public static String  dropStr(String str){
		String teml = str.substring(str.length()-1,str.length());
		if("/".equals(teml)){//是以/结尾 就
			return dropStr(str.substring(0,str.length()-1));
		}else{
			return str;
		}

	}
	/**
	 * 生成随机数字验证码
	 * @param length
	 * @return
	 */
	public static String getRandomNum(int length) { //length表示生成字符串的长度  
		String base = "6482570319";     
		Random random = new Random();     
		StringBuffer sb = new StringBuffer();     
		for (int i = 0; i < length; i++) {     
			int number = random.nextInt(base.length());     
			sb.append(base.charAt(number));     
		}     
		return sb.toString();     
	}
	/** 
	 * double 乘法 
	 * @param d1 
	 * @param d2 
	 * @return 
	 */ 
	public static double mul(double d1,double d2){ 
		BigDecimal bd1 = new BigDecimal(Double.toString(d1)); 
		BigDecimal bd2 = new BigDecimal(Double.toString(d2)); 
		return bd1.multiply(bd2).doubleValue(); 
	} 


	/** 
	 * double 相加 
	 * @param d1 
	 * @param d2 
	 * @return 
	 */ 
	public static double sum(double d1,double d2){ 
		BigDecimal bd1 = new BigDecimal(Double.toString(d1)); 
		BigDecimal bd2 = new BigDecimal(Double.toString(d2)); 
		return bd1.add(bd2).doubleValue(); 
	} 


	/** 
	 * double 相减 
	 * @param d1 
	 * @param d2 
	 * @return 
	 */ 
	public static double sub(double d1,double d2){ 
		BigDecimal bd1 = new BigDecimal(Double.toString(d1)); 
		BigDecimal bd2 = new BigDecimal(Double.toString(d2)); 
		return bd1.subtract(bd2).doubleValue(); 
	} 

	/*public static void main(String[] args) {
		Double a2 = 0.1;
		Double a1 = 0.3;
		Double as = StringUtils.sub(a2,a1);
		System.out.println(as*100);
	}*/
	/** 
	 * double 除法 
	 * @param d1 
	 * @param d2 
	 * @param scale 四舍五入 小数点位数 
	 * @return 
	 */ 
	public static double div(double d1,double d2,int scale){ 
		//  当然在此之前，你要判断分母是否为0，   
		//  为0你可以根据实际需求做相应的处理 

		BigDecimal bd1 = new BigDecimal(Double.toString(d1)); 
		BigDecimal bd2 = new BigDecimal(Double.toString(d2)); 
		return bd1.divide 
				(bd2,scale,BigDecimal.ROUND_HALF_UP).doubleValue(); 
	} 

	public static String getListString(String listString) {
		if(null == listString || listString.length() <= 0 || "".equals(listString))
			return null;
		String res = "";
		res = listString + "";
		res.substring(1);
		res.substring(res.length() - 1, res.length());
		return res;
	}
	// 判断一个字符串是否都为数字  
	public static boolean isDigit(String strNum) {  
		Pattern pattern = Pattern.compile("[0-9]{1,}");  
		Matcher matcher = pattern.matcher((CharSequence) strNum);  
		return matcher.matches();  
	}
	/***
	 * 防止sql注入
	 * @param str
	 * @return
	 */
	public static boolean sql_inj(String str) {
		String inj_str = "':and:exec:insert:select:delete:update:count:*:%:chr:mid:master:truncate:char:declare:;:or:-:+:,";
		String inj_stra[] = inj_str.split(":");
		for (int i = 0; i < inj_stra.length; i++) {
			if (str.indexOf(inj_stra[i])!=-1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取真正的字符串值,将字符串null置为真正的null
	 * @param strNum
	 * @return
	 */
	public static String getRelString(String str) {
		if(StringUtils.isNotBlank(str) && "null".equals(str)){
			str = null;
		}
		return str;  
	}
	
//	/**
//	 * @return
//	 */
//	public static String getUserNameIDHash(String username,String id){
//		StringBuffer userInfo=new StringBuffer();
//		userInfo.append(id).append(username);
//		return String.valueOf(userInfo.toString().hashCode());
//	}
	
	/**
	 * 获取真正的字符串值,将字符串null置为真正的null
	 * @param username
      * @param id
	 * @return
	 */
	public static String getUserNameIDHash(String username,String id){
		StringBuffer userInfo=new StringBuffer();
		userInfo.append(id).append(username);
		return sha1IdAndName(userInfo.toString());
	}

	/**
	 * 对字符串进行散列, 支持md5与sha1算法.
	 */
	public static String sha1IdAndName(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");


			byte[] result = digest.digest(input.getBytes());

			return Base64.getEncoder().encodeToString(result);
		} catch (GeneralSecurityException e) {
			throw Exceptions.unchecked(e);
		}
	}



	public static String getIDHash(String id){
		StringBuffer userInfo=new StringBuffer();
		userInfo.append(id);
		return sha1IdAndName(userInfo.toString());
	}

	/**
	 * 利用正则表达式验证密码强度
	 * @param password
	 * @return
	 */
	public static boolean validatePassword(String password) {
		boolean flag = false;
		String regex = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,18}$";
		try {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(password);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

}
