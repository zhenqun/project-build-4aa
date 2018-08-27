package com.ido85.party.sso.log.application.impl;

import com.ido85.party.sso.distribute.generator.IdGenerator;
import com.ido85.party.sso.log.application.PersonLoginLogApplication;
import com.ido85.party.sso.log.domain.PersonLog;
import com.ido85.party.sso.log.resources.PersonLogResources;
import com.ido85.party.sso.platform.data.datasource.TargetDataSource;
import com.ido85.party.sso.security.utils.ListUntils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/6/17.
 */
@Named
public class PersonLoginLogApplicationImpl implements PersonLoginLogApplication {

    @Inject
    private PersonLogResources personLogResources;

    @Inject
    private IdGenerator idGenerator;

    /**
     * 增加登录日志
     * @param id
     * @param hash
     * @param logType
     * @param clientName
     */
    @TargetDataSource(name="logmain")
    public void addLoginLog(String id, String hash, String logType, String clientName) {
        PersonLog log = new PersonLog();
        log.setCreateDate(new Date());
        log.setHash(hash);
        log.setLogContent(clientName);
        log.setLogId(idGenerator.next());
        log.setLogName("登录日志");
        log.setLogType(logType);
        log.setUserId(id);
        personLogResources.save(log);
    }

    /**
     * 实名认证日志
     * @param id
     * @param hash
     * @param relnameauth
     */
    public void addRelnameAuthLog(String id, String hash, String relnameauth,String logType) {
        PersonLog log = new PersonLog();
        log.setCreateDate(new Date());
        log.setHash(hash);
        log.setLogContent("您已通过实名认证!");
        log.setLogId(idGenerator.next());
        log.setLogName("认证日志");
        log.setLogType(logType);
        log.setUserId(id);
        personLogResources.save(log);
    }

    /**
     * 角色认证日志
     * @param id
     * @param hash
     * @param partyerauth
     * @param roleNames
     */
    public void addRoleAuthLog(String id, String hash, String partyerauth, List<String> roleNames,String logType) {
        PersonLog log = null;
        List<PersonLog> logList = null;
        if(ListUntils.isNotNull(roleNames)){
            logList = new ArrayList<>();
            for(String roleName:roleNames){
                log = new PersonLog();
                log.setCreateDate(new Date());
                log.setHash(hash);
                log.setLogId(idGenerator.next());
                log.setLogContent(roleName);
                log.setLogName("认证日志");
                log.setLogType(logType);
                log.setUserId(id);
                logList.add(log);
            }
        }
        personLogResources.save(logList);
    }
}
