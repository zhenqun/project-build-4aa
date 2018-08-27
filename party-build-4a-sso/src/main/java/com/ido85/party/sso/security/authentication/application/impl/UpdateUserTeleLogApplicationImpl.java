package com.ido85.party.sso.security.authentication.application.impl;

import com.ido85.party.sso.distribute.generator.IdGenerator;
import com.ido85.party.sso.security.authentication.application.UpdateUserTeleLogApplication;
import com.ido85.party.sso.security.authentication.domain.UpdateUserTeleLog;
import com.ido85.party.sso.security.authentication.repository.UpdateUserTeleLogResources;
import com.ido85.party.sso.security.authentication.repository.UserResources;
import com.ido85.party.sso.security.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/12/4.
 */
@Named
@Slf4j
public class UpdateUserTeleLogApplicationImpl  implements UpdateUserTeleLogApplication {

    @Inject
    private UserResources userResource;

    @Inject
    private UpdateUserTeleLogResources updateUserTeleLogResources;

    @Inject
    private IdGenerator idGenerator;




    @Override
    @Transactional
    public void addUpdateUserTeleLog(String oldTele, String newTele, String name,
                            String idCard, String hash,String updateHash, String updateBy, String updateName,String verifyCode) {

        int count=0;
        count =userResource.updateUserTele(newTele,hash,new Date());
        log.info("此次更新的手机个数："+count+"更新前手机号"+oldTele+"更新后手机号"
                 +newTele+"更新人"+updateBy+"==="+updateName+"更新时间"+new Date()+"手机验证码"+verifyCode);
        //新增更新记录
        UpdateUserTeleLog  teleLog = new UpdateUserTeleLog();

        teleLog.setId(StringUtils.toString(idGenerator.next()));
        teleLog.setOldTelephone(oldTele);
        teleLog.setNewTelephone(newTele);
        teleLog.setIdCard(idCard);
        teleLog.setRelName(name);
        teleLog.setCreateDate(new Date());
        teleLog.setIsSuccess("0");
        teleLog.setUpdateBy(updateBy);
        teleLog.setUpdateDate(new Date());
        teleLog.setVerifyCode(verifyCode);
        teleLog.setUpdateHash(updateHash);
        teleLog.setUpdateName(updateName);
        updateUserTeleLogResources.save(teleLog);
    }

}
