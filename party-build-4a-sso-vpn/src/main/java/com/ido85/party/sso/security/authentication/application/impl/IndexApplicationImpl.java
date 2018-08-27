package com.ido85.party.sso.security.authentication.application.impl;

import com.ido85.party.sso.dto.index.NewNoticeQueryParam;
import com.ido85.party.sso.dto.index.NoticeDto;
import com.ido85.party.sso.dto.index.NoticeQueryParam;
import com.ido85.party.sso.security.authentication.application.IndexApplication;
import com.ido85.party.sso.security.authentication.domain.Notice;
import com.ido85.party.sso.security.authentication.repository.NoticeResources;
import com.ido85.party.sso.security.utils.ListUntils;
import com.ido85.party.sso.security.utils.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Named
public class IndexApplicationImpl implements IndexApplication {

	@Inject
	private EntityManager em;
	
	@Inject
	private NoticeResources noticeRes;
	
	@Override
	public List<NoticeDto> noticeQuery(NoticeQueryParam param) {
		List<NoticeDto> dtoList = null;
		NoticeDto dto = null;
		Integer pageNo = param.getPageNo();
		Integer pageSize = param.getPageSize();
		StringBuffer sb = new StringBuffer("select n from Notice n where n.delFlag = '0' and n.isRelease = 't' ");
		Query q = em.createQuery(sb.toString(),Notice.class);
		q.setFirstResult((pageNo - 1) * pageSize);
		q.setMaxResults(pageSize);
		List<Notice> list = q.getResultList();
		if(ListUntils.isNotNull(list)){
			dtoList = new ArrayList<NoticeDto>();
			for(Notice notice:list){
				dto = new NoticeDto();
				dto.setCreateDate(notice.getCreateDate());
				dto.setNoticeContent(notice.getNoticeContent());
				dto.setNoticeId(StringUtils.toString(notice.getNoticeId()));
				dto.setNoticeTitle(notice.getNoticeTitle());
				dto.setReleaseDate(notice.getReleaseDate());
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	/**
	 * 公告详情
	 */
	public Notice noticeDetail(String noticeId) {
		return noticeRes.getNoticeById(StringUtils.toLong(noticeId));
	}

	/**
	 * 最新公告查询（用于九宫格弹框提示）
	 * @param param
	 * @return
     */
	public List<NoticeDto> newNoticeQuery(NewNoticeQueryParam param) {
		List<NoticeDto> dtoList = null;
		NoticeDto dto = null;
		Integer pageNo = 1;
		Integer pageSize = param.getPageSize();
		Date date = param.getNoticeDate();
		StringBuffer sb = new StringBuffer("select n from Notice n where n.delFlag = '0' and n.isRelease = 't' " +
				"and n.releaseDate > :date ORDER BY n.releaseDate DESC");
		Query q = em.createQuery(sb.toString(),Notice.class);
		q.setParameter("date",date);
		q.setFirstResult((pageNo - 1) * pageSize);
		q.setMaxResults(pageSize);
		List<Notice> list = q.getResultList();
		if(ListUntils.isNotNull(list)){
			dtoList = new ArrayList<NoticeDto>();
			for(Notice notice:list){
				dto = new NoticeDto();
				dto.setCreateDate(notice.getCreateDate());
				dto.setNoticeContent(notice.getNoticeContent());
				dto.setNoticeId(StringUtils.toString(notice.getNoticeId()));
				dto.setNoticeTitle(notice.getNoticeTitle());
				dto.setReleaseDate(notice.getReleaseDate());
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	/**
	 * 最新一条（用于九宫格弹框提示）
	 * @param param
	 * @return
	 */
	public List<NoticeDto> newestNoticeQuery(NewNoticeQueryParam param) {
		List<NoticeDto> dtoList = null;
		Notice notice = null;
		NoticeDto dto = null;
//		Integer pageNo = 1;
//		Integer pageSize = param.getPageSize();
//		Date date = param.getNoticeDate();
		StringBuffer sb = new StringBuffer("select n from Notice n where n.delFlag = '0' and n.isRelease = 't' " +
				" ORDER BY n.releaseDate DESC");
		Query q = em.createQuery(sb.toString(),Notice.class);
		List<Notice> list = q.getResultList();
		if(ListUntils.isNotNull(list)){
			dtoList = new ArrayList<NoticeDto>();
			notice = list.get(0);
			dto = new NoticeDto();
			dto.setCreateDate(notice.getCreateDate());
			dto.setNoticeContent(notice.getNoticeContent());
			dto.setNoticeId(StringUtils.toString(notice.getNoticeId()));
			dto.setNoticeTitle(notice.getNoticeTitle());
			dto.setReleaseDate(notice.getReleaseDate());
			dtoList.add(dto);
		}
		return dtoList;
	}

}
