package com.ido85.party.aaaa.mgmt.business.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Administrator
 * @date 2017/12/25
 */
@Data
@Entity
@Table(name="tp_b_permission_rule")
public class PermissionRule implements Serializable{

    private static final long serialVersionUID = 2267225741246605119L;

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="permission_id")
    private Long permissionId;

    @Column(name="rule_code")
    private String ruleCode;

    @Column(name="del_flag")
    private String delFlag;

    @Column(name="create_date")
    private Date createDate;
}
