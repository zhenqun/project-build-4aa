package com.ido85.party.sso.controller;

import com.ido85.party.sso.dto.index.*;
import com.ido85.party.sso.security.authentication.application.IndexApplication;
import com.ido85.party.sso.security.authentication.domain.LoginLog;
import com.ido85.party.sso.security.authentication.domain.Notice;
import com.ido85.party.sso.security.authentication.domain.User;
import com.ido85.party.sso.security.utils.ListUntils;
import com.ido85.party.sso.security.utils.RelativeDateFormat;
import com.ido85.party.sso.security.utils.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@ControllerAdvice
public class IndexController {

	
	@Inject
	private IndexApplication indexApp;
	
	@Inject
	private EntityManager em;
	
	/**
	 * 公告列表查询
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/acc/noticeQuery", method={RequestMethod.POST})
	@ResponseBody
	public List<NoticeDto> noticeQuery(@Valid @RequestBody NoticeQueryParam param){
		if(null == param.getPageNo()||param.getPageNo().intValue()<=0){
			param.setPageNo(1);
		}
		if( null == param.getPageSize()){
			param.setPageSize(20);
		}
		List<NoticeDto> list = indexApp.noticeQuery(param);
		return list;
	}
	
	/**
	 * 公告详情查询
	 * @param noticeId
	 * @return
	 */
	@RequestMapping(path="/acc/noticeDetail/{noticeId}", method={RequestMethod.GET})
	@ResponseBody
	public NoticeDto noticeDetail(@PathVariable("noticeId")String noticeId){
		Notice notice = indexApp.noticeDetail(noticeId);
		NoticeDto dto = null;
		if(null != notice){
			dto = new NoticeDto();
			dto.setCreateDate(notice.getCreateDate());
			dto.setNoticeContent(notice.getNoticeContent());
			dto.setNoticeId(StringUtils.toString(notice.getNoticeId()));
			dto.setNoticeTitle(notice.getNoticeTitle());
			dto.setReleaseDate(notice.getReleaseDate());
		}
		return dto;
	}
	
	/**
	 * log查询
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/acc/loginLog", method={RequestMethod.POST})
	@ResponseBody
	public List<LoginlogDto> loginLog(@Valid @RequestBody LoginLogParam param,HttpServletRequest request){
		if(null == param.getPageNo()||param.getPageNo().intValue()<=0){
			param.setPageNo(1);
		}
		if( null == param.getPageSize()){
			param.setPageSize(20);
		}
		Integer pageNo = param.getPageNo();
		Integer pageSize = param.getPageSize();
		List<LoginlogDto> dtoList = null;
		LoginlogDto dto  = null;
		User user = null;
		String userId = null;
		SecurityContextImpl securityContextImpl = (SecurityContextImpl)request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
		if(null != securityContextImpl){
			 user = (User)securityContextImpl.getAuthentication().getPrincipal();
			 userId = user.getId();
		}
		StringBuffer sb = new StringBuffer("select l from LoginLog l where l.loginType = '1000' and l.userId = :userId order by l.loginDate desc");
		Query q = em.createQuery(sb.toString(),LoginLog.class);
		q.setParameter("userId", userId);
		q.setFirstResult((pageNo - 1) * pageSize);
		q.setMaxResults(pageSize);
		List<LoginLog> list = q.getResultList();
		if(ListUntils.isNotNull(list)){
			dtoList = new ArrayList<>();
			for(LoginLog log:list){
				dto = new LoginlogDto();
				dto.setClientName(log.getClientName());
				dto.setLogId(StringUtils.toString(log.getLogId()));
				dto.setLoginDate(log.getLoginDate());
				dto.setDateDetail(RelativeDateFormat.format(log.getLoginDate()));
				dtoList.add(dto);
			}
		}
		return dtoList;
	}
      /*
	 * 最新公告查询（用于九宫格弹框提示）
	 * @param request
	 * @return
	 */
	@RequestMapping(path="/acc/newNoticeQuery", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<NoticeDto> newNoticeQuery(HttpServletRequest request){
		String size = request.getParameter("size");
		String lastDate = request.getParameter("date");
		List<NoticeDto> list = null;
		NewNoticeQueryParam param = new NewNoticeQueryParam();
		if (StringUtils.isBlank(size)){ //
			size = "20";
		}

		param.setPageSize(StringUtils.toInteger(size));
		if (StringUtils.isNotBlank(lastDate)) {
			param.setNoticeDate(new Date(StringUtils.toLong(lastDate)));
		}
		if(null == param.getNoticeDate()){
			list = indexApp.newestNoticeQuery(param);
		}else{
			list = indexApp.newNoticeQuery(param);
		}
		if (null == list) {
			list = new ArrayList<>();
		}
		return list;
	}
}
