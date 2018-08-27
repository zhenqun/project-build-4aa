package com.ido85.party.sso.security.authentication.application;

import com.ido85.party.sso.security.authentication.domain.MessageLog;

public interface MessageLogApplication {

	/**
	 * 查询最后一条短信
	 * @param mobile
	 * @return
	 */
	MessageLog getLastMessageByMobile(String mobile);

}
