/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.ido85.party.platform.utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.fasterxml.jackson.databind.util.ISO8601Utils;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 * @author ThinkGem
 * @version 2014-4-15
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	private static String[] parsePatterns = {
			"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", 
			"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
			"yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM",
			"yyyyMMddHHmmss","yyyyMMdd","yyyy-MM-dd'T'HH:mm:ss.SSS","yyyy-MM-dd'T'HH:mm:ss:SSSZZ",
			"yyyy-MM-dd'T'HH:mm:ss.SSSX","yyyy-MM-dd'T'HH:mm:ss.SSS Z"};

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}

	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, Object... pattern) {
		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}

	public static String formatDate(Date date) {
		return formatDate(date, "yyyy-MM-dd");
	}
	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date, String pattern) {
		return formatDate(date, pattern);
	}
	/**
	 * 将传入的日期时间，转换格式 为pattern 格式的时间
	 */
	public static Date formatDateToPattern(Date date, String pattern) {
		try {
			return parse(formatDate(date, pattern), pattern);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}

	/**
	 * 日期型字符串转化为日期 格式
	 * {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", 
		"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
		"yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM",
		"yyyyMMddHHmmss","yyyyMMdd","yyyy-MM-dd'T'HH:mm:ss.SSS","yyyy-MM-dd'T'HH:mm:ss:SSSZZ",
	    "yyyy-MM-dd'T'HH:mm:ss.SSSX","yyyy-MM-dd'T'HH:mm:ss.SSS Z"}
	 */
	public static Date parseDate(Object str) {
		if (str == null){
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(24*60*60*1000);
	}

	/**
	 * 获取过去的小时
	 * @param date
	 * @return
	 */
	public static long pastHour(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(60*60*1000);
	}

	/**
	 * 获取过去的分钟
	 * @param date
	 * @return
	 */
	public static long pastMinutes(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(60*1000);
	}

	/**
	 * 转换为时间（天,时:分:秒.毫秒）
	 * @param timeMillis
	 * @return
	 */
	public static String formatDateTime(long timeMillis){
		long day = timeMillis/(24*60*60*1000);
		long hour = (timeMillis/(60*60*1000)-day*24);
		long min = ((timeMillis/(60*1000))-day*24*60-hour*60);
		long s = (timeMillis/1000-day*24*60*60-hour*60*60-min*60);
		long sss = (timeMillis-day*24*60*60*1000-hour*60*60*1000-min*60*1000-s*1000);
		return (day>0?day+",":"")+hour+":"+min+":"+s+"."+sss;
	}


	/**
	 * 获取两个日期之间的天数
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceOfTwoDate(Date before, Date after) {
		long beforeTime = before.getTime();
		long afterTime = after.getTime();
		return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
	}


	/**
	 * 格转为ISO8601格式
	 * @param date
	 * @param millis 是否包含毫秒数
	 * @return
	 */
	public static String formatISO8601Date(Date date, boolean millis){

		return ISO8601Utils.format(date, millis, TimeZone.getDefault());
	}
	/****
	 * 找出在某个范围之内的时间集合
	 * @param start 开始
	 * @param end 结束
	 * @param list 源
	 * @return teml 返回
	 */
	public static List<Date> splitDate(Date start,Date end,List<Date> list){
		List<Date> teml = new ArrayList<>();
		if(null!=list&&list.size()>0){
			for(Date d : list){
				if(d.getTime()==start.getTime()||d.getTime()==end.getTime()){
					teml.add(d);
				}
				if(d.after(start)&&d.before(end)){
					teml.add(d);
				}
			}
		}
		return teml;	


	}

	/**
	 * 格转为ISO8601格式
	 * @param date
	 * @return
	 */
	public static String formatISO8601Date(Date date){
		return ISO8601Utils.format(date, false, TimeZone.getDefault());
	}

	/**
	 * 格转为ISO8601格式
	 * @param date
	 * @return
	 */
	public static String formatISO8601Date(long timeMillis){
		return formatISO8601Date(new Date(timeMillis));
	}

	public static Date parseISO8601Date(String date){
		try {
			return ISO8601Utils.parse(date, new ParsePosition(0));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 日期型字符串转化为日期 格式
	 * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", 
	 *   "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
	 *   "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
	 */  
	public static Date parse(String strDate, String pattern)  
			throws ParseException  
	{  
		return StringUtils.isBlank(strDate) ? null : new SimpleDateFormat(  
				pattern).parse(strDate);  
	}  

	/** 
	 * 在日期上增加数个整月 
	 */  
	public static Date addMonth(Date date, int n)  
	{  
		Calendar cal = Calendar.getInstance();  
		cal.setTime(date);  
		cal.add(Calendar.MONTH, n);  
		return cal.getTime();  
	}  

	/***
	 * 获取当前时间的后几天
	 * @param addDay
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date getDateAfter(int addDay){
		Date date = new Date();//当前时间
		date.setDate(date.getDate()+addDay);
		return date;
	}
	/***
	 * 获取当前时间的后几天返回值字符串
	 * @param addDay
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getDateStrAfter(int addDay){
		Date date = new Date();
		date.setDate(date.getDate()+addDay);
		return date.toLocaleString();
	}
	/***
	 * 获取当前时间后几个月日期 返回date
	 * @param addMonth
	 * @return
	 */
	public static Date getDateAfterMonth(int addMonth){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, addMonth);
		return calendar.getTime();
	}
	/***
	 * 获取当前时间后几个月日期 返回Str
	 * @param addMonth
	 * @return
	 */
	public static String getDateStrAfterMonth(int addMonth){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, addMonth);
		return sdf.format(calendar.getTime());
	}
	/***
	 * 获取时间后几个月日期 返回date
	 * @param addMonth
	 * @param dateStr
	 * @return
	 */
	public static Date getDateAfterMonthByDateStr(int addMonth,String dateStr){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		Date now;
		try {
			now = sdf.parse(dateStr);
			calendar.setTime(now);
			System.out.println(sdf.format(calendar.getTime()));
			calendar.add(Calendar.MONTH, addMonth);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return calendar.getTime();

	}
	/****
	 * 获取时间后几个月日期 返回date
	 * @param addMonth
	 * @param date
	 * @return
	 */
	public static Date getDateAfterMonthByDate(int addMonth,Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, addMonth);
		return calendar.getTime();
	}

	/****
	 * 获取时间后几天 返回date
	 * @param addMonth
	 * @param date
	 * @return
	 */
	public static Date getDateAfterDayByDate(int addDay,Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, addDay);
		return calendar.getTime();
	}

	/****
	 * 获取时间后几个月日期 返回Str
	 * @param addMonth
	 * @param dateStr
	 * @return
	 */
	public static String getDateAfterMonth(int addMonth,String dateStr){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		Date now;
		try {
			now = sdf.parse(dateStr);
			calendar.setTime(now);
			calendar.add(Calendar.MONTH, addMonth);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sdf.format(calendar.getTime());
	}
	/****
	 * 获取时间后几个月日期 返回str
	 * @param addMonth
	 * @param date
	 * @return
	 */
	public static String  getDateAfterMonth(int addMonth,Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, addMonth);
		return sdf.format(calendar.getTime());
	}

	/***
	 * 判断两个时间是不是在同一天
	 * @param date1
	 * @param date2
	 * @return
	 */
	public  static boolean isSameDate(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);

		boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
				.get(Calendar.YEAR);
		boolean isSameMonth = isSameYear
				&& cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
		boolean isSameDate = isSameMonth
				&& cal1.get(Calendar.DAY_OF_MONTH) == cal2
				.get(Calendar.DAY_OF_MONTH);

		return isSameDate;
	}

	/***
	 * 判断两个时间是不是在同一个月
	 * @param date1
	 * @param date2
	 * @return
	 */
	public  static boolean isSameMonth(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);

		boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
				.get(Calendar.YEAR);
		boolean isSameMonth = isSameYear
				&& cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);

		return isSameMonth;
	}

	/**
	 * utc格式返回本地时间
	 * 
	 * @param utcTimeStr 例如：2016-04-24T16:00:00.000Z
	 * @return
	 * @throws ParseException
	 */
	public static String utc2local(String utcTimeStr, String pattern) {
		try {
			utcTimeStr = utcTimeStr.replace("Z", " UTC");// 注意是空格+UTC
			Date d = parseDate(utcTimeStr, parsePatterns);
			return formatDate(d, pattern);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 得到某年某周的第一天
	 *
	 * @param year
	 * @param week
	 * @return
	 */
	public static Date getFirstDayOfWeek(int year, int week) {
		week = week - 1;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DATE, 1);

		Calendar cal = (Calendar) calendar.clone();
		cal.add(Calendar.DATE, week * 7);

		return getFirstDayOfWeek(cal.getTime());
	}

	/**
	 * 得到某年某周的最后一天
	 *
	 * @param year
	 * @param week
	 * @return
	 */
	public static Date getLastDayOfWeek(int year, int week) {
		week = week - 1;
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DATE, 1);
		Calendar cal = (Calendar) calendar.clone();
		cal.add(Calendar.DATE, week * 7);

		return getLastDayOfWeek(cal.getTime());
	}

	/**
	 * 取得当前日期所在周的第一天
	 *
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK,
				calendar.getFirstDayOfWeek()); // Monday
		return calendar.getTime();
	}

	/**
	 * 取得当前日期所在周的最后一天
	 *
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK,
				calendar.getFirstDayOfWeek() + 6); // Sunday
		return calendar.getTime();
	}

	/**
	 * 取得当前日期所在周的前一周最后一天
	 *
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfLastWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setTime(date);
		return getLastDayOfWeek(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.WEEK_OF_YEAR) - 1);
	}

	/**
	 * 取得当前日期所在周的前一周第一天
	 *
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfLastWeek(Date date) {
		return getFirstDayOfWeek(getLastDayOfLastWeek(date));
	}

	/**
	 * 返回指定日期的月的第一天
	 *
	 * @param year
	 * @param month
	 * @return
	 */
	public static Date getFirstDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH), 1);
		return calendar.getTime();
	}

	/**
	 * 返回指定年月的月的第一天
	 *
	 * @param year
	 * @param month
	 * @return
	 */
	public static Date getFirstDayOfMonth(Integer year, Integer month) {
		Calendar calendar = Calendar.getInstance();
		if (year == null) {
			year = calendar.get(Calendar.YEAR);
		}
		if (month == null) {
			month = calendar.get(Calendar.MONTH);
		}
		calendar.set(year, month, 1);
		return calendar.getTime();
	}

	/**
	 * 返回指定日期的月的最后一天
	 *
	 * @param year
	 * @param month
	 * @return
	 */
	public static Date getLastDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH), 1);
		calendar.roll(Calendar.DATE, -1);
		return calendar.getTime();
	}

	/**
	 * 返回指定年月的月的最后一天
	 *
	 * @param year
	 * @param month
	 * @return
	 */
	public static Date getLastDayOfMonth(Integer year, Integer month) {
		Calendar calendar = Calendar.getInstance();
		if (year == null) {
			year = calendar.get(Calendar.YEAR);
		}
		if (month == null) {
			month = calendar.get(Calendar.MONTH);
		}
		calendar.set(year, month, 1);
		calendar.roll(Calendar.DATE, -1);
		return calendar.getTime();
	}

	/**
	 * 返回指定日期的上个月的最后一天
	 *
	 * @param year
	 * @param month
	 * @return
	 */
	public static Date getLastDayOfLastMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH) - 1, 1);
		calendar.roll(Calendar.DATE, -1);
		return calendar.getTime();
	}

	/**
	 * 返回指定日期的季的第一天
	 *
	 * @param year
	 * @param quarter
	 * @return
	 */
	public static Date getFirstDayOfQuarter(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return getFirstDayOfQuarter(calendar.get(Calendar.YEAR),
				getQuarterOfYear(date));
	}

	/**
	 * 返回指定年季的季的第一天
	 *
	 * @param year
	 * @param quarter
	 * @return
	 */
	public static Date getFirstDayOfQuarter(Integer year, Integer quarter) {
		Calendar calendar = Calendar.getInstance();
		Integer month = new Integer(0);
		if (quarter == 1) {
			month = 1 - 1;
		} else if (quarter == 2) {
			month = 4 - 1;
		} else if (quarter == 3) {
			month = 7 - 1;
		} else if (quarter == 4) {
			month = 10 - 1;
		} else {
			month = calendar.get(Calendar.MONTH);
		}
		return getFirstDayOfMonth(year, month);
	}

	/**
	 * 返回指定日期的季的最后一天
	 *
	 * @param year
	 * @param quarter
	 * @return
	 */
	public static Date getLastDayOfQuarter(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return getLastDayOfQuarter(calendar.get(Calendar.YEAR),
				getQuarterOfYear(date));
	}

	/**
	 * 返回指定年季的季的最后一天
	 *
	 * @param year
	 * @param quarter
	 * @return
	 */
	public static Date getLastDayOfQuarter(Integer year, Integer quarter) {
		Calendar calendar = Calendar.getInstance();
		Integer month = new Integer(0);
		if (quarter == 1) {
			month = 3 - 1;
		} else if (quarter == 2) {
			month = 6 - 1;
		} else if (quarter == 3) {
			month = 9 - 1;
		} else if (quarter == 4) {
			month = 12 - 1;
		} else {
			month = calendar.get(Calendar.MONTH);
		}
		return getLastDayOfMonth(year, month);
	}

	/**
	 * 返回指定日期的上一季的最后一天
	 *
	 * @param year
	 * @param quarter
	 * @return
	 */
	public static Date getLastDayOfLastQuarter(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return getLastDayOfLastQuarter(calendar.get(Calendar.YEAR),
				getQuarterOfYear(date));
	}

	/**
	 * 返回指定年季的上一季的最后一天
	 *
	 * @param year
	 * @param quarter
	 * @return
	 */
	public static Date getLastDayOfLastQuarter(Integer year, Integer quarter) {
		Calendar calendar = Calendar.getInstance();
		Integer month = new Integer(0);
		if (quarter == 1) {
			month = 12 - 1;
		} else if (quarter == 2) {
			month = 3 - 1;
		} else if (quarter == 3) {
			month = 6 - 1;
		} else if (quarter == 4) {
			month = 9 - 1;
		} else {
			month = calendar.get(Calendar.MONTH);
		}
		return getLastDayOfMonth(year, month);
	}

	/**
	 * 返回指定日期的季度
	 *
	 * @param date
	 * @return
	 */
	public static int getQuarterOfYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH) / 3 + 1;
	}

	/**
	 * 将时间转化成yyyy-MM-dd 23:59:59格式
	 * @param date
	 * @return
	 */
	public static Date forDateAddDay(Date packageEndDate) throws Exception {
		String newDate = formatDate(packageEndDate, "yyyy-MM-dd");
		newDate = newDate + " 23:59:59";
		return parse(newDate,"yyyy-MM-dd hh:mm:ss");
	}


	/**
	 * 将时间转化成yyyy-MM-dd 23:59:59格式
	 * @param date
	 * @return
	 */
	public static Date forDateDelDay(Date packageEndDate) throws Exception {
		String newDate = formatDate(packageEndDate, "yyyy-MM-dd");
		newDate = newDate + " 00:00:00";
		return parse(newDate,"yyyy-MM-dd hh:mm:ss");
	}
	/**
	 * 获取传入的日期的最早的和最晚的日期
	 * @param dates
	 * @return
	 */
	public static List<Date> getFirstAndLastDate(List<Date> dates){
		if(ListUntils.isNull(dates)){
			return null;
		}
		if(dates.size() <= 2){
			return dates;
		}
		Collections.sort(dates);
		Date date = dates.get(dates.size() - 1);
		dates = dates.subList(0, 1);
		dates.add(date);
		return dates;
	}
	/***
	 * 两个日期相差的分钟
	 * @param date
	 * @param date2
	 * @return
	 */
	public static Long difference(Date date,Date date2) {
		Calendar dateOne=Calendar.getInstance(),dateTwo=Calendar.getInstance();
		dateOne.setTime(date);	//设置为当前系统时间 
		dateTwo.setTime(date2);		//设置为2015年1月15日
		long timeOne=dateOne.getTimeInMillis();
		long timeTwo=dateTwo.getTimeInMillis();
		long minute=(timeOne-timeTwo)/(1000*60);//转化minute
		return minute;
	}
}
