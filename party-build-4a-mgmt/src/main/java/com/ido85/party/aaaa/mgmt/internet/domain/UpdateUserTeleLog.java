package com.ido85.party.aaaa.mgmt.internet.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/12/4.
 */
@Data
@Entity
@Table(name="tf_f_update_tele_log")
public class UpdateUserTeleLog implements Serializable {

    @Id
    @Column(name="id")
    private String id;

    @Column(name="old_telephone")
    private String oldTelephone;

    @Column(name="new_telephone")
    private String newTelephone;

    @Column(name="verify_code")
    private String verifyCode;

    @Column(name="rel_name")
    private String relName;

    @Column(name="id_card")
    private String idCard;

    @Column(name="update_by")
    private String updateBy;

    @Column(name="update_name")
    private String updateName;

    @Column(name="update_hash")
    private String updateHash;


    @Column(name="update_date")
    private Date updateDate;

    @Column(name="create_date")
    private Date createDate;

    @Column(name="is_success")
    private String isSuccess;
}
