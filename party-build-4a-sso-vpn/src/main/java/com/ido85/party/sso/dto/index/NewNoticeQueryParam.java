package com.ido85.party.sso.dto.index;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/6/29.
 */
@Data
public class NewNoticeQueryParam implements Serializable{


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Date noticeDate;

    private Integer pageSize;
}
