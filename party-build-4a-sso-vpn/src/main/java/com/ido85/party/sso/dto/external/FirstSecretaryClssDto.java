package com.ido85.party.sso.dto.external;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/10.
 */
@Data
public class FirstSecretaryClssDto implements Serializable{

    private static final long serialVersionUID = 8641550797657884204L;

    private String idCard;

    private String name;
}
