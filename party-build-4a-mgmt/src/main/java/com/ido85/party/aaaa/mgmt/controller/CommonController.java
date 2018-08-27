package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.business.application.BusinessClientApplication;
import com.ido85.party.aaaa.mgmt.business.application.RoleApplication;
import com.ido85.party.aaaa.mgmt.business.domain.ClientExpand;
import com.ido85.party.aaaa.mgmt.business.dto.ClientRolesDto;
import com.ido85.party.aaaa.mgmt.business.dto.ClientsRolesDto;
import com.ido85.party.aaaa.mgmt.business.dto.RoleDto;
import com.ido85.party.aaaa.mgmt.business.resources.CLientExpandResources;
import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.dto.OrganizationDto;
import com.ido85.party.aaaa.mgmt.dto.ResetVpnPassword;
import com.ido85.party.aaaa.mgmt.dto.SendMessageDto;
import com.ido85.party.aaaa.mgmt.dto.SendMessageParam;
import com.ido85.party.aaaa.mgmt.dto.role.RoleQueryDto;
import com.ido85.party.aaaa.mgmt.dto.role.RoleQueryParam;
import com.ido85.party.aaaa.mgmt.dto.userinfo.CheckVerifyCode;
import com.ido85.party.aaaa.mgmt.security.authentication.application.CommonApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.MessageLog;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.SmTemplate;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.MessageLogResources;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.SmTemplateResouces;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserClientRelResource;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserResources;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import com.ido85.party.aaaa.mgmt.security.utils.SmsService;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.*;


@RestController
public class CommonController {

	@Inject
	private RoleApplication roleApplication;
	@Inject
	private BusinessClientApplication businessClientApplication;

	@Inject
	private SmTemplateResouces smTemplateResouces;

	@Inject
	private MessageLogResources messageLogResources;

	@Inject
	private IdGenerator idGenerator;

	@Inject
	private SmsService smsService;

	@Inject
	private UserClientRelResource userClientRelResource;


	@Inject
	private CLientExpandResources cLientExpandResources;
	@Inject
	private RestTemplate restTemplate;

	@Inject
	private CommonApplication commonApplication;

	@Inject
	private UserResources userres;

	/**
	 * 获取当前登录用户获取组织机构信息
	 * @return
	 */
	@RequestMapping(path="/manage/common/getOrganizations", method={RequestMethod.GET})
	@ResponseBody
	public List<OrganizationDto> getOrganizations(String orgId,String clientId,String treeLevel,String keyword){
		List<OrganizationDto> orgDtoList = new ArrayList<OrganizationDto>();
		List<OrganizationDto> neworgDtoList = null;
		User user = null;
		user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(null != user){
			//查询该用户该应用的管理范围
			String manageId = userClientRelResource.getManageIdByUserIdClientId(user.getId(),clientId);
			//查询该应用的url
			ClientExpand ce = cLientExpandResources.getClientById(clientId);
			if(null != ce){
				String url = ce.getLevelUrl();
				String isEureka = ce.getIsEureka();
				String orgSerachUrl = ce.getSearchUrl();
				if(StringUtils.isNull(keyword)){
					orgDtoList = leftTree(isEureka,url,manageId,orgId);
				}else{
					orgDtoList = searchLeftTree(isEureka,orgSerachUrl,keyword,manageId,orgId);
				}
			}
		}
		//如果ou为空，则改为node0
		if(ListUntils.isNotNull(orgDtoList)){
			for(OrganizationDto dto:orgDtoList){
				if(StringUtils.isNull(dto.getOuName())){
					dto.setOuName("node0");
				}
			}
		}
		//如果为特殊账号 则显示全部树
		String username = user.getUsername();
		if(username.equals("370703198805270014")){
			return orgDtoList;
		}
		//如果type为1，则只能显示一级
		if("1".equals(treeLevel)){
			if(ListUntils.isNotNull(orgDtoList)){
				for(OrganizationDto dto:orgDtoList){
					dto.setHasChildren(0);
				}
			}
		}
		//如果type为0，则只显示本身一级
		if("0".equals(treeLevel)){
			if(ListUntils.isNotNull(orgDtoList)){
				neworgDtoList = new ArrayList<OrganizationDto>();
				neworgDtoList.add(orgDtoList.get(0));
				return neworgDtoList;
			}
		}
		return orgDtoList;
	}

	private List<OrganizationDto> searchLeftTree(String isEureka, String orgSerachUrl, String keyword, String manageId, String orgId) {
		List<OrganizationDto> orgDtoList = new ArrayList<OrganizationDto>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		Map<String, String> map = new HashMap<>();
		map.put("keyWord",keyword);
		map.put("topOrgId",manageId);
		map.put("maxSearchNum","10");
		HttpEntity<Map<String, String>> formEntity = new HttpEntity(map,headers);

		//如果是服务发现
		try {
			if("1".equals(isEureka)){
				if(StringUtils.isNull(orgId)){
					orgDtoList = Arrays.asList(restTemplate.postForObject(orgSerachUrl, formEntity, OrganizationDto[].class));
				}else{
					orgDtoList = Arrays.asList(restTemplate.postForObject(orgSerachUrl, formEntity, OrganizationDto[].class));
				}
			}
			if("0".equals(isEureka)){
				RestTemplate restTemplat2 = new RestTemplate();
				if(StringUtils.isNull(orgId)){
					orgDtoList = Arrays.asList(restTemplat2.postForObject(orgSerachUrl, formEntity, OrganizationDto[].class));
				}else{
					orgDtoList = Arrays.asList(restTemplat2.postForObject(orgSerachUrl, formEntity, OrganizationDto[].class));
				}
			}
			if (ListUntils.isNotNull(orgDtoList)){
				OrganizationDto dto = orgDtoList.get(0);
				dto.setParentId(null);
			}
			if(ListUntils.isNull(orgDtoList)){
				return new ArrayList<OrganizationDto>();
			}
			return orgDtoList;
		} catch (Exception e){
			return null;
		}
	}

	private List<OrganizationDto> leftTree(String isEureka,String url,String manageId,String orgId) {
		List<OrganizationDto> orgDtoList = new ArrayList<OrganizationDto>();
		//如果是服务发现
		if("1".equals(isEureka)){
			if(StringUtils.isNull(orgId)){
				orgDtoList = Arrays.asList(restTemplate.getForObject(url+manageId, OrganizationDto[].class));
			}else{
				orgDtoList = Arrays.asList(restTemplate.getForObject(url+orgId,  OrganizationDto[].class));
			}
		}
		if("0".equals(isEureka)){
			RestTemplate restTemplat2 = new RestTemplate();
			if(StringUtils.isNull(orgId)){
				orgDtoList = Arrays.asList(restTemplat2.getForObject(url+manageId, OrganizationDto[].class));
			}else{
				orgDtoList = Arrays.asList(restTemplat2.getForObject(url+orgId,  OrganizationDto[].class));
			}
		}
		OrganizationDto dto = orgDtoList.get(0);
		dto.setParentId(null);
		return orgDtoList;
	}

	/**
	 * 查询应用以及角色
	 * @return
	 */
	@RequestMapping(path="/manage/business/clientQuery", method={RequestMethod.GET})
	@ResponseBody
	public List<ClientRolesDto> getClientsRoles(){
		List<ClientRolesDto> dtoList = new ArrayList<ClientRolesDto>();
		dtoList = businessClientApplication.getClientsRoles();

		return dtoList;
	}


	/**
	 * 查询应用以及角色
	 * @return
	 */
	@RequestMapping(path="/manage/business/clientRolesQuery", method={RequestMethod.GET})
	@ResponseBody
	public List<ClientRolesDto> getClientsAndRoles(){
		List<ClientRolesDto> dtoList =getClientsRoles();
		RoleQueryParam param = null;
		List<RoleDto> roles = new ArrayList<>();
		RoleDto role = new RoleDto();
		if(dtoList!=null){
			for (ClientRolesDto c: dtoList) {
				roles = new ArrayList<>();
				param = new RoleQueryParam();
				param.setPageNo(1);
				param.setPageSize(20);
				param.setClientId(c.getClientId());
				try {
					List<RoleQueryDto> roleList = roleApplication.roleQuery(param);
					if(roleList!=null){
                        for (RoleQueryDto roleQ: roleList) {
                            role = new RoleDto();
                            role.setRoleId(StringUtils.toLong(roleQ.getRoleId()));
                            role.setRoleName(roleQ.getRoleName());
                            role.setDescription(roleQ.getRoleDescription());
							role.setDetail(roleQ.getDetail());
                            roles.add(role);
                        }
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}

				c.setRoles(roles);
			}
		}
		return dtoList;
	}
	/**
	 * 发送短信
	 * @return
	 */
	@RequestMapping(path="/sendMessage", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,String> sendMessage(@Valid @RequestBody SendMessageParam param){
		Map<String,String> map = new HashMap<String,String>();
		String telephone = param.getTelephone();
		String type = param.getType();
		String verifycode = StringUtils.getRandomNum(6);
		MessageLog messageLog = new MessageLog();
		//查模板
		SmTemplate smTemplate = smTemplateResouces.getTemplateByType(type);
		StringBuffer messageContent = null;
		String expirdMinute = null;
		if(null != smTemplate){
			messageContent = new StringBuffer();
			expirdMinute = smTemplate.getExpireMinute();	
			messageContent.append(smTemplate.getSmTemplateOrgname()+" "+
					smTemplate.getSmTemplateName()+smTemplate.getSmTemplateContent());
		}
		String content = String.format(messageContent.toString(), verifycode,expirdMinute);
		SendMessageDto result = null;
		//设置messageLog值
		messageLog.setMessageContent(content);
		messageLog.setCreateDate(new Date());
		messageLog.setMessageType(type);
		messageLog.setVerifycode(verifycode);
		messageLog.setVerifycodeId(idGenerator.next());
		messageLog.setTelephone(telephone);
		messageLog.setExpireDate(smsService.calculatMessageExpiredDate(expirdMinute));
		//发送短信
		try {
			result = smsService.sendverificationCode(telephone,content);
			if(null != result){
				messageLog.setDetail(result.getMessage());
				String status = result.getReturnstatus();
				if("Success".equals(status)){
					messageLog.setSuccess(true);
					map.put("flag", "success");
					map.put("message", "发送成功!");
				}else if("Faild".equals(status)){
					messageLog.setSuccess(false);
					map.put("flag", "fail");
					map.put("message", "发送失败!");
				}
			}
		} catch (Exception e){
			messageLog.setDetail(e.getMessage());
			messageLog.setSuccess(false);
		} finally {
			messageLogResources.save(messageLog);
		}
		return map;
	}

	/**
	 * 检测验证码是否正确
	 * @param param
	 * @return
     */
	@RequestMapping(path="/forget/checkVerifyCode", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> checkVerifyCode(@Valid @RequestBody CheckVerifyCode param){
		Map<String,Object> result = new HashMap<>();
			String telephone = param.getTelephone();
			//检测用户手机是否已经注册
			User user = userres.getUserByTelephone(telephone);
			if(null == user){
				result.put("flag","flag");
				result.put("message","该手机未注册!");
			return result;
		}
		return commonApplication.isVerifyCodeValid(param.getTelephone(),param.getType(),param.getVerifyCode());
	}

	/**
	 * 上级重置vpn密码
	 * @return
	 */
	@RequestMapping(path="/manage/resetVpnPassword", method={RequestMethod.POST})
	@ResponseBody
	public boolean resetVpnPassword(@Valid ResetVpnPassword param){
		boolean flag = commonApplication.resetVpnPassword(param);
		return flag;
	}

}
