package com.ido85.party.sso.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ido85.party.sso.dto.ApplicationCategoryDto;
import com.ido85.party.sso.dto.ApplicationDto;
import com.ido85.party.sso.security.authentication.application.CommonApplication;
import com.ido85.party.sso.security.authentication.domain.User;

@RestController
public class RunnerController {

	@Inject
	private CommonApplication commonApp;
	
//	/**
//	 * 应用九宫格查询
//	 * @return
//	 * @throws UnsupportedEncodingException 
//	 */
//	@RequestMapping(path="/applicationQuery/{type}/{userId}", method={RequestMethod.GET})
//	@ResponseBody
//	public List<ApplicationCategoryDto> applicationQuery(@PathVariable(value="type")String type,@PathVariable(value="userId")String userId) throws UnsupportedEncodingException{
//		List<ApplicationCategoryDto> result = null;
////		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
////		if(null != user){
//			result = commonApp.applicationQuery("0",userId);
////		}
//		return result;
//	}
}
