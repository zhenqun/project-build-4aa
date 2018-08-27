package com.ido85.party.aaa.authentication.reposities;

import com.ido85.party.aaa.authentication.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;


public interface UserRoleResource extends JpaRepository<UserRole, Long>{

    @Transactional
    @Modifying
    @Query("update UserRole u set u.delFlag = '1' , u.delDate = :date where u.authenticationUserId in :ids")
    void deleteUserRole(@Param("ids") List<String> userIds, @Param("date") Date date);

    @Query("select u.userId from UserRole u where u.authenticationUserId in :userIds and u.roleId = :roleId")
    List<Long> getUserIdsByauthUserid(List<String> userIds, String roleId);
}
