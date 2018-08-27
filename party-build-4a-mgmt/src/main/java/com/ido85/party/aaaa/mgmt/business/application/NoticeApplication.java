package com.ido85.party.aaaa.mgmt.business.application;

import java.util.List;

import com.ido85.party.aaaa.mgmt.business.dto.notice.AddNoticeParam;
import com.ido85.party.aaaa.mgmt.business.dto.notice.DelNoticeParam;
import com.ido85.party.aaaa.mgmt.business.dto.notice.NoticeDto;
import com.ido85.party.aaaa.mgmt.business.dto.notice.NoticeQueryParam;
import com.ido85.party.aaaa.mgmt.business.dto.notice.ReleaseNoticeParam;

public interface NoticeApplication {

	/**
	 * 添加公告
	 * @param param
	 * @return
	 */
	boolean saveOrupdateNotice(AddNoticeParam param);

	/**
	 * 删除公告
	 * @param param
	 * @return
	 */
	boolean delNotice(DelNoticeParam param);

	/**
	 * 发布公告
	 * @param param
	 * @return
	 */
	boolean releaseNotice(ReleaseNoticeParam param);

	/**
	 * 公告查询
	 * @param param
	 * @return
	 */
	List<NoticeDto> noticeQuery(NoticeQueryParam param);

	/**
	 * 公告查询数量查询
	 * @param param
	 * @return
	 */
	Long noticeQueryCnt(NoticeQueryParam param);

	/**
	 * 详情
	 * @param noticeId
	 * @return
	 */
	NoticeDto noticeDetail(String noticeId);

}
