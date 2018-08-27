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
@Table(name="td_b_com_condition")
public class ComCondition implements Serializable{

    private static final long serialVersionUID = 7704788502783888200L;

    @Id
    private Long id;

    @Column(name="rule_code")
    private String ruleCode;

    @Column(name="order_no")
    private String orderNo;

    @Column(name="sub_condition_id")
    private Long subConditionId;

    @Column(name="del_flag")
    private String delFlag;

    @Column(name="create_date")
    private Date createDate;
}
