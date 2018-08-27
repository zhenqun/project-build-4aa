package com.ido85.party.aaaa.mgmt.business.application.impl;


import com.ido85.party.aaaa.mgmt.business.application.ApplyUserApplication;
import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.OutManageDto;
import com.ido85.party.aaaa.mgmt.business.resources.ApplyUserRepository;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/10/9 0009.
 */
@Named
public class ApplyUserApplicationImpl  implements ApplyUserApplication {

    @Inject
    private ApplyUserRepository applyUserRepository;




    /**
     * 根据业务管理员id
     * 获取业务信息
     * @param in
     * @return
     */
    @Override
    public List<OutManageDto> querybusinessSystemByIds(List<String> in)throws Exception {
        if(ListUntils.isNull(in)){
            return null;
        }
        return applyUserRepository.querybusinessSystemByIds(in);
    }


    public  int reNameOrgName(String orgName,String orgId){

        if(StringUtils.isNull(orgName)){
            return 0;
        }

        if(StringUtils.isNull(orgId)){
            return 0;
        }

        applyUserRepository.reNameCreateOrgNameByOrgId(orgName,orgId);
        return applyUserRepository.reNameOrgNameByOrgId(orgName,orgId);


    }


}
