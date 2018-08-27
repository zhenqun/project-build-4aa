/**
 * 
 */
package com.ido85.party.userinfo.web;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author rongxj
 *
 */
@RestController
public class BusinessUserController {

	@Value("${userLogoUrl}")
	private String userLogoUrl;
	
	@Value("${defaultUserLogoUrl}")
	private String defaultUserLogoUrl;
	
	

}
