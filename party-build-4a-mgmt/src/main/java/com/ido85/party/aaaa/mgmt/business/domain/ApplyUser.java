package com.ido85.party.aaaa.mgmt.business.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/10/9 0009.
 */
@Data
@Entity
@Table(name = "tf_f_apply_user")
public class ApplyUser extends BaseEntity implements Serializable {
    private static final long serivalVersion=1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "fz_user_id")
    private String fzUserId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "apply_manage_name")
    private String applyManageName;

    @Column(name = "apply_manage_id")
    private String applyManageId;

    @Column(name = "apply_manage_code")
    private String applyManageCode;

    @Column(name = "apply_user_id")
    private String applyUserId;

    @Column(name="del_date")
    private Date delDate;

    @Column(name="del_by")
    private String delBy;

    @Column(name = "create_manage_id")
    private String createManageId;

    @Column(name = "create_manage_code")
    private String createManageCode;

    @Column(name = "create_manage_name")
    private String createManageName;

    @Column(name ="level")
    private String level;

    @Column(name ="type")
    private String type;

}
