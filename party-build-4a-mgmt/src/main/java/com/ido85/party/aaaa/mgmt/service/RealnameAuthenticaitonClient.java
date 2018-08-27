package com.ido85.party.aaaa.mgmt.service;

import com.ido85.party.aaaa.mgmt.business.dto.RealnameAuthenticationParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Base64;
import java.util.Map;


@Named
@RibbonClient("party-build-4a-authentication")
public class RealnameAuthenticaitonClient {

	@Inject
	@LoadBalanced
	private RestTemplate restTemplate;
	
	@Value("${security.oauth2.server.tokenUrl}")
	private String tokenUrl;
	
	@Value("${security.oauth2.server.resourceId}")
	private String RESOURCE_ID;
	
	@Value("${security.oauth2.server.resourceSecret}")
	private String RESOURCE_SECRET;
	
	/**
	 * 实名认证
	 */
	@SuppressWarnings("unchecked")
	public Map<String,String> realnameAuthentication(String idCard, String name){
		RealnameAuthenticationParam param = new RealnameAuthenticationParam();
		param.setIdCard(idCard);
		param.setName(name);
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(mediaType);
		headers.add("Authorization","bearer "+getToken());
		HttpEntity<RealnameAuthenticationParam> formEntity = new HttpEntity<>(param, headers);
		Map<String,String> map = (Map<String,String>)restTemplate.postForEntity("http://party-build-4a-authentication/authentication/realnameAuthentication", formEntity, Map.class).getBody();
		if(null == map || !map.containsKey("flag")){
			map.put("flag","fail");
			map.put("message","实名认证服务异常!请于技术人员联系!");
			return map;
		}
		if("exception".equals("flag")){
			map.put("flag","fail");
			map.put("message","实名认证服务异常!请于技术人员联系!");
			return map;
		}
		return map;
	}
	
	/**
	 * 获取token
	 * @return
	 */
	private String getToken() {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		String key  = RESOURCE_ID+":"+RESOURCE_SECRET;
		String authorization = Base64.getEncoder().encodeToString(key.getBytes());
		headers.add("Authorization",
				"Basic "+ authorization);
//		headers.add("Authorization", "Basic cGFydHktNGEtYXV0aGVudGljYXRpb246aWRvODVSJkRjZW50ZXI=");
		HttpEntity<String> formEntity = new HttpEntity<>("", headers);
		@SuppressWarnings("unchecked")
		Map<String,String> o = restTemplate.exchange(tokenUrl, HttpMethod.POST,formEntity,Map.class).getBody();
		if(null != o){
			return o.get("access_token").toString();
		}else{
			return null;
		}
	}
}
