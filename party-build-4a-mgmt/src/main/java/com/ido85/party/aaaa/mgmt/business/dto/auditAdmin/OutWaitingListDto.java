package com.ido85.party.aaaa.mgmt.business.dto.auditAdmin;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 private Date applyDate;
 private Date approveDate;
 private String idCard;
 private String reason;
 private String relName;
 private String remark;
 private String status;
 private String telephone;
 private String applyTime;
 private String approveTime;
 * Created by Administrator on 2017/10/10 0010.
 */
@Data
public class OutWaitingListDto {
    private String itemId;
    private String relName;
    private String idCard;
    private String remark;
    private String telephone;
    private String status;
    private Date applyDate;
    private List<OutManageDto> roles;
    private String applyTime;
    private Date approveDate;
    private String approveBy;
    private String approveTime;


    public OutWaitingListDto(Date applyDate, String idCard, String itemId, String relName, String remark, String telephone, String status) {
        this.applyDate = applyDate;
        this.idCard = idCard;
        this.itemId = itemId;
        this.relName = relName;
        this.remark = remark;
        this.telephone = telephone;
        this.status = status;
    }

    public OutWaitingListDto() {
    }
}
