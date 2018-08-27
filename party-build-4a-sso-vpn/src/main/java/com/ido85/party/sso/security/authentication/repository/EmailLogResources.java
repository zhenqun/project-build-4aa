package com.ido85.party.sso.security.authentication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.sso.security.authentication.domain.EmailLog;



public interface EmailLogResources extends JpaRepository<EmailLog, Long> {
	/**
	 * 根据用户的邮箱和日志id来获取日志信息
	 * @param email
	 * @param emailLogId
	 * @return
	 */
	@Query("SELECT t FROM EmailLog t where t.logId = :logId  and t.delFlag = '0'")
	EmailLog getEmailLogInfo( @Param("logId")Long logId);
	
	
	
	
	
}
