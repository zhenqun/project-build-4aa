package com.ido85.party.aaaa.mgmt.security.authentication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.security.authentication.domain.MessageLog;


public interface MessageLogResources extends JpaRepository<MessageLog, Long>{

	@Query("select m from MessageLog m where m.messageType = :type and m.telephone = :telephone order by m.createDate desc")
	List<MessageLog> getVerifyCodeByTelephoneandType(@Param("telephone")String telephone, @Param("type")String type);

}
