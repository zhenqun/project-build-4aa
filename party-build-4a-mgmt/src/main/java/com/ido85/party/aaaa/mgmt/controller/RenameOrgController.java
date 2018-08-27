package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.business.application.ApplyUserApplication;
import com.ido85.party.aaaa.mgmt.business.application.AssistManageApplication;
import com.ido85.party.aaaa.mgmt.business.application.BusinessUserApplication;
import com.ido85.party.aaaa.mgmt.business.domain.BusinessRole;
import com.ido85.party.aaaa.mgmt.dto.OrderUpdateDto;
import com.ido85.party.aaaa.mgmt.dto.RenameOrgSysDataDto;
import com.ido85.party.aaaa.mgmt.dto.expand.OrgTransferDto;
import com.ido85.party.aaaa.mgmt.dto.expand.OutOrderQueryDto;
import com.ido85.party.aaaa.mgmt.dto.expand.TransferOtherSysData;
import com.ido85.party.aaaa.mgmt.security.authentication.application.SecurityUserApplication;
import com.ido85.party.aaaa.mgmt.security.utils.JsonUtil;
import com.ido85.party.aaaa.mgmt.security.utils.RedisUtils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 党组织定时改名
 * Created by jeeseli on 2018/4/18.
 */

@Slf4j
@RestController
public class RenameOrgController {


    /**
     *  注册服务
     */
    private static  RestTemplate  restTemplate= new RestTemplate();
    /**
     * 注入管理员相关
     */
    @Inject
    private BusinessUserApplication businessUserApplication;

    /**
     * 注入安全员或审计员相关
     */
    @Inject
    private SecurityUserApplication securityUserApplication;

    /**
     * 注入辅助安全员相关---辅助
     */
    @Inject
    private AssistManageApplication assistManageApplication;

    /**
     * 注入辅助安全员系统中申请人相关信息
     */
    @Inject
    private ApplyUserApplication applyUserApplication;

    /**
     * redis锁的主键值
     */
    private static final String RENAME_ORG_ORDER_REDIS_KEY = "4areNameOrgForRedis";

    /**
     * redis锁的锁定时间
     */
    private static final String REDIS_KEY_TIME = "30";

    /**
     * 工单获取地址
     */
    @Value("${order.getOrgOrder}")
    private String getOrgOrder;

    /**
     *  整建制工单更新路径
     */
    @Value("${order.updateOrderState}")
    private String orderUpdateUrl;

    /**
     * 党组织改名  每小时执行一次  整点执行
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void cronJob() {
        try {
            if (RedisUtils.setNx(RENAME_ORG_ORDER_REDIS_KEY, REDIS_KEY_TIME)) {
                log.info("党组织改名定时开启========>+RenameOrgController==========");
                this.renameOrg();
                log.info("党组织改名定时结束========>+RenameOrgController==========");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void renameOrg() {
        //获取工单数据
        OutOrderQueryDto[] outOrderQueryDtosArr =null;
        outOrderQueryDtosArr =this.getOrder();
        if (null !=  outOrderQueryDtosArr && outOrderQueryDtosArr.length>0) {
            //开始处理改名工单
            this.resolveOrder(outOrderQueryDtosArr);
        }
    }


    /**
     * 获取工单
     */
    public OutOrderQueryDto[] getOrder() {
        Map<String,String> params = new HashMap<String,String>(2);
        params.put("orderTag","0");
        params.put("targetSys","PARTY_BUILD_4A");
        params.put("orderType","9");
        HttpEntity<Map> entity = new HttpEntity<>(params);
        OutOrderQueryDto[] outOrderQueryDtosArr=null;
        outOrderQueryDtosArr= restTemplate.exchange(getOrgOrder, HttpMethod.POST,entity,OutOrderQueryDto[].class).getBody();
        return outOrderQueryDtosArr;
    }

    /**
     * 解析工单并处理
     * @param outOrderQueryDtosArr
     */
    @Transactional(rollbackFor = Exception.class)
    private void resolveOrder(OutOrderQueryDto[] outOrderQueryDtosArr) {
        String jsonData = null;
        String newOrgName = null;
        String oldOrgName = null;
        String orgId = null;
//        List<OrgTransferDto> branchList = null;
        RenameOrgSysDataDto transferOtherSysData = null;
        for (OutOrderQueryDto outOrderQueryDto : outOrderQueryDtosArr) {
            if (null != outOrderQueryDto) {
                jsonData = outOrderQueryDto.getData();
                if (!StringUtils.isNull(jsonData)) {
                    transferOtherSysData = (RenameOrgSysDataDto) JsonUtil.jsonToBean(jsonData, RenameOrgSysDataDto.class);
                    if (null != transferOtherSysData) {
                            //使用原来整建制工单
                            //原党组织名字  id  新组织名字
                            newOrgName = transferOtherSysData.getOrgName();
                            orgId = transferOtherSysData.getOrgId();
                            //安全中心----
                            securityUserApplication.reNameOrgName(newOrgName, orgId);
                            //安全中心中包括辅助安全员模块
                            securityUserApplication.reNameAssistOrgName(newOrgName, orgId);
                            //管理员系统
                            businessUserApplication.reNameOrgName(newOrgName, orgId);
                            //辅助安全员系统----辅助安全员
                            assistManageApplication.reNameOrgName(newOrgName, orgId);
                            //辅助安全员申请相关模块
                            applyUserApplication.reNameOrgName(newOrgName, orgId);

                    }
                }
                this.updateOrderTag(outOrderQueryDto.getOrderId());

            }

        }
    }



    public void updateOrderTag(String orderId){
        OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
        orderUpdateDto.setOrderId(orderId);
        orderUpdateDto.setOrderTag("3");
        orderUpdateDto.setMessage("账号相关改名成功!");
        this.postUpdateTransOrg(orderUpdateDto);

    }

    /**
     *  更新工单状态
     * @param orderUpdateDto
     * @return
     */
    public  boolean postUpdateTransOrg(OrderUpdateDto orderUpdateDto ){
        boolean orderFlag= false;
        try{
            orderFlag = restTemplate.postForObject(orderUpdateUrl, orderUpdateDto, Boolean.class);
        }catch (Exception e){
            e.printStackTrace();
            log.error("更新改名工单失败========>");
        }
        return orderFlag;
    }




}
