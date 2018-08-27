package com.ido85.party.sso.service;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.ido85.party.sso.dto.UpdatePasswordParam;
import com.ido85.party.sso.dto.VerifyCodeParam;

@Named
@RibbonClient("party-build-4a-sync")
public class VpnClient {

	@LoadBalanced
	@Inject
	private RestTemplate restTemplate;
	
	@Inject
	private TokenService tokenServie;
	
	/**
	 * 校验验证码  调用Vpn服务
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> userVpnPwdModify(String uid,String ou,String newPassword,String oldPassword){
		UpdatePasswordParam param = new UpdatePasswordParam();
		param.setNewPwd(newPassword);
		param.setOldPwd(oldPassword);
		param.setOu(ou);
		param.setUid(uid);
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(mediaType);
		headers.add("Authorization","bearer "+tokenServie.getToken());
		HttpEntity<UpdatePasswordParam> formEntity = new HttpEntity<>(param, headers);
		return (Map<String, String>)restTemplate.postForEntity("http://party-build-4a-sync/account/updatePassword", formEntity, Map.class).getBody();
	}

}
