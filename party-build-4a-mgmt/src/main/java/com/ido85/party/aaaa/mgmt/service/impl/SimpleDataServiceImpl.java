package com.ido85.party.aaaa.mgmt.service.impl;

import com.ido85.party.aaaa.mgmt.service.SimpleDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Administrator
 * @date 2017/12/6
 */
@Named
public class SimpleDataServiceImpl implements SimpleDataService{
    /**
     * 实例化注册服务
     */
    private static  RestTemplate restTemplate = new RestTemplate();
//    static {
//        if(restTemplate!=null){
//            restTemplate  = new RestTemplate();
//        }
//    }

    @Value("${checkOrgInWhole}")
    private String CheckOrgInWhole;

    /**
     * 该组织是否处于整建制
     * true 代表处于整建制  false不是
     * @param orgId
     * @return
     */
    @Override
    public boolean checkOrgInWhole(String orgId) {
        boolean flag = restTemplate.getForObject(CheckOrgInWhole+orgId,boolean.class);
        return flag;
    }
}
