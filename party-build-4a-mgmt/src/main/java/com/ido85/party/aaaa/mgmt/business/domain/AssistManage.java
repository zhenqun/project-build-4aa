package com.ido85.party.aaaa.mgmt.business.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/11.
 */
@Data
@Entity
@Table(name = "tf_f_assist_manage")
public class AssistManage extends BaseEntity implements Serializable {
    private static final long serivalVersion=1L;
    @Id
    @Column(name = "assist_manage_id")
    private String assistManageId;

    @Column(name = "fz_user_id")
    private String fzuserId;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "manage_id")
    private String manageId;

    @Column(name = "manage_name")
    private String manageName;

    @Column(name = "manage_code")
    private String manageCode;

    @Column(name = "create_manage_id")
    private String createManageId;

    @Column(name = "create_manage_name")
    private String createManageName;

    @Column(name = "create_manage_code")
    private String createManageCode;



}
