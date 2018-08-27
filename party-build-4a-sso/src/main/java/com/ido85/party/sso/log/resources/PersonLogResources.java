package com.ido85.party.sso.log.resources;

import com.ido85.party.sso.log.domain.PersonLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Administrator on 2017/6/17.
 */
public interface PersonLogResources extends JpaRepository<PersonLog,Long>{
}
