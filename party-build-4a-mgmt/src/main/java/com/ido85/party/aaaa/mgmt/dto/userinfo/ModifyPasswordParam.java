package com.ido85.party.aaaa.mgmt.dto.userinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/16.
 */
@Data
public class ModifyPasswordParam implements Serializable{


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String telephone;

    private String password;

    private String verifyCode;
}
