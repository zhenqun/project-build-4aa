package com.ido85.party.aaaa.mgmt.controller;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ido85.party.aaaa.mgmt.business.application.NoticeApplication;
import com.ido85.party.aaaa.mgmt.business.dto.notice.AddNoticeParam;
import com.ido85.party.aaaa.mgmt.business.dto.notice.DelNoticeParam;
import com.ido85.party.aaaa.mgmt.business.dto.notice.NoticeDto;
import com.ido85.party.aaaa.mgmt.business.dto.notice.NoticeQueryParam;
import com.ido85.party.aaaa.mgmt.business.dto.notice.ReleaseNoticeParam;
import com.ido85.party.aaaa.mgmt.dto.base.OutBasePageDto;

@RestController
public class NoticeController {

	@Inject
	private NoticeApplication noticeApp;
	
	/**
	 * 添加公告
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/business/saveOrupdateNotice", method={RequestMethod.POST})
	@ResponseBody
	public boolean saveOrupdateNotice(@Valid @RequestBody AddNoticeParam param){
		boolean flag = noticeApp.saveOrupdateNotice(param);
		return flag;
	}
	
	/**
	 * 删除公告
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/business/delNotice", method={RequestMethod.POST})
	@ResponseBody
	public boolean delNotice(@Valid @RequestBody DelNoticeParam param){
		boolean flag = noticeApp.delNotice(param);
		return flag;
	}
	
	/**
	 * 发布公告
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/business/releaseNotice", method={RequestMethod.POST})
	@ResponseBody
	public boolean releaseNotice(@Valid @RequestBody ReleaseNoticeParam param){
		boolean flag = noticeApp.releaseNotice(param);
		return flag;
	}
	
	
	/**
	 * 公告列表查询
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/business/noticeQuery", method={RequestMethod.POST})
	@ResponseBody
	public OutBasePageDto<NoticeDto> noticeQuery(@Valid @RequestBody NoticeQueryParam param){
		if(null == param.getPageNo()||param.getPageNo().intValue()<=0){
			param.setPageNo(1);
		}
		if( null == param.getPageSize()){
			param.setPageSize(20);
		}
		Long cnt = noticeApp.noticeQueryCnt(param);;
		List<NoticeDto> list = noticeApp.noticeQuery(param);
		OutBasePageDto<NoticeDto> page = new OutBasePageDto<NoticeDto>();
		page.setCount(cnt);
		page.setData(list);
		page.setPageNo(param.getPageNo()); 
		page.setPageSize(param.getPageSize());
		return page;
	}
	
	/**
	 * 公告详情查询
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/business/noticeDetail/{noticeId}", method={RequestMethod.GET})
	@ResponseBody
	public NoticeDto noticeDetail(@PathVariable("noticeId")String noticeId){
		return noticeApp.noticeDetail(noticeId);
	}
}
