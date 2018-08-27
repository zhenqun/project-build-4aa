/**
 * 
 */
package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.dto.expand.ChangeVPNBatchDto;
import com.ido85.party.aaaa.mgmt.dto.ldap.AddAccountDto;
import com.ido85.party.aaaa.mgmt.dto.ldap.AddBatchAccountDto;
import com.ido85.party.aaaa.mgmt.dto.ldap.AddTemporaryAccountDto;
import com.ido85.party.aaaa.mgmt.dto.ldap.UpdatePasswordParam;
import com.ido85.party.aaaa.mgmt.ldap.application.LdapApplication;
import com.ido85.party.aaaa.mgmt.ldap.crypt.SHA512Crypt;
import com.ido85.party.aaaa.mgmt.ldap.domain.Account;
import com.ido85.party.aaaa.mgmt.ldap.domain.OrgUnit;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import com.ido85.party.aaaa.mgmt.service.LdifService;
import com.ido85.party.aaaa.mgmt.service.OrgUnitService;
import com.ido85.party.aaaa.mgmt.service.SyncService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rongxj
 *
 */
@RestController
public class AccountMgmtController {
	
	@Inject
	private SyncService service;
	
	@Inject
	private OrgUnitService orgUnitService;
	
	@Inject
	private LdapTemplate ldapTemplate;

	@Inject
	private LdifService ldifService;
	
	@Value("${ldap.base-dn}")
	private String baseDn;


	@PersistenceContext(unitName = "business")
	private EntityManager businessEntity;

	@PersistenceContext(unitName = "admin")
	private EntityManager adminEntity;


	@Inject
	private LdapApplication ldapApplication;

	@RequestMapping(value = "/account/saveTemporaryAccount",method={RequestMethod.POST})
	@Transactional
	public boolean saveTemporaryAccount(@Valid @RequestBody List<AddTemporaryAccountDto> dtoList){
		
		Account account = null;
//		List<Account> accountList = null;
		if(dtoList != null && dtoList.size()>0){
//			accountList = new ArrayList<Account>();
			for(AddTemporaryAccountDto dto:dtoList){
				String username = dto.getIdCard();
				//创建ldap 对象
				account = new Account();
				account.setGidNumber(1000);
				account.setHomeDirectory("/home/guests/PreLicensing/"+username);
				account.setMail(null);
				account.setName(username);
				account.setOu("PreLicensing");
				account.setSurname(username);
				account.setUid(username);
				account.setUidNumber(1000);
				account.setUserPassword(SHA512Crypt.sha512Crypt("123456"));
//					account.setDn(service.buildDn(account.getUid(), account.getOu()));
				account.setShadowLastChange(17277);
				account.setShadowMax(365);
				account.setShadowMin(5);
				account.setShadowWarning(30);
//				accountList.add(account);
				try {
					service.save(account);
				} catch (NameAlreadyBoundException e) {
					//如果已经存在则为已经成功
					return true;
				}
			}
//			service.saveAll(accountList);
		}
		return true;
	}
	
	@RequestMapping(value="/orgs",method={RequestMethod.POST})
	public Iterable<OrgUnit> getOrgUnits(){
		Iterable<OrgUnit> orgs = orgUnitService.getOrgUnits();

		ldifService.exportLdif("People", "out/person.ldif");
		
		return orgs;
	}



	@RequestMapping(value="/saveOrg",method={RequestMethod.POST})
	public OrgUnit newOrg(){
		OrgUnit org = new OrgUnit();
		org.setOu("PreLicensing");
		orgUnitService.newOrg(org);
		return org;
	}
	
	
	@RequestMapping(value="/account/saveAccount",method={RequestMethod.POST})
	public boolean saveAccount(@Valid @RequestBody AddAccountDto dto){
		Account account = null;
//		String username = dto.getUsername();
		String ou = dto.getOu();
		String password = dto.getPassword();
		String idCard = dto.getIdCard();
//		Ou ou = ouApplication.getOuByOrgId(orgId);
		if(null != ou){
			//创建ldap 对象
			account = new Account();
			account.setGidNumber(1000);
			account.setHomeDirectory("/home/"+idCard);
			account.setMail(null);
			account.setName(idCard);
			account.setOu(ou);
			account.setSurname(idCard);
			account.setUid(idCard);
			account.setUidNumber(10001);
			if(StringUtils.isNull(password)){
				account.setUserPassword(SHA512Crypt.sha512Crypt(idCard.substring(idCard.length()-6, idCard.length()),
						idCard.substring(idCard.length()-6, idCard.length())));
			}else {
				account.setUserPassword(password);
			}
//					account.setDn(service.buildDn(account.getUid(), account.getOu()));
			account.setShadowLastChange(17277);
			account.setShadowMax(365);
			account.setShadowMin(5);
			account.setShadowWarning(30);
			try {
				service.save(account);
			} catch (NameAlreadyBoundException e) {
				//如果已经存在则为已经成功
				return true;
			} catch (Exception e) {
				return false;
			}
//			//删除原账号
//			Account at = service.getAccount(idCard, "PreLicensing");
//			if(null != at){
//				service.delete(at);
//			}
			return true;
		}
		return false;
	}
	
	
	@RequestMapping(value="/account/saveBatchAccount",method={RequestMethod.POST})
	public boolean saveBatchAccount(@Valid @RequestBody AddBatchAccountDto param){
		List<Account> accountList = null;
		String username = param.getUsername();
		Account account = null;
		String str = null;
		if(null != username){
			accountList = new ArrayList<>();
			String[] userStr = username.split(",");
			for(int i=0;i<userStr.length;i++){
				str = userStr[i];
				account = new Account();
				account.setGidNumber(1000);
				account.setHomeDirectory("/home/"+str);
				account.setMail(null);
				account.setName(str);
				account.setOu("node38");
				account.setSurname(str);
				account.setUid(str);
				account.setUidNumber(10001);
				account.setUserPassword(SHA512Crypt.sha512Crypt(str.substring(str.length()-6, str.length()),
						str.substring(str.length()-6, str.length())));
				account.setShadowLastChange(17277);
				account.setShadowMax(365);
				account.setShadowMin(5);
				account.setShadowWarning(30);
				accountList.add(account);
				service.save(account);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * 修改密码
	 * @param param
	 * @return
	 */
	@RequestMapping(value="/account/updatePassword",method={RequestMethod.POST})
	public Map<String,String> updatePassword(@Valid @RequestBody UpdatePasswordParam param){
		Map<String,String> map = new HashMap<>();
//		String oldPwd = param.getOldPwd();
		String newPwd = param.getNewPwd();
		Account at = service.getAccount(param.getUid(), param.getOu());
		if(null == at){
			map.put("flag", "fail");
			map.put("message", "账号不存在");
			return map;
		}else{
//			String pwd = at.getUserPassword();
//			if(!SHA512Crypt.matches(oldPwd, pwd)){
//				map.put("flag", "fail");
//				map.put("message", "原密码错误");
//				return map;
//			}else{
				at.setUserPassword(newPwd);
				service.save(at);
				map.put("flag", "success");
				map.put("message", "修改成功");
				return map;
//			}
		}
	}

	/**
	 * 未创建vpn的账号
	 */
	@RequestMapping(value="/account/noVpn",method={RequestMethod.POST})
	public void noVpn(){
		AddAccountDto dto = null;
		String uid = null;
		String ou = null;
		Account account = null;
		boolean flag = false;
		//查出库里已经存储的所有账号
		StringBuffer sb = new StringBuffer("select distinct v.vpn,v,ou from r_4a_user_vpn v ");
		StringBuffer sb2 = new StringBuffer("select DISTINCT v.vpn,v.ou_name from r_4a_user_vpn v");
		Query q=businessEntity.createNativeQuery(sb.toString());
		Query q2=adminEntity.createNativeQuery(sb2.toString());
		List<Object[]> bObjects = q.getResultList();
		List<Object[]> aObjects = q2.getResultList();
		//查询出未开通的
		for(Object[] o:bObjects){
			uid = StringUtils.toString(o[0]);
			ou = StringUtils.toString(o[1]);
			account = service.getAccount(uid,ou);
			if(null == account){
				dto = new AddAccountDto();
				dto.setOu(ou);
				dto.setIdCard(uid);
				System.out.println("-------------开始创建账号------------"+"ou:"+ou+",uid:"+uid);
//				flag = ldapApplication.createVPN(dto);
//				if(flag){
//					System.out.println("创建成功!"+"ou:"+ou+",uid:"+uid);
//				}
			}
		}
		System.out.println("-------------业务员创建完毕!---------------------");
		for(Object[] o:aObjects){
			uid = StringUtils.toString(o[0]);
			ou = StringUtils.toString(o[1]);
			account = service.getAccount(uid,ou);
			if(null == account){
				dto = new AddAccountDto();
				dto.setOu(ou);
				dto.setIdCard(uid);
				System.out.println("-------------开始创建账号------------"+"ou:"+ou+",uid:"+uid);
//				flag = ldapApplication.createVPN(dto);
//				if(flag){
//					System.out.println("创建成功!"+"ou:"+ou+",uid:"+uid);
//				}
			}
		}
		System.out.println("-------------安全员审计员创建完毕!---------------------");
	}

}
