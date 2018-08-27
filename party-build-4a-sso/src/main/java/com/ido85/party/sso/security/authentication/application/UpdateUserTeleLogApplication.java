package com.ido85.party.sso.security.authentication.application;

/**
 * Created by Administrator on 2017/12/4.
 */
public interface UpdateUserTeleLogApplication {

    /**
     * 增加修改日志
     * @throws Exception
     */
    void addUpdateUserTeleLog(String oldTele,String newTele,String name,String idCard,String hash,
                              String updateHash,String updateBy,String updateName ,String verifyCode) throws Exception;



}
