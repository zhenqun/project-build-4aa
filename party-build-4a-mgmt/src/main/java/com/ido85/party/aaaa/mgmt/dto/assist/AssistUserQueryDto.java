package com.ido85.party.aaaa.mgmt.dto.assist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Administrator on 2017/10/10.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssistUserQueryDto {

    private String idCard;

    private String isActive;

    private Integer pageNo;

    private Integer pageSize;

    private String relName;

    private String state;

    private String telephone;
}
