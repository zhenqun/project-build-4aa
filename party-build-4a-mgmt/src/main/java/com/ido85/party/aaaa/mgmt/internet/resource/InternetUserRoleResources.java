package com.ido85.party.aaaa.mgmt.internet.resource;

import com.ido85.party.aaaa.mgmt.internet.domain.InternetUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/12/8.
 */
public interface InternetUserRoleResources extends JpaRepository<InternetUserRole,Long>{

    @Query("select t from InternetUserRole t where t.userId = :userId")
    List<InternetUserRole> getRolesByUserId(@Param("userId") String id);
}
