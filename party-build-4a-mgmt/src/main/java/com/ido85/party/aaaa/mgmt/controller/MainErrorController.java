/**
 * 
 */
package com.ido85.party.aaaa.mgmt.controller;


import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author rongxj
 *
 */
@Controller
@ControllerAdvice
public class MainErrorController implements ErrorController {

	@Inject
	private ErrorAttributes errorAttributes;

	@Inject
	private ServerProperties serverProperties;


	@RequestMapping(path = "/error")
	public String errorHtml(HttpServletRequest request, Model model){
		fillErrorMessage(request, model);
		return "error/500";
	}

	@RequestMapping(path="/error", produces = "application/json")
	@ResponseBody
	public ResponseEntity<Map> error404(HttpServletRequest request, Model model){
		fillErrorMessage(request, model);
		Map<String, Object> body = getErrorAttributes(request, isIncludeStackTrace(request));
		HttpStatus status = getStatus(request);
		return new ResponseEntity(body, status);
	}

	@RequestMapping(path = "/error/404")
	public String errorHtml404(HttpServletRequest request, Model model){
		fillErrorMessage(request, model);
		return "error/500";
	}

	@RequestMapping(path = "/error/401")
	public String errorHtml401(HttpServletRequest request, Model model){
		fillErrorMessage(request, model);
		return "error/500";
	}

	@RequestMapping(path = "/error/403")
	public String errorHtml403(HttpServletRequest request, Model model){
		fillErrorMessage(request, model);
		return "error/500";
	}

	@RequestMapping(path = "/error/500")
	public String errorHtml500(HttpServletRequest request, Model model){
		fillErrorMessage(request, model);
		return "error/500";
	}



//	@ExceptionHandler(NoHandlerFoundException.class)
//	@RequestMapping(path={"/error/404.html","404", "404.html"})
//	public String error404(HttpServletRequest request, Model model, @CookieValue() String sessionId){
//		return "error/404";
//	}

//	@Override
	public String getErrorPath() {
		return "/error";
	}

	protected Session getSession(HttpServletRequest request){
		String sessionId = WebUtils.getCookie(request, "SESSION").getValue();
		if(sessionId == null){
			return null;
		}
		SessionRepository sessionRepository = (SessionRepository) request.getAttribute("org.springframework.session.SessionRepository");
		return sessionRepository.getSession(sessionId);
	}

	protected Object getSessionAttribute(HttpServletRequest request, String attributeName){
		Session session = getSession(request);
		if(session == null){
			return session.getAttribute(attributeName);
		}
		return null;
	}

	/**
	 * 获取错误的信息
	 * @param request
	 * @param includeStackTrace
	 * @return
	 */
	private Map<String, Object> getErrorAttributes(HttpServletRequest request,
												   boolean includeStackTrace) {
		RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		return this.errorAttributes.getErrorAttributes(requestAttributes,
				includeStackTrace);
	}

	/**
	 * 获取错误编码
	 * @param request
	 * @return
	 */
	private HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request
				.getAttribute("javax.servlet.error.status_code");
		if (statusCode == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		try {
			return HttpStatus.valueOf(statusCode);
		}
		catch (Exception ex) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}

	/**
	 * Determine if the stacktrace attribute should be included.
	 * @param request the source request
	 * //@param produces the media type produced (or {@code MediaType.ALL})
	 * @return if the stacktrace attribute should be included
	 */
	protected boolean isIncludeStackTrace(HttpServletRequest request) {
		ErrorProperties.IncludeStacktrace include = this.serverProperties.getError().getIncludeStacktrace();
		if (include == ErrorProperties.IncludeStacktrace.ALWAYS) {
			return true;
		}
		if (include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM) {
			return getTraceParameter(request);
		}
		return false;
	}

	/**
	 * 是否包含trace
	 * @param request
	 * @return
	 */
	private boolean getTraceParameter(HttpServletRequest request) {
		String parameter = request.getParameter("trace");
		if (parameter == null) {
			return false;
		}
		return !"false".equals(parameter.toLowerCase());
	}

	/**
	 * 返回错误信息
	 * @param request
	 * @param model
	 */
	void fillErrorMessage(HttpServletRequest request, Model model) {
		Map userInfo = (Map)getSessionAttribute(request,"USER_INFO");
		model.addAttribute("USER_INFO", userInfo);
		Exception exception = (Exception) request.getAttribute("exception");
		if(exception instanceof HttpClientErrorException){
			HttpClientErrorException clientErrorException = (HttpClientErrorException)exception;
			model.addAttribute("errorCode", clientErrorException.getStatusCode());
		}else{
			model.addAttribute("errorCode",	request.getAttribute("javax.servlet.error.status_code"));
		}
		model.addAttribute("errorCode",	request.getAttribute("javax.servlet.error.status_code"));
		Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
		String errorMessage = null;
		if (throwable != null) {
			errorMessage = throwable.getMessage();
		}
		model.addAttribute("errorMessage", errorMessage);
	}
}
