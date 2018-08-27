package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.business.application.ApplyUserApplication;
import com.ido85.party.aaaa.mgmt.business.application.ApplyUserBasicApplication;
import com.ido85.party.aaaa.mgmt.business.application.AssistManageApplication;
import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.*;
import com.ido85.party.aaaa.mgmt.dto.base.OutBasePageDto;
import com.ido85.party.aaaa.mgmt.dto.userinfo.AssistClientDto;
import com.ido85.party.aaaa.mgmt.security.authentication.application.AssistUserApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.common.UserUtils;
import com.ido85.party.aaaa.mgmt.security.utils.DateUtils;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/17 0017.
 */
@RestController
public class AuditAdminController {
    @Inject
    private AssistManageApplication assistManageApp;
    @Inject
    private ApplyUserBasicApplication basicApplication;
    @Inject
    private ApplyUserApplication applyUserApp;
    @Inject
    private AssistUserApplication assistUserApp;
    /**
     * 备案查询
     * @param
     * @return
     */
    @RequestMapping(path="/auditAdmin/informationList", method={RequestMethod.POST})
    @ResponseBody
    public OutBasePageDto<OutWaitingListDto> auditAdminQuery(@Valid @RequestBody InApplyUserDto in)throws Exception{
        if(null == in.getPageNo()||in.getPageNo().intValue()<=0){
            in.setPageNo(1);
        }
        if( null == in.getPageSize()){
            in.setPageSize(20);
        }
        if(StringUtils.isNotBlank(in.getStatus())&&in.getStatus().equals("0")){//申请状态为0全部  默认查1：申请中；2：通过；3：驳回
            List<String> strList=new ArrayList<>();
            strList.add("1");
            strList.add("2");
            strList.add("3");
            in.setStatusList(strList);
        }else if(StringUtils.isNotBlank(in.getStatus())&&in.getStatus().equals("5")) {//5 已备案(2通过&3驳回)
            List<String> strList = new ArrayList<>();
            strList.add("2");
            strList.add("3");
            in.setStatusList(strList);
        }else if(StringUtils.isNotBlank(in.getStatus())){//1：未备案(申请中)
            List<String> strList=new ArrayList<>();
            strList.add(in.getStatus());
            in.setStatusList(strList);
        }
        //获取当前登陆人的   业务系统clientIds  管理范围manageIds
        List<AssistClientDto> assistClientDtoList=assistUserApp.getAssistUserManage();
        if(ListUntils.isNull(assistClientDtoList)){
            return null;
        }
        List<String> userclientIds=new ArrayList<>();
        List<String> usermanageIds=new ArrayList<>();
        for(AssistClientDto dto:assistClientDtoList){
            userclientIds.add(dto.getClientId());
            usermanageIds.add(dto.getOrgId());
        }
        //根据当前登陆人的业务系统clientIds，管理范围manageIds，通过辅助安全员管理应用表tf_f_assist_manage，匹配应用client_id，创建者范围create_manage_id
        //获取辅助安全员的辅助安全员管理范围manageIds  应用clientIds
       List<OutManageDto> fzUsers=assistManageApp.getFzUserClientidsAndManageIds(userclientIds,usermanageIds);
        if(ListUntils.isNull(fzUsers)){
            return null;
        }
        List<String>fzUserClientIds=new ArrayList<>();
        List<String>fzUserManageIds=new ArrayList<>();
       for(OutManageDto manageDto:fzUsers) {
           fzUserClientIds.add(manageDto.getClientId());
           fzUserManageIds.add(manageDto.getManageId());
       };
        if(StringUtils.isBlank(in.getClientId())&&StringUtils.isBlank(in.getManageId())){//没有选择业务系统 没有选择管理范围
            in.setClientIds(fzUserClientIds);////放入默认业务系统
            in.setManageIds(fzUserManageIds);//放入默认管理范围
        }else if(StringUtils.isBlank(in.getClientId())&&StringUtils.isNotBlank(in.getManageId())){//没有选择业务系统  选择管理范围
            in.setClientIds(fzUserClientIds);//放入默认业务系统
            if(ListUntils.isNotNull(fzUserClientIds)) {
                //放入条件  管理范围id
                fzUserManageIds = new ArrayList<>();
                fzUserManageIds.add(in.getManageId());
            }
            in.setManageIds(fzUserManageIds);
        }else if(StringUtils.isNotBlank(in.getClientId())&&StringUtils.isBlank(in.getManageId())){//选择业务系统   没有选择管理范围
            fzUserClientIds=new ArrayList<>();
            //放入条件 业务系统id
            fzUserClientIds.add(in.getClientId());
            in.setClientIds(fzUserClientIds);
            //放入默认管理范围
            in.setManageIds(fzUserManageIds);
        }else{//选择业务系统  选择管理范围
            fzUserClientIds=new ArrayList<>();
            //放入条件 业务系统id
            fzUserClientIds.add(in.getClientId());
            in.setClientIds(fzUserClientIds);
            fzUserManageIds=new ArrayList<>();
            //放入条件  管理范围id
            fzUserManageIds.add(in.getManageId());
            in.setManageIds(fzUserManageIds);
        }
        OutBasePageDto res=new OutBasePageDto();
        List<OutWaitingListDto> data = basicApplication.queryInformationList(in);//基本信息
        List<String> itemIds=new ArrayList<>();
        if(ListUntils.isNotNull(data)) {
            data.forEach((OutWaitingListDto dto) -> {
                if(null!=dto.getApplyDate()) {
                    dto.setApplyTime(DateUtils.formatISO8601Date(dto.getApplyDate()));
                }
                if (null != dto.getApproveDate()) {
                    dto.setApproveTime(DateUtils.formatISO8601Date(dto.getApproveDate()));
                }
                itemIds.add(dto.getItemId());
            });
            List<OutManageDto> outManageDtos=applyUserApp.querybusinessSystemByIds(itemIds);//根据业务管理员id获取业务信息
            if(ListUntils.isNotNull(outManageDtos)){
                for(OutWaitingListDto waitingListDto:data){
                    List<OutManageDto> roles=new ArrayList<>();
                    for(OutManageDto manageDto:outManageDtos){
                        if(waitingListDto.getItemId().equals(manageDto.getItemId())){
                            roles.add(manageDto);
                        }
                    }
                    waitingListDto.setRoles(roles);
                }
            }
        }
        res.setData(data);
        res.setCount(basicApplication.queryInformationListCount(in));
        res.setPageNo(in.getPageNo());
        res.setPageSize(in.getPageSize());
        return res;
    }

    /**
     * 备案否决
     * @param
     * @return
     */
    @RequestMapping(path="/auditAdmin/veto", method={RequestMethod.POST})
    @ResponseBody
    public OutMessageDto auditAdminVeto(@Valid @RequestBody InManageDto in)throws Exception{
        OutMessageDto res=new OutMessageDto();
        in.setRerson(in.getItems().get(0).getReason());
        List<String>itemIds=new ArrayList<>();
        List<InReasonDto> items=in.getItems();
        for(InReasonDto dto:items){
            itemIds.add(dto.getItemId());
        }
        in.setItemIds(itemIds);
        //获取当前登录用户信息
        User user = UserUtils.getCurrentUser();
           in.setUserId(user.getId());
 //       in.setUserId("2253887031349248");
        res=basicApplication.auditAdminVeto(in);
        return res;
    }
}
