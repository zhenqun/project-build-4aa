package com.ido85.party.aaaa.mgmt.business.resources;

import com.ido85.party.aaaa.mgmt.business.domain.AssistManage;
import com.ido85.party.aaaa.mgmt.business.domain.ClientExpand;
import com.ido85.party.aaaa.mgmt.dto.assist.AssistClientsQueryResultDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/10/11.
 */
public interface AssistManageResources  extends JpaRepository<AssistManage, String> {

    @Query("select c from AssistManage c where c.fzuserId = :fzuserId")
    List<AssistManage> getCategoryByClientId(@Param("fzuserId")String fzuserId);

    @Query("select c from AssistManage c where c.fzuserId in :fzuserId")
    List<AssistManage> getAssistByUserIds(@Param("fzuserId") List<String> fzuserId);

    @Query("select c from AssistManage c where c.manageId = :orgId")
    List<AssistManage> getAMByManageId(@Param("orgId") String oldOrgId);

    @Query("select c from AssistManage c where c.createManageId = :orgId")
    List<AssistManage> getAMBYCreateManageId(@Param("orgId") String oldOrgId);
}
