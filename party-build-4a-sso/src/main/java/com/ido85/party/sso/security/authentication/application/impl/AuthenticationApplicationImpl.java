package com.ido85.party.sso.security.authentication.application.impl;

import com.ido85.party.sso.distribute.generator.IdGenerator;
import com.ido85.party.sso.dto.RoleDto;
import com.ido85.party.sso.security.authentication.application.AuthenticationApplication;
import com.ido85.party.sso.security.authentication.domain.Retention;
import com.ido85.party.sso.security.authentication.repository.AuthenticationInfoResources;
import com.ido85.party.sso.security.authentication.repository.RetentionResources;
import com.ido85.party.sso.security.utils.StringUtils;
import com.ido85.party.sso.security.utils.XmlUtils;
import com.ido85.party.sso.ws.client.NciicClient;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;
@Named
public class AuthenticationApplicationImpl implements AuthenticationApplication {

	@PersistenceContext(unitName = "system")
	private EntityManager entityManager;
	@Inject
	private IdGenerator idGenerator;

	@PersistenceContext(unitName = "system")
	private EntityManager em;
	
	@Inject
	private RetentionResources retentionResources;
	
	@Inject
	private AuthenticationInfoResources authenticationInfoResources;
	
	@Override
	public List<RoleDto> authentiInfoQuery(List<String> uniqueKeyList,String relName,String idCard) {
		List<RoleDto> list = null;
		RoleDto roleDto = null;
		if(null != uniqueKeyList && uniqueKeyList.size() > 0){
			StringBuffer sb = new StringBuffer("select a.roleId,a.userId,r.name,a.relName,a.authenticationInfoId,a.delFlag,a.uniqueKey,r.description "
					+ "from com.ido85.party.sso.security.authentication.domain.AuthenticationInfo a,Role r where r.id = a.roleId and a.delFlag = '0' " +
					" and a.idCard = :idCard and a.validFlag = '0' ");
			sb.append(" and a.uniqueKey in :uniqueKey");
			Query q = entityManager.createQuery(sb.toString());
			q.setParameter("uniqueKey", uniqueKeyList);
			q.setParameter("idCard",idCard);
			List<Object[]> oList = q.getResultList();
			String name = null;
			if(null != oList && oList.size() > 0){
				list = new ArrayList<RoleDto>();
				for(Object[] o:oList){
					name = StringUtils.toString(o[3]);
					if(name.equals(relName)){
						roleDto = new RoleDto();
						roleDto.setRoleId(StringUtils.toString(o[0]));
						roleDto.setRoleName(StringUtils.toString(o[2]));
						roleDto.setUserId(StringUtils.toString(o[1]));
						roleDto.setAuthencationInfoId(StringUtils.toLong(o[4]));
						roleDto.setDelFlag(StringUtils.toString(o[5]));
						roleDto.setUniqueKey(StringUtils.toString(o[6]));
						roleDto.setRoleDes(StringUtils.toString(o[7]));
						list.add(roleDto);
					}
				}
			}
		}
		return list;
	}
	
	
	@Override
	public Map<String, Object> nciicAuthentication(String idCard, String name) {
		Map<String, Object> param = new HashMap<>();
		boolean nciisCheckResult = false;//是否通过实名认证
		//首先查询是否认证过
//		Retention r =retentionResources.getRetentionByIdcardAndName(idCard,name);
		Retention r = null;
		List<Retention> rList = retentionResources.getRetentionsByIdcardAndName(idCard,name);
		if(null != rList && rList.size() > 0){
			r = rList.get(0);
		}
		if(null == r){
			//实名
			String inConditions = "<?xml version='1.0' encoding='utf-8'?>\n" + "<ROWS>\n" + "<INFO>\n"
					+ "<SBM>八五创新</SBM>\n" + "</INFO>\n"
					+ "<ROW>\n"
					+ "<GMSFHM>公民身份号码</GMSFHM>\n"
					+ "<XM>姓名</XM>\n"
					+ "</ROW>\n"
					+ "<ROW FSD='370000' YWLX='党员认证'>\n"
					+ "<GMSFHM>"+idCard+"</GMSFHM>\n" 
					+ "<XM>"+name+"</XM>\n" + "</ROW>\n" + "</ROWS>";
			String result = "";
			try {
				NciicClient nciicClient = new NciicClient();
				result = nciicClient.executeClient(inConditions);
				System.out.println(result);
				String parseStr = XmlUtils.parseXml(result);
				if("一致一致".equals(parseStr)){
					nciisCheckResult = true;
				}
				//系统留存信息
				Retention retention = new Retention();
				retention.setIdCard(idCard);
				retention.setRelName(name);
				retention.setRetentionDate(new Date());
				retention.setRetentionId(idGenerator.next());
				retention.setResult(nciisCheckResult);
				retention.setContent(result);
				retentionResources.save(retention);
				if(nciisCheckResult){
					param.put("flag", "success");
					param.put("message", "实名认证成功!");
					return param;
				}else{
					param.put("flag", "fail");
					param.put("message", "实名认证失败!");
					return param;
				}
			} catch (Exception e) {
				e.printStackTrace();
				param.put("flag", "fail");
				param.put("message", "实名认证失败!");
				return param;
			}
		}else{
			boolean result = r.isResult();
			if(result){
				param.put("flag", "success");
				param.put("message", "实名认证成功!");
				return param;
			}else{
				param.put("flag", "fail");
				param.put("message", "实名认证失败!");
				return param;
			}
		}
	}


	@Override
	public void updateAuthencationState(List<Long> authencationInfoIds) {
		authenticationInfoResources.updateAuthencationState(authencationInfoIds);
	}


}
