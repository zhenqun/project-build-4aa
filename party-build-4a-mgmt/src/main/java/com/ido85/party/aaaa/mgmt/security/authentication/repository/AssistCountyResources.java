package com.ido85.party.aaaa.mgmt.security.authentication.repository;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.AssistCounty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

/**
 * Created by Administrator on 2017/10/17.
 */
public interface AssistCountyResources extends JpaRepository<AssistCounty, String> {


    @Query("delete from AssistCounty  T WHERE  T.manageId = :orgId")
    void deleteByOrgId(@Param("orgId")String orgId);

    @Query("select count(manageId) from AssistCounty where manageId = :orgId ")
    int getManageId(@Param("orgId")String orgId);

    @Transactional
    @Modifying
    @Query("update AssistCounty  set manageName = :orgName where manageId = :orgId")
    int updateOrgNameByOrgId(@Param("orgName")String orgName,@Param("orgId")String orgId);
}
