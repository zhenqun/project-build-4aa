package com.ido85.party.aaaa.mgmt.security.authentication.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/6/17.
 */
@Data
public class CancelSecUserClientParam implements Serializable{

    private static final long serialVersionUID = -327373290377613048L;

    private List<String> ids;

    private String clientId;
}
