package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.business.application.BusinessUserApplication;
import com.ido85.party.aaaa.mgmt.business.application.RoleApplication;
import com.ido85.party.aaaa.mgmt.business.domain.*;
import com.ido85.party.aaaa.mgmt.business.dto.ClientRolesDto;
import com.ido85.party.aaaa.mgmt.business.resources.*;
import com.ido85.party.aaaa.mgmt.distribute.generator.IdGenerator;
import com.ido85.party.aaaa.mgmt.dto.DataHandleDto;
import com.ido85.party.aaaa.mgmt.dto.UserDto;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.UserVpnRel;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserVpnRelResources;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by Administrator on 2017/9/12.
 */

@Component
@Configurable
@EnableScheduling
@Slf4j
public class DataHandleController {

    @Inject
    private BusinessUserResources businessUserResources;

    @Inject
    private BusinessuserVpnRelResources businessuserVpnRelResources;

    @Inject
    private UserRoleResource userRoleResource;

    @Inject
    private RoleApplication roleApplication;

    @Inject
    private OrgOuRelResources orgOuRelResources;
    @Inject
    private BusinessuserVpnRelResources userVpnRelResources;

    @Inject
    private IdGenerator idGenerator;


//    @Scheduled(cron = "00 18 21 * * ?")
    public void HandleDataJob() {

        List<BusinessUser> list = new ArrayList<>();

        List<DataHandleDto> userRoleList = null;

        Set<String> ouList = null;
        Set<String> node0List = null;
        OrgOuRel rel = null;
        DataHandleDto dto = null;
        String node = null;

        list = businessUserResources.getUserAndIdCard();

        if (ListUntils.isNotNull(list)) {

            for (BusinessUser user : list) {

                userRoleList = roleApplication.getRoleUserById(user.getId());
//                userRoleList = userRoleResource.getRoleUserById(user.getId());

                if (ListUntils.isNotNull(userRoleList)) {
                    ouList = new HashSet<>();
                    node0List = new HashSet<>();
                    for (DataHandleDto clientRolesDto : userRoleList) {


                        if (this.addList().contains(clientRolesDto.getClientId())){

                            rel = orgOuRelResources.getRelBy(clientRolesDto.getManageId());
                            if(null != rel){
                                node = "node" + rel.getOuId();
                                if(!ouList.contains(node)){
                                    ouList.add(node);
                                }
                            }
                        } else {
                            node = "node0";
                            node0List.add(node);
                        }

                    }
                    //判断节点
                    if((null != ouList && ouList.size() > 1) || (null == ouList && null != node0List && node0List.size() == 0)){
                        log.info("========无法确认节点"+user.getId());
                    }
                    if(null != ouList && ouList.size() == 1){
                        log.info("=========插入一条用户vpn关联,userId："+user.getId());
                        this.insertUserVpn(node, user.getId(), user.getIdCard());
                    }

                    if(null != ouList && ouList.size() == 0 && null != node0List && node0List.size()==1){
                        log.info("=========(node0)插入一条用户vpn关联,userId："+user.getId());
                        this.insertUserVpn("node0", user.getId(), user.getIdCard());
                    }
                }

            }
        }

    }

    public void insertUserVpn(String node, String userId, String idCard) {
        BusinessUserVpnRel rel = new BusinessUserVpnRel();
        rel.setUserVpnId(idGenerator.next());
        rel.setUserId(userId);
        rel.setVpn(idCard);
        rel.setOu(node);
        rel.setFlag("1");
        rel.setCreateDate(new Date());
        userVpnRelResources.save(rel);
    }

    public List<String> addList() {
        List<String> list = new ArrayList<String>();
        list.add("party-org-info-mgmt-ui");
        list.add("party-people-info-mgmt-ui");
        list.add("pparty-org-transfer");
        list.add("party-build-edu-back-ui");
        list.add("party-branch-back-ui");
        list.add("party-develop-ui");
        list.add("party-dues");
        list.add("party-build-orm");
        list.add("party-big-data-center");
        return list;
    }


}
