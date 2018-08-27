/**
 * 
 */
package com.ido85.party.sso.security.authentication.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import com.ido85.party.sso.platform.data.datasource.TargetDataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import com.ido85.party.sso.security.authentication.domain.User;

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
	int changePassword(@Param("password")String password, @Param("id")String id);
	
	/***
	 * 实名认证修改
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("UPDATE User u set u.name = :name ,u.idCard = :idCard ,u.hash = :hashCode where u.id = :id")
	int realnameAuthentication(@Param("id")String id, @Param("idCard")String idCard, @Param("name")String realName,@Param("hashCode")String hashCode);
	
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

	@Query("SELECT u from User u where u.telePhone = :telePhone ")
	User getUserByPhone(@Param("telePhone")String telephone);
	
	@Query("select u from User u where u.telePhone = :username or u.username = :username or u.email = :username")
	UserDetails getUserByAccountEmailTelephone(@Param("username")String username);
	
	/**
	 * 修改用户头像
	 * @param username
	 * @param logoUrl
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("UPDATE User u set u.logo = :logoUrl where u.id = :userId")
	int modifyUserLogo(@Param("userId")String userId, @Param("logoUrl")String logoUrl);

	@Query("select u.logo from User u where u.username= :username")
	String getUserLogoByUsername(@Param("username")String username);

	@Query("select u from User u where u.id = :id")
	User getUserByUserId(@Param("id")String userId);

	@TargetDataSource(name = "read")
	@Query("select u from User u where u.username = :username or u.telePhone = :username or u.idCardHash = :hash")
	User getUserbyAccountTelIdcard(@Param("username")String username,@Param("hash")String hash);

	@Transactional
	@Modifying
	@Query("update User u set u.accountLocked = 't' where u.id = :id")
	void lockUser(@Param("id")String userId);

	@Query("select u from User u where u.accountExpired = 'f' and u.hash in :hashs")
	List<User> queryUserInfoByUserId(@Param("hashs")List<String> hashs);

	@Query("select u from User u where u.id = :userId")
	User getUserById(@Param("userId") String userId);

	@Transactional
	@Modifying
	@Query("update User set expireDate = :date where id = :userId")
    void setUserExpireDate(@Param("userId") String userId, @Param("date") Date date);

//	@Query("select u from User u where u.orgId = :orgId")
//	List<User> getUserByOrgId(@Param("orgId")String orgId);

	@Query(value = "select u.hash ,(to_char(u.create_date, 'yyyyMMddhh24miss')) as create_date,u.tele_phone from t_4a_actors u where to_char(u.create_date, 'yyyy-MM-dd hh24:mi:ss') >= :endTime  order by u.create_date ASC ",nativeQuery = true)
	List<Object[]> getUserHash(@Param("endTime")String endTime);

	@Query(value ="select (to_char(u.create_date, 'yyyy-MM-dd hh24:mi:ss')) as create_date from t_4a_actors u where u.hash = :hashValue",nativeQuery = true)
	String getUserCreateTime(@Param("hashValue")String hashValue);

	@Query(value ="select (to_char(u.update_date, 'yyyy-MM-dd hh24:mi:ss')) as update_date from t_4a_actors u where u.hash = :hashValue",nativeQuery = true)
	String getUserUpdateTime(@Param("hashValue")String hashValue);

	@Query("select t from User t where t.hash = :hash")
    User getUserByIdcardHash(@Param("hash") String hash);
	@Query("select r from User r where r.id= :id")
	int getUserId(@Param("id") Long id);



	@Query(value = "select u.hash ,(to_char(u.create_date, 'yyyyMMddhh24miss')) as create_date,u.tele_phone from t_4a_actors u where u.update_date is not NULL and  to_char(u.update_date, 'yyyy-MM-dd hh24:mi:ss') >= :updateTime  order by u.update_date ASC ",nativeQuery = true)
	List<Object[]> getUserUpdateTimeList(@Param("updateTime")String updateTime);

	@Query(value = "select u.hash ,(to_char(u.create_date, 'yyyyMMddhh24miss')) as create_date,u.tele_phone from t_4a_actors u where u.hash= :hash",nativeQuery = true)
	List<Object[]> updateTeleByAdminToSimple(@Param("hash")String hash);

	@Transactional
	@Modifying
	@Query("update User u set u.username = :telePhone, u.telePhone= :telePhone, u.updateDate= :updateDate  where u.hash = :hash")
	int updateUserTele(@Param("telePhone")String telePhone,@Param("hash")String hash,@Param("updateDate")Date updateDate);
}
