package com.ido85.party.sso.security.authentication.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/5.
 */
@Data
@Entity
@Table(name="td_m_url")
public class CenterUrl implements Serializable{

    private static final long serialVersionUID = -6826388009063898135L;

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="url")
    private String url;

    @Column(name="del_flag")
    private String delFlag;

    @Column(name="description")
    private String description;

    @Column(name="area")
    private String area;
}
