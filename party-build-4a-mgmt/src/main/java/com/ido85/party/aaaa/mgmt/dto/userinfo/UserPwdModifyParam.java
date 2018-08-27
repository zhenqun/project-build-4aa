package com.ido85.party.aaaa.mgmt.dto.userinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/19.
 */
@Data
public class UserPwdModifyParam implements Serializable{


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String newPassword;

    private String oldPassword;
}


