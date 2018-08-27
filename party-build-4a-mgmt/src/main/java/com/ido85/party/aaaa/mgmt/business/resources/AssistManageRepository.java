package com.ido85.party.aaaa.mgmt.business.resources;

import com.ido85.party.aaaa.mgmt.business.domain.AssistManage;
import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.OutManageDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Administrator on 2017/10/10 0010.
 */
public interface AssistManageRepository extends JpaRepository<AssistManage,String> {



    /*根据当前登陆人的业务系统clientIds，管理范围manageIds，通过辅助安全员管理应用表tf_f_assist_manage，匹配应用client_id，创建者范围create_manage_id
    获取辅助安全员的辅助安全员管理范围manageIds  应用clientIds*/
    @Query("select new com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.OutManageDto(t.clientId,t.manageId) from  AssistManage t where t.delFlag='0' and t.clientId in:userclientIds and t.createManageId in:usermanageIds")
    List<OutManageDto> getFzUserClientidsAndManageIds(@Param("userclientIds") List<String> userclientIds,@Param("usermanageIds") List<String> usermanageIds);

    @Transactional
    @Modifying
    @Query("update AssistManage set manageName = :orgName where manageId = :orgId")
    int reNameOrgName(@Param("orgName")String orgName,@Param("orgId") String orgId);

    @Transactional
    @Modifying
    @Query("update AssistManage set createManageName = :orgName where createManageId = :orgId")
    int reNameCreateOrgName(@Param("orgName")String orgName,@Param("orgId") String orgId);
}
