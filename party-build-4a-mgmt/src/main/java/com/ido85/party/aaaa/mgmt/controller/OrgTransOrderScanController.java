package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.business.domain.AssistManage;
import com.ido85.party.aaaa.mgmt.business.domain.BusinessUserVpnRel;
import com.ido85.party.aaaa.mgmt.business.domain.UserRole;
import com.ido85.party.aaaa.mgmt.business.resources.AssistManageResources;
import com.ido85.party.aaaa.mgmt.business.resources.BusinessuserVpnRelResources;
import com.ido85.party.aaaa.mgmt.business.resources.UserRoleResource;
import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.dto.expand.*;
import com.ido85.party.aaaa.mgmt.ldap.application.LdapApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.OrderLog;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.OrderWorkLog;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.UserClientRel;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.UserVpnRel;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.OrderLogResouraces;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.OrderWorkLogResources;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserClientRelResource;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserVpnRelResources;
import com.ido85.party.aaaa.mgmt.security.utils.JsonUtil;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import com.ido85.party.aaaa.mgmt.security.utils.RedisUtils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.*;

/**
 *
 * @author yin
 * @date 2017/9/25
 */
@Component
@Configurable
@EnableScheduling
@Slf4j
public class OrgTransOrderScanController {

    @Inject
    private UserRoleResource userRoleResource;

    @Inject
    private UserClientRelResource userClientRelResource;

    /**
     *  工单获取地址
     */
    @Value("${order.getOrgOrder}")
    private String getOrgOrder;

    /**
     *  工单状态更改地址
     */
    @Value("${order.updateOrderState}")
    private String updateOrderState;

    /**
     * 根据工单id批量修改状态为锁定状态
     */
    @Value("${order.updateOrderLock}")
    private String updateOrderLock;

    @Inject
    private BusinessuserVpnRelResources businessuserVpnRelResources;

    @Inject
    private LdapApplication ldapApplication;

    @Inject
    private UserVpnRelResources userVpnRelResources;

    @Inject
    private IdGenerator idGenerator;

    @Inject
    private OrderLogResouraces orderLogResouraces;

    @Inject
    private AssistManageResources assistManageResources;

    /**
     * redis锁的主键值
     */
    private static final String ORG_ORDER_REDIS_KEY = "orgOrderRedisKey";

    /**
     * redis锁的锁定时间
     */
    private static final String REDIS_KEY_TIME = "30";

    @Inject
    private OrderWorkLogResources orderWorkLogResources;

//    @Scheduled(cron = "0 0/1 * * * ?")
    @Scheduled(cron = "0 0/1 1-5 * * ? ")
    public void orderScan(){
        try{
            if(RedisUtils.setNx(ORG_ORDER_REDIS_KEY,REDIS_KEY_TIME)){
                log.info("============开始扫描整建制转接工单===========");
                orderDo();
                log.info("============整建制转接工单完成===========");
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("=================整建制工单发生异常==============",e);
        }
    }

    /**
     * 工单开始
     */
    private void orderDo() {
        List<TransferOtherSysData> orderList = null;
       //获取工单数据
        OutOrderQueryDto[] outOrderQueryDtosArr = this.getOrder();
        if(null != outOrderQueryDtosArr){
            log.info("============扫描到整建制转接工单,开始处理===========");
        }
        //修改工单状态为-1
        this.updateOrderLock(outOrderQueryDtosArr);
        //处理工单
        if(null != outOrderQueryDtosArr && outOrderQueryDtosArr.length > 0){
            this.resolveOrder(outOrderQueryDtosArr);
        }
    }

    /**
     * 修改工单状态为-1
     * @param outOrderQueryDtosArr
     */
    private void updateOrderLock(OutOrderQueryDto[] outOrderQueryDtosArr) {
        List<String> orderIds = null;
        if(null != outOrderQueryDtosArr && outOrderQueryDtosArr.length > 0){
            orderIds = new ArrayList<>();
            for(OutOrderQueryDto dto:outOrderQueryDtosArr){
                orderIds.add(dto.getOrderId());
            }
            RestTemplate restTemplate = new RestTemplate();
            try{
                restTemplate.postForObject(updateOrderLock,orderIds,Boolean.class);
            }catch (Exception e){
                e.printStackTrace();
                log.error("===============锁定工单失败",e);
            }
        }
    }

    /**
     * 获取工单
     * @return
     */
    public OutOrderQueryDto[] getOrder() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String,String> params = new HashMap<String,String>(2);
        params.put("orderTag","0");
        params.put("targetSys","PARTY_BUILD_4A_MGMT");
        params.put("orderType","7");
        HttpEntity<Map> entity = new HttpEntity<>(params);
        OutOrderQueryDto[] outOrderQueryDtosArr = restTemplate.exchange(getOrgOrder, HttpMethod.POST,entity,OutOrderQueryDto[].class).getBody();
        return outOrderQueryDtosArr;
    }

    /**
     * 解析工单
     * @param outOrderQueryDtosArr
     */
    @Transactional(rollbackFor = Exception.class)
    private void resolveOrder(OutOrderQueryDto[] outOrderQueryDtosArr) {
        String jsonData = null;
        TransferOtherSysData transferOtherSysData = null;
        for(OutOrderQueryDto outOrderQueryDto:outOrderQueryDtosArr){
            if(null != outOrderQueryDto){
                jsonData = outOrderQueryDto.getData();
                if(!StringUtils.isNull(jsonData)){
                    transferOtherSysData = (TransferOtherSysData)JsonUtil.jsonToBean(jsonData,TransferOtherSysData.class);
                    this.orderRun(transferOtherSysData,outOrderQueryDto);
                    this.orderSerialNumber(outOrderQueryDto);
                    this.orderState(outOrderQueryDto);
                }
            }
        }
    }

    /**
     * 工单处理开始
     * @param data
     */
    private void orderRun(TransferOtherSysData data,OutOrderQueryDto outOrderQueryDto) {
        List<OrgTransferDto> branchList = null;
        List<UserClientRel> userClientRels = null;
        List<UserRole> userRoles = null;
        String sourceNodeId = null;
        String targetNodeId = null;
        String newOrgCode = null;
        String oldOrgId = null;
        String newOrgName = null;
            if(null != data){
                branchList = data.getBranchList();
                sourceNodeId = data.getSourceNodeId();
                targetNodeId = data.getTargetNodeId();
                if(ListUntils.isNotNull(branchList)){
                    for(OrgTransferDto orgTransferDto:branchList){
                        newOrgCode = orgTransferDto.getNewOrgCode();
                        oldOrgId = orgTransferDto.getOldOrgId();
                        newOrgName = orgTransferDto.getNewOrgName();
                        /**业务处理**/
                        userClientRels = userClientRelResource.getRelByManageId(oldOrgId);
                        if(ListUntils.isNotNull(userClientRels)){
                            //创建安全员审计员日志
                            this.transUserClientRelLog(userClientRels,outOrderQueryDto.getSerialNumber());
                            //更新安全员审计员
                            this.transUserClientRel(userClientRels,newOrgCode,newOrgName,sourceNodeId,targetNodeId);
                        }
                        userRoles = userRoleResource.getUrByManageId(oldOrgId);
                        if(ListUntils.isNotNull(userRoles)){
                            //创建管理员日志
                            this.transUserRoleLog(userRoles,outOrderQueryDto.getSerialNumber());
                            //更新管理员
                            this.transUserRole(userRoles,newOrgCode,newOrgName,sourceNodeId,targetNodeId);
                        }
                        List<AssistManage> assistManageList = assistManageResources.getAMByManageId(oldOrgId);
                        if(ListUntils.isNotNull(assistManageList)){
                            //辅助安全员管理范围日志
                            this.transAssistManageLog(assistManageList,outOrderQueryDto.getSerialNumber());
                            //更新辅助安全员管理范围
                            this.transAssistManage(assistManageList,newOrgCode,newOrgName,sourceNodeId,targetNodeId);
                        }
                        List<AssistManage> assistManageOrList = assistManageResources.getAMBYCreateManageId(oldOrgId);
                        if(ListUntils.isNotNull(assistManageOrList)){
                            //创建辅助安全员创建人管理范围日志
                            this.transAssistManageOrLog(assistManageOrList,outOrderQueryDto.getSerialNumber());
                            // 更新辅助安全员创建人管理范围
                            this.transAssistManagerOr(assistManageOrList,newOrgCode,newOrgName);
                        }
                    }
                }
            }
    }

    /**
     * 创建辅助安全员创建人管理范围日志
     * @param assistManageOrList
     * @param serialNumber
     */
    private void transAssistManageOrLog(List<AssistManage> assistManageOrList, String serialNumber) {
        OrderWorkLog orderWorkLog = null;
        List<OrderWorkLog> orderWorkLogList = new ArrayList<>();
        if(ListUntils.isNotNull(assistManageOrList)){
            for(AssistManage assistManage:assistManageOrList){
                orderWorkLog = new OrderWorkLog();
                orderWorkLog.setCreateDate(new Date());
                orderWorkLog.setId(idGenerator.next());
                orderWorkLog.setOldManageCode(assistManage.getCreateManageCode());
                orderWorkLog.setOldManageName(assistManage.getCreateManageName());
                orderWorkLog.setOldManageId(assistManage.getCreateManageId());
                orderWorkLog.setSerialNumber(serialNumber);
                orderWorkLog.setType("4");
                orderWorkLog.setUserId(assistManage.getCreateBy());
                orderWorkLogList.add(orderWorkLog);
            }
            orderWorkLogResources.save(orderWorkLogList);
        }
    }

    /**
     * 辅助安全员管理范围日志
     * @param assistManageList
     * @param serialNumber
     */
    private void transAssistManageLog(List<AssistManage> assistManageList, String serialNumber) {
        OrderWorkLog orderWorkLog = null;
        List<OrderWorkLog> orderWorkLogList = new ArrayList<>();
        if(ListUntils.isNotNull(assistManageList)){
            for(AssistManage assistManage:assistManageList){
                orderWorkLog = new OrderWorkLog();
                orderWorkLog.setCreateDate(new Date());
                orderWorkLog.setId(idGenerator.next());
                orderWorkLog.setOldManageCode(assistManage.getManageCode());
                orderWorkLog.setOldManageName(assistManage.getManageName());
                orderWorkLog.setOldManageId(assistManage.getManageId());
                orderWorkLog.setSerialNumber(serialNumber);
                orderWorkLog.setType("3");
                orderWorkLog.setUserId(assistManage.getFzuserId());
                orderWorkLogList.add(orderWorkLog);
            }
            orderWorkLogResources.save(orderWorkLogList);
        }
    }

    /**
     * 创建管理员日志
     * @param userRoles
     * @param serialNumber
     */
    private void transUserRoleLog(List<UserRole> userRoles, String serialNumber) {
        OrderWorkLog orderWorkLog = null;
        List<OrderWorkLog> orderWorkLogList = new ArrayList<>();
        if(ListUntils.isNotNull(userRoles)){
            for(UserRole userRole:userRoles){
                orderWorkLog = new OrderWorkLog();
                orderWorkLog.setCreateDate(new Date());
                orderWorkLog.setId(idGenerator.next());
                orderWorkLog.setOldManageCode(userRole.getManageCode());
                orderWorkLog.setOldManageName(userRole.getManageName());
                orderWorkLog.setOldManageId(userRole.getManageId());
                orderWorkLog.setSerialNumber(serialNumber);
                orderWorkLog.setType("1");
                orderWorkLog.setUserId(userRole.getUserId());
                orderWorkLogList.add(orderWorkLog);
            }
            orderWorkLogResources.save(orderWorkLogList);
        }
    }

    /**
     * 安全员审计员工单处理日志
     * @param userClientRels
     * @param serialNumber
     */
    private void transUserClientRelLog(List<UserClientRel> userClientRels,String serialNumber) {
        OrderWorkLog orderWorkLog = null;
        List<OrderWorkLog> orderWorkLogList = new ArrayList<>();
        if(ListUntils.isNotNull(userClientRels)){
            for(UserClientRel userClientRel:userClientRels){
                orderWorkLog = new OrderWorkLog();
                orderWorkLog.setCreateDate(new Date());
                orderWorkLog.setId(idGenerator.next());
                orderWorkLog.setOldManageCode(userClientRel.getManageCode());
                orderWorkLog.setOldManageName(userClientRel.getManageName());
                orderWorkLog.setOldManageId(userClientRel.getManageId());
                orderWorkLog.setSerialNumber(serialNumber);
                orderWorkLog.setType("2");
                orderWorkLog.setUserId(userClientRel.getUserId());
                orderWorkLogList.add(orderWorkLog);
            }
            orderWorkLogResources.save(orderWorkLogList);
        }
    }

    /**
     * 修改工单状态
     * @param outOrderQueryDto
     */
    private void orderState(OutOrderQueryDto outOrderQueryDto) {
        RestTemplate restTemplate = new RestTemplate();
        OrderLog orderLog = null;
        boolean orderFlag = false;
        orderLog = new OrderLog();
        //修改工单状态
        OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
        orderUpdateDto.setOrderId(outOrderQueryDto.getOrderId());
        orderUpdateDto.setOrderTag("3");
        try{
            orderFlag = restTemplate.postForObject(updateOrderState,orderUpdateDto,Boolean.class);
        }catch(Exception e){
            orderLog.setOrderTag("4");
        }
        orderLog.setJsonData(outOrderQueryDto.getData());
        orderLog.setStartDate(new Date());
        orderLog.setId(idGenerator.next());
        orderLog.setOrderId(outOrderQueryDto.getOrderId());
        orderLog.setEndDate(new Date());
        orderLog.setSerialNumber(outOrderQueryDto.getSerialNumber());
        orderLog.setMethod("0");
        //添加日志
        if(orderFlag){
            orderLog.setOrderTag("3");
            orderLogResouraces.save(orderLog);
        }
    }

    /**
     * 根据seriaNumber和orderType更新工单
     * @param outOrderQueryDto
     */
    private void orderSerialNumber(OutOrderQueryDto outOrderQueryDto) {
        RestTemplate restTemplate = new RestTemplate();
        OrderLog orderLog = null;
        boolean orderFlag = false;
        orderLog = new OrderLog();
        //修改工单状态
        OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
        orderUpdateDto.setSerialNumber(outOrderQueryDto.getSerialNumber());
        orderUpdateDto.setOrderTag("0");
        orderUpdateDto.setOrderType("8");
        try{
            orderFlag = restTemplate.postForObject(updateOrderState,orderUpdateDto,Boolean.class);
        }catch(Exception e){
            e.printStackTrace();
            log.error("==============根据seriaNumber和orderType更新工单异常",e);
        }
        orderLog.setJsonData(outOrderQueryDto.getData());
        orderLog.setStartDate(new Date());
        orderLog.setId(idGenerator.next());
        orderLog.setOrderId(outOrderQueryDto.getOrderId());
        orderLog.setEndDate(new Date());
        orderLog.setSerialNumber(outOrderQueryDto.getSerialNumber());
        orderLog.setMethod("1");
        //添加日志
        if(orderFlag){
            orderLog.setOrderTag("3");
            orderLogResouraces.save(orderLog);
        }
    }

    /**
     * 更新辅助安全员创建人管理范围
     * @param assistManageOrList
     * @param newOrgCode
     * @param newOrgName
     */
    private void transAssistManagerOr(List<AssistManage> assistManageOrList, String newOrgCode, String newOrgName) {
        for(AssistManage assistmanage:assistManageOrList){
            assistmanage.setCreateManageCode(newOrgCode);
            assistmanage.setCreateManageName(newOrgName);
        }
        assistManageResources.save(assistManageOrList);
    }

    /**
     * 更新辅助安全员管理范围
     * @param assistManageList
     * @param newOrgCode
     * @param newOrgName
     * @param sourceNodeId
     * @param targetNodeId
     */
    private void transAssistManage(List<AssistManage> assistManageList, String newOrgCode, String newOrgName, String sourceNodeId, String targetNodeId) {
        for(AssistManage assistManage:assistManageList){
            assistManage.setManageCode(newOrgCode);
            assistManage.setManageName(newOrgName);
        }
        assistManageResources.save(assistManageList);
    }

    /**
     * 更新管理员范围以及vpn账号
     * @param userRoles
     * @param newOrgCode
     * @param newOrgName
     * @param sourceNodeId
     * @param targetNodeId
     */
    private void transUserRole(List<UserRole> userRoles, String newOrgCode, String newOrgName,String sourceNodeId,String targetNodeId) {
        List<ChangeVPNBatchDto> changeVPNBatchDtos = new ArrayList<>();
        ChangeVPNBatchDto changeVPNBatchDto = null;
        List<String> userIds = new ArrayList<>();
        List<ChangeVPNBatchDto> vpnList = new ArrayList<>();
        for(UserRole userRole:userRoles){
            if(null != userRole){
                userRole.setManageCode(newOrgCode);
                userRole.setManageName(newOrgName);
                userIds.add(userRole.getUserId());
            }
        }
        //获取uservpn关联
        List<BusinessUserVpnRel> userVpnRels = businessuserVpnRelResources.getRelByUserIds(userIds);
        for(BusinessUserVpnRel businessUserVpnRel:userVpnRels){
            if(null != businessUserVpnRel){
                if(!("node"+targetNodeId).equals(businessUserVpnRel.getOu())){
                    changeVPNBatchDto = new ChangeVPNBatchDto();
                    changeVPNBatchDto.setOu("node"+targetNodeId);
                    changeVPNBatchDto.setVpn(businessUserVpnRel.getVpn());
                    changeVPNBatchDtos.add(changeVPNBatchDto);
                    changeVPNBatchDto.setOldOu(businessUserVpnRel.getOu());
                    businessUserVpnRel.setOu("node"+targetNodeId);
                }
            }
        }
        //更换vpn节点
        try{
            ldapApplication.changeVPNOuBatch(changeVPNBatchDtos);
        }catch (Exception e){
            e.printStackTrace();
            log.error("==========vpn账号更新失败===========", e);
        }
        businessuserVpnRelResources.save(userVpnRels);
        userRoleResource.save(userRoles);
    }

    /**
     * 更新安全员审计员管理范围
     * @param userClientRels
     * @param newOrgCode
     * @param newOrgName
     * @param sourceNodeId
     * @param targetNodeId
     */
    private void transUserClientRel(List<UserClientRel> userClientRels,String newOrgCode,String newOrgName,String sourceNodeId,String targetNodeId) {
        List<ChangeVPNBatchDto> changeVPNBatchDtos = new ArrayList<>();
        ChangeVPNBatchDto changeVPNBatchDto = null;
        List<String> userIds = new ArrayList<>();
        List<ChangeVPNBatchDto> vpnList = new ArrayList<>();
        for(UserClientRel userClientRel:userClientRels){
            if(null != userClientRel){
                //创建安全员审计员管理范围快照
                userClientRel.setManageCode(newOrgCode);
                userClientRel.setManageName(newOrgName);
                userIds.add(userClientRel.getUserId());
            }
        }
        //获取安全员审计员uservpn关联
        List<UserVpnRel> userVpnRels = userVpnRelResources.getRelByUserIds(userIds);
        for(UserVpnRel userVpnRel:userVpnRels){
            if(null != userVpnRel){
                if(!("node"+targetNodeId).equals(userVpnRel.getOuName())){
                    changeVPNBatchDto = new ChangeVPNBatchDto();
                    changeVPNBatchDto.setOu("node"+targetNodeId);
                    changeVPNBatchDto.setVpn(userVpnRel.getVpn());
                    changeVPNBatchDto.setOldOu(userVpnRel.getOuName());
                    changeVPNBatchDtos.add(changeVPNBatchDto);
                    userVpnRel.setOuName("node"+targetNodeId);
                }
            }
        }
        //更换vpn节点
        try{
            ldapApplication.changeVPNOuBatch(changeVPNBatchDtos);
        }catch (Exception e){
            e.printStackTrace();
            log.error("==========vpn账号更新失败===========",e);
        }
        userVpnRelResources.save(userVpnRels);
        userClientRelResource.save(userClientRels);
    }

}
