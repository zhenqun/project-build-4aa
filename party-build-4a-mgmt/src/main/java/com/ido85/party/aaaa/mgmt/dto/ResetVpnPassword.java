package com.ido85.party.aaaa.mgmt.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2017/7/5.
 */
@Data
public class ResetVpnPassword {

    @NotNull
    private String userId;

    @NotNull
    private String type;//1 安全员  2审计员  3管理员

}
