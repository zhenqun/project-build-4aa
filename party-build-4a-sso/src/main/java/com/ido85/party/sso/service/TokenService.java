package com.ido85.party.sso.service;

import java.util.Base64;
import java.util.Map;

import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
@Named
@Slf4j
public class TokenService {

	@Value("${tokenUrl}")
	private String tokenUrl;
	
	@Value("${vpntokenUrl}")
	private String vpntokenUrl;
	
	@Value("${security.oauth2.server.resourceId}")
	private String RESOURCE_ID;
	
	@Value("${security.oauth2.server.resourceSecret}")
	private String RESOURCE_SECRET;
	
	/**
	 * 获取token
	 * @return
	 */
	public String getToken() {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		String key  = RESOURCE_ID+":"+RESOURCE_SECRET;
		String authorization = Base64.getEncoder().encodeToString(key.getBytes());
		headers.add("Authorization",
				"Basic "+ authorization);
//		headers.add("Authorization", "Basic cGFydHktNGEtYXV0aGVudGljYXRpb246aWRvODVSJkRjZW50ZXI=");
		HttpEntity<String> formEntity = new HttpEntity<>("", headers);
		log.info("===================tokenUrl:"+tokenUrl);
		log.info("===================authorization:"+authorization);
		@SuppressWarnings("unchecked")
		Map<String,String> o = restTemplate.exchange(tokenUrl, HttpMethod.POST,formEntity,Map.class).getBody();
		if(null != o){
			log.info("===================result"+o.get("access_token").toString());
			return o.get("access_token").toString();
		}else{
			return null;
		}
	}

	public String getVpnToken() {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		String key  = RESOURCE_ID+":"+RESOURCE_SECRET;
		String authorization = Base64.getEncoder().encodeToString(key.getBytes());
		headers.add("Authorization",
				"Basic "+ authorization);
//		headers.add("Authorization", "Basic cGFydHktNGEtYXV0aGVudGljYXRpb246aWRvODVSJkRjZW50ZXI=");
		HttpEntity<String> formEntity = new HttpEntity<>("", headers);
		@SuppressWarnings("unchecked")
		Map<String,String> o = restTemplate.exchange(vpntokenUrl, HttpMethod.POST,formEntity,Map.class).getBody();
		if(null != o){
			return o.get("access_token").toString();
		}else{
			return null;
		}
	}
	
}
