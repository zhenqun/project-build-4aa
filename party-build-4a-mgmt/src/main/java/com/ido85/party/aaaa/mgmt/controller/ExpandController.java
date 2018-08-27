package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.dto.expand.AccountSyncParam;
import com.ido85.party.aaaa.mgmt.dto.expand.CheckAccount;
import com.ido85.party.aaaa.mgmt.dto.expand.CheckOrgAccount;
import com.ido85.party.aaaa.mgmt.security.authentication.application.CommonApplication;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/7.
 */
@RestController
public class ExpandController {

    @Inject
    private CommonApplication commonApplication;

    /**
     * 查询该组织下是否存在账号  党组织使用
     * @return
     */
    @RequestMapping(path="/expand/checkOrgAccount", method={RequestMethod.POST})
    @ResponseBody
    public boolean checkOrgAccount(@Valid @RequestBody  CheckOrgAccount param){
        return commonApplication.checkOrgAccount(param);
    }

    /**
     * 信息管理系统账号同步  信息管理使用
     * @return
     */
    @RequestMapping(path="/expand/accountSync", method={RequestMethod.POST})
    @ResponseBody
    public Map<String,String> accountSync(@Valid @RequestBody AccountSyncParam param){
        return commonApplication.accountSync(param);
    }

    /**
     * 查询这个人是不是三员
     */
    @RequestMapping(path="/expand/checkHashAdmin", method={RequestMethod.POST})
    @ResponseBody
    public List<String> checkHashAdmin(@RequestBody List<String> hashs){
        return commonApplication.checkHashAdmin(hashs);
    }

    /**
     * 检测该账号是否可以进行删除（党员信息整改）
     */
    @RequestMapping(path="/expand/checkAccount",method = {RequestMethod.POST})
    @ResponseBody
    public Map<String,String> checkAccount(@RequestBody CheckAccount checkAccount){
        return commonApplication.checkAccount(checkAccount);
    }

    /**
     * 删除账号（党员信息整改）
     */
    @RequestMapping(path="/expand/delAccount",method = {RequestMethod.POST})
    @ResponseBody
    public boolean delAccount(@RequestBody CheckAccount checkAccount){
        return commonApplication.delAccount(checkAccount);
    }
    /**
     * 查询该组织下是否存在账号 修改组织类别使用
     * @return
     */
    @RequestMapping(path="/expand/checkOrgTypeAccount", method={RequestMethod.POST})
    @ResponseBody
    public boolean checkOrgTypeAccount(@Valid @RequestBody  CheckOrgAccount param){
        return commonApplication.checkOrgTypeAccount(param);
    }


}
