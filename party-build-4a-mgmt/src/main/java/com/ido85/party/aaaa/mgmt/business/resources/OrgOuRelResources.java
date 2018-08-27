package com.ido85.party.aaaa.mgmt.business.resources;

import com.ido85.party.aaaa.mgmt.business.domain.OrgOuRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by Administrator on 2017/9/12.
 */
public interface OrgOuRelResources extends JpaRepository<OrgOuRel, Long> {

    @Query("select r from OrgOuRel r where r.orgId = :manageId")
    OrgOuRel getRelBy(@Param("manageId")String manageId);
}
