package com.ido85.party.sso.ws.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxy;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.transport.http.CommonsHttpMessageSender;
import org.codehaus.xfire.transport.http.EasySSLProtocolSocketFactory;
import org.codehaus.xfire.util.dom.DOMOutHandler;

import com.ido85.party.sso.ws.serv.ServiceInf;

public class NciicClient {
	private static String inLicence = "?v?zIX>)H3?[IGR<P1S]dfN\\&T/l?o?g?kb_1;&mSM/DC]B=7)"
			+ "1%6%>$2h<p^tGnb8?o;b5s$`7s[e\\qK.Tk^j^f_y?x?vSmWb]rA;UiSoVjP[?x7s^qW[GgM/:"
			+ "b?m*p`$?/%o?vbP`0_eYjNa?x?vWg`7RuZp?x?m8f?h?f;d5/*n[cBaToBm?x?vUhKvEdDz?"
			+ "x5EB54RLL@Van3F2W?/?.?a3s.p5/?jLwCsXkDz+q?vaXCc^fHu^/NxcXIf?xQ[?g?g?g7m3c?v`}"
			+ "cVI.bX@;Jo@hWnUk&r8bb0It3i?x?v?jUzXsUjLpQrTyCnNx?x?vHu^u]hMuFhMg`4GgYeSqVt?x?"
			+ "vFzD;FhZp<c?g<f?g?f?v$sGxH[YjHd?x9n8eEwDxaVcNc9RkM.Sya O/P[?x=x6t]kKcGbGc\\l\\hKg?x";

	private static String inConditions = "<?xml version='1.0' encoding='utf-8'?>\n" + "<ROWS>\n" + "<INFO>\n"
			+ "<SBM>八五创新</SBM>\n" + "</INFO>\n" + "<ROW FSD='山东' YWLX='test'>\n"
			+ "<GMSFHM>370124199110025016</GMSFHM>\n" + "<XM>尹帅</XM>\n" + "</ROW>\n" + "</ROWS>";
	public static final String SERVICE_URL = "http://10.254.23.6:8099/nciic_ws/services/";
	private static final String SERVICE_NAME = "NciicServices";

	public NciicClient() {
	}

//	public static void main(String[] args) throws MalformedURLException {
//
//		try {
//
//			new NciicClient().executeClient(inConditions);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public String executeClient(String condition) throws MalformedURLException {
		long time = System.currentTimeMillis();
		ProtocolSocketFactory easy = new EasySSLProtocolSocketFactory();
		Protocol protocol = new Protocol("https", easy, 443);
		Protocol.registerProtocol("https", protocol);
		Service serviceModel = new ObjectServiceFactory().create(ServiceInf.class, "NciicServices", null, null);
		ServiceInf service = (ServiceInf) new XFireProxyFactory().create(serviceModel, SERVICE_URL + SERVICE_NAME);
		Client client = ((XFireProxy) Proxy.getInvocationHandler(service)).getClient();
		client.addOutHandler(new DOMOutHandler());
		client.setProperty(CommonsHttpMessageSender.GZIP_ENABLED, Boolean.TRUE);
		client.setProperty(CommonsHttpMessageSender.HTTP_TIMEOUT, "0");
		String result = null;
		try {
			result = service.nciicCheck(inLicence, condition);
			System.out.println(result);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		time = System.currentTimeMillis() - time;
		System.out.println("------out time -----" + time);
		return result;
	}
}
