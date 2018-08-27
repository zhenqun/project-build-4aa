package com.ido85.party.aaaa.mgmt.dto.expand;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/7.
 */
@Data
public class CheckAccount implements Serializable{
    private static final long serialVersionUID = -2884131801269982055L;

    private String idCard;

    private String name;

    private String telphone;
}
