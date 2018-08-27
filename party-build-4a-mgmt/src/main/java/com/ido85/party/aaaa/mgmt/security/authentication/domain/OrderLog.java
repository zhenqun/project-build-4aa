package com.ido85.party.aaaa.mgmt.security.authentication.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Administrator on 2017/9/26.
 */
@Entity
@Table(name="td_b_order_log")
@Data
public class OrderLog {

    @Id
    private Long id;

    @Column(name="order_id")
    private String orderId;

    @Column(name="data")
    private String jsonData;

    @Column(name="order_tag")
    private String orderTag;

    @Column(name="start_date")
    private Date startDate;

    @Column(name="end_date")
    private Date endDate;

    @Column(name="serial_number")
    private String serialNumber;

    @Column(name="method")
    private String method;
}
