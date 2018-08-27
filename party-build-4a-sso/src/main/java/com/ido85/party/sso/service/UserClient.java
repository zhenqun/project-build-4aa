package com.ido85.party.sso.service;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ido85.party.sso.dto.HeadpicUploadDto;
import com.ido85.party.sso.dto.InBaseUserDto;
import com.ido85.party.sso.dto.RealnameAuthenticationParam;
import com.ido85.party.sso.dto.RetrievePasswordForWebParam;
import com.ido85.party.sso.dto.RetrievePasswordParam;
import com.ido85.party.sso.dto.UserPwdModifyParam;
import com.ido85.party.sso.dto.VerifyRtPwdVfCodeParam;

@Named
@RibbonClient("party-build-4a-authentication")
public class UserClient {

	@LoadBalanced
	@Inject
	private RestTemplate restTemplate;
	
	@Inject
	private TokenService tokenService;
	
	/**
	 * 验证找回密码短信验证码是否正确
	 */
	public Map<String, Object> verifyRetrievePwdVerificationCode(VerifyRtPwdVfCodeParam param) {
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(mediaType);
		headers.add("Authorization","bearer "+tokenService.getToken());
		HttpEntity<VerifyRtPwdVfCodeParam> formEntity = new HttpEntity<>(param, headers);
		return (Map<String, Object>)restTemplate.postForEntity("http://party-build-4a-authentication/internet/verifyRetrievePwdVerificationCode", formEntity, Map.class).getBody();
	}
	
	/**
	 * 找回密码
	 * @param param
	 * @return
	 */
	public Map<String, Object> retrievePasswordForWeb(RetrievePasswordForWebParam param) {
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(mediaType);
		headers.add("Authorization","bearer "+tokenService.getToken());
		HttpEntity<RetrievePasswordForWebParam> formEntity = new HttpEntity<>(param, headers);
		return (Map<String, Object>)restTemplate.postForEntity("http://party-build-4a-authentication/internet/retrievePasswordForWeb", formEntity, Map.class).getBody();
	}

	/**
	 * 修改密码
	 * @param username
	 * @param newPassword
	 * @return
	 */
	public Map<String, Object> retrievePassword(UserPwdModifyParam param) {
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(mediaType);
		headers.add("Authorization","bearer "+tokenService.getToken());
		HttpEntity<UserPwdModifyParam> formEntity = new HttpEntity<>(param, headers);
		return (Map<String, Object>)restTemplate.postForEntity("http://party-build-4a-authentication/internet/userPwdModify", formEntity, Map.class).getBody();
	}

	/**
	 * 找回密码
	 * @param param
	 * @return
	 */
	public Map<String, Object> retrievePassword(RetrievePasswordParam param) {
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(mediaType);
		headers.add("Authorization","bearer "+tokenService.getToken());
		HttpEntity<RetrievePasswordParam> formEntity = new HttpEntity<>(param, headers);
		return (Map<String, Object>)restTemplate.postForEntity("http://party-build-4a-authentication/internet/retrievePassword", formEntity, Map.class).getBody();
	}
	
	/**
	 * 修改用户头像
	 * @param fileMap
	 * @return
	 */
	public String headpicUpload(HttpServletRequest request,String userId) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		HeadpicUploadDto dto = new HeadpicUploadDto();
		dto.setFileMap(fileMap);
		dto.setUserId(userId);
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(mediaType);
		headers.add("Authorization","bearer "+tokenService.getToken());
		HttpEntity<HeadpicUploadDto> formEntity = new HttpEntity<>(dto, headers);
		return restTemplate.postForEntity("http://party-build-4a-authentication/internet/headpicUpload", formEntity, Map.class).getBody().toString();
	}
	
	/**
	 * 调用注册服务
	 */
//	public Map<String, Object> register(InBaseUserDto dto){
//		HttpHeaders headers = new HttpHeaders();
//		MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
//		headers.setContentType(mediaType);
//		headers.add("Authorization","bearer "+tokenService.getToken());
//		HttpEntity<InBaseUserDto> formEntity = new HttpEntity<>(dto, headers);
//		return (Map<String, Object>)restTemplate.postForEntity("http://party-build-4a-authentication/internet/register", formEntity, Map.class).getBody();
//	}

//	/**
//	 * 实名认证服务
//	 * @param idCard
//	 * @param name
//	 * @return
//	 */
//	public boolean nciicAuthentication(String idCard, String name) {
//		RealnameAuthenticationParam param = new RealnameAuthenticationParam();
//		param.setIdCard(idCard);
//		param.setName(name);
//		HttpHeaders headers = new HttpHeaders();
//		MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
//		headers.setContentType(mediaType);
//		headers.add("Authorization","bearer "+tokenService.getVpnToken());
//		HttpEntity<RealnameAuthenticationParam> formEntity = new HttpEntity<>(param, headers);
//		return (boolean)restTemplate.postForEntity("http://party-build-4a-authentication/authentication/realnameAuthentication", formEntity, Boolean.class).getBody();
//	}
}
