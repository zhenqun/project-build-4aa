package com.ido85.party.aaaa.mgmt.dto.userinfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2017/8/30.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModifyMobileParam {
    @NotNull
    @NotBlank
    private String newMobile;
    @NotNull
    @NotBlank
    private String oldMobile;
    @NotNull
    @NotBlank
    private String verifyCode;
}
