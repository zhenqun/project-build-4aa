package com.ido85.party.sso.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Administrator on 2017/8/31.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModifyMobileParam {

    @NonNull
    @NotBlank
    private String oldMobile;
    @NonNull
    @NotBlank
    private String newMobile;
    @NonNull
    @NotBlank
    private  String verifyCode;

}
