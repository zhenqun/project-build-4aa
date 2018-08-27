package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.utils.RelativeDateFormat;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collection;

@Controller
//@RequestMapping(value = {"/manage/announcement"})
public class AnnouncementController {
//	@RequestMapping(value = {"", "/"}, method = {RequestMethod.GET })
	@PreAuthorize(value="hasRole('ROLE_SECURITY')")
	public String index (Model model) {
		model.addAttribute("menuName", "announcement");
		model.addAttribute("showApp", false);
		
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
		
		return "announcement/index";
	}
	
//	@RequestMapping(value = {"/update/{noticeId}"}, method = {RequestMethod.GET })
	@PreAuthorize(value="hasRole('ROLE_SECURITY')")
	public String update (@PathVariable("noticeId") String noticeId, Model model) {
		model.addAttribute("menuName", "announcement");
		model.addAttribute("showApp", false);

		if (StringUtils.isNoneBlank(noticeId) && "add".equals(noticeId)) {
			model.addAttribute("updateTitle", "添加公告");
		} else {
			model.addAttribute("noticeId", noticeId);
			model.addAttribute("updateTitle", "编辑公告");
		}
		
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
		
		return "announcement/editOrAdd";
	}
}
