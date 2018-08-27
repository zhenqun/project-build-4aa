package com.ido85.party.aaaa.mgmt.dto.userinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/17.
 */
@Data
public class ModifyVpnPasswordParam implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String vpn;

    private String password;

}
