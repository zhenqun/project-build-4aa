package com.ido85.party.aaa.authentication.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/12.
 */
@Data
public class DeleteAuthenticationParam implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;

    private String clientId;

    private Long roleId;
}
