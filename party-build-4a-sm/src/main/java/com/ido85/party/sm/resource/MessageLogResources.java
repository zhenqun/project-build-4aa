package com.ido85.party.sm.resource;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.sm.domain.MessageLog;
import org.springframework.security.access.prepost.PreAuthorize;


public interface MessageLogResources extends JpaRepository<MessageLog, Long>{

	@Query("select m from MessageLog m where m.messageType = :type and m.telephone = :telephone  and m.isSuccess = true order by m.createDate desc")
	List<MessageLog> getVerifyCodeByTelephoneandType(@Param("telephone")String telephone, @Param("type")String type);

	@Query("select count(m.verifycodeId) from MessageLog  m where m.telephone = :telephone and m.createDate > :startDate and m.createDate < :endDate")
    Long getTelSendCount(@Param("telephone") String telephone, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
