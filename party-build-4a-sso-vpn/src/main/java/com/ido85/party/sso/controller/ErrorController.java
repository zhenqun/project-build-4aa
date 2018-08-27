/**
 * 
 */
package com.ido85.party.sso.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author rongxj
 *
 */
@Controller
public class ErrorController {

	@RequestMapping(path = "error.html", method = RequestMethod.GET)
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
	
	@RequestMapping(path = {"/error/404.html","404", "404.html"}, method = RequestMethod.GET)
	public String error404(HttpServletRequest request, Model model){

		return "error/404";
	}
	
	@RequestMapping(path = "favicon.ico", method = RequestMethod.GET)
    public String favicon() {
        return "forward:images/favicon.ico";
    }
}
