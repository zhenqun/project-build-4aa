package com.ido85.party.aaaa.mgmt.business.application.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.security.core.context.SecurityContextHolder;

import com.ido85.party.aaaa.mgmt.business.application.NoticeApplication;
import com.ido85.party.aaaa.mgmt.business.domain.Notice;
import com.ido85.party.aaaa.mgmt.business.dto.notice.AddNoticeParam;
import com.ido85.party.aaaa.mgmt.business.dto.notice.DelNoticeParam;
import com.ido85.party.aaaa.mgmt.business.dto.notice.NoticeDto;
import com.ido85.party.aaaa.mgmt.business.dto.notice.NoticeQueryParam;
import com.ido85.party.aaaa.mgmt.business.dto.notice.ReleaseNoticeParam;
import com.ido85.party.aaaa.mgmt.business.resources.NoticeResources;
import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.security.authentication.application.UserApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
@Named
public class NoticeApplicationImpl implements NoticeApplication {

	@Inject
	private NoticeResources noticeResources;

	@PersistenceContext(unitName = "business")
	private EntityManager businessEntity;
	
	@Inject
	private IdGenerator idGenerator;
	
	@Inject
	private UserApplication userApp;
	
	/**
	 * 添加公告
	 */
	@Transactional
	public boolean saveOrupdateNotice(AddNoticeParam param) {
		String noticeContent = param.getNoticeContent();
		String noticeTitle = param.getNoticeTitle();
		String noticeId = param.getNoticeId();
		Notice notice = null;
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(null != user){
			//新增
			if(StringUtils.isNull(noticeId)){
				notice = new Notice();
				notice.setCreateBy(user.getId());
				notice.setCreateDate(new Date());
				notice.setDelFlag("0");
				notice.setNoticeContent(noticeContent);
				notice.setNoticeId(idGenerator.next());
				notice.setNoticeTitle(noticeTitle);
				notice.setRelease(false);
				notice.setUpdateBy(null);
				notice.setUpdateDate(null);
				notice.setRelease(false);
				noticeResources.save(notice);
				return true;
			}else{
				notice = noticeResources.getNoticeById(StringUtils.toLong(noticeId));
				if(null == notice){
					return false;
				}
				notice.setNoticeContent(noticeContent);
				notice.setNoticeTitle(noticeTitle);
				notice.setUpdateBy(user.getId());
				notice.setUpdateDate(new Date());
				noticeResources.save(notice);
				return true;
			}
		}
		return false;
	}

	/**
	 * 删除公告
	 */
	@Transactional
	public boolean delNotice(DelNoticeParam param) {
		List<String> ids = param.getIds();
		List<Long> noticeIds = null;
		List<Notice> nts = new ArrayList<Notice>();
		if(ListUntils.isNotNull(ids)){
			noticeIds = new ArrayList<Long>();
			for(String id:ids){
				noticeIds.add(StringUtils.toLong(id));
			}
		}
		List<Notice> notices = noticeResources.getNoticeByIds(noticeIds);
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId = null;
		if(null != user){
			userId = user.getId();
		}
		if(ListUntils.isNotNull(notices)){
			for(Notice nt:notices){
				nt.setDelFlag("1");
				nt.setDelBy(userId);
				nt.setDelDate(new Date());
				nts.add(nt);
			}
			noticeResources.save(nts);
			return true;
		}
		return false;
	}

	/**
	 * 发布公告
	 */
	@Transactional
	public boolean releaseNotice(ReleaseNoticeParam param) {
		List<String> ids = param.getIds();
		List<Long> noticeIds = null;
		List<Notice> nts = new ArrayList<Notice>();
		if(ListUntils.isNotNull(ids)){
			noticeIds = new ArrayList<Long>();
			for(String id:ids){
				noticeIds.add(StringUtils.toLong(id));
			}
		}
		List<Notice> notices = noticeResources.getNoticeByIds(noticeIds);
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId = null;
		if(null != user){
			userId = user.getId();
		}
		if(ListUntils.isNotNull(notices)){
			for(Notice nt:notices){
				nt.setRelease(true);
				nt.setReleaseBy(userId);
				nt.setReleaseDate(new Date());
				nts.add(nt);
			}
			noticeResources.save(nts);
			return true;
		}
		return false;
	}

	/**
	 * 公告查询
	 */
	public List<NoticeDto> noticeQuery(NoticeQueryParam param) {
		List<NoticeDto> dtoList = null;
		NoticeDto dto = null;
		String noticeTitle = param.getNoticeTitle();
		Integer pageNo = param.getPageNo();
		Integer pageSize = param.getPageSize();
		Set<String> userIds = new HashSet<>();
		StringBuffer sb = new StringBuffer("select n from Notice n where n.delFlag = '0'");
		if(!StringUtils.isNull(noticeTitle)){
			sb.append(" and n.noticeTitle like :title");
		}
		Query q = businessEntity.createQuery(sb.toString(),Notice.class);
		if(!StringUtils.isNull(noticeTitle)){
			q.setParameter("title", "%"+noticeTitle+"%");
		}
		q.setFirstResult((pageNo - 1) * pageSize);
		q.setMaxResults(pageSize);
		List<Notice> list = q.getResultList();
		if(ListUntils.isNotNull(list)){
			dtoList = new ArrayList<NoticeDto>();
			for(Notice notice:list){
				dto = new NoticeDto();
				if(!StringUtils.isNull(notice.getCreateBy())){
					userIds.add(notice.getCreateBy());
					dto.setCreateBy(notice.getCreateBy());
				}
				dto.setCreateDate(notice.getCreateDate());
//				dto.setNoticeContent(notice.getNoticeContent());
				dto.setNoticeId(StringUtils.toString(notice.getNoticeId()));
				dto.setNoticeTitle(notice.getNoticeTitle());
				dto.setRelease(notice.isRelease());
				if(!StringUtils.isNull(notice.getUpdateBy())){
					userIds.add(notice.getUpdateBy());
					dto.setUpdateBy(notice.getUpdateBy());
				}
				dto.setUpdateDate(notice.getUpdateDate());
				dtoList.add(dto);
			}
			
			//获取用户姓名
			if(null != userIds && userIds.size() > 0){
				Map<String, String> map = userApp.getUserByIds(userIds);
				if(null != map && map.size() > 0){
					for(NoticeDto noticedto:dtoList){
						noticedto.setCreateBy(map.get(noticedto.getCreateBy()));
						noticedto.setUpdateBy(map.get(noticedto.getUpdateBy()));
					}
				}
			}
		}
		return dtoList;
	}


	/**
	 * 公告数量查询
	 */
	public Long noticeQueryCnt(NoticeQueryParam param) {
		String noticeTitle = param.getNoticeTitle();
		StringBuffer sb = new StringBuffer("select count(n.noticeId) from Notice n where n.delFlag = '0' ");
		if(!StringUtils.isNull(noticeTitle)){
			sb.append(" and n.noticeTitle like :title");
		}
		Query q = businessEntity.createQuery(sb.toString());
		if(!StringUtils.isNull(noticeTitle)){
			q.setParameter("title", "%"+noticeTitle+"%");
		}
		return StringUtils.toLong(q.getSingleResult());
	}

	@Override
	public NoticeDto noticeDetail(String noticeId) {
		Notice notice = noticeResources.getNoticeById(StringUtils.toLong(noticeId));
		NoticeDto dto = null;
		if(null != notice){
			dto = new NoticeDto();
			dto.setNoticeContent(notice.getNoticeContent());
			dto.setNoticeId(StringUtils.toString(notice.getNoticeId()));
			dto.setNoticeTitle(notice.getNoticeTitle());
		}
		return dto;
	}

}
