package com.ido85.party.aaaa.mgmt.business.application;

import java.util.Map;

/**
 * Created by Administrator on 2017/12/25.
 */
public interface RuleApplication {

    /**
     * 根据角色id判断所有权限是否合法
     * @param roleId
     * @return
     */
    Map<String,String> checkPermissionRule(Long roleId,String manageType,String manageLevel,String manageId,String manageCode);
}
