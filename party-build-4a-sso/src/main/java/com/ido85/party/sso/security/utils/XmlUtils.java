package com.ido85.party.sso.security.utils;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XmlUtils {
//	public static void main(String[] args) throws  DocumentException {
//		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//				"<ROWS>\n" +
//				"	<ROW no=\"1\">\n" +
//				"		<!-- no属性为记录编号 -->\n" +
//				"		<INPUT>\n" +
//				"			<!--输入项节点-->\n" +
//				"			<gmsfhm>230602******</gmsfhm>\n" +
//				"			<!--公民身份号码-->\n" +
//				"			<xm>白若晶</xm>\n" +
//				"			<!--姓名-->\n" +
//				"		</INPUT>\n" +
//				"		<OUTPUT>\n" +
//				"			<!--输出项节点-->\n" +
//				"			<ITEM>\n" +
//				"				<gmsfhm></gmsfhm>\n" +
//				"				<!--公民身份号码的核查结果-->\n" +
//				"				<result_gmsfhm>一致</result_gmsfhm>\n" +
//				"			</ITEM>\n" +
//				"			<ITEM>\n" +
//				"				<xm></xm>\n" +
//				"				<result_xm>一致</result_xm>\n" +
//				"				<!--姓名的核查结果-->\n" +
//				"			</ITEM>\n" +
//				"		</OUTPUT>\n" +
//				"	</ROW>\n" +
//				"</ROWS>";
//			System.out.println(parseXml(result));
//		 }
	
	public static String parseXml(String result) throws DocumentException{
		Document dom=DocumentHelper.parseText(result);
		Element root=dom.getRootElement();
		if(null == root || "RESPONSE".equals(root.getName())){
			return "";
		}
		Element outputElement=root.element("ROW").element("OUTPUT");
		if(null == outputElement){
			return "";
		}
		List<Element> elements = outputElement.elements();
		Element element = elements.get(0);
		String name = element.elementText("result_gmsfhm");
		Element element2 = elements.get(1);
		String name2 = element2.elementText("result_xm");
		return name+name2;
	}
	
}
