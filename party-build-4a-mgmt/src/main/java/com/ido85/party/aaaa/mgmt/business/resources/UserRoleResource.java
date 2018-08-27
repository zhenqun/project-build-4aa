package com.ido85.party.aaaa.mgmt.business.resources;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.business.domain.UserRole;

import javax.transaction.Transactional;

public interface UserRoleResource extends JpaRepository<UserRole, Long> {

    @Query("select u from UserRole u where u.userId=:userId and u.roleId = :roleId")
    UserRole getRoleByUserIdRoleId(@Param("userId") String userId, @Param("roleId") Long roleId);

    @Query("select u from UserRole u where u.userId = :userId")
    List<UserRole> getRoleByUserId(@Param("userId") String userId);

    @Query("select l from UserRole l where l.roleId = :id")
    List<UserRole> getRelByRoleId(@Param("id") Long roleId);

    @Query("select l from UserRole l where l.id = :relId")
    UserRole getRoleByRelId(@Param("relId") Long relId);
	@Query("select l from UserRole l where l.manageId = :id")
	List<UserRole> getUrByManageId(@Param("id") String oldOrgId);
    @Query("select c from UserRole c where c.userId in :ids")
    List<UserRole> getRelByUserId(@Param("ids") List<String> ids);

    @Query("select u from UserRole u where u.userId=:userId ")
    UserRole getRoleByUserIdUserId(@Param("userId") String userId);

    @Transactional
    @Modifying
//    @Query(value = "UPDATE r_4a_user_role r SET role_id = :role FROM t_4a_actors T, r_4a_client_user_rel c  WHERE r.user_id = T . ID and c.user_id =t.id  and  r.manage_id= :manageId and r.role_id = :roleId and t.id_card= :idCard ",nativeQuery = true)
    @Query("update UserRole r set r.roleId = :role  where r.manageId= :manageId and r.roleId = :roleId and r.userId = :id ")
    int updateRoleByMangeIdPeople(@Param("manageId") String manageId, @Param("roleId") Long roleId, @Param("role") Long role,@Param("id")String id);

    //	@Query("update UserRole r set r.roleId ='-4943112096028815360' where r.manageId= :manageId and r.roleId = :roleId ")
//	int updateRoleByMangeIdOrg(@Param("manageId")String manageId,@Param("roleId")Long roleId);
    @Transactional
    @Modifying
    @Query(value = "delete  from t_4a_role where client_id ='party-org-info-mgmt-ui' AND ID NOT IN (\n" +
            "\t'-4943112096028815360',\n" +
            "\t'-4942560878455402496',\n" +
            "\t'-4942815560286908416'\n" +
            ")  ",nativeQuery = true)
    void delOrgRole();

    @Transactional
    @Modifying
    @Query(value = "delete  from t_4a_role where client_id = 'party-people-info-mgmt-ui' AND ID NOT IN (\n" +
            "\t'-4940250523211591680',\n" +
            "'-4939082210620207104',\n" +
            "'-4939403742941655040'\n" +
            ")   ",nativeQuery =true )
    void delPeopleRole();

//    @Transactional
//    @Modifying
//    @Query(value = "DELETE FROM r_4a_user_role r USING  t_4a_role o  where  o. ID = r.role_id and  o.client_id ='party-org-info-mgmt-ui' and  r.user_id= :userAccount and r.manage_id = :manageId " +
//            "  AND r.ID NOT IN('-4943112096028815360','-4942560878455402496','-4942815560286908416','2708078027155869696')",nativeQuery = true)
//    int delDulpiUserRole(@Param("manageId") String manageId,@Param("userAccount") String userAccount);
//
//    @Transactional
//    @Modifying
//    @Query(value = "DELETE FROM r_4a_user_role r USING  t_4a_role o  where  o. ID = r.role_id  and  o.client_id ='party-people-info-mgmt-ui' and  r.user_id= :userAccount and r.manage_id = :manageId " +
//            " and role_id  NOT IN ('-4940250523211591680','-4939082210620207104','-493940374294165504','2706470981743710208')",nativeQuery = true)
//    int delDulPeopleRole(@Param("manageId") String manageId,@Param("userAccount") String userAccount);



	//县转镇
	@Transactional
	@Modifying
	@Query("update UserRole  set roleId= :zRole  where roleId= :xRole and manageId = :orgId ")
	int  updateOrgRoleByOrgIdXToZ(@Param("xRole")Long xRole,@Param("zRole")Long zRole,@Param("orgId")String orgId);
	//县转镇
	@Transactional
	@Modifying
	@Query("update UserRole  set roleId= :zRole  where roleId= :xRole and manageId = :orgId ")
	int updatePeopleRoleByOrgIdXToZ(@Param("xRole")Long xRole,@Param("zRole")Long zRole,@Param("orgId")String orgId);
	//镇转县
	@Transactional
	@Modifying
	@Query("update UserRole  set roleId= :xRole  where roleId= :zRole and manageId = :orgId ")
	int updateOrgRoleByOrgIdZToX(@Param("xRole")Long xRole,@Param("zRole")Long zRole,@Param("orgId")String orgId);
	//镇转县
	@Transactional
	@Modifying
	@Query("update UserRole  set roleId= :xRole  where roleId= :zRole and manageId = :orgId ")
	int updatePeopleRoleByOrgIdZToX(@Param("xRole")Long xRole,@Param("zRole")Long zRole,@Param("orgId")String orgId);
	//镇转基层
	@Transactional
	@Modifying
	@Query("update UserRole  set roleId= :jRole  where roleId= :zRole and manageId = :orgId ")
	int updateOrgRoleByOrgIdZToJ(@Param("zRole")Long zRole,@Param("jRole")Long jRole,@Param("orgId")String orgId);
	//镇转基层
	@Transactional
	@Modifying
	@Query("update UserRole  set roleId= :jRole  where roleId= :zRole and manageId = :orgId ")
	int updatePeopleRoleByOrgIdZToJ(@Param("zRole")Long zRole,@Param("jRole")Long jRole,@Param("orgId")String orgId);
	//基层转镇
	@Transactional
	@Modifying
	@Query("update UserRole  set roleId= :zRole  where roleId= :jRole and manageId = :orgId ")
	void updateOrgRoleByOrgIdJToZ(@Param("zRole")Long zRole,@Param("jRole")Long jRole,@Param("orgId")String orgId);
	//基层转镇
	@Transactional
	@Modifying
	@Query("update UserRole  set roleId= :zRole  where roleId= :jRole and manageId = :orgId ")
	int updatePeopleRoleByOrgIdJToZ(@Param("zRole")Long zRole,@Param("jRole")Long jRole,@Param("orgId")String orgId);

	/**
	 * 党组织定时改名
	 * @param orgName
	 * @param orgId
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("update UserRole set manageName = :orgName where manageId = :orgId")
	int renameOrgName(@Param("orgName")String orgName,@Param("orgId")String orgId);

}
