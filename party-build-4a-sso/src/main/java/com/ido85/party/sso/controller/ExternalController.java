package com.ido85.party.sso.controller;

import com.ido85.party.sso.security.authentication.application.ExternalApplication;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.authentication.dto.InQueryUserInfoDto;
import com.ido85.party.sso.security.authentication.dto.OutQueryUserInfoDto;
import com.ido85.party.sso.security.utils.ListUntils;
import com.ido85.party.sso.security.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fire on 2017/2/22.
 */
@RestController
public class ExternalController {
	@Inject
	private ExternalApplication externalApp;

	@Value("${userLogoUrl}")
	private String userLogoUrl;
	
	@Value("${defaultUserLogoUrl}")
	private String defaultUserLogoUrl;
	
	@RequestMapping(path = "/user/usersinfo/query", method = RequestMethod.POST)
	public List<OutQueryUserInfoDto> queryUserInfoByOrgIdAndUserId(@Valid @RequestBody InQueryUserInfoDto in)throws Exception{
		List<OutQueryUserInfoDto> res = new ArrayList<>();
		OutQueryUserInfoDto dto = null;
		List<User> userList = externalApp.queryUserInfoByUserId(in.getHashs());
		if(ListUntils.isNotNull(userList)){
			for(User u:userList){
				dto = new OutQueryUserInfoDto();
				dto.setId(u.getId());
				dto.setLogo(StringUtils.isNull(u.getLogo())?defaultUserLogoUrl:userLogoUrl+u.getLogo());
				dto.setUsername(u.getUsername());
				dto.setHash(u.getHash());
				res.add(dto);
			}
		}
		return res;
	}

}
