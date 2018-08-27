package com.ido85.party.aaaa.mgmt.business.application;


import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.InApplyUserDto;
import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.InManageDto;
import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.OutMessageDto;
import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.OutWaitingListDto;

import java.util.List;

/**
 * Created by Administrator on 2017/10/9 0009.
 */
public interface ApplyUserBasicApplication {


    /**
     * 业务管理员信息列表展示
     */
    List<OutWaitingListDto> queryInformationList(InApplyUserDto in)throws Exception;
    /**
     * 业务管理员信息列表展示总数
     */
    Long queryInformationListCount(InApplyUserDto in) throws Exception;

    /**
     * 备案否决
     * @param
     * @return
     */
    OutMessageDto auditAdminVeto(InManageDto in)throws Exception;
}
