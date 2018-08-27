package com.ido85.party.sso.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/5.
 */
@Data
public class CenterUrlDto implements Serializable{
    private static final long serialVersionUID = -3522379754324428540L;

    private String area;

    private String url;
}
