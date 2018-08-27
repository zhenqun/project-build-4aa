package com.ido85.party.sm.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Data
@Entity
@Table(name="tf_f_message_log")
public class MessageLog implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="verifycode_id")
	private Long verifycodeId;
	
	@Column(name="verifycode")
	private String verifycode;
	
	@Column(name="user_id")
	private String userId;
	
	@Column(name="message_type")
	private String messageType;
	
	@Column(name="create_date")
	private Date createDate;
	
	@Column(name="expire_date")
	private Date expireDate;
	
	@Column(name="detail")
	private String detail;
	
	@Column(name="telephone")
	private String telephone;
	
	@Column(name="is_success")
	private boolean isSuccess;
	
	@Column(name="message_content")
	private String messageContent;
}
