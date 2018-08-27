package com.ido85.party.aaaa.mgmt.business.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/6/18.
 */
@Data
public class CancelBusiUserClientParam implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<String> ids;

    private String clientId;
}
