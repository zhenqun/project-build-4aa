package com.ido85.party.aaaa.mgmt.internet.application;

import java.util.List;

import com.ido85.party.aaaa.mgmt.dto.ApplicationAllotParam;
import com.ido85.party.aaaa.mgmt.internet.domain.InternetUser;
import com.ido85.party.aaaa.mgmt.internet.dto.ApplicationDto;
import com.ido85.party.aaaa.mgmt.internet.dto.IntegerUserDto;
import com.ido85.party.aaaa.mgmt.internet.dto.IntegerUserQueryParam;

/***
 * 网上 会员用户
 *
 */
public interface InternetUserApplication {
	
	/**
	 * 根据查询条件查询网站用户
	 * @param param
	 * @return
	 */
	public List<IntegerUserDto> getInternetUserByCondition(IntegerUserQueryParam param);

	/**
	 * 网站会员状态修改
	 * @param userId
	 * @param status
	 * @return
	 */
	public boolean modifyInternetUserStatus(Long userId, Integer status);

	/**
	 * 网站会员应用分配 
	 * @param param
	 * @return
	 */
	public boolean allotInternetUserApp(ApplicationAllotParam param);

	/**
	 * 网站会员查询总数
	 * @param param
	 * @return
	 */
	public Long getInternetUserCntByCondition(IntegerUserQueryParam param);

	/**
	 * 根据userId查询用户
	 * @param userId
	 * @return
	 */
	public InternetUser getInternetUserById(Long userId);

	/**
	 * 保存用户
	 * @param user
	 */
	public void saveInternetUser(InternetUser user);

	/**
	 * 查询internet应用
	 * @return
	 */
	public List<ApplicationDto> applicationQuery();


	/**
	 * 增加修改日志(sso)
	 * @throws Exception
	 */
	void addUpdateUserTeleLog(String oldTele,String newTele,String name,String idCard,String hash,
							  String updateHash,String updateBy,String updateName ,String verifyCode) throws Exception;

	List<Object[]> updateTeleByAdminToSimple(String hash);
}
