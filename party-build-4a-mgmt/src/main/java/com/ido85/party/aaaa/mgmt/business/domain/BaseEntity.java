package com.ido85.party.aaaa.mgmt.business.domain;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 实体的基类
 * Created by Administrator on 2017/10/11.
 */
@Data
@MappedSuperclass
public class BaseEntity implements Serializable{
    private static final long serialVersionUID = 1L;
    @Column(name="create_by")
    protected String createBy;
    @Column(name="create_date")
    protected Date createDate;
    @Column(name="update_by")
    protected String updateBy;
    @Column(name="update_date")
    protected Date updateDate;
    @Column(name="del_flag")
    protected String delFlag;
    @Column(name="del_date")
    protected Date delDate;
    @Column(name="del_by")
    protected String delBy;

    @PrePersist
    public void preInsert() throws Exception{
        this.updateDate = new Date();
        this.createDate = new Date();
        this.delFlag = "0";
    }

    @PreUpdate
    public void preUpdate()  throws Exception{
        this.updateDate = new Date();
    }
    public void preUpdate(String updateBy) {
        this.updateDate = new Date();
        this.updateBy = updateBy;
    }
    public void preInsert(String userId) {
        this.updateDate = new Date();
        this.createDate = new Date();
        this.createBy = userId;
        this.updateBy = userId;
        this.delFlag = "0";
    }

    public BaseEntity() {
    }
}
