package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.business.resources.UserRoleResource;
import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.dto.base.PermissionRoleEnum;
import com.ido85.party.aaaa.mgmt.dto.expand.OrderUpdateDto;
import com.ido85.party.aaaa.mgmt.dto.expand.OrgTransferDto;
import com.ido85.party.aaaa.mgmt.dto.expand.OutOrderQueryDto;
import com.ido85.party.aaaa.mgmt.dto.expand.TransferOtherSysData;
import com.ido85.party.aaaa.mgmt.security.authentication.application.SecurityUserApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.DelSecurityUserLog;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.UserClientRel;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.AssistCountyResources;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.OrderLogResouraces;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.OrderWorkLogResources;
import com.ido85.party.aaaa.mgmt.security.utils.JsonUtil;
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
import org.thymeleaf.util.ListUtils;

import javax.inject.Inject;
import java.util.*;

/**
 * 权限细分整建制
 * 定时跑工单----
 * 1、因整建制(县转镇) 需要删除安全员所有账号信息
 * 2、处理党组织和党员系统的角色id(县转镇、镇转县、党委转基层、基层转党委)
 * Created by jeeseli on 2018/3/16.
 */
@Component
@Configurable
@EnableScheduling
@Slf4j
public class DelSecurityAccountController {

    /**
     * 主键生成器
     */
    @Inject
    private IdGenerator idGenerator;

    /**
     * 工单获取地址
     */
    @Value("${order.getOrgOrder}")
    private String orderQueryUrl;

    /**
     * 工单状态更改地址
     */
    @Value("${order.updateOrderState}")
    private String orderUpdateUrl;

    @Inject
    private SecurityUserApplication securityUserApp;

    /**
     * 整建制中权限细分标志
     */
    private static final String PARTY_PERMISSION = "PARTY_PERMISSION";

    /**
     * 整建制中order_tag标签
     */
    private static final String ORDER_TAG = "8";


    /**
     * redis锁的主键值
     */
    private static final String UPDATE_ROLE_ORDER_REDIS_KEY = "delSecurity_updateRole";

    /**
     * redis锁的锁定时间
     */
    private static final String REDIS_KEY_TIME = "30";

    @Inject
    private OrderWorkLogResources orderWorkLogResources;

    /**
     * 辅助安全员模块
     */
    @Inject
    private AssistCountyResources assistCountyResources;

    /**
     * 注册服务
     */
    private static RestTemplate restTemplate = new RestTemplate();

    /**
     * 管理员角色
     */
    @Inject
    private UserRoleResource userRoleResource;

    @Inject
    private OrderLogResouraces orderLogResouraces;


    /**
     * 每天1-5点之间执行
     */
    @Scheduled(cron = "0 0/1 1-5 * * ? ")
//    @Scheduled(cron = "0 0/10 * * * ?")
    public void orderScan() {
        try {
            if (RedisUtils.setNx(UPDATE_ROLE_ORDER_REDIS_KEY, REDIS_KEY_TIME)) {
                log.info("==权限细分整建制开始扫描===========");
                updateRoleAndDelSecurity();
                log.info("==权限细分整建制扫描完成===========");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("=====权限细分整建制扫描发生异常==============", e);
        }
    }

    /**
     *
     */
    @Transactional(rollbackFor = Exception.class)
    private void updateRoleAndDelSecurity() {
        List<UserClientRel> userClientRels = null;
        TransferOtherSysData transferOtherSysData = null;
        String jsonData = null;
        String newOrgCode = null;
        String oldOrgCode = null;
        String newOrgId = null;
        String oldOrgId = null;
        List<OrgTransferDto> branchList = null;
        Map<String, String> params = new HashMap<String, String>(3);
        params.put("orderType", ORDER_TAG);
        params.put("orderTag", "0");
        params.put("targetSys", PARTY_PERMISSION);
        boolean orderFlag = false;
        boolean flag = false;
        boolean flagCreate = false;
        DelSecurityUserLog transOrderLog = null;
        HttpEntity<Map> entity = new HttpEntity<>(params);
        OutOrderQueryDto[] outOrderQueryDtosArr = null;
        int  assistCount =0;
        /**
         *  调用方法查询整建制工单信息
         */
        outOrderQueryDtosArr = this.postTransOrg(entity);
        List<String> ids = new ArrayList<>();
        if (null != outOrderQueryDtosArr && outOrderQueryDtosArr.length > 0) {
            for (OutOrderQueryDto outOrderQueryDto : outOrderQueryDtosArr) {
                //解析工单信息
                jsonData = outOrderQueryDto.getData();
                if (!StringUtils.isNull(jsonData)) {
                    //解析json串
                    transferOtherSysData = (TransferOtherSysData) JsonUtil.jsonToBean(jsonData, TransferOtherSysData.class);
                    if (null != transferOtherSysData) {
                        branchList = transferOtherSysData.getBranchList();
                        for (OrgTransferDto orgTransferDto : branchList) {
                            newOrgCode = orgTransferDto.getNewOrgCode();
                            oldOrgCode = orgTransferDto.getOldOrgCode();
                            newOrgId = orgTransferDto.getNewOrgId();
                            oldOrgId = orgTransferDto.getOldOrgId();
                            //县(24)转镇(30)
                            if (30 == newOrgCode.length() && 24 == oldOrgCode.length()) {
                                //删除安全员账号所有信息 -----逻辑删除
                                userClientRels = securityUserApp.getRelByManageId(oldOrgId);
                                if (!ListUtils.isEmpty(userClientRels)) {
                                    ids.add(userClientRels.get(0).getUserId());
                                    //逻辑删除安全员所有信息（包含ldap账号）+记录了删除安全员账号信息
                                    securityUserApp.delSecUserClient(ids);
                                }
                                assistCount= assistCountyResources.getManageId(oldOrgId);
                                if(assistCount>0) {
                                    //删除辅助安全员模块内容
                                    assistCountyResources.deleteByOrgId(oldOrgId);
                                }
                                //更新党组织角色
                                userRoleResource.updateOrgRoleByOrgIdXToZ(StringUtils.toLong(PermissionRoleEnum.ORG_ROLE_XIAN), StringUtils.toLong(PermissionRoleEnum.ORG_ROLE_ZHEN), newOrgId);
                                //更新党员信息管理角色
                                userRoleResource.updatePeopleRoleByOrgIdXToZ(StringUtils.toLong(PermissionRoleEnum.PEOPLE_ROLE_XIAN), StringUtils.toLong(PermissionRoleEnum.PEOPLE_ROLE_ZHEN), newOrgId);
                            }
                            //镇(30)转县(24)
                            if (24 == newOrgCode.length() && 30 == oldOrgCode.length()) {
                                userRoleResource.updateOrgRoleByOrgIdZToX(StringUtils.toLong(PermissionRoleEnum.ORG_ROLE_XIAN), StringUtils.toLong(PermissionRoleEnum.ORG_ROLE_ZHEN), newOrgId);
                                userRoleResource.updatePeopleRoleByOrgIdZToX(StringUtils.toLong(PermissionRoleEnum.PEOPLE_ROLE_XIAN), StringUtils.toLong(PermissionRoleEnum.PEOPLE_ROLE_ZHEN), newOrgId);
                            }
                            //镇转基层
                            if (30 == oldOrgCode.length() && newOrgCode.length() > 30) {
                               int count = userRoleResource.updateOrgRoleByOrgIdZToJ(StringUtils.toLong(PermissionRoleEnum.ORG_ROLE_ZHEN), StringUtils.toLong(PermissionRoleEnum.ORG_ROLE_JICENG), newOrgId);
                               int coun1= userRoleResource.updatePeopleRoleByOrgIdZToJ(StringUtils.toLong(PermissionRoleEnum.PEOPLE_ROLE_ZHEN), StringUtils.toLong(PermissionRoleEnum.PEOPLE_ROLE_JICENG), newOrgId);
                            }
                            //基层转镇
                            if (30 == newOrgCode.length() && oldOrgCode.length() > 30) {
                                userRoleResource.updateOrgRoleByOrgIdJToZ(StringUtils.toLong(PermissionRoleEnum.ORG_ROLE_ZHEN), StringUtils.toLong(PermissionRoleEnum.ORG_ROLE_JICENG), newOrgId);
                                userRoleResource.updatePeopleRoleByOrgIdJToZ(StringUtils.toLong(PermissionRoleEnum.PEOPLE_ROLE_ZHEN), StringUtils.toLong(PermissionRoleEnum.PEOPLE_ROLE_JICENG), newOrgId);
                            }
                        }
                    }
                    OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
                    orderUpdateDto.setOrderId(outOrderQueryDto.getOrderId());
//                    orderUpdateDto.setSerialNumber(outOrderQueryDto.getSerialNumber());
                    orderUpdateDto.setOrderTag("3");
                    orderFlag = this.postUpdateTransOrg(orderUpdateDto);
                }

            }

        }
    }

    /**
     * 更新整建制工单状态
     *
     * @param orderUpdateDto
     * @return
     */
    public boolean postUpdateTransOrg(OrderUpdateDto orderUpdateDto) {
        boolean orderFlag = false;
        try {
            orderFlag = restTemplate.postForObject(orderUpdateUrl, orderUpdateDto, Boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("更新整建制工单失败========>");
        }
        return orderFlag;
    }

    /**
     * 获取工单
     *
     * @return
     */
    public OutOrderQueryDto[] postTransOrg(HttpEntity entity) {
//        RestTemplate restTemplate = new RestTemplate();
        OutOrderQueryDto[] outOrderQueryDtos = null;
        try {
            outOrderQueryDtos = restTemplate.exchange(orderQueryUrl, HttpMethod.POST, entity, OutOrderQueryDto[].class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询整建制工单失败========>");
        }
        return outOrderQueryDtos;
    }
//    @RequestMapping(path = "/expand/delUserClient", method = RequestMethod.POST)
//    public boolean cancelSecUserClient(@Valid @RequestBody CancelSecUserClientParam param) {
//        return securityUserApp.delSecUserClient(param);
//    }


}
