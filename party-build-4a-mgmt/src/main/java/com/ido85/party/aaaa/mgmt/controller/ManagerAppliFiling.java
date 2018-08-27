package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.dto.ManagerAppliFiling.ManagerDto;
import com.ido85.party.aaaa.mgmt.dto.ManagerAppliFiling.UserRoles;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Date;
import java.util.*;

/**
 * Created by Administrator on 2017/10/12.
 */
@Controller
public class ManagerAppliFiling {
    @RequestMapping(value = "/manage/appliQuery", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> appliQuery(Map<String,Object> param){
        Map<String,Object> list = new HashMap<>();
        List<ManagerDto> resp= new ArrayList<>();
        List<UserRoles> userRoles = new ArrayList<>();
        ManagerDto mager = new ManagerDto();
        for(int i=0; i<5; i++){
            mager.setCreateDate(2017101+i);
            mager.setIdCard("37291519950602022"+i);
            mager.setItemId("1"+i);
            mager.setRelName("张三丰"+i);
            mager.setRemark(i+"级干部");
            mager.setTelephone("1532201020"+i);
            mager.setState("1");
            resp.add(mager);
        }

        UserRoles userRole = new UserRoles();
        userRole.setClientId("1");
        userRole.setClientName("党员教育系统1");
        userRole.setManageId("guanl1");
        userRole.setRoleId("juese1");
        userRole.setRoleName("通用角色1");
        userRoles.add(userRole);
        mager.setRoles(userRoles);
        UserRoles userRole1 = new UserRoles();
        userRole1.setClientId("2");
        userRole1.setClientName("党员教育系统2");
        userRole1.setManageId("guanl2");
        userRole1.setRoleId("juese1");
        userRole1.setRoleName("通用角色1");
        userRoles.add(userRole1);
        mager.setRoles(userRoles);
        UserRoles userRole2 = new UserRoles();
        userRole2.setClientId("1");
        userRole2.setClientName("党员教育系统1");
        userRole2.setManageId("guanl2");
        userRole2.setRoleId("juese2");
        userRole2.setRoleName("通用角色2");
        userRoles.add(userRole2);
        mager.setRoles(userRoles);
        list.put("data",resp);
        list.put("count",resp.size());
        list.put("pageNo",1);
        list.put("pageSize",20);
        return list;
    }
}
