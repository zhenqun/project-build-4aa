package com.ido85.party.aaaa.mgmt.business.dto.rule;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author Administrator
 * @date 2017/12/25
 */
@Data
public class RuleDto implements Serializable{

    private static final long serialVersionUID = 4822378556935230726L;

    private String ruleCode;

    private String isCom;

    private String errDes;
}
