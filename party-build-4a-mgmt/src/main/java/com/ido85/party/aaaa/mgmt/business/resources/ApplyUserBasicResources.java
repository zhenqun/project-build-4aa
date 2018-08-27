package com.ido85.party.aaaa.mgmt.business.resources;

import com.ido85.party.aaaa.mgmt.business.domain.ApplyUserBasic;
import com.ido85.party.aaaa.mgmt.dto.assist.ApplyUserClientManageDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/10/17.
 */
public interface ApplyUserBasicResources  extends JpaRepository<ApplyUserBasic, String> {



    @Query("select  u from ApplyUserBasic  u where u.delFlag='0' and u.status='1'  and u.applyUserId  in :itemIds")
   List<ApplyUserBasic> getApplyUseBasicById(@Param("itemIds") List<String> itemIds);
    /**
     * 备案否决
     * @param
     * @param name
     * @return
     */
    @Transactional
    @Modifying
    @Query("update ApplyUserBasic t set t.status='3',t.reason=:reason,t.updateBy=:userId,t.updateDate=:date,t.approveDate=:date,t.approveBy=:approveBy where t.delFlag='0' and t.applyUserId in:itemIds")
    int auditAdminVeto(@Param("itemIds") List<String> itemIds, @Param("reason") String rerson, @Param("userId") String userId, @Param("date") Date date, @Param("approveBy") String name);


    @Transactional
    @Modifying
    @Query("update ApplyUserBasic t set t.status='2',t.approveDate=:date ,t.approveBy = :name  where t.delFlag='0' and t.applyUserId = :itemId")
    int updateStatusAndTime(@Param("itemId") String itemId, @Param("date") Date date,@Param("name") String name);

    @Query("select t.status from ApplyUserBasic t where t.delFlag='0' and t.applyUserId  in :itemIds")
    List<String> queryStatuses(@Param("itemIds") List<String> itemIds);
}
