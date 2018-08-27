package com.ido85.party.sso.log.application;

import java.util.List;

/**
 * Created by Administrator on 2017/6/17.
 */
public interface PersonLoginLogApplication {

    /**
     * 添加登录日志
     * @param id
     * @param hash
     * @param log_type
     * @param clientName
     */
    void addLoginLog(String id, String hash, String log_type, String clientName);

    /**
     * 添加实名认证日志
     * @param id
     * @param hash
     * @param relnameauth
     */
    void addRelnameAuthLog(String id, String hash, String relnameauth,String type);

    /**
     * 添加角色认证日志
     * @param id
     * @param hash
     * @param partyerauth
     * @param roleNames
     */
    void addRoleAuthLog(String id, String hash, String partyerauth, List<String> roleNames,String type);
}
