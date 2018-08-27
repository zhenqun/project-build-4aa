package com.ido85.party.aaaa.mgmt.internet.resource;

import javax.transaction.Transactional;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.internet.domain.InternetUser;

import java.util.Date;
import java.util.List;


public interface InternetUserResources extends JpaRepository<InternetUser, Long> {

	/***
	 * 修改网站会员状态
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("UPDATE InternetUser u set u.disabled = :disabled where u.id = :id")
	int modifyAdminStatus(@Param("id")Long userId, @Param("disabled")boolean status);

	@Query("select count(distinct t.id) from InternetUser t,InternetUserRole r where t.id = r.userId and r.roleId = '1' and r.delFlag = '0'")
	Long registerCnt();

	@Query(value = "select count(DISTINCT t.id) from t_4a_actors t,r_4a_user_role r where t.id=r.user_id and r.role_id='1' and r.del_flag ='0' and " +
			"to_char(t.create_date,'yyyy-MM-dd') like :day",nativeQuery = true)
	Long yesRegisterCnt(@Param("day") String yesterDay);

	@Query("select t from InternetUser t where t.hash = :hash")
	InternetUser getUserByIdcardHash(@Param("hash") String hash);
	
	@Query("select t from InternetUser t where t.hash = :hash")
	InternetUser getInterUserByHash(@Param("hash") String hash);

	@Query("SELECT u from InternetUser u where u.telePhone = :telePhone ")
	InternetUser getUserByPhone(@Param("telePhone")String telephone);


	@Transactional
	@Modifying
	@Query("update InternetUser u set u.username = :telePhone, u.telePhone= :telePhone, u.updateDate= :updateDate  where u.hash = :hash")
	int updateUserTele(@Param("telePhone")String telePhone,@Param("hash")String hash,@Param("updateDate")Date updateDate);

	@Query(value = "select u.hash ,(to_char(u.create_date, 'yyyyMMddhh24miss')) as create_date,u.tele_phone from t_4a_actors u where u.hash= :hash",nativeQuery = true)
	List<Object[]> updateTeleByAdminToSimple(@Param("hash")String hash);

}
