package com.ido85.party.sso.security.authentication.repository;

import com.ido85.party.sso.security.authentication.domain.CenterUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by Administrator on 2017/12/5.
 */
public interface CenterUrlResources extends JpaRepository<CenterUrl,Long>{

    @Query("select t.url from CenterUrl t where t.delFlag = '0' and t.area = :area")
    String getUrlBuArea(@Param("area") String area);
}
