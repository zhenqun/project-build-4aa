package com.ido85.party.aaaa.mgmt.dto.expand;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by wangzb on 2017/6/11.
 */
@Data
public class OutOrderQueryDto implements Serializable{
    private static final long serialVersionUID = 1L;

    private String orderId;//流水id
    private String serialNumber;//工单流水
    private String orderType;//工单类型
    private String orderTag;//工单标志 0 待处理 1 处理中 2 等待数据转入 3 工单完成 4 处理异常
    private String data;//数据 json 字符串
    private String operationType;//操作类型 INNER_TRANS 省内党员组织关系转接 OUTTER_TRANS 外省党员组织关系转接
    private String nodeId;//节点id
    private String sourceSys;//源系统
    private String targetSys;//目标系统
    private String message;//处理信息
    private String parentId;//父流水id

    public OutOrderQueryDto() {
    }

    public OutOrderQueryDto(String orderId, String serialNumber, String orderType, String orderTag, String data, String operationType, String nodeId,
                            String sourceSys, String targetSys, String message, String parentId) {
        this.orderId = orderId;
        this.serialNumber = serialNumber;
        this.orderType = orderType;
        this.orderTag = orderTag;
        this.data = data;
        this.operationType = operationType;
        this.nodeId = nodeId;
        this.sourceSys = sourceSys;
        this.targetSys = targetSys;
        this.message = message;
        this.parentId = parentId;
    }
}
