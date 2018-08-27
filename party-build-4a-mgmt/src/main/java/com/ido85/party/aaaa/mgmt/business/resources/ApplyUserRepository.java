package com.ido85.party.aaaa.mgmt.business.resources;

import com.ido85.party.aaaa.mgmt.business.domain.ApplyUser;
import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.OutManageDto;
import com.ido85.party.aaaa.mgmt.dto.assist.ApplyUserClientManageDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/10/9 0009.
 */
public interface ApplyUserRepository extends JpaRepository<ApplyUser,String> {

    @Query("select new com.ido85.party.aaaa.mgmt.dto.assist.ApplyUserClientManageDto(c.clientId ,c.applyManageId,c.applyManageName ,c.applyManageCode) from ApplyUser c where c.applyUserId = :item and c.delFlag ='0' ")
    List<ApplyUserClientManageDto> getApplyUserClient(@Param("item") String item);



    @Query("select c from ApplyUser c where c.applyUserId = :item  and c.delFlag='0' ")
    List<ApplyUser> getApplyUserClientAndManage(@Param("item") String item);



    /**
     * 根据业务管理员id
     * 获取业务信息
     * @param in
     * @return
     */
    @Query("select new com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.OutManageDto (" +
            " t.id,t.clientId,ce.clientName,t.applyManageId,t.applyManageCode,t.applyManageName,t.roleId,r.description,t.applyUserId )" +
            " from ApplyUser t,ClientExpand ce,BusinessRole r where t.delFlag='0' and" +
            " t.clientId=ce.clientId " +
            " and r.id=t.roleId" +
            " and t.applyUserId in :applyUserIds")
    List<OutManageDto> querybusinessSystemByIds(@Param("applyUserIds") List<String> in);

    /**
     * 辅助安全员刷权限
     * @param manageId
     * @param roleId
     * @param role
     * @return
     */
    @Transactional
    @Modifying
    @Query("update ApplyUser r set r.roleId = :role  where r.applyManageId= :manageId and r.roleId = :roleId  and r.delFlag='0' ")
    int updateRoleApplyUser(@Param("manageId") String manageId, @Param("roleId") Long roleId, @Param("role") Long role);


    /**
     * 党组织定时改名-----被申请人
     * @param orgName
     * @param orgId
     * @return
     */
    @Transactional
    @Modifying
    @Query("update ApplyUser r set r.applyManageName = :orgName where r.applyUserId = :orgId")
    int reNameOrgNameByOrgId(@Param("orgName")String orgName,@Param("orgId")String orgId);


    /**
     * 党组织改名操作-------申请人
     * @param orgName
     * @param orgId
     * @return
     */
    @Transactional
    @Modifying
    @Query("update ApplyUser r set r.createManageName = :orgName where r.createManageId = :orgId")
    int reNameCreateOrgNameByOrgId(@Param("orgName")String orgName,@Param("orgId")String orgId);

}
