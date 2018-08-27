package com.ido85.party.aaaa.mgmt.business.dto.rule;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author Administrator
 * @date 2017/12/26
 */
@Data
public class ConditionDto implements Serializable{

    private static final long serialVersionUID = -4941787753357753589L;

    private String judgeMethod;

    private String field;

    private String valueType;

    private String minValue;

    private String maxValue;

    private String enumValue;

    private String regValue;
}
