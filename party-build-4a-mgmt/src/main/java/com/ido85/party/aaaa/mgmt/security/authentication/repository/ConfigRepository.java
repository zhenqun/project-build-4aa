package com.ido85.party.aaaa.mgmt.security.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.Config;


public interface ConfigRepository extends JpaRepository<Config, Long> {
	/**
	 * 根据系统编码获取编码值
	 * @param configCode
	 * @return
	 */
	@Query(" select t from Config t where t.configCode = :configCode")
	Config queryConfigInfoByCode(@Param("configCode")String configCode);
}
