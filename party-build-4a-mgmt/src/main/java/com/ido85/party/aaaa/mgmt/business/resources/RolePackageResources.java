package com.ido85.party.aaaa.mgmt.business.resources;

import com.ido85.party.aaaa.mgmt.business.domain.RolePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Administrator on 2017/10/9.
 */
public interface RolePackageResources extends JpaRepository<RolePackage,Long>{

    @Query("select t from RolePackage t where t.manageId in :ids and t.delFlag = '0'")
    List<RolePackage> getRelByManageId(@Param("ids") List<String> manageIds);

    @Query("select t from RolePackage  t where t.manageId in :ids and t.delFlag = '0'")
    List<RolePackage> assistRoleAddedQuery(@Param("ids") List<String> manageIds);

    @Transactional
    @Modifying
    @Query("update RolePackage r set r.roleId = :role  where r.manageId= :manageId and r.roleId = :roleId and r.delFlag='0'  ")
    int updateRolePackage(@Param("manageId") String manageId, @Param("roleId") Long roleId, @Param("role") Long role);

}
