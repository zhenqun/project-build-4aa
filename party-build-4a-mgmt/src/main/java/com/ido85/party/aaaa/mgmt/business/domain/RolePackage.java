package com.ido85.party.aaaa.mgmt.business.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Administrator on 2017/10/9.
 */
@Data
@Entity
@Table(name="r_4a_role_package")
public class RolePackage {

    @Id
    private Long id;

    @Column(name="role_id")
    private Long roleId;
    @Column(name="manage_id")
    private String manageId;
    @Column(name="client_id")
    private String clientId;
    @Column(name="create_by")
    private String createBy;
    @Column(name="create_date")
    private Date createDate;
    @Column(name="update_by")
    private String updateBy;
    @Column(name="update_date")
    private Date updateDate;
    @Column(name="del_flag")
    private String delFlag;
    @Column(name="del_date")
    private Date delDate;
    @Column(name="del_by")
    private String delBy;
}
