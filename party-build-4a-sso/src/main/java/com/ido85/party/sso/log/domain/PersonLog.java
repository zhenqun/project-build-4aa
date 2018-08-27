package com.ido85.party.sso.log.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/6/17.
 */
@Data
@Entity
@Table(name="tf_f_sso_log")
public class PersonLog implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="log_id")
    private Long logId;

    @Column(name="log_type")
    private String logType;

    @Column(name="log_name")
    private String logName;

    @Column(name="log_content")
    private String logContent;

    @Column(name="create_date")
    private Date createDate;

    @Column(name="user_id")
    private String userId;

    @Column(name="hash")
    private String hash;

}
