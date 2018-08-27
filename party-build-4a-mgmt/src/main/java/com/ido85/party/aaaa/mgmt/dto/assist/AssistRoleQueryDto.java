package com.ido85.party.aaaa.mgmt.dto.assist;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/9.
 */
@Data
public class AssistRoleQueryDto implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String clientId;

    private String clientName;

    private String description;

    private String detail;

    private String isCommon;//0否 1是

    private String roleId;

}
