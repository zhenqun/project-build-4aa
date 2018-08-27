package com.ido85.party.aaa.authentication.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UIController {
	
	@RequestMapping(path={"/register"})
	public String register(){
		return "register";
	}
}
