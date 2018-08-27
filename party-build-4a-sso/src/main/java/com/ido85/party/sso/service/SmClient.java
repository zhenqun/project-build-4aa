package com.ido85.party.sso.service;

import com.ido85.party.sso.dto.SendMessageParam;
import com.ido85.party.sso.dto.VerifyCodeParam;
import com.ido85.party.sso.dto.VerifyRtPwdVfCodeParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

@Named
@RibbonClient("party-build-4a-sm")
public class SmClient {

	@LoadBalanced
	@Inject
	private RestTemplate restTemplate;
	
	@Inject
	private TokenService tokenServie;

	@Value("${smUrl}")
	private String smUrl;

	/**
	 * 校验验证码  调用短信服务
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> isVerifyCodeValid(String telephone, String type,String veifyCode){
		RestTemplate restTemplate = new RestTemplate();
		VerifyCodeParam param = new VerifyCodeParam();
		param.setTelephone(telephone);
		param.setType(type);
		param.setVeifyCode(veifyCode);
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(mediaType);
//		headers.add("Authorization","bearer "+tokenServie.getToken());
		HttpEntity<VerifyCodeParam> formEntity = new HttpEntity<>(param, headers);
		return (Map<String, Object>)restTemplate.postForEntity(smUrl+"/isVerifyCodeValid", formEntity, Map.class).getBody();
	}

	/**
	 * 验证找回密码短信验证码是否正确
	 * @param param
	 * @return
	 */
	public Map<String, Object> verifyRetrievePwdVerificationCode(VerifyRtPwdVfCodeParam param) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(mediaType);
		headers.add("Authorization","bearer "+tokenServie.getToken());
		HttpEntity<VerifyRtPwdVfCodeParam> formEntity = new HttpEntity<>(param, headers);
		return (Map<String, Object>)restTemplate.postForEntity(smUrl+"/internet/verifyRetrievePwdVerificationCode", formEntity, Map.class).getBody();
	}

	/**
	 * 发送短信
	 * @param param
	 * @return
	 */
	public Map<String, Object> sendMessage(SendMessageParam param) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(mediaType);
		headers.add("Authorization","bearer "+tokenServie.getToken());
		HttpEntity<SendMessageParam> formEntity = new HttpEntity<>(param, headers);
		return (Map<String, Object>)restTemplate.postForEntity(smUrl+"/sendMessage", formEntity, Map.class).getBody();
	}

}
