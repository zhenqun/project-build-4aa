package com.ido85.party.aaaa.mgmt.dto.assist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2017/10/9.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddAssistUserScopeParam {

    @NotNull
    private String clientId;
    @NotNull
    private String manageCode;
    @NotNull
    private String manageId;
    @NotNull
    private String manageName;
    @NotNull
    private String ouName;
    @NotNull
    private String createManageId;

    private String createManageCode;

    private String createManageName;

}
