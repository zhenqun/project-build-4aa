package com.ido85.party.aaaa.mgmt.controller;


import com.ido85.party.aaaa.mgmt.business.resources.BusinessUserResources;
import com.ido85.party.aaaa.mgmt.dto.base.ConfigEnum;
import com.ido85.party.aaaa.mgmt.internet.resource.InternetUserResources;
import com.ido85.party.aaaa.mgmt.security.authentication.application.ConfigApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserResources;
import com.ido85.party.aaaa.mgmt.security.utils.DateUtils;
import com.ido85.party.aaaa.mgmt.security.utils.EmailUtil;
import com.ido85.party.aaaa.mgmt.security.utils.RedisUtils;
import com.ido85.party.aaaa.mgmt.security.utils.SendMailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by Administrator on 2017/9/1.
 */
@Component
@Configurable
@EnableScheduling
@Slf4j
public class RunnerController {

    @Inject
    private InternetUserResources internetUserResources;

    @Inject
    private UserResources userResources;

    @Inject
    private BusinessUserResources businessUserResources;

    private static final String mailRedisKey = "SENDMAILKEY";

    @Value("${isSendMail}")
    private String isSendMail;

    @Inject
    private ConfigApplication configApplication;

    @Scheduled(cron = "0 0 1 * * ?")
    public void Statistics(){
        if(isSendMail.equals("1")){
            if(RedisUtils.setNx(mailRedisKey,"30")){
                log.info("开始给赵总发邮件============");
                try {
                    SendEmail();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                log.info("给赵总发完了邮件============");
            }
        }
    }

    private void SendEmail() throws Exception {
        String yesterDay = DateUtils.yesterday();
        //统计党员注册数,昨日注册数
        Long registerCnt = this.RegisterCnt();
        Long yesRegisterCnt = this.YesRegisterCnt(yesterDay);
        //统计安全员日登陆人数，人登陆人次
        Long secuLoginCnt = this.SecuLoginCnt(yesterDay);
        Long secuLoginTimeCnt = this.SecuLoginTimeCnt(yesterDay);
        //统计审计员日登陆人数，人登陆人次
        Long autLoginCnt = this.AutLoginCnt(yesterDay);
        Long autLoginTimeCnt = this.AutLoginTimeCnt(yesterDay);
        //统计管理员日登陆人数，人登陆人次
        Long adminLoginCnt = this.AdminLoginCnt(yesterDay);
        Long adminLoginTimeCnt = this.AdminLoginTimeCnt(yesterDay);
        //统计安全员总数，昨日新增数
        Long secuCnt = this.SecuCnt();
        Long yesSecuCnt = this.YesSecuCnt(yesterDay);
        //统计审计员总数，昨日新增数
        Long autCnt = this.AutCnt();
        Long yesAutCnt = this.YesAutCnt(yesterDay);
        //统计管理员总数，昨日新增数
        Long adminCnt = this.AdminCnt();
        Long yesAdminCnt = this.YesAdminCnt(yesterDay);
        String content ="党员总注册数：" +registerCnt+" ,党员注册数:"+yesRegisterCnt+
                "<br/>安全员登录人数："+secuLoginCnt+",安全员登录次数:"+secuLoginTimeCnt+"<br/>审计员登录人数 ："+autLoginCnt+",审计员登录次数："+autLoginTimeCnt
                +"<br/>管理员登录人数："+adminLoginCnt+",管理员登录次数："+adminLoginTimeCnt+"<br/>安全员总数："+secuCnt+"，昨日安全员新增数:"+yesSecuCnt+"<br/>审计员总数:"+autCnt+
                ",审计员昨日新增数:"+yesAutCnt+"<br/>管理员总数:"+adminCnt+",昨日管理员新增数:"+yesAdminCnt;
        //发送邮件
        EmailUtil.sendMail(configApplication.queryConfigInfoByCode(ConfigEnum.MAIL_FROM.getCode()).getConfigValue(),
                configApplication.queryConfigInfoByCode(ConfigEnum.MAIL_PWD.getCode()).getConfigValue(),configApplication.queryConfigInfoByCode(ConfigEnum.MAIL_TO.getCode()).getConfigValue(),
                configApplication.queryConfigInfoByCode(ConfigEnum.MAIL_COPY.getCode()).getConfigValue(),yesterDay+configApplication.queryConfigInfoByCode(ConfigEnum.MAIL_SUBJECT.getCode()).getConfigValue(),content,null);
    }

    private Long YesAdminCnt(String yesterDay) {
        return businessUserResources.yesAdminCnt(yesterDay);
    }

    private Long AdminCnt() {
        return businessUserResources.adminCnt();
    }

    private Long YesAutCnt(String yesterDay) {
        return userResources.yesAutCnt(yesterDay);
    }

    private Long AutCnt() {
        return userResources.autCnt();
    }

    private Long YesSecuCnt(String yesterDay) {
        return userResources.yesSecuCnt(yesterDay);
    }

    private Long SecuCnt() {
        return userResources.secuCnt();
    }

    private Long AdminLoginTimeCnt( String yesterDay) {
        return businessUserResources.adminLoginTimeCnt(yesterDay);
    }

    private Long AdminLoginCnt( String yesterDay) {
        return businessUserResources.adminLoginCnt(yesterDay);
    }

    private Long AutLoginTimeCnt( String yesterDay) {
        return userResources.autLoginTimeCnt(yesterDay);
    }

    private Long AutLoginCnt( String yesterDay) {
        return userResources.auLoginCnt(yesterDay);
    }

    private Long SecuLoginTimeCnt( String yesterDay) {
        return userResources.secuLoginTimeCnt(yesterDay);
    }

    private Long SecuLoginCnt( String yesterDay) {
        return userResources.secuLoginCnt(yesterDay);
    }

    private Long YesRegisterCnt( String yesterDay) {
        return internetUserResources.yesRegisterCnt(yesterDay);
    }

    private Long RegisterCnt() {
        Long cnt = internetUserResources.registerCnt();
        return cnt;
    }
}
