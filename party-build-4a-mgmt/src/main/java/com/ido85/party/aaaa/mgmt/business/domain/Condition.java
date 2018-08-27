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
@Table(name="td_b_condition")
public class Condition implements Serializable{

    private static final long serialVersionUID = -4514088168201194371L;

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="rule_code")
    private String ruleCode;

    @Column(name="filed")
    private String filed;

    @Column(name="judge_method")
    private String judgeMethod;

    @Column(name="value_type")
    private String valueType;

    @Column(name="min_value")
    private String minValue;

    @Column(name="max_value")
    private String maxValue;

    @Column(name="enum_value")
    private String enumValue;

    @Column(name="reg_value")
    private String regValue;

    @Column(name="del_flag")
    private String delFlag;

    @Column(name="create_date")
    private Date createDate;
}
