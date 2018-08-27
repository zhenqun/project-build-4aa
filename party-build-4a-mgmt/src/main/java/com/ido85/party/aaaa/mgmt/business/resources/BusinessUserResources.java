package com.ido85.party.aaaa.mgmt.business.resources;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.business.domain.BusinessUser;


public interface BusinessUserResources extends JpaRepository<BusinessUser, String> {

	/***
	 * 修改业务管理员状态
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("UPDATE BusinessUser u set u.disabled = :disabled where u.id = :id")
	int modifyBusinessUserStatus(@Param("id")Long userId, @Param("disabled")boolean status);

	/**
	 * 根据hash获取用户
	 * @param hash
	 * @return
	 */
	@Query("select u from BusinessUser u where u.hash = :hash")
	BusinessUser getUserByHash(@Param("hash")String hash);

	/**
	 * 根据ids获取所有用户
	 * @param ids
	 * @return
	 */
	@Query("select u from BusinessUser u where u.id in :ids")
	List<BusinessUser> getUserByIds(@Param("ids")List<String> ids);

	/**
	 * 根据用户ids  改变用户状态  锁定状态
	 * @param ids
	 * @param s
	 */
	@Transactional
	@Modifying
	@Query("UPDATE BusinessUser u set u.accountLocked = :state where u.id in :ids")
	int modifyBusinessUsersStatus(@Param("ids")List<String> ids, @Param("state")Boolean s);

	/**
	 * 注销用户
	 * @param ids
	 */
	@Transactional
	@Modifying
	@Query("UPDATE BusinessUser u set u.accountExpired = :state where u.id in :ids")
	void cancellationBusinessUser(@Param("state")boolean state,@Param("ids")List<String> ids);
//
//	/**
//	 * 为业务管理员设置管理范围（批量）
//	 * @param orgId
//	 * @param ids
//	 */
//	@Transactional
//	@Modifying
//	@Query("UPDATE BusinessUser u set u.manageOrgId = :manageOrgId,u.manageOrgName = :manageOrgName,u.manageOrgCode = :manageOrgCode where u.id in :ids")
//	void setManageScope(@Param("manageOrgId")String manageOrgId,@Param("manageOrgName")String manageOrgName,@Param("manageOrgCode")String manageOrgCode, @Param("ids")List<String> ids);

	@Query("select u from BusinessUser u where u.id=:userId")
	BusinessUser getUserBuId(@Param("userId")String userId);

	@Query("select u from BusinessUser u where u.hash in :hashs")
	List<BusinessUser> findUsersByHash(@Param("hashs")List<String> hashs);

	@Query("select u from BusinessUser u where u.id in :ids")
	List<BusinessUser> getUserBuIds(@Param("ids") List<String> only1List);

	@Query(value = "select count(1) from tf_f_login_log t where to_char(t.login_date,'yyyy-MM-dd') like :day and t.login_type ='1000'",nativeQuery = true)
    Long adminLoginTimeCnt(@Param("day") String yesterDay);

	@Query(value = "select count(DISTINCT t.user_id) from tf_f_login_log t where to_char(t.login_date,'yyyy-MM-dd') like :day and t.login_type ='1000'",nativeQuery = true)
	Long adminLoginCnt(@Param("day") String yesterDay);

	@Query(value = "select count(distinct t.id_card) from t_4a_actors t where to_char(t.create_date,'yyyy-MM-dd') like :day",nativeQuery = true)
    Long yesAdminCnt(@Param("day") String yesterDay);

    @Query(value = "select count(distinct t.id_card) from t_4a_actors t",nativeQuery = true)
	Long adminCnt();


    @Query(value = "select t.* from t_4a_actors t where t.id in (select t.id from t_4a_actors t) and t.id not in (select r.user_id from r_4a_user_vpn r)" ,nativeQuery = true)
	List<BusinessUser> getUserAndIdCard();

	@Query("select u from  BusinessUser u where  u.telePhone = :telephone  and u.isActivation='t'")
	BusinessUser checkMobile(@Param("telephone")String telephone);
    @Query("select u from BusinessUser u,ClientUserRel l,com.ido85.party.aaaa.mgmt.business.domain.UserRole r where u.id = l.userId and u.id = r.userId and u.hash in :hashs")
    List<BusinessUser> checkHashAdmin(@Param("hashs") List<String> hashs);


	@Query(value ="select  count(DISTINCT t.id) from t_4a_actors t ,r_4a_user_role r where  t.id=r.user_id  and r.role_id='8' and t.hash = :hash ",nativeQuery = true)
	Long checkIsAssistUser(@Param("hash") String hash);

	@Query("select u from BusinessUser u where u.id=:userId ")
	BusinessUser getUserByOrgAndPeople(@Param("userId")String userId);

}
