package com.ido85.party.aaaa.mgmt.security.authentication.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 *
 * @author Administrator
 * @date 2017/12/13
 */
@Data
@Entity
@Table(name="td_b_order_work_log")
public class OrderWorkLog {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="serial_number")
    private String serialNumber;

    @Column(name="type")
    private String type;

    @Column(name="user_id")
    private String userId;

    @Column(name="old_manage_code")
    private String oldManageCode;

    @Column(name="old_manage_name")
    private String oldManageName;

    @Column(name="old_manage_id")
    private String oldManageId;

    @Column(name="create_date")
    private Date createDate;
}
