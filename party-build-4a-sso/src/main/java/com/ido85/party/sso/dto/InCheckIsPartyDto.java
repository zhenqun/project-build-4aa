package com.ido85.party.sso.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Administrator on 2017/12/5.
 */
@Data
public class InCheckIsPartyDto {
    @NotBlank
    private String idCard;
    @NotBlank
    private String relName;
}
