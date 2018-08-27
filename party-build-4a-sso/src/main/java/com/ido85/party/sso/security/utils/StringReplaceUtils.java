package com.ido85.party.sso.security.utils;

public class StringReplaceUtils {

	/**
	 * 对字符串处理:将指定位置到指定位置的字符以星号代替
	 * 
	 * @param content
	 *            传入的字符串
	 * @param begin
	 *            开始位置
	 * @param end
	 *            结束位置
	 * @return
	 */
	public static String getStarString(String content) {
		int len = content.length();
		String starStr = "";
		String rex = "*";
		String rexs = "";
		if(len <= 0){
			starStr = content;
		}
		if(len == 1){
			starStr = "*";
		}
		if(len == 2){
			starStr = content.substring(0, 1)+rex;
		}
		if(len > 2){
			for(int i=0;i<len;i++){
				if(i>0 && i<len-1){
					rexs += rex;
				}
				starStr = content.substring(0, 1)+rexs+content.substring(len-1,len);
			}
		}
		return starStr;
	}
	
//	public static void main(String[] args) {
//		System.out.println(getStarString("李良政啊哈"));
//	}
}