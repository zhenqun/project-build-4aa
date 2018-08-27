package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.dto.LogDto;
import com.ido85.party.aaaa.mgmt.dto.LogQueryParam;
import com.ido85.party.aaaa.mgmt.dto.base.OutBasePageDto;
import com.ido85.party.aaaa.mgmt.internet.dto.InLoginLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.InRegisterLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.OutLoginLogQueryDto;
import com.ido85.party.aaaa.mgmt.internet.dto.OutRegisterLogQueryDto;
import com.ido85.party.aaaa.mgmt.security.authentication.dto.OutGrantLogDetailDto;
import com.ido85.party.aaaa.mgmt.service.LogService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

@RestController
public class LogController {
	
	@Inject
	private LogService logService;
	
	/**
	 * 授权日志查询
	 * @param param
	 * @return
	 */
	@RequestMapping(path="/manage/audit/grantlogQuery", method={RequestMethod.POST})
	@ResponseBody
	public OutBasePageDto<LogDto> logQuery(@Valid @RequestBody LogQueryParam param) throws Exception {
		if(null == param.getPageNo()||param.getPageNo().intValue()<=0){
			param.setPageNo(1);
		}
		if( null == param.getPageSize()){
			param.setPageSize(20);
		}
		List<LogDto> logList = logService.logQuery(param);
		OutBasePageDto<LogDto> page = new OutBasePageDto<>();
		page.setCount(logService.logQueryCnt(param));
		page.setData(logList);
		page.setPageNo(param.getPageNo()); 
		page.setPageSize(param.getPageSize());
		return page;
	}

	/**
	 * 	 接口详情 (id: 1632)
		 接口名称 日志详情查看
		 请求类型 post
		 请求Url  /manage/audit/logdetailQuery
	 * @param logId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path="/manage/audit/logdetailQuery/{logId}", method={RequestMethod.POST})
	@ResponseBody
	public List<OutGrantLogDetailDto> queryGrantLogDetail(@PathVariable("logId") String logId) throws Exception {
		return logService.queryGrantLogDetail(logId);
	}

	/**
	 *
	 * 	 接口详情 (id: 1630)
		 接口名称 注册日志查询
		 请求类型 post
		 请求Url  /manage/audit/registerlogQuery
	 * @param in
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path="/manage/audit/registerlogQuery", method={RequestMethod.POST})
	@ResponseBody
	public OutBasePageDto<OutRegisterLogQueryDto> queryRegisterLog(@Valid @RequestBody InRegisterLogQueryDto in) throws Exception {
		OutBasePageDto<OutRegisterLogQueryDto> res = logService.queryRegisterLog(in);
		return res;
	}
	/**
	 *
	 * 	 接口详情 (id: 1588)
		 接口名称 登录日志查询
		 请求类型 post
		 请求Url  /manage/audit/loginlogQuery
	 * @param in
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path="/manage/audit/loginlogQuery", method={RequestMethod.POST})
	@ResponseBody
	public OutBasePageDto<OutLoginLogQueryDto> queryRegisterLog(@Valid @RequestBody InLoginLogQueryDto in) throws Exception {
		OutBasePageDto<OutLoginLogQueryDto> res = logService.queryLoginLog(in);
		return res;
	}

}
