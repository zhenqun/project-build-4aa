package com.ido85.party.aaaa.mgmt.service;

/**
 * Created by Administrator on 2017/12/6.
 */
public interface SimpleDataService {

    /**
     * 该组织是否处于整建制
     * @param orgId
     * @return
     */
    public boolean checkOrgInWhole(String orgId);
}
