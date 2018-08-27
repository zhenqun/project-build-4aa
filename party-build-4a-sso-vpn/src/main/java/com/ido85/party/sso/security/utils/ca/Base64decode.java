package com.ido85.party.sso.security.utils.ca;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
 
public class Base64decode {
 
	  private final static String CODE_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";  
	    private final static int ORGINAL_LEN = 8;  
	    private final static int NEW_LEN = 6;  
	    
	public static String decodeBase64(String encodeStr) throws Exception{  
        StringBuilder sb = new StringBuilder("");  
        for (int i = 0; i < encodeStr.length(); i++){  
              
            char c = encodeStr.charAt(i);       //��"1tC5sg=="�ַ���һ�����ֲ�  
            int k = CODE_STR.indexOf(c);        //�ֲ����ַ���CODE_STR�е�λ��,��0��ʼ,�����'=',����-1  
            if(k != -1){                        //������ַ�����'='  
                String tmpStr = Integer.toBinaryString(k);  
                int n = 0;  
                while(tmpStr.length() + n < NEW_LEN){  
                    n ++;  
                    sb.append("0");  
                }  
                sb.append(tmpStr);  
            }  
        }  
          
        /** 
         * 8���ֽڷֲ�һ�Σ��õ��ܵ��ַ��� 
         * �����Ǽ��ܵ�ʱ�򲹵ģ���ȥ 
         */  
        int newByteLen = sb.length() / ORGINAL_LEN;           
          
        /** 
         * ������ת���ֽ����� 
         */  
        byte[] b = new byte[newByteLen];  
        for(int j = 0; j < newByteLen; j++){  
            b[j] = (byte)Integer.parseInt(sb.substring(j * ORGINAL_LEN, (j+1) * ORGINAL_LEN),2);  
        }  
          
        /** 
         * �ֽ����黹ԭ��String 
         */  
        return new String(b, "gb2312");  
    }  

 
    public static void main(String[] args) throws Exception {

        
         System.out.println(new Base64decode().decodeBase64("MzcwMTA0MTk4NzEyMzQ1Njc4"));
    }
 
}