package com.ido85.party.sso.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2017/7/17.
 */
@Data
public class AddAuthenticationParam {

    @NotNull
    private String idCard;
    @NotNull
    private String relName;
    @NotNull
    private String uniqueKey;
    @NotNull
    private String userId;
}
