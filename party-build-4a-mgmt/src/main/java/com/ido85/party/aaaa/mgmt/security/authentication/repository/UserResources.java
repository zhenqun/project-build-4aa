/**
 * 
 */
package com.ido85.party.aaaa.mgmt.security.authentication.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;


/**
 * user 资源类
 * @author rongxj
 *
 */
public interface UserResources extends JpaRepository<User, String> {

	
	
	@Query("SELECT u from User u where u.username = :uname")
	User getUserByAccount(@Param("uname") String account);
	
	@Query("SELECT u from User u where u.email = :email")
	User getUserByUserEmail(@Param("email") String email);
	/***
	 * 修改密码
	 * @param password
	 * @param id
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("UPDATE User u set u.password = :password where u.id = :id")
	int changePassword(@Param("password")String password, @Param("id")Long id);

	/***
	 * 修改管理员状态
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("UPDATE User u set u.disabled = :disabled where u.id = :id")
	int modifyAdminStatus(@Param("id")Long userId, @Param("disabled")boolean status);

	/***
	 * 更新最后登录时间
	 * @param password
	 * @param id
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("UPDATE User u set u.lastLoginDate = :lastLoginDate where u.username = :name")
	void updateLashLoginDate(@Param("name")String name, @Param("lastLoginDate")Date date);

	/**
	 * 根据hash获取用户
	 * @param hash
	 * @return
	 */
	@Query("select u from User u where u.hash = :hash")
	User getUserByHash(@Param("hash")String hash);

	/**
	 * 修改状态
	 * @param ids
	 * @param s
	 */
	@Transactional
	@Modifying
	@Query("UPDATE User u set u.accountLocked = :state where u.id in :ids")
	void modifyBusinessUsersStatus(@Param("ids")List<String> ids, @Param("state")Boolean s);

	/**
	 * 注销安全员
	 * @param b
	 * @param ids
	 */
	@Transactional
	@Modifying
	@Query("UPDATE User u set u.accountExpired = :state where u.id in :ids")
	void cancellationBusinessUser(@Param("state")boolean b, @Param("ids")List<String> ids);

//	/**
//	 * 设置管理范围
//	 * @param orgId
//	 * @param ids
//	 */
//	@Transactional
//	@Modifying
//	@Query("UPDATE User u set u.manageOrgId = :manageOrgId,u.manageOrgName = :manageOrgName,u.manageOrgCode = :manageOrgCode where u.id in :ids")
//	void setManageScope(@Param("manageOrgId")String manageOrgId,@Param("manageOrgName")String manageOrgName,
//			@Param("manageOrgCode")String manageOrgCode, @Param("ids")List<String> ids);

	@Query("select u from User u where u.idCard = :idCard and u.isActivation='f'")
	User getUserByIdcardAndName( @Param("idCard")String idCard);

	@Query("select u from User u where u.id in :ids")
	List<User> getUserBuIds(@Param("ids")List<String> ids);
	
	/**
	 * 修改用户头像
	 * @param username
	 * @param logoUrl
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("UPDATE User u set u.logo = :logoUrl where u.username = :username")
	int modifyUserLogo(@Param("username")String username, @Param("logoUrl")String logoUrl);

	@Query("select u from User u where u.id in :ids")
	List<User> getUserByIds(@Param("ids")Set<String> userIds);

	@Transactional
	@Modifying
	@Query("update User u set u.accountLocked = 't' where u.id = :id")
	void lockUser(@Param("id")String userId);

	@Query("select u from User u where u.username = :username or u.idCard = :username or u.username = :username")
	User getUserByIdcardTelAccount(@Param("username")String name);

	@Query("select u from User u where u.id = :id")
	User getUserByUserId(@Param("id")String id);

	@Query("select u from User u where (u.idCard = :idCard or u.telePhone = :phone) and u.isActivation='t'")
	User getUserByIdCardTelphone(@Param("idCard")String idCard, @Param("phone")String telephone);

	@Query("select u from User u where u.telePhone = :phone and u.isActivation='t' ")
	User getUserByTelephone(@Param("phone") String telephone);

	@Transactional
	@Modifying
	@Query("update User u set u.password = :pwd where u.id = :id")
	int modifyUserPassword(@Param("id") String id, @Param("pwd") String pwd);

	@Query("select u from  User u where  u.telePhone = :oldMobile  and u.isActivation='t'")
	User checkMobile(@Param("oldMobile")String oldMobile);


	@Query("select u from  User u where  u.telePhone = :newMobile  and u.isActivation='t'")
	User checkNewMobile(@Param("newMobile")String newMobile);

	@Transactional
	@Modifying
	@Query("update User u set u.telePhone = :newMobile where u.id= :id ")
	int updateUserMobile(@Param("id")String id,@Param("newMobile")String newMobile);

	@Query(value = "select count(distinct t.user_id) from tf_f_login_log t,r_4a_user_role r " +
			"where t.user_id=r.user_id and r.role_id = '4' and to_char(t.login_date,'yyyy-MM-dd') like :day and t.login_type='1000'",
	nativeQuery = true)
    Long secuLoginCnt(@Param("day") String yesterDay);

	@Query(value = "select count(1) from tf_f_login_log t,r_4a_user_role r " +
			"where t.user_id=r.user_id and r.role_id = '4' and to_char(t.login_date,'yyyy-MM-dd') like :day and t.login_type='1000'",
			nativeQuery = true)
	Long secuLoginTimeCnt(@Param("day") String yesterDay);

	@Query(value = "select count(1) from tf_f_login_log t,r_4a_user_role r " +
			"where t.user_id=r.user_id and r.role_id = '5' and to_char(t.login_date,'yyyy-MM-dd') like :day and t.login_type='1000'",
			nativeQuery = true)
	Long autLoginTimeCnt(@Param("day") String yesterDay);

	@Query(value = "select count(DISTINCT t.user_id) from tf_f_login_log t,r_4a_user_role r " +
			"where t.user_id=r.user_id and r.role_id = '5' and to_char(t.login_date,'yyyy-MM-dd') like :day and t.login_type='1000'",
			nativeQuery = true)
	Long auLoginCnt(@Param("day") String yesterDay);

	@Query(value="select count(t.id_card) from t_4a_actors t,r_4a_user_role r where t.id=r.user_id and r.role_id='4'",nativeQuery = true)
    Long secuCnt();

	@Query(value="select count(t.id_card) from t_4a_actors t,r_4a_user_role r where t.id=r.user_id and r.role_id='4' " +
			"and to_char(t.create_date,'yyyy-MM-dd') like :day",nativeQuery = true)
	Long yesSecuCnt(@Param("day") String yesterDay);

	@Query(value="select count(t.id_card) from t_4a_actors t,r_4a_user_role r where t.id=r.user_id and r.role_id='5'",nativeQuery = true)
	Long autCnt();

	@Query(value="select count(t.id_card) from t_4a_actors t,r_4a_user_role r where t.id=r.user_id and r.role_id='5' " +
			"and to_char(t.create_date,'yyyy-MM-dd') like :day",nativeQuery = true)
	Long yesAutCnt(@Param("day") String yesterDay);

	@Query("select t from User t ,UserClientRel l where t.id = l.userId and t.hash in :hashs")
    List<User> checkHashAdmin(@Param("hashs") List<String> hashs);
}
