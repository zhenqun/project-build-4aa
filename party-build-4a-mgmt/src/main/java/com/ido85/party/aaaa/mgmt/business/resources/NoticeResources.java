package com.ido85.party.aaaa.mgmt.business.resources;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.aaaa.mgmt.business.domain.Notice;

public interface NoticeResources extends JpaRepository<Notice, Long>{

	@Query("select n from Notice n where n.noticeId = :id and n.delFlag = '0'")
	Notice getNoticeById(@Param("id")Long noticeId);

	@Query("select n from Notice n where n.noticeId in :ids and n.delFlag = '0'")
	List<Notice> getNoticeByIds(@Param("ids")List<Long> noticeIds);

}
