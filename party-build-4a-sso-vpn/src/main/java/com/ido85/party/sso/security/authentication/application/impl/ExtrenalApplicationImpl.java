package com.ido85.party.sso.security.authentication.application.impl;

import com.ido85.party.sso.dto.external.FirstSecretaryClssDto;
import com.ido85.party.sso.dto.external.FirstSecretaryClssParam;
import com.ido85.party.sso.security.authentication.application.ExternalApplication;
import com.ido85.party.sso.security.utils.ListUntils;
import com.ido85.party.sso.security.utils.StringUtils;
import org.codehaus.groovy.util.StringUtil;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2017/11/10.
 */
@Named
public class ExtrenalApplicationImpl implements ExternalApplication{

    @Inject
    private EntityManager entityManager;

    /**
     * 查询基层干部学历教育拥有班级管理权限的所有管理员
     * @param param
     * @return
     */
    @Override
    public List<FirstSecretaryClssDto> getEBranchUserInfoByIds(FirstSecretaryClssParam param) {
        if(StringUtils.isNull(param.getManageId())){
            return null;
        }
        List<FirstSecretaryClssDto> dtoList = null;
        FirstSecretaryClssDto firstSecretaryClssDto = null;
        StringBuffer sb = new StringBuffer("select t.id_card,t.\"name\" from t_4a_actors t,r_4a_user_role r,r_4a_role_permission p,t_4a_permission e where \n" +
                "t.id = r.user_id and r.role_id = p.role_id and p.permission_id = e.permission_id and e.permission_name = 'FIRSTSECRETARY_CLASS' and r.manage_id = :manageId");
        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("manageId",param.getManageId());
        List<Object[]> objects = query.getResultList();
        if(ListUntils.isNotNull(objects)){
            dtoList = new ArrayList<>();
            for(Object[] o:objects){
                firstSecretaryClssDto = new FirstSecretaryClssDto();
                firstSecretaryClssDto.setIdCard(StringUtils.toString(o[0]));
                firstSecretaryClssDto.setName(StringUtils.toString(o[1]));
                dtoList.add(firstSecretaryClssDto);
            }
            HashSet h = new HashSet(dtoList);
            dtoList.clear();
            dtoList.addAll(h);
        }
        return dtoList;
    }
}
