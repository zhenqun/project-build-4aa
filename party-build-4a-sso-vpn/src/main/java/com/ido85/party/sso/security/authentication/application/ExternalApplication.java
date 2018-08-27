package com.ido85.party.sso.security.authentication.application;

import com.ido85.party.sso.dto.external.FirstSecretaryClssDto;
import com.ido85.party.sso.dto.external.FirstSecretaryClssParam;

import java.util.List;

/**
 * Created by Administrator on 2017/11/10.
 */
public interface ExternalApplication{

    /**
     * 查询基层干部学历教育拥有班级管理权限的所有管理员
     * @param param
     * @return
     */
    List<FirstSecretaryClssDto> getEBranchUserInfoByIds(FirstSecretaryClssParam param);

}
