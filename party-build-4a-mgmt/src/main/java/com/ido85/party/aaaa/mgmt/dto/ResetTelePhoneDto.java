package com.ido85.party.aaaa.mgmt.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Administrator on 2017/12/4.
 */
@Data
public class ResetTelePhoneDto {

    @NotBlank
    private String newTelephone;

    @NotBlank
    private String oldTelephone;

    @NotBlank
    private String relName;

    @NotBlank
    private String idCard;

    @NotBlank
    private String updateBy;

    @NotBlank
    private String updateName;

    @NotBlank
    private String hash;//更新管理员hash

    @NotBlank
    private String verifyCode;//验证码

    private String pictureVerifyCode;



}
