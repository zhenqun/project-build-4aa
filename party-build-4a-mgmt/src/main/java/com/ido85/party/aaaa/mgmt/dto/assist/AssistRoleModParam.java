package com.ido85.party.aaaa.mgmt.dto.assist;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/9.
 */
@Data
public class AssistRoleModParam implements Serializable{

    private static final long serialVersionUID = 1L;

    private String roleId;

    private String clientId;
}
