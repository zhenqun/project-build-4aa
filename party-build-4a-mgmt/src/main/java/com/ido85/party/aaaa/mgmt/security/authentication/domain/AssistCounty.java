package com.ido85.party.aaaa.mgmt.security.authentication.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/10/17.
 */
@Data
@Entity
@Table(name = "t_4a_assist_county")
public class AssistCounty {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "org_id")
    private  String manageId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "org_name")
    private  String manageName;

    @Column(name="org_code")
    private  String manageCode;

}
