package com.ido85.party.aaaa.mgmt.security.authentication.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/6.
 */
@Data
@Entity
@Table(name="tf_f_reset_vpnpwd")
public class ResetVpnLog {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="user_id")
    private String userId;

    @Column(name="vpn")
    private String vpn;

    @Column(name="ou")
    private String ou;

    @Column(name="create_by")
    private String createBy;

    @Column(name="create_date")
    private Date createDate;

    @Column(name="type")
    private String type;

}
