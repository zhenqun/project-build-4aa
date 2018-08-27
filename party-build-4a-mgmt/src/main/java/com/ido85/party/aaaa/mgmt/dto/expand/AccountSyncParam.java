package com.ido85.party.aaaa.mgmt.dto.expand;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2017/8/1.
 */
@Data
public class AccountSyncParam {

    @NotNull
    private String username;

    @NotNull
    private String relName;

    @NotNull
    private String password;

    @NotNull
    private String telephone;

    @NotNull
    private String vpn;

    @NotNull
    private String clientId;

    @NotNull
    private Long roleId;
}
