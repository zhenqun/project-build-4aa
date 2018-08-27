package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.business.domain.BusinessUser;
import com.ido85.party.aaaa.mgmt.business.domain.ClientExpand;
import com.ido85.party.aaaa.mgmt.business.dto.ExportAuthorizationCodeParam;
import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.InApplyUserDto;
import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.InManageDto;
import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.OutMessageDto;
import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.OutWaitingListDto;
import com.ido85.party.aaaa.mgmt.business.resources.CLientExpandResources;
import com.ido85.party.aaaa.mgmt.dto.OrganizationDto;
import com.ido85.party.aaaa.mgmt.dto.ResetVpnPassword;
import com.ido85.party.aaaa.mgmt.dto.assist.*;
import com.ido85.party.aaaa.mgmt.dto.base.OutBasePageDto;
import com.ido85.party.aaaa.mgmt.dto.userinfo.AssistClientDto;
import com.ido85.party.aaaa.mgmt.security.authentication.application.AssistUserApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.application.CommonApplication;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserClientRelResource;
import com.ido85.party.aaaa.mgmt.security.common.UserUtils;
import com.ido85.party.aaaa.mgmt.security.utils.DateUtils;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.ido85.party.aaaa.mgmt.business.application.RoleApplication;
import com.ido85.party.aaaa.mgmt.dto.assist.AssistRoleAddedQueryDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.ListUtils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/10/9.
 */
@RestController
public class AssistController {

    @Inject
    private AssistUserApplication assistUserApplication;

    @Inject
    private RoleApplication roleApplication;

   /* @Inject
    private UserClientRelResource userClientRelResource;
*/
    @Inject
    private CLientExpandResources cLientExpandResources;

    @Inject
    private CommonApplication commonApp;

    @Inject
    private RestTemplate restTemplate;

    @Inject
    private AuditAdminController auditAdminController;


    /**
     * 查询该安全员能授权给辅助安全员的应用系统
     * @return
     */
    @RequestMapping(path="/assistSecurity/getAssistUserManage", method={RequestMethod.POST})
    @ResponseBody
    public List<AssistClientDto> getAssistUserManage(){
       return  assistUserApplication.getAssistUserManage();
    }

    /**
     * 查询安全员可以开通辅助安全员的应用系统
     *
     * 先查询所有管理范围为县级的应用，再查询应用是否配置了角色包
     * @return
     */
    @RequestMapping(path = "/assistSecurity/getAvaliableClients", method = RequestMethod.POST)
    @ResponseBody
    public List<AssistClientDto> getAvaliableClients() {
        List<AssistClientDto> allClients = assistUserApplication.getAssistUserManage();
        List<AssistRoleAddedQueryDto> hasPackRoles = roleApplication.assistRoleAddedQuery();
        List<String> hasPackClients = hasPackRoles.stream()
                .map(x -> x.getClientId())
                .collect(Collectors.toList());
        return allClients.stream()
                .filter(x -> hasPackClients.indexOf(x.getClientId()) > -1)
                .collect(Collectors.toList());
    }

    /**
     * 查看该安全员是否有辅助安全员相关模块(17地市的县级)
     * @return
     */
    @RequestMapping(path = "/assistSecurity/checkIsAssistModule", method = RequestMethod.POST)
    @ResponseBody
    public boolean checkIsAssistModule(){
        try {
           return assistUserApplication.getAssistIsModule();
        }catch (Exception e){
            return  false;
        }
    }


    /**
     * 开通辅助安全员
     * @param param
     * @param request
     * @return
     * @throws Exception
     */

    @RequestMapping(path="/assistSecurity/addAssistUser", method={RequestMethod.POST})
    @ResponseBody
    public Map<String,String> addSecurityUser(@Valid @RequestBody List<InAddAssistUserDto> param, HttpServletRequest request)throws Exception {
        if (null != param) {
            param.stream()
                    .forEach(x -> {
                        if (null != x) {
                            String idCard = x.getIdCard();
                            if (StringUtils.isNotBlank(idCard)) {
                                x.setIdCard(idCard.toUpperCase());
                            }
                        }
                    });
        }
        Map<String,String> result = assistUserApplication.addAssistSecurityUser(param,request);
        return result;
    }


    /**
     * 辅助安全员查询
     * @param param
     * @return
     */

    @RequestMapping(path="/assistSecurity/getAllAssistUser", method={RequestMethod.POST})
    public OutBasePageDto<AssistUserDto> getAllAssistUser(@Valid@RequestBody AssistUserQueryDto param) throws Exception {
        if(null == param.getPageNo()||param.getPageNo().intValue()<=0){
            param.setPageNo(1);
        }
        if( null == param.getPageSize()){
            param.setPageSize(20);
        }
        List<AssistUserDto> userlist = assistUserApplication.getAssistUserByCondition(param);
        Long cnt = assistUserApplication.getAssistUserCntByCondition(param);
        OutBasePageDto<AssistUserDto> page = new OutBasePageDto<AssistUserDto>();
        page.setCount(cnt);
        page.setData(userlist);
        page.setPageNo(param.getPageNo());
        page.setPageSize(param.getPageSize());
        return page;
    }

    /**
     * 重新授权时查询该用户的应用管理范围
     */
    @RequestMapping(path="/assistSecurity/getAssistClientAndManage",method = RequestMethod.POST)
    @ResponseBody
    public List<GrantAssistClients> getAssistUserClientBusiUserClient(@Valid @RequestBody AssistUserIdDto fzuserId){

        return assistUserApplication.getAssistUserClientAndManage(fzuserId);
    }


    /**
     *
     * 辅助安全员重新授权
     * @return
     */
    @RequestMapping(path="/assistSecurity/grantAssistUser", method={RequestMethod.POST})
    @ResponseBody
    public Map<String,String> grantAssistUser(@Valid @RequestBody List<GrantAssistUserParam> param, HttpServletRequest request){
        Map<String,String> result = assistUserApplication.grantAssistUser(param,request);
        return result;
    }

    /**
     * 撤销辅助安全员
     */
    @RequestMapping(path="/assistSecurity/cancelAssistUser",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,String> cancelAssistUserClient(@Valid @RequestBody  AssistUserMultiDto fzuserIds){

        Map<String,String> map =assistUserApplication.cancelAssistUserClient(fzuserIds);
        return map;
    }

    /**
     * 修改辅助安全员状态(0 禁用 1启用)
     * @param param
     * @param request
     * @return
     */
    @RequestMapping(path="/assistSecurity/updateAssistState",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,String> updateAssistState(@Valid @RequestBody UpdateAssistStateDto param,HttpServletRequest request){

        Map<String,String> result = assistUserApplication.modifyAssistUserStatus(param,request);
        return result;
    }


    /**
     * 县级安全员重置辅助安全员VPN密码
     * @param fzuserId
     * @return
     */
    @RequestMapping(path = "/assistSecurity/resetVpnPassword", method = RequestMethod.POST)
    @ResponseBody
    public boolean resetVpnPassword(@Valid @RequestBody CheckIsAssistModuleDto fzuserId) {
        if(null==fzuserId){
            return false;
        }
        ResetVpnPassword param = new ResetVpnPassword();
        param.setUserId(fzuserId.getFzuserId());
        param.setType("3");
        return commonApp.resetVpnPassword(param);
    }

    /**
     * 申请校验
     * @param param
     * @return
     */
    @RequestMapping(path = "/assistSecurity/checkApplyUser", method = RequestMethod.POST)
    @ResponseBody
    public List<CheckApplyUserResult> checkApplyUsers(@RequestBody  ApplyUserIdsDto param){
      return assistUserApplication.checkApplyUser(param);
    }


    /**
     * 对提交申请的管理员进行审核通过
     * @param param
     * @return
     */
    @RequestMapping(path = "/assistSecurity/passApplyUser", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,String> passApplyUsers(@RequestBody  ApplyUserIdsDto param){
        return assistUserApplication.passApplyUser(param);
    }


    /**
     * 安全中心备案查询
     * @param param
     * @return
     */
    @RequestMapping(path = "/assistSecurity/auditAdminList", method = RequestMethod.POST)
    @ResponseBody
    public OutBasePageDto<OutWaitingListDto> auditAdminList(@Valid @RequestBody InApplyUserDto param){
        try {
            return auditAdminController.auditAdminQuery(param);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 备案否决
     * @param
     * @return
     */
    @RequestMapping(path="/assistSecurity/vetoApplyUser", method={RequestMethod.POST})
    @ResponseBody
    public OutMessageDto auditAdminVeto(@RequestBody InManageDto param){
        try {
            return  auditAdminController.auditAdminVeto(param);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 导出辅助安全员授权码
     * @return
     */
    @RequestMapping(path="/assistSecurity/exportAuthorizationCode", method={RequestMethod.POST})
    @ResponseBody
    public String exportAuthorizationCode(@Valid @RequestBody ExportAuthorizationCodeParam param, HttpServletRequest request){
        String result;
        try {
            result = assistUserApplication.exportAuthorizationCode(param,request);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return result;
    }
}


