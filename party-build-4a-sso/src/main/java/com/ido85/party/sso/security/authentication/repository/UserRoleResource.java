package com.ido85.party.sso.security.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ido85.party.sso.security.authentication.domain.UserRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserRoleResource extends JpaRepository<UserRole, Long>{


    @Query("select count(r.userId) from UserRole r where r.userId = :id")
    int getUserRoleId(@Param("id")Long id);

}
