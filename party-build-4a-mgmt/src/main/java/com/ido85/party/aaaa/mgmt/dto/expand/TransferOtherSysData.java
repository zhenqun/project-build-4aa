package com.ido85.party.aaaa.mgmt.dto.expand;

import lombok.Data;

import java.util.List;

/**
 * Created by wangzb on 2017/9/8.
 * description:
 */
@Data
public class TransferOtherSysData {
    private String idCardNo;//身份证号
    private String 	name;//姓名
    private List<OrgTransferDto> branchList;//更改党组织列表
    private String sourceOrgId;//源组织id
    private String targetOrgId;//目标组织id
    private String transferType;//转接类型--1 转出到外省 2 转接 3 整建制转接
    private String sourceNodeId;//源节点id--整建制sso使用
    private String targetNodeId;//目标节点id --整建制sso使用
}
