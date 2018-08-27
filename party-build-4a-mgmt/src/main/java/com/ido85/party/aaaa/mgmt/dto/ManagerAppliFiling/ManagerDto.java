package com.ido85.party.aaaa.mgmt.dto.ManagerAppliFiling;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/10/12.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerDto {

    private Number createDate;

    private String idCard;

    private String itemId;

    private String relName;

    private String remark;

    private String telephone;

    private String state;

    private List<UserRoles> roles;

}
