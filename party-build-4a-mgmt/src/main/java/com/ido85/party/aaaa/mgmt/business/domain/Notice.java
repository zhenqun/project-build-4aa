package com.ido85.party.aaaa.mgmt.business.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Entity
@Table(name="tf_f_notice")
@Data
public class Notice implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="notice_id")
	private Long noticeId;
	
	@Column(name="notice_title")
	private String noticeTitle;
	
	@Column(name="notice_content")
	private String noticeContent;
	
	@Column(name="create_date")
	private Date createDate;
	
	@Column(name="create_by")
	private String createBy;
	
	@Column(name="update_by")
	private String updateBy;
	
	@Column(name="update_date")
	private Date updateDate;
	
	@Column(name="del_flag")
	private String delFlag;
	
	@Column(name="del_by")
	private String delBy;
	
	@Column(name="del_date")
	private Date delDate;
	
	@Column(name="is_release")
	private boolean isRelease;
	
	@Column(name="release_by")
	private String releaseBy;
	
	@Column(name="release_date")
	private Date releaseDate;
}
