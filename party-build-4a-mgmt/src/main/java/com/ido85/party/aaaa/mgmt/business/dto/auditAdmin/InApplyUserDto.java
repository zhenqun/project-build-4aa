package com.ido85.party.aaaa.mgmt.business.dto.auditAdmin;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Administrator on 2017/10/9 0009.
 */
@Data
public class InApplyUserDto {
    private String clientId;
    private String idCard;
    private String manageId;
    private String relName;
    private String status;//0：全部；1：申请中；2：通过；3：驳回
    private String telephone;
    private String userId;
    private Integer pageNo;
    private Integer pageSize;


    private List<String> manageIds;//后端用  前端不用传  注：当前端不传manageId时使用
    private  List<String>clientIds;//后端用  前端不用传  注：当前端不传clientId时使用
    private List<String> statusList;//后端用  前端不用传  注：当status为0/5时使用
}
