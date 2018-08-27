package com.ido85.party.aaaa.mgmt.business.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Administrator on 2017/9/12.
 */
@Entity
@Table(name = "tf_f_org_ou_rel")
@Data
public class OrgOuRel {

    @Id
    @Column(name = "org_ou_rel_id")
    private String orgOuRelId;

    @Column(name = "ou_id")
    private String ouId;

    @Column(name = "org_id")
    private String orgId;

    @Column(name = "del_flag")
    private  String delFlag;

    @Column(name="update_by")
    private String updateBy;

    @Column(name="update_time")
    private Date updateTime;

}
