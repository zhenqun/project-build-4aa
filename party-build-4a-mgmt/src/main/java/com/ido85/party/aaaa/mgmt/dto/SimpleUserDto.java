package com.ido85.party.aaaa.mgmt.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.Date;

/**
 * Created by Administrator on 2018/1/18.
 */
@Data
public class SimpleUserDto {
    private String orgId;
    private String orgCode;
    private String orgName;
    private String userId;
    private String hash;
    private String sex;
    private String telephone;
    private String idCard;
    private String userName;
    private Date joinPartyDate;
    private String interName;
    private String nation;
    private String domainType;
    private Date birthday;
    private String status;
    private String political;
    private Integer sort;

    public SimpleUserDto() {
    }
}
