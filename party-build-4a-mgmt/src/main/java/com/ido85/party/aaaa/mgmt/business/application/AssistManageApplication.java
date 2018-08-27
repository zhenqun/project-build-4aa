package com.ido85.party.aaaa.mgmt.business.application;

import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.OutManageDto;

import java.util.List;

/**
 * Created by Administrator on 2017/10/10 0010.
 */
public interface AssistManageApplication {


    /*根据当前登陆人的业务系统clientIds，管理范围manageIds，通过辅助安全员管理应用表tf_f_assist_manage，匹配应用client_id，创建者范围create_manage_id
    获取辅助安全员的辅助安全员管理范围manageIds  应用clientIds*/
    List<OutManageDto> getFzUserClientidsAndManageIds(List<String> userclientIds, List<String> usermanageIds)throws Exception;

    int reNameOrgName(String newOrgName,String orgId);
}
