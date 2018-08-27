package com.ido85.party.aaaa.mgmt.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Administrator on 2018/2/28.
 */
@Data
public class InFreshPermissionDto {

    @NotBlank
    private String clientId;

    @NotBlank
    private String type;//0---县级以上 1---直属党工委 2----基层党组织

    @NotBlank
    private String roleId;

}
