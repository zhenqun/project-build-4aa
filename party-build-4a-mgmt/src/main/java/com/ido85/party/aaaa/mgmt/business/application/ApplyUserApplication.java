package com.ido85.party.aaaa.mgmt.business.application;


import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.OutManageDto;

import java.util.List;

/**
 * Created by Administrator on 2017/10/9 0009.
 */
public interface ApplyUserApplication {

    /**
     * 根据业务管理员id
     * 获取业务信息
     * @param in
     * @return
     */
    List<OutManageDto> querybusinessSystemByIds(List<String> in)throws Exception;


    int reNameOrgName(String orgName,String orgId);
}
