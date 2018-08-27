package com.ido85.party.sso.dto.external;

import lombok.Data;

/**
 * Created by Administrator on 2017/8/30.
 */
@Data
public class CheckHaveAdmin {

    private String orgId;

    private String clientId;

    private String permissionName;
}
