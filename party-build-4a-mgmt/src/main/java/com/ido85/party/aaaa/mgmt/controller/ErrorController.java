/**
 * 
 */
package com.ido85.party.aaaa.mgmt.controller;


import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author rongxj
 *
 */
@Controller
public class ErrorController {

	@RequestMapping("error.html")
	public String error(HttpServletRequest request, Model model){
		
		model.addAttribute("errorCode",	request.getAttribute("javax.servlet.error.status_code"));
		Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
		String errorMessage = null;
		if (throwable != null) {
			errorMessage = throwable.getMessage();
		}
		model.addAttribute("errorMessage", errorMessage);
		return "error";
	}
	
//	@RequestMapping(path={"/error/404.html","404", "404.html"})
//	public String error404(HttpServletRequest request, Model model){
//		return "error/404";
//	}
	
	@RequestMapping("favicon.ico")
    public String favicon() {
        return "forward:images/favicon.ico";
    }
}
