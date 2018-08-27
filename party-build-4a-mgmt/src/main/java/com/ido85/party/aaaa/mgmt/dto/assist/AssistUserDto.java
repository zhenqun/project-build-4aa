package com.ido85.party.aaaa.mgmt.dto.assist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/10/10.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssistUserDto {

    private String authorizationCode;

    private List<AssistClientsQueryResultDto> clients;

    private String createDate;

    private String fzuserId;

    private String idCard;

    private String isActive;

    private String relName;

    private String state;

    private String telephone;
}
