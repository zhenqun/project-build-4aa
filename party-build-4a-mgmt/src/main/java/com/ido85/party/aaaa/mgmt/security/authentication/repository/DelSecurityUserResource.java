package com.ido85.party.aaaa.mgmt.security.authentication.repository;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.DelSecurityUserLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Administrator on 2018/3/20.
 */
public interface DelSecurityUserResource  extends JpaRepository<DelSecurityUserLog,String>{
}
