package com.ido85.party.sso.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2017/7/17.
 */
@Data
public class UpdateUserLogoParam {

    @NotNull
    private String userId;

    @NotNull
    private String fileName;
}
