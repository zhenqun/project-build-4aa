/**
 * 
 */
package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.business.application.ImportBusinessUserApplication;
import com.ido85.party.aaaa.mgmt.business.dto.CheckBusinessUserExistParam;
import com.ido85.party.aaaa.mgmt.business.dto.CheckBusinessUserParam;
import com.ido85.party.aaaa.mgmt.dto.assist.ImportAssistorDto;
import com.ido85.party.aaaa.mgmt.security.authentication.application.AssistUserApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.utils.RelativeDateFormat;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import com.ido85.party.aaaa.mgmt.security.utils.excel.ImportExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;



/**
 * 
 * @author rongxj
 *
 */
@Controller
public class UIController {

	@Autowired
	private ImportBusinessUserApplication ibuApp;

	@Autowired
	private AssistUserApplication assistUserApplication;

	@Value("${vpnSsoUrl}")
	private String vpnUrl = "";
	
	@RequestMapping(path={"/register"},method={RequestMethod.GET})
	public String register(Model model){
		model.addAttribute("vpnSsoUrl", vpnUrl);
		return "register";
	}

	@RequestMapping(path = {"/modify-vpn"}, method = RequestMethod.GET)
	public String modifyVpn(Model model){
		return "modify-vpn";
	}

	@RequestMapping(path = {"/modify-phone"}, method = RequestMethod.GET)
	public String modifyPhone(Model model){
		return "modify-phone";
	}
	
	@RequestMapping(path={"/"},method={RequestMethod.GET})
	public String forward(Principal pricipal,HttpServletRequest request,Model model){
		boolean isLogin = SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
		if(isLogin){
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//			User user = (User) authentication.getPrincipal();
//			SecurityExpressionOperations securtyOper = new WebSecurityExpressionRoot(authentication, null);
			Collection<? extends GrantedAuthority> aus = authentication.getAuthorities();
			if(null != aus){
				SimpleGrantedAuthority roleSecurity = new SimpleGrantedAuthority("ROLE_SECURITY");
				SimpleGrantedAuthority roleAuditor = new SimpleGrantedAuthority("ROLE_AUDITOR");
				if(aus.contains(roleSecurity)){
					return "redirect:/manage/worker-manage";
				}
				if(aus.contains(roleSecurity)){
					return "redirect:/manage/security-manage";
				}
				if(aus.contains(roleSecurity)){
					return "redirect:/manage/audit-manage";
				}
				if(aus.contains(roleAuditor)){
					return "redirect:/manage/audit-index";
				}
//				if(aus.contains(roleAuditor)){
//					return "redirect:/manage/reg-record";
//				}
//				if(aus.contains(roleAuditor)){
//					return "redirect:/manage/auth-record";
//				}
			}
		}
		return "login";
	}
	
	//获取权限
	@RequestMapping(value = {"/manage/getAuth"},method = RequestMethod.GET)
	@ResponseBody
	public Collection<? extends GrantedAuthority> getAuth() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority> aus = authentication.getAuthorities();
		
		return aus;
	} 
	
	
	//审计员管理
	@PreAuthorize(value="hasRole('ROLE_SECURITY')")
	@RequestMapping(value = {"/manage/audit-manage"},method = RequestMethod.GET)
	public String admin(Model model){
		model.addAttribute("menuName","audit");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(null != authentication){
			User user = (User) authentication.getPrincipal();
			
			if(null != user){
				Collection<? extends GrantedAuthority> aus = authentication.getAuthorities();
				if(null != aus){
					SimpleGrantedAuthority roleSecurity = new SimpleGrantedAuthority("ROLE_SECURITY");
					SimpleGrantedAuthority roleAuditor = new SimpleGrantedAuthority("ROLE_AUDITOR");
					if(aus.contains(roleSecurity)){
						model.addAttribute("ROLENAME", "安全员");
					}
					if(aus.contains(roleAuditor)){
						model.addAttribute("ROLENAME","审计员");
					}
				}
				model.addAttribute("name", user.getName());
				model.addAttribute("lastLoginDate", RelativeDateFormat.format(user.getLastLoginDate())); 
			}
		}
		return "audit-manage";
	}
	//业务管理员管理
	@PreAuthorize(value="hasRole('ROLE_SECURITY')")
	@RequestMapping(value = {"/manage/worker-manage"},method = RequestMethod.GET)
	public String worker(Model model){
		model.addAttribute("menuName","worker");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(null != authentication){
			User user = (User) authentication.getPrincipal();
			if(null != user){
				Collection<? extends GrantedAuthority> aus = authentication.getAuthorities();
				if(null != aus){
					SimpleGrantedAuthority roleSecurity = new SimpleGrantedAuthority("ROLE_SECURITY");
					SimpleGrantedAuthority roleAuditor = new SimpleGrantedAuthority("ROLE_AUDITOR");
					if(aus.contains(roleSecurity)){
						model.addAttribute("ROLENAME", "安全员");
					}
					if(aus.contains(roleAuditor)){
						model.addAttribute("ROLENAME","审计员");
					}
				}
//				model.addAttribute("orgName", user.getOrgName());
				model.addAttribute("name", user.getName());
				model.addAttribute("lastLoginDate", RelativeDateFormat.format(user.getLastLoginDate())); 
			}
		}
		return "worker-manage";
	}
	//安全员管理
	@PreAuthorize(value="hasRole('ROLE_SECURITY')")
	@RequestMapping(value = {"/manage/security-manage"},method = RequestMethod.GET)
	public String user(Model model){
		model.addAttribute("menuName","security");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(null != authentication){
			User user = (User) authentication.getPrincipal();
			if(null != user){
				Collection<? extends GrantedAuthority> aus = authentication.getAuthorities();
				if(null != aus){
					SimpleGrantedAuthority roleSecurity = new SimpleGrantedAuthority("ROLE_SECURITY");
					SimpleGrantedAuthority roleAuditor = new SimpleGrantedAuthority("ROLE_AUDITOR");
					if(aus.contains(roleSecurity)){
						model.addAttribute("ROLENAME", "安全员");
					}
					if(aus.contains(roleAuditor)){
						model.addAttribute("ROLENAME","审计员");
					}
				}
//				model.addAttribute("orgName", user.getOrgName());
				model.addAttribute("name", user.getName());
				model.addAttribute("lastLoginDate", RelativeDateFormat.format(user.getLastLoginDate())); 
			}
		} 
		return "security-manage";
	}

	//安全员管理
	@PreAuthorize(value="hasRole('ROLE_SECURITY')")
	@RequestMapping(value = {"/manage/auth-config"},method = RequestMethod.GET)
	public String authConfig(Model model){
		model.addAttribute("menuName","auth-config");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(null != authentication){
			User user = (User) authentication.getPrincipal();
			if(null != user){
				Collection<? extends GrantedAuthority> aus = authentication.getAuthorities();
				if(null != aus){
					SimpleGrantedAuthority roleSecurity = new SimpleGrantedAuthority("ROLE_SECURITY");
					SimpleGrantedAuthority roleAuditor = new SimpleGrantedAuthority("ROLE_AUDITOR");
					if(aus.contains(roleSecurity)){
						model.addAttribute("ROLENAME", "安全员");
					}
					if(aus.contains(roleAuditor)){
						model.addAttribute("ROLENAME","审计员");
					}
				}
//					model.addAttribute("orgName", user.getOrgName());
				model.addAttribute("name", user.getName());
				model.addAttribute("lastLoginDate", RelativeDateFormat.format(user.getLastLoginDate()));
			}
		}
		return "authConfig";
	}

	//配置辅助安全员角色包
	@PreAuthorize(value="hasRole('ROLE_SECURITY')")
	@RequestMapping(value = {"/manage/role-packs"},method = RequestMethod.GET)
	public String rolePacks(Model model){
		model.addAttribute("menuName","role-packs");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return "rolePacks";
	}

	//管理员申请备案
	@PreAuthorize(value="hasRole('ROLE_SECURITY')")
	@RequestMapping(value = {"/manage/manager-appliFiling"},method = RequestMethod.GET)
	public String managerAppliFiling(Model model){
		model.addAttribute("menuName","manager-appliFiling");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return "manager-appliFiling";
	}

	//添加辅助安全员
	@PreAuthorize(value="hasRole('ROLE_SECURITY')")
	@RequestMapping(value = {"/manage/assist-manage"},method = RequestMethod.GET)
	public String addAiderSecurity(Model model){
		model.addAttribute("menuName","assist-manage");

		return "assist-manage/assist-manage";
	}

	//辅助安全员管理
	@PreAuthorize(value="hasRole('ROLE_SECURITY')")
	@RequestMapping(value = {"/manage/aiderSecurity-manage"},method = RequestMethod.GET)
	public String aiderSecurityManage(Model model){
		model.addAttribute("menuName","aiderSecurity-manage");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return "aiderSecurity-manage";
	}

	//审计员首页
	@PreAuthorize(value="hasRole('ROLE_AUDITOR')")
	@RequestMapping(value = {"/manage/audit-index"},method = RequestMethod.GET)
	public String auditIndex(Model model){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(null != authentication){
			User user = (User) authentication.getPrincipal();
			if(null != user){
				Collection<? extends GrantedAuthority> aus = authentication.getAuthorities();
				if(null != aus){
					SimpleGrantedAuthority roleSecurity = new SimpleGrantedAuthority("ROLE_SECURITY");
					SimpleGrantedAuthority roleAuditor = new SimpleGrantedAuthority("ROLE_AUDITOR");
					if(aus.contains(roleSecurity)){
						model.addAttribute("ROLENAME", "安全员");
					}
					if(aus.contains(roleAuditor)){
						model.addAttribute("ROLENAME","审计员");
					}
				}
//				model.addAttribute("orgName", user.getOrgName());
				model.addAttribute("name", user.getName());
				model.addAttribute("lastLoginDate", RelativeDateFormat.format(user.getLastLoginDate()));
			}
		}
		return "audit-index";
	}
	
	//登录日志
	@PreAuthorize(value="hasRole('ROLE_AUDITOR')")
	@RequestMapping(value = {"/manage/login-record"},method = RequestMethod.GET)
	public String record(Model model){
		model.addAttribute("_pwdInfo", "666");
		model.addAttribute("menuName","login-record");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(null != authentication){
			User user = (User) authentication.getPrincipal();
			if(null != user){
				Collection<? extends GrantedAuthority> aus = authentication.getAuthorities();
				if(null != aus){
					SimpleGrantedAuthority roleSecurity = new SimpleGrantedAuthority("ROLE_SECURITY");
					SimpleGrantedAuthority roleAuditor = new SimpleGrantedAuthority("ROLE_AUDITOR");
					if(aus.contains(roleSecurity)){
						model.addAttribute("ROLENAME", "安全员");
					}
					if(aus.contains(roleAuditor)){
						model.addAttribute("ROLENAME","审计员");
					}
				}
//				model.addAttribute("orgName", user.getOrgName());
				model.addAttribute("name", user.getName());
				model.addAttribute("lastLoginDate", RelativeDateFormat.format(user.getLastLoginDate())); 
			}
		}
		return "login-record";
	}
	//注册日志
	@PreAuthorize(value="hasRole('ROLE_AUDITOR')")
	@RequestMapping(value = {"/manage/reg-record"},method = RequestMethod.GET)
	public String reg(Model model){
		model.addAttribute("menuName","reg-record");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(null != authentication){
			User user = (User) authentication.getPrincipal();
			if(null != user){
				Collection<? extends GrantedAuthority> aus = authentication.getAuthorities();
				if(null != aus){
					SimpleGrantedAuthority roleSecurity = new SimpleGrantedAuthority("ROLE_SECURITY");
					SimpleGrantedAuthority roleAuditor = new SimpleGrantedAuthority("ROLE_AUDITOR");
					if(aus.contains(roleSecurity)){
						model.addAttribute("ROLENAME", "安全员");
					}
					if(aus.contains(roleAuditor)){
						model.addAttribute("ROLENAME","审计员");
					}
				}
//				model.addAttribute("orgName", user.getOrgName());
				model.addAttribute("name", user.getName());
				model.addAttribute("lastLoginDate", RelativeDateFormat.format(user.getLastLoginDate())); 
			}
		}
		return "reg-record";
	}
	//授权日志
	@PreAuthorize(value="hasRole('ROLE_AUDITOR')")
	@RequestMapping(value = {"/manage/auth-record"},method = RequestMethod.GET)
	public String auth(Model model){
		model.addAttribute("menuName","auth-record");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(null != authentication){
			User user = (User) authentication.getPrincipal();
			if(null != user){
				Collection<? extends GrantedAuthority> aus = authentication.getAuthorities();
				if(null != aus){
					SimpleGrantedAuthority roleSecurity = new SimpleGrantedAuthority("ROLE_SECURITY");
					SimpleGrantedAuthority roleAuditor = new SimpleGrantedAuthority("ROLE_AUDITOR");
					if(aus.contains(roleSecurity)){
						model.addAttribute("ROLENAME", "安全员");
					}
					if(aus.contains(roleAuditor)){
						model.addAttribute("ROLENAME","审计员");
					}
				}
//				model.addAttribute("orgName", user.getOrgName());
				model.addAttribute("name", user.getName());
				model.addAttribute("lastLoginDate", RelativeDateFormat.format(user.getLastLoginDate())); 
			}
		}
		return "auth-record";
	}
	
	//修改密码
	@RequestMapping(path = {"/modify"}, method = RequestMethod.GET)
	public String modify(Model model){		
		return "modify";
	}
	
	//修改头像
	@RequestMapping(path = {"/head-pic"}, method = RequestMethod.GET)
	public String head(Model model){		
		return "head-pic";
	}
	
	//忘记密码1
	@RequestMapping(path = {"/forget"}, method = RequestMethod.GET)
	public String forget(Model model){		
		return "forget";
	}

	
	//导入业务员
	
	@RequestMapping(value = {"/exportUserExcl"}, method = RequestMethod.POST)
	@ResponseBody
	public List exportUserExcl (HttpServletRequest req) throws Exception {
		Part part = req.getPart("file"); 
		String header = part.getHeader("Content-Disposition");
        String fileName = header.substring(header.indexOf("filename=\"") + 10, header.lastIndexOf("\""));        
        InputStream in = part.getInputStream();
        ImportExcel ei = new ImportExcel(fileName, in, 0, 0);

		List<UserInfo> userList = ei.getDataList(UserInfo.class, null);
		if (null != userList) {
			userList.stream()
				.forEach(x -> {
					if (null != x) {
						String idCard = x.getIdCard();
						if (StringUtils.isNotBlank(idCard)) {
							x.setIdCard(idCard.toUpperCase());
						}
					}
				});
		}
		CheckBusinessUserExistParam param = new CheckBusinessUserExistParam();
		param.setClientId(req.getParameter("clientId"));

		param.setUsers(
			userList.stream()
				.map(x -> {
					CheckBusinessUserParam user = new CheckBusinessUserParam();
					user.setIdCard(x.getIdCard());
					user.setName(x.getRelName());
					return user;
				})
				.collect(Collectors.toList())
		);
		List<String> checkResult = ibuApp.checkBusinessUserExists(param);

		if (checkResult != null && checkResult.size() > 0) {
			userList.stream()
				.forEach(x -> {
					x.setIsExist(checkResult.indexOf(x.getIdCard() + "|" + x.getRelName()) > -1 ? "1" : "0");
				});
		}

		return userList;
   }

	@RequestMapping(value = {"/importAssistor"}, method = RequestMethod.POST)
	@ResponseBody
	public List<ImportAssistorDto> importAssistor(HttpServletRequest req) throws Exception {
		Part part = req.getPart("file");
		String header = part.getHeader("Content-Disposition");
		String fileName = header.substring(header.indexOf("filename=\"") + 10, header.lastIndexOf("\""));
		InputStream in = part.getInputStream();
		ImportExcel ei = new ImportExcel(fileName, in, 0, 0);

		List<ImportAssistorDto> userList = ei.getDataList(ImportAssistorDto.class, null);
		if (null != userList) {
			// 身份证号中的 X 大写
			userList.stream()
					.forEach(x -> {
						if (null != x) {
							String idCard = x.getIdCard();
							if (StringUtils.isNotBlank(idCard)) {
								x.setIdCard(idCard.toUpperCase());
							}
						}
					});
		}

		List<CheckBusinessUserParam> checkParams = userList.stream()
			.map(x -> {
				CheckBusinessUserParam user = new CheckBusinessUserParam();
				user.setIdCard(x.getIdCard());
				user.setName(x.getRelName());
				return user;
			})
			.collect(Collectors.toList());

		List<String> checkResult = assistUserApplication.checkAssistUserExists(checkParams);

		if (checkResult != null && checkResult.size() > 0) {
			userList.stream()
					.forEach(x -> {
						x.setIsExist(checkResult.indexOf(x.getIdCard() + "|" + x.getRelName()) > -1 ? "1" : "0");
					});
		}

		return userList;
	}
}
