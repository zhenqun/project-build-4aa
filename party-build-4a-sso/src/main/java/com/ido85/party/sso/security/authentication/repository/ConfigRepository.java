package com.ido85.party.sso.security.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.sso.security.authentication.domain.Config;

import javax.transaction.Transactional;


public interface ConfigRepository extends JpaRepository<Config, Long> {
	/**
	 * 根据系统编码获取编码值
	 * @param configCode
	 * @return
	 */
	@Query(" select t from Config t where t.configCode = :configCode")
	Config queryConfigInfoByCode(@Param("configCode")String configCode);

	@Transactional
	@Modifying
	@Query("update Config t set  t.configValue = :lastNum where  t.configCode='SEND_END_TIME'")
	int insertIntoConfig(@Param("lastNum")String lastNum);

	@Transactional
	@Modifying
	@Query("update Config t set  t.configValue = :lastNum where  t.configCode='SEND_UPDATE_TIME'")
	int insertIntoUpdateConfig(@Param("lastNum")String lastNum);

	@Transactional
	@Modifying
	@Query("update Config t set  t.configValue = '1' where  t.configCode='SERVER_STATUS'")
	int updateS();

	@Transactional
	@Modifying
	@Query("update Config t set  t.configValue = '0' where  t.configCode='SERVER_STATUS'")
	int updateOrig();
}
