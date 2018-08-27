package com.ido85.party.sm.application;

import com.ido85.party.sm.domain.MessageLog;

import java.util.Date;

public interface MessageLogApplication {

	/**
	 * 查询最后一条短信
	 * @param mobile
	 * @return
	 */
	MessageLog getLastMessageByMobile(String mobile);

	/**
	 *
	 * @param telephone
	 * @param startDate
	 * @param endDate
	 */
    Long getTelSendCount(String telephone, Date startDate, Date endDate);
}
