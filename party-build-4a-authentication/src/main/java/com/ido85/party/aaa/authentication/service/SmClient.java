//package com.ido85.party.aaa.authentication.service;
//
//import java.util.Base64;
//import java.util.Map;
//
//import javax.inject.Inject;
//import javax.inject.Named;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
//import org.springframework.cloud.netflix.ribbon.RibbonClient;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.web.client.RestTemplate;
//
//import com.ido85.party.aaa.authentication.dto.VerifyCodeParam;
//
//@Named
//@RibbonClient("party-build-4a-sm")
//public class SmClient {
//
//	@LoadBalanced
//	@Inject
//	private RestTemplate restTemplate;
//	
//	@Value("${tokenUrl}")
//	private String tokenUrl;
//	
//	@Value("${security.oauth2.server.resourceId}")
//	private String RESOURCE_ID;
//	
//	@Value("${security.oauth2.server.resourceSecret}")
//	private String RESOURCE_SECRET;
//	
//	/**
//	 * 校验验证码  调用短信服务
//	 */
//	@SuppressWarnings("unchecked")
//	public Map<String, Object> isVerifyCodeValid(String telephone, String type,String veifyCode){
////		MultiValueMap<String, String> var = new LinkedMultiValueMap<>();
////		var.add("telephone", telephone);
////		var.add("type", type);
////		var.add("veifyCode", veifyCode);
//		VerifyCodeParam param = new VerifyCodeParam();
//		param.setTelephone(telephone);
//		param.setType(type);
//		param.setVeifyCode(veifyCode);
//		HttpHeaders headers = new HttpHeaders();
//		MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
//		headers.setContentType(mediaType);
//		headers.add("Authorization","bearer "+getToken());
//		HttpEntity<VerifyCodeParam> formEntity = new HttpEntity<>(param, headers);
//		return (Map<String, Object>)restTemplate.postForEntity("http://party-build-4a-sm/isVerifyCodeValid", formEntity, Map.class).getBody();
//	}
//	
//	/**
//	 * 获取token
//	 * @return
//	 */
//	private String getToken() {
//		RestTemplate restTemplate = new RestTemplate();
//		HttpHeaders headers = new HttpHeaders();
//		String key  = RESOURCE_ID+":"+RESOURCE_SECRET;
//		String authorization = Base64.getEncoder().encodeToString(key.getBytes());
//		headers.add("Authorization",
//				"Basic "+ authorization);
////		headers.add("Authorization", "Basic cGFydHktNGEtYXV0aGVudGljYXRpb246aWRvODVSJkRjZW50ZXI=");
//		HttpEntity<String> formEntity = new HttpEntity<>("", headers);
//		@SuppressWarnings("unchecked")
//		Map<String,String> o = restTemplate.exchange(tokenUrl, HttpMethod.POST,formEntity,Map.class).getBody();
//		if(null != o){
//			return o.get("access_token").toString();
//		}else{
//			return null;
//		}
//	}
//}
