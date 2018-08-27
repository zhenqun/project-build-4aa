package com.ido85.party.aaaa.mgmt.business.dto.rule;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author Administrator
 * @date 2017/12/25
 */
@Data
public class PermissionRuleDto implements Serializable{

    private static final long serialVersionUID = 5998032055395858591L;

    private Long permissionId;

    private String permissionDes;

    private String ruleCode;

    private String andOrTag;

    private String isCom;

    private String errDes;

    public PermissionRuleDto(Long permissionId, String permissionDes, String ruleCode, String isCom, String errDes, String andOrTag) {
        this.permissionId = permissionId;
        this.permissionDes = permissionDes;
        this.ruleCode = ruleCode;
        this.isCom = isCom;
        this.errDes = errDes;
        this.andOrTag = andOrTag;
    }
}
