package com.ido85.party.sso.security.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ido85.party.sso.security.authentication.domain.Notice;

public interface NoticeResources extends JpaRepository<Notice, Long>{

	@Query("select n from Notice n where n.delFlag = '0' and n.noticeId = :id  and n.isRelease = 't' order by n.releaseDate desc ")
	Notice getNoticeById(@Param("id")Long id);

}
