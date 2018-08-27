package com.ido85.party.aaaa.mgmt.security.authentication.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 删除安全员账号日志表
 * Created by Administrator on 2018/3/16.
 */
@Data
@Entity
@Table(name = "tf_f_del_user_log")
public class DelSecurityUserLog  implements Serializable{

    @Id
    @Column(name = "id")
    private String id;

    @Column(name="org_id")
    private String orgId;

    @Column(name="org_name")
    private String orgName;

    @Column(name="org_code")
    private String orgCode;

    @Column(name="id_card")
    private String idCard;

    @Column(name="rel_name")
    private String relName;

    @Column(name="ou")
    private String ou;

    @Column(name="create_date")
    private Date createDate;




}
