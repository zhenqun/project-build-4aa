package com.ido85.party.sso.security.authentication.application;

import com.ido85.party.sso.dto.index.NewNoticeQueryParam;
import com.ido85.party.sso.dto.index.NoticeDto;
import com.ido85.party.sso.dto.index.NoticeQueryParam;
import com.ido85.party.sso.security.authentication.domain.Notice;

import java.util.List;

public interface IndexApplication {

	/**
	 * 查询主页通知公告
	 * @param param
	 * @return
	 */
	List<NoticeDto> noticeQuery(NoticeQueryParam param);

	/**
	 * 公告详情
	 * @param noticeId
	 * @return
	 */
	Notice noticeDetail(String noticeId);

	/**
	 * 最新公告查询（用于九宫格弹框提示）
	 * @param param
	 * @return
     */
	List<NoticeDto> newNoticeQuery(NewNoticeQueryParam param);

	/**
	 * 查询最新一条（用于九宫格弹框提示）
	 * @param param
	 * @return
	 */
	List<NoticeDto> newestNoticeQuery(NewNoticeQueryParam param);
}
