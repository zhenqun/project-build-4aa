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
@Table(name = "tf_f_apply_user_basic")
public class ApplyUserBasic extends BaseEntity implements Serializable {
    private static final long serivalVersion=1L;
    @Id
    @Column(name = "apply_user_id")
    private String applyUserId;

    @Column(name = "rel_name")
    private String relName;

    @Column(name = "id_card")
    private String idCard;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "remark")
    private String remark;

    @Column(name = "apply_date")
    private Date applyDate;

    @Column(name = "apply_by")
    private String applyBy;

    @Column(name = "status")
    private String status;

    @Column(name = "reason")
    private String reason;

    @Column(name = "approve_date")
    private Date approveDate;

    @Column(name = "ou")
    private String ou;

    @Column(name="del_date")
    private Date delDate;

    @Column(name="del_by")
    private String delBy;

    @Column(name="approve_by")
    private String approveBy;//审核人

}
