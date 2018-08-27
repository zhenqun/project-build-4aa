package com.ido85.party.aaaa.mgmt.business.application.impl;


import com.ido85.party.aaaa.mgmt.business.application.AssistManageApplication;
import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.OutManageDto;
import com.ido85.party.aaaa.mgmt.business.resources.AssistManageRepository;
import com.ido85.party.aaaa.mgmt.security.common.BaseApplication;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * Created by Administrator on 2017/10/10 0010.
 */
@Named
@Slf4j
public class AssistManageApplicationImpl extends BaseApplication implements AssistManageApplication {
    @Inject
    private AssistManageRepository assistManageRepository;



    /*根据当前登陆人的业务系统clientIds，管理范围manageIds，通过辅助安全员管理应用表tf_f_assist_manage，匹配应用client_id，创建者范围create_manage_id
    获取辅助安全员的辅助安全员管理范围manageIds  应用clientIds*/
    @Override
    public List<OutManageDto> getFzUserClientidsAndManageIds(List<String> userclientIds, List<String> usermanageIds) throws Exception {
        if(ListUntils.isNull(userclientIds)||ListUntils.isNull(usermanageIds)){
            return null;
        }
        return assistManageRepository.getFzUserClientidsAndManageIds(userclientIds,usermanageIds);
    }


    public  int reNameOrgName(String newOrgName,String orgId){
        if(StringUtils.isNull(newOrgName)){
            return 0;
        }
        if(StringUtils.isNull(orgId)){
            return 0;
        }
        //修改创建人的组织名称
        log.info("辅助安全员系统改名========>+AssistManageApplicationImpl=======+reNameOrgName");
//        assistManageRepository.reNameCreateOrgName(newOrgName,orgId);
       return  assistManageRepository.reNameOrgName(newOrgName,orgId);

    }
}
