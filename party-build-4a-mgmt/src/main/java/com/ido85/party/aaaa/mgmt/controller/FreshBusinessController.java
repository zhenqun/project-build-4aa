package com.ido85.party.aaaa.mgmt.controller;

import com.ido85.party.aaaa.mgmt.business.application.RuleApplication;
import com.ido85.party.aaaa.mgmt.business.domain.BusinessUser;
import com.ido85.party.aaaa.mgmt.business.resources.ApplyUserRepository;
import com.ido85.party.aaaa.mgmt.business.resources.BusinessUserResources;
import com.ido85.party.aaaa.mgmt.business.resources.RolePackageResources;
import com.ido85.party.aaaa.mgmt.business.resources.UserRoleResource;
import com.ido85.party.aaaa.mgmt.dto.InFreshPermissionDto;
import com.ido85.party.aaaa.mgmt.dto.OrgLevelTypeDto;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.ListUtils;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 刷权限细分数据
 * Created by Administrator on 2018/2/27.
 */
@RestController
@Slf4j
public class FreshBusinessController {

    @PersistenceContext(unitName = "business")
    private EntityManager businessEntity;
    @Inject
    private RuleApplication ruleApplication;

    @Inject
    private UserRoleResource userRoleResource;

    @Inject
    private ApplyUserRepository applyUserRepository;

    @Inject
    private RolePackageResources rolePackageResources;

    @Inject
    private BusinessUserResources userResources;


    @RequestMapping(path = "/fresh/freshData", method = {RequestMethod.POST})
    public String cronJob(@RequestBody @Valid InFreshPermissionDto dto) throws Exception {
        if (null == dto) {
            return "fail";
        }
        List<OrgLevelTypeDto> orgLevelTypeDtoList = null;
        //党组织client_id
//        String id = "party-people-info-mgmt-ui";
//        String id = "party-org-info-mgmt-ui";
        String id = dto.getClientId();
        String role = dto.getRoleId();
        String type = dto.getType();//0---县级以上 1---直属党工委 2----基层党组织

        String manageId = null;
        String roleId = null;
        String manageName = null;
        String userAccount = null;
        boolean flag = true;
        int count = 0;
        StringBuffer sql = new StringBuffer("SELECT DISTINCT\n" +
                "\tr.role_id,\n" +
                "\tr.manage_id,\n" +
                "\tr.manage_name,\n" +
                "\ttt.id\n" +
                "FROM\n" +
                "\tt_4a_actors tt,\n" +
                "\tr_4a_user_role r,\n" +
                "\tr_4a_client_user_rel C,\n" +
                "t_4a_role o\n" +
                "WHERE\n" +
                "\ttt. ID = r.user_id\n" +
                "AND tt. ID = C .user_id\n" +
                "and o.client_id=c.client_id\n" +
                "and o.id=r.role_id\n" +
                "AND tt. ID IS NOT NULL and  c.client_id = :clientId");
        Query q = businessEntity.createNativeQuery(sql.toString());
        q.setParameter("clientId", id);
        List<Object[]> oList = q.getResultList();
        if (ListUntils.isNotNull(oList)) {
            for (Object[] o : oList) {
                roleId = StringUtils.toString(o[0]);
                manageId = StringUtils.toString(o[1]);
                manageName = StringUtils.toString(o[2]);
                userAccount = StringUtils.toString(o[3]);
                orgLevelTypeDtoList = this.queryOrg(manageId, type);
//                if("party-org-info-mgmt-ui".equals(id)){
//                }
//                if ("party-people-info-mgmt-ui".equals(id)){
//                    orgLevelTypeDtoList = this.queryPeople(manageId, type);
//                }
                //授权规则判断
                if (!ListUtils.isEmpty(orgLevelTypeDtoList)) {
                    Map<String, String> map = ruleApplication.checkPermissionRule(StringUtils.toLong(roleId), orgLevelTypeDtoList.get(0).getType(),
                            orgLevelTypeDtoList.get(0).getLevel(), manageId, orgLevelTypeDtoList.get(0).getManageCode());
                    //如果满足条件
                    if (null != map && map.containsKey("flag") && "success".equals(map.get("flag"))) {
                        //角色是否重置
                        flag = this.isExistOrgAndRole(userAccount, StringUtils.toLong(role), manageId, id);
                        if (flag) {
                            count = userRoleResource.updateRoleByMangeIdPeople(manageId, StringUtils.toLong(roleId), StringUtils.toLong(role), userAccount);
                            log.info("日志输出========>此次刷新的个数党组织" + manageName + "个数" + count);
                        }

//                      int  count=userRoleResource.updateRoleByMangeIdOrg(manageId,StringUtils.toLong(roleId));

                    }


                }

            }
        }
        return "success";
    }


    public List<OrgLevelTypeDto> queryOrg(String manageId, String type) {
        List<OrgLevelTypeDto> assistClients = null;
        OrgLevelTypeDto clients = null;
        //查询党组织level和type
        //0---市级及以上                              1--- 2----基层党组织  3--(121,133,142,221,231,144,146,511)县级以上  4---（241,261,299）直属党工委   5---611 leve >4
        StringBuffer stringBuffer = new StringBuffer("select level,d01021 ,org_code from t_d01 where  del_flag='0'  and  id= :manageId  ");

        //0---县级及以上
        if ("0".equals(type)) {
            stringBuffer.append(" and level in (1,2,3)  ");
        }
        //1---县级角色
//        if("1".equals(type)){
//            stringBuffer.append(" and level = 3 ");
//        }
        //直属党工委
        if("1".equals(type)){
            stringBuffer.append(" and d01021 in (241,261,299,611,421) and level =4 ");
        }

        //基层党组织
        if ("2".equals(type)) {
            stringBuffer.append("  ((d01021  not in (241,261,299,611,421)) and level =4))  ");
//            stringBuffer.append(" and d01021 in (621,631,632,911,921,931,932)  or  (d01021 = 611  and level is null) ");
        }


        //-直属党工委
//        if ("1".equals(type)) {
//            stringBuffer.append("  and d01021 not in (621,631,632,921,931,932) and \"level\"=4   ");
//        }



//        //县级以上
//        if("3".equals(type)){
//             stringBuffer.append(" and  ");
//        }
//
//        //基层党组织
//        if("5".equals(type)){
//            stringBuffer.append(" and d01021 = 611  and level is null");
//        }
//        //type为空  普通权限
//
//        //611  level=4  直属党工委
//        if("6".equals(type)){
//            stringBuffer.append("and d01021 = 611 and  level=4 ");
//        }
//        if("3".equals(type)){
//            stringBuffer.append(" and d01021 =611 and level is null ");
//        }




        Query q = businessEntity.createNativeQuery(stringBuffer.toString());
        q.setParameter("manageId", manageId);
        List<Object[]> orgList = q.getResultList();
        if (ListUntils.isNotNull(orgList)) {
            assistClients = new ArrayList<>();
            for (Object[] o : orgList) {
                clients = new OrgLevelTypeDto();
                clients.setLevel(StringUtils.toString(o[0]));
                clients.setType(StringUtils.toString(o[1]));
                clients.setManageCode(StringUtils.toString(o[2]));
                assistClients.add(clients);
            }
        }
        return assistClients;

    }


    /**
     * 党员
     * @param manageId
     * @param type
     * @return
     */
    public List<OrgLevelTypeDto> queryPeople(String manageId, String type) {
        List<OrgLevelTypeDto> assistClients = null;
        OrgLevelTypeDto clients = null;
        //查询党组织level和type
        //0---市级及以上                              1--- 2----基层党组织  3--(121,133,142,221,231,144,146,511)县级以上  4---（241,261,299）直属党工委   5---611 leve >4
        StringBuffer stringBuffer = new StringBuffer("select level,d01021 ,org_code from t_d01 where  del_flag='0'  and  id= :manageId  ");

        //0---县级及以上
        if ("0".equals(type)) {
            stringBuffer.append(" and level in (1,2,3)  ");
        }
        //1---县级角色
//        if("1".equals(type)){
//            stringBuffer.append(" and level = 3 ");
//        }
        //县级直属党工委
        if("1".equals(type)){
            stringBuffer.append(" and d01021 in (241,261,299,611,421) and level =4 ");
        }

        //基层党组织
        if ("2".equals(type)) {
            stringBuffer.append(" and (level not in (1,2,3,4) or  (d01021  not in (241,261,299,611,421) and level =4))   ");
//            stringBuffer.append(" and d01021 in (621,631,632,911,921,931,932)  or  (d01021 = 611  and level is null) ");
        }


        //-直属党工委
//        if ("1".equals(type)) {
//            stringBuffer.append("  and d01021 not in (621,631,632,921,931,932) and \"level\"=4   ");
//        }



//        //县级以上
//        if("3".equals(type)){
//             stringBuffer.append(" and  ");
//        }
//
//        //基层党组织
//        if("5".equals(type)){
//            stringBuffer.append(" and d01021 = 611  and level is null");
//        }
//        //type为空  普通权限
//
//        //611  level=4  直属党工委
//        if("6".equals(type)){
//            stringBuffer.append("and d01021 = 611 and  level=4 ");
//        }
//        if("3".equals(type)){
//            stringBuffer.append(" and d01021 =611 and level is null ");
//        }




        Query q = businessEntity.createNativeQuery(stringBuffer.toString());
        q.setParameter("manageId", manageId);
        List<Object[]> orgList = q.getResultList();
        if (ListUntils.isNotNull(orgList)) {
            assistClients = new ArrayList<>();
            for (Object[] o : orgList) {
                clients = new OrgLevelTypeDto();
                clients.setLevel(StringUtils.toString(o[0]));
                clients.setType(StringUtils.toString(o[1]));
                clients.setManageCode(StringUtils.toString(o[2]));
                assistClients.add(clients);
            }
        }
        return assistClients;

    }



    @RequestMapping(path = "/fresh/delRole", method = {RequestMethod.POST})
    public String delDul(@RequestBody @Valid InFreshPermissionDto dto) throws Exception {
        if (null == dto) {
            return "fail";
        }
        String clientId = dto.getClientId();

        if ("party-org-info-mgmt-ui".equals(clientId)) {
            userRoleResource.delOrgRole();
        }
        if ("party-people-info-mgmt-ui".equals(clientId)) {
            userRoleResource.delPeopleRole();

        }

        return "success";
    }

    /**
     * 辅助安全员刷数据角色包
     *
     * @param dto
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/fresh/freshAssist", method = {RequestMethod.POST})
    public String freshAssist(@RequestBody @Valid InFreshPermissionDto dto) throws Exception {
        if (null == dto) {
            return "fail";
        }
        List<OrgLevelTypeDto> orgLevelTypeDtoList = null;
        //党组织client_id
        String id = dto.getClientId();
        String role = dto.getRoleId();
        String type = dto.getType();//0---县级以上 1---直属党工委 2----基层党组织

        String manageId = null;
        String roleId = null;
        //clientId
        StringBuffer sql = new StringBuffer("select role_id,manage_id from r_4a_role_package where client_id= :clientId and del_flag= '0' ");
        Query q = businessEntity.createNativeQuery(sql.toString());
        q.setParameter("clientId", id);
        List<Object[]> oList = q.getResultList();
        if (ListUntils.isNotNull(oList)) {
            for (Object[] o : oList) {
                roleId = StringUtils.toString(o[0]);
                manageId = StringUtils.toString(o[1]);
                orgLevelTypeDtoList = this.queryOrg(manageId, type);
                //授权规则判断
                if (!ListUtils.isEmpty(orgLevelTypeDtoList)) {
                    Map<String, String> map = ruleApplication.checkPermissionRule(StringUtils.toLong(roleId), orgLevelTypeDtoList.get(0).getType(),
                            orgLevelTypeDtoList.get(0).getLevel(), manageId, orgLevelTypeDtoList.get(0).getManageCode());
                    //如果满足条件
                    if (null != map && map.containsKey("flag") && "success".equals(map.get("flag"))) {
                        //查询省市县三级别
                        int count = rolePackageResources.updateRolePackage(manageId, StringUtils.toLong(roleId), StringUtils.toLong(role));
//                      int  count=userRoleResource.updateRoleByMangeIdOrg(manageId,StringUtils.toLong(roleId));
                        log.info("辅助安全员========>刷新角色包信息" + manageId + "个数" + count);

                    }
                }

            }
        }
        return "success";
    }


    @RequestMapping(path = "/fresh/freshAssistApply", method = {RequestMethod.POST})
    public String freshAssistApplu(@RequestBody @Valid InFreshPermissionDto dto) throws Exception {
        if (null == dto) {
            return "fail";
        }
        List<OrgLevelTypeDto> orgLevelTypeDtoList = null;
        //党组织client_id
        String id = dto.getClientId();
        String role = dto.getRoleId();
        String type = dto.getType();//0---县级以上 1---直属党工委 2----基层党组织

        String manageId = null;
        String roleId = null;
        //clientId
        StringBuffer sql = new StringBuffer("select r.role_id, r.apply_manage_id,r.apply_manage_name  from tf_f_apply_user r where r.client_id= :clientId and r.del_flag='0' ");
        Query q = businessEntity.createNativeQuery(sql.toString());
        q.setParameter("clientId", id);
        List<Object[]> oList = q.getResultList();
        if (ListUntils.isNotNull(oList)) {
            for (Object[] o : oList) {
                roleId = StringUtils.toString(o[0]);
                manageId = StringUtils.toString(o[1]);
                orgLevelTypeDtoList = this.queryOrg(manageId, type);
                //授权规则判断
                if (!ListUtils.isEmpty(orgLevelTypeDtoList)) {
                    Map<String, String> map = ruleApplication.checkPermissionRule(StringUtils.toLong(roleId), orgLevelTypeDtoList.get(0).getType(),
                            orgLevelTypeDtoList.get(0).getLevel(), manageId, orgLevelTypeDtoList.get(0).getManageCode());
                    //如果满足条件
                    if (null != map && map.containsKey("flag") && "success".equals(map.get("flag"))) {
                        //查询省市县三级别
                        int count = applyUserRepository.updateRoleApplyUser(manageId, StringUtils.toLong(roleId), StringUtils.toLong(role));
                        log.info("辅助安全员========>刷新申请人员角色信息党组织" + manageId + "个数" + count);
                    }

                }

            }
        }
        return "success";
    }


    /**
     * 此处的roleId为传入进来的6个角色id
     *
     * @param userAccount
     * @param roleId
     * @param manageId
     * @param clientId
     * @return
     */

    public boolean isExistOrgAndRole(String userAccount, Long roleId, String manageId, String clientId) {
        int count = 0;
        StringBuffer sql = new StringBuffer("SELECT DISTINCT\n" +
                "\tr.role_id,\n" +
                "\tr.manage_id,\n" +
                "\tr.manage_name,\n" +
                "\ttt.id_card\n" +
                "FROM\n" +
                "\tt_4a_actors tt,\n" +
                "\tr_4a_user_role r,\n" +
                "\tr_4a_client_user_rel C,\n" +
                "t_4a_role o\n" +
                "WHERE\n" +
                "\ttt. ID = r.user_id\n" +
                "AND tt. ID = C .user_id\n" +
                "and o.client_id=c.client_id\n" +
                "and o.id=r.role_id\n" +
                "AND tt. ID IS NOT NULL and  c.client_id = :clientId and tt.id = :userAccount and r.role_id = :roleId and r.manage_id = :manageId ");
        Query q = businessEntity.createNativeQuery(sql.toString());
        q.setParameter("clientId", clientId);
        q.setParameter("userAccount", userAccount);
        q.setParameter("roleId", roleId);
        q.setParameter("manageId", manageId);
        List<Object[]> oList = q.getResultList();
        if (ListUtils.isEmpty(oList)) {
            return true;
        } else {
            return false;
        }


    }


    /**
     * 最后删除多余的角色信息
     *
     * @param dto
     */
    @RequestMapping(path = "/fresh/delFinalRole", method = {RequestMethod.POST})
    public String delFinalData(@RequestBody @Valid InFreshPermissionDto dto) {
        String userRoleId = null;
        BusinessUser user = null;
        String roleId = null;
        if (null == dto.getClientId()) {
            return "fail";
        }
        String clientId = dto.getClientId();
        /**
         * 党组织和党员的管理员
         */
//        StringBuffer sql = new StringBuffer("select  t.id from t_4a_actors t ," +
//                " r_4a_client_user_rel c where c.user_id =t.id  and c.client_id= :clientId ");
//        Query q = businessEntity.createNativeQuery(sql.toString());
//        q.setParameter("clientId", clientId);
//        List<String> userList = q.getResultList();
        StringBuffer sql = new StringBuffer("SELECT DISTINCT\n" +
                "\tr.role_id,\n" +
                "\tr.manage_id,\n" +
                "\tr.id\n" +
                "FROM\n" +
                "\tt_4a_actors tt,\n" +
                "\tr_4a_user_role r,\n" +
                "\tr_4a_client_user_rel C,\n" +
                "t_4a_role o\n" +
                "WHERE\n" +
                "\ttt. ID = r.user_id\n" +
                "AND tt. ID = C .user_id\n" +
                "and o.client_id=c.client_id\n" +
                "and o.id=r.role_id\n" +
                "AND tt. ID IS NOT NULL and  c.client_id = :clientId");
        Query q = businessEntity.createNativeQuery(sql.toString());
        q.setParameter("clientId", clientId);
        List<Object[]> oList = q.getResultList();
        if (!ListUtils.isEmpty(oList)) {
            for (Object[] o : oList) {
                roleId = StringUtils.toString(o[0]);
                userRoleId = StringUtils.toString(o[2]);
                if ("party-org-info-mgmt-ui".equals(dto.getClientId())) {
                    if (!"-4943112096028815360".equals(roleId) && !"-4942815560286908416".equals(roleId) && !"-4942560878455402496".equals(roleId)) {
                        userRoleResource.delete(StringUtils.toLong(userRoleId));
                    }

                }
                if ("party-people-info-mgmt-ui".equals(dto.getClientId())) {
                    if (!"-4940250523211591680".equals(roleId) && !"-4939403742941655040".equals(roleId) && !"-4939082210620207104".equals(roleId)) {
                        userRoleResource.delete(StringUtils.toLong(userRoleId));
                    }


                }

//        if (!ListUtils.isEmpty(userList)) {
//            for(String  businessUserId :userList){
//                user = userResources.getUserByOrgAndPeople(businessUserId);
//                Set<BusinessRole> roles = null;
//                if (null != user) {
//                    roles = user.getRoles();
//                    for (BusinessRole role : roles) {
//                        //党组织或党员client_id
//                        roleId = StringUtils.toString(role.getId());
//                        if ("party-org-info-mgmt-ui".equals(role.getClientId())) {
//                            if (!"-4943112096028815360".equals(roleId) && !"-4942815560286908416".equals(roleId) && !"-4942560878455402496".equals(roleId)) {
//                                userRoleResource.delete(role.getId());
//                            }
//
//                        }
//                        if ("party-people-info-mgmt-ui".equals(role.getClientId())) {
//                            if (!"-4940250523211591680".equals(roleId) && !"-4939403742941655040".equals(roleId) && !"-4939082210620207104".equals(roleId)) {
//                                userRoleResource.delete(role.getId());
//                            }
//
//                        }
//
//                    }
//                }
            }
        }

        return "success";
    }


}


