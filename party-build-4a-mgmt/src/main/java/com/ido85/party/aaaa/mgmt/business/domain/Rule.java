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
@Table(name="td_b_rule")
public class Rule implements Serializable{

    private static final long serialVersionUID = -9190427696659418610L;

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="rule_name")
    private String ruleName;

    @Column(name="rule_code")
    private String ruleCode;

    @Column(name="is_com")
    private String isCom;

    @Column(name="err_des")
    private String errDes;

    @Column(name="del_flag")
    private String delFlag;

    @Column(name="create_date")
    private Date createDate;

    @Column(name="and_or_tag")
    private String andOrTag;
}
