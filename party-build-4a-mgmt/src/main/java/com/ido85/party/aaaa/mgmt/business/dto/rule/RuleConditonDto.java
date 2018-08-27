package com.ido85.party.aaaa.mgmt.business.dto.rule;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Administrator
 * @date 2017/12/26
 */
@Data
public class RuleConditonDto implements Serializable{

    private static final long serialVersionUID = 2725546842744766468L;

    private String ruleCode;

    private String isCom;

    private List<ConditionDto> conditionDtoList;
}
