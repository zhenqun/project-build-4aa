/**
 * 
 */
package com.ido85.party.sso.security.authentication.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.sso.security.authentication.domain.User;

/**
 * user 资源类
 * @author rongxj
 *
 */
public interface UserResources extends JpaRepository<User, String> {

	
	
	@Query("SELECT u from User u where u.username = :uname and u.accountExpired = false")
	User getUserByAccount(@Param("uname") String account);
	
//	@Query("SELECT u from User u where u.email = :email")
//	User getUserByUserEmail(@Param("email") String email);
	/***
	 * 修改密码
	 * @param password
	 * @param id
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("UPDATE User u set u.password = :password where u.id = :id")
	int changePassword(@Param("password")String password, @Param("id")String id);
	
	/***
	 * 更新最后登录时间
	 * @param password
	 * @param id
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("UPDATE User u set u.lastLoginDate = :lastLoginDate where u.email = :name or u.username = :name or u.idCard = :name")
	void updateLashLoginDate(@Param("name")String name, @Param("lastLoginDate")Date date);

	/***
	 * 实名认证修改
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("UPDATE User u set u.name = :name ,u.idCard = :idCard,u.hash = :hashCode where u.id = :id")
	int realnameAuthentication(@Param("id")String id, @Param("idCard")String idCard, @Param("name")String realName,@Param("hashCode")String hashCode);

	@Query("SELECT u from User u where u.telePhone = :telePhone and u.isActivation = 't' ")
	User getUserByPhone(@Param("telePhone")String telephone);
	
	@Query("select u from User u where u.telePhone = :username or u.username = :username or u.email = :username")
	User getUserByAccountEmailTelephone(@Param("username")String username);

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

	@Query("select u.logo from User u where u.username= :username")
	String getUserLogoByUsername(@Param("username")String username);

	@Query("select u from User u where u.name = :name and u.idCard = :idCard and u.isActivation ='f'")
	User getUserByIdcardAndName(@Param("name")String name, @Param("idCard")String idCard);

	/**
	 * e支部调用
	 * @param userIds
	 * @return
	 */
	@Query("select u from User u where u.id in :userIds and u.accountExpired = false ")
	List<User> getEBranchUserInfoByIds(@Param("userIds")List<String> userIds);
	
	@Query("select u from User u where u.id = :id")
	User getUserByUserId(@Param("id")String userId);

	@Query("select u from User u where u.username = :username or u.idCard = :username or u.telePhone = :username")
	User getUserbyAccountTelIdcard(@Param("username")String username);

	@Transactional
	@Modifying
	@Query("update User u set u.accountLocked = 't' where u.id = :id")
	void lockUser(@Param("id")String userId);

	@Query("select u from User u where u.idCard = :idCard")
	User getUserByIdcard(@Param("idCard")String idCard);

	@Query("select u from User u where (u.idCard = :idCard or u.telePhone = :telephone) and u.isActivation ='t' ")
	User getUserbyTelIdcard(@Param("telephone")String telephone, @Param("idCard")String idCard);

	@Query("select u from User u where telePhone = :oldMobie  and u.isActivation ='t' ")
	User checkMobile(@Param("oldMobie")String oldMobile);

	@Query("select u from User u where telePhone = :newMobile  and u.isActivation ='t' ")
	User checkNewMobile(@Param("newMobile")String newMobile);

	@Transactional
	@Modifying
	@Query("update User u set u.telePhone = :mobile where u.id = :id")
	int updateMobile(@Param("id")String id ,@Param("mobile")String mobile);

}
