package com.ido85.party.aaaa.mgmt.dto.expand;

import lombok.Data;

/**
 * Created by wangzb on 2017/9/21.
 * description:整建制转接列表
 */
@Data
public class OrgTransferDto {
    private String oldOrgId;//原组织id
    private String oldOrgCode;//原组织code
    private String oldOrgName;//原组织名称
    private String newOrgId;//新组织id
    private String newOrgCode;//新组织code
    private String newOrgName;//新组织名称
}
