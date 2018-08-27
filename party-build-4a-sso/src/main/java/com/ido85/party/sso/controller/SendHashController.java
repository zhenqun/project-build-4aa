package com.ido85.party.sso.controller;

import com.ido85.party.sso.security.authentication.application.ConfigApplication;
import com.ido85.party.sso.security.authentication.application.UserApplication;
import com.ido85.party.sso.security.authentication.repository.UserResources;
import com.ido85.party.sso.security.utils.ListUntils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;

/**
 * 定时推送sso表中hash给外部接口 by lihj  2017/9/1.
 */

@Component
@Configurable
@EnableScheduling
@Slf4j
public class SendHashController {

    private static final String SEND_END_TIME = "SEND_END_TIME";
    private static final String SERVER_STATUS ="SERVER_STATUS";
    private static final String SEND_UPDATE_TIME="SEND_UPDATE_TIME";
    private static final String ORG_SYNC_LOCK="sendHashSchedule";

    //配置文件中推送的地址
    @Value("${sendHashurl}")
    private String sendHashurl;

    @Inject
    private UserResources resources;

    @Inject
    private ConfigApplication configApplication;

    @Inject
    private UserApplication userApplication;

//    @LoadBalanced
//    @Inject
//    private RestTemplate restTemplate;
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void  cronJob() throws Exception {
//        if(RedisUtils.setNx(ORG_SYNC_LOCK,"1")) {
            log.info("===============推送开始");
            String status = this.getServerStatus();
            log.info("===============获取状态：" + status);
            if ("0".equals(status)) {
                this.updateServerStatus();//修改服务器状态 其他服务器不可调用
                log.info("===============第一次修改状态" + status);
                try {
                    this.senHashToServer();
                    //修改服务器状态字段，以便其他服务器进行调用
                    this.updateServerForOrig();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("============调用简项库服务异常");
                    this.updateServerForOrig();
                }
            }
//        }
    }

    public void senHashToServer() throws Exception {
        log.info("===============正式调用");
        RestTemplate restTemplate = new RestTemplate();
        int toIndex = 1000;
        List<Object[]> hashList = new ArrayList<>();
        List<Object[]> newList = new ArrayList<>();
        List<Object[]> updateList = new ArrayList<>();
        List<Object[]> newUpdateList = new ArrayList<>();
        boolean receiveValue = true;
        HttpEntity<List> entity = null;
        String createTimeStr = null;//获取到的最近的注册时间
        String udpateTime=null;
        String updateTimeStr = null;//获取到的最近的更新时间
        //通过配置文件中字段值查询时间段内注册人员hash值
        hashList = userApplication.getUserHash(this.getEndTime());
        log.info("==============hashList大小：" + hashList.size());

        if (ListUntils.isNotNull(hashList)) {
            for (int i = 0; i < hashList.size(); i += 1000) {
                if (i + 1000 > hashList.size()) {
                    toIndex = hashList.size() - i;
                }
                newList = hashList.subList(i, i + toIndex);

                //获取此次发送list的最后一个值
                entity = new HttpEntity(newList);
                receiveValue = restTemplate.exchange(sendHashurl, HttpMethod.POST, entity, boolean.class).getBody();
                log.info("===============调用接口结束结果为" + receiveValue);
                if (!receiveValue) {
                    log.info("===============通信错误，请联系管理员！");
                    throw new Exception("通信错误，请联系管理员");
                }
            }
            //通过list集合中最新注册的hash值获取到该用户注册时间
            createTimeStr = this.getUserCreateTime(newList.get(newList.size() - 1)[0].toString());
            int r = this.insertEndTimetoConfig(createTimeStr);
            log.info("===============最后一次更新时间" + createTimeStr);
        }


        //获取更新手机号后的注册信息
        updateList = userApplication.getUserUpdateTimeList(this.getUpdateTime());
        log.info("==============此次更新手机号的updateList大小：" + updateList.size());
        if (ListUntils.isNotNull(updateList)) {
            for (int i = 0; i < updateList.size(); i += 1000) {
                if (i + 1000 > updateList.size()) {
                    toIndex = updateList.size() - i;
                }
                newUpdateList = updateList.subList(i, i + toIndex);

                //获取此次发送list的最后一个值
                entity = new HttpEntity(newUpdateList);
                try {
                    receiveValue = restTemplate.exchange(sendHashurl, HttpMethod.POST, entity, boolean.class).getBody();
                }catch (Exception e){
                    receiveValue =false;
                }
                log.info("===============调用接口查询更新后的数据结果为" + receiveValue);
                if (!receiveValue) {
                    log.info("===============更新后数据通信错误，请联系管理员！");
                    throw new Exception("更新通信错误，请联系管理员");
                }
            }
            //通过list集合中最新更新手机号hash值获取到该用户注册时间
            updateTimeStr = this.getUserUpateTime(newUpdateList.get(newUpdateList.size() - 1)[0].toString());
            int r = this.insertEndUpdateTimetoConfig(updateTimeStr);
            log.info("===============最后一次获取更新手机号的更新时间" + updateTimeStr);

        }
    }

    /**
     * 实例化RestTemplate
     *
     * @return
     */
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    /**
     * 获取Config中的发送截止时间段
     * @return
     */
    public String getEndTime() {
        return configApplication.queryConfigInfoByCode(SEND_END_TIME).getConfigValue();
    }
    /**
     * 获取Config中的发送更新时间段
     * @return
     */
    public String getUpdateTime() {
        return configApplication.queryConfigInfoByCode(SEND_UPDATE_TIME).getConfigValue();
    }


    /**
     * 获取Config中服务器状态值
     * @return
     */
    public String getServerStatus(){
        return configApplication.queryConfigInfoByCode(SERVER_STATUS).getConfigValue();
    }

    /**
     * 通过获取到hash值获取创建时间，然后更新到Config
     * @param hashValue
     * @return
     */
    public String getUserCreateTime(String hashValue){

        return  userApplication.getUserCreateTime(hashValue);

    }
    /**
     * 通过获取到hash值获取创建时间，然后更新到Config
     * @param hashValue
     * @return
     */
    public String getUserUpateTime(String hashValue){

        return  userApplication.getUserUpdateTime(hashValue);

    }

    /**
     * 把获取的最后一个list集合值写入config表
     * @param lastNum
     */
    public int insertEndTimetoConfig(String  lastNum) {
        return configApplication.insertIntoConfig(lastNum);
    }

    /**
     * 把获取的最后一个list集合值写入config表
     * @param lastNum
     */
    public int insertEndUpdateTimetoConfig(String  lastNum) {
        return configApplication.insertIntoUpdateConfig(lastNum);
    }

    /**
     * 修改服务器状态为1  其他服务器不可调用
     * @return
     */
    public int updateServerStatus(){
        return configApplication.updateServerS();
    }

    /**
     * 把服务器状态修改原状态 0
     * @return
     */
    public int updateServerForOrig(){
        return configApplication.updateServerOrig();
    }

}
