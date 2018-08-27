package com.ido85.party.aaaa.mgmt.business.application.impl;


import com.ido85.party.aaaa.mgmt.business.application.ApplyUserBasicApplication;
import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.InApplyUserDto;
import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.InManageDto;
import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.OutMessageDto;
import com.ido85.party.aaaa.mgmt.business.dto.auditAdmin.OutWaitingListDto;
import com.ido85.party.aaaa.mgmt.business.resources.ApplyUserBasicResources;
import com.ido85.party.aaaa.mgmt.security.authentication.domain.User;
import com.ido85.party.aaaa.mgmt.security.authentication.repository.UserResources;
import com.ido85.party.aaaa.mgmt.security.common.BaseApplication;
import com.ido85.party.aaaa.mgmt.security.utils.DateUtils;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import com.ido85.party.aaaa.mgmt.security.utils.StringUtils;
import org.springframework.data.jpa.repository.Modifying;
import org.thymeleaf.util.ListUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/10/9 0009.
 */
@Named
public class ApplyUserBasicApplicationImpl extends BaseApplication implements ApplyUserBasicApplication {
    @Inject
    private ApplyUserBasicResources applyUserBasicResources;
    @Inject
    private UserResources userResources;

    @Override
    public List<OutWaitingListDto> queryInformationList(InApplyUserDto in) throws Exception {
        StringBuffer sql=new StringBuffer("select ");
        sql.append(" DISTINCT t.apply_user_id,t.rel_name,t.id_card,t.remark,t.telephone,t.status,t.apply_date,t.approve_date,t.approve_by");
        sql.append(" from tf_f_apply_user_basic t, tf_f_apply_user au");
        sql.append(" where t.apply_user_id=au.apply_user_id");
        sql.append(" and t.del_flag='0' and au.del_flag='0'");
        if(ListUntils.isNotNull(in.getStatusList())){
            sql.append(" and t.status in:statusList");
        }
        if(StringUtils.isNotBlank(in.getIdCard())){
            sql.append(" and t.id_card like :idCard");
        }
        if(StringUtils.isNotBlank(in.getRelName())){
            sql.append(" and t.rel_name like :relName");
        }
        if(StringUtils.isNotBlank(in.getTelephone())){
            sql.append(" and t.telephone like :telephone");
        }
        if(ListUntils.isNotNull(in.getClientIds())){
            sql.append(" and au.client_id in:clientIds");
        }
        if(ListUntils.isNotNull(in.getManageIds())&&StringUtils.isBlank(in.getManageId())){
            sql.append(" and au.create_manage_id in :applyManageIds");
        }
        if(ListUntils.isNotNull(in.getManageIds())&&StringUtils.isNotBlank(in.getManageId())){
            sql.append(" and au.apply_manage_id in :applyManageIds");
        }
        sql.append("  order by t.apply_date desc ");
        Query query=businessEntity.createNativeQuery(sql.toString());
        if(ListUntils.isNotNull(in.getStatusList())){
            query.setParameter("statusList",in.getStatusList());
        }
        if(StringUtils.isNotBlank(in.getIdCard())){
            query.setParameter("idCard",in.getIdCard()+"%");
        }
        if(StringUtils.isNotBlank(in.getRelName())){
            query.setParameter("relName",in.getRelName()+"%");
        }
        if(StringUtils.isNotBlank(in.getTelephone())){
            query.setParameter("telephone",in.getTelephone()+"%");
        }
        if(ListUntils.isNotNull(in.getClientIds())){
            query.setParameter("clientIds", in.getClientIds());
        }
        if(ListUntils.isNotNull(in.getManageIds())){
            query.setParameter("applyManageIds",in.getManageIds());
        }
        query.setFirstResult((in.getPageNo() - 1) * in.getPageSize());
        query.setMaxResults(in.getPageSize());
        List<Object[]>objArray=query.getResultList();
        if(ListUntils.isNull(objArray)){
            return null;
        }
        List<OutWaitingListDto> res=new ArrayList<>();
        OutWaitingListDto dto=null;
        for(Object[] obj:objArray){
            dto=new OutWaitingListDto();
            dto.setItemId(StringUtils.toString(obj[0]));
            dto.setRelName(StringUtils.toString(obj[1]));
            dto.setIdCard(StringUtils.toString(obj[2]));
            dto.setRemark(StringUtils.toString(obj[3]));
            dto.setTelephone(StringUtils.toString(obj[4]));
            dto.setStatus(StringUtils.toString(obj[5]));
            if(StringUtils.toString(obj[6]).length()>19) {
                String str = StringUtils.toString(obj[6]).substring(0, 19);
                dto.setApplyDate(DateUtils.parseDate(str));
            }else{
                dto.setApplyDate(DateUtils.parseDate(obj[6]));
            }
            if(StringUtils.toString(obj[7]).length()>19) {
                String str = StringUtils.toString(obj[7]).substring(0, 19);
                dto.setApproveDate(DateUtils.parseDate(str));
            }else{
                dto.setApproveDate(DateUtils.parseDate(obj[7]));
            }
            dto.setApproveBy(StringUtils.toString(obj[8]));
            res.add(dto);
        }
        return res;
    }


    /**
     * 管理员信息列表展示总数
     */
    @Override
    public Long queryInformationListCount(InApplyUserDto in) throws Exception {
        StringBuffer sql=new StringBuffer("select ");
        sql.append(" count(DISTINCT t.apply_user_id)");
        sql.append(" from tf_f_apply_user_basic t, tf_f_apply_user au where t.apply_user_id=au.apply_user_id");
        sql.append(" and t.del_flag='0' and au.del_flag='0'");
        if(ListUntils.isNotNull(in.getStatusList())){
            sql.append(" and t.status in:statusList");
        }
        if(StringUtils.isNotBlank(in.getIdCard())){
            sql.append(" and t.id_card like :idCard");
        }
        if(StringUtils.isNotBlank(in.getRelName())){
            sql.append(" and t.rel_name like :relName");
        }
        if(StringUtils.isNotBlank(in.getTelephone())){
            sql.append(" and t.telephone like :telephone");
        }
        if(ListUntils.isNotNull(in.getClientIds())){
            sql.append(" and au.client_id in:clientIds");
        }
        if(ListUntils.isNotNull(in.getManageIds())&&StringUtils.isBlank(in.getManageId())){
            sql.append(" and au.create_manage_id in :applyManageIds");
        }
        if(ListUntils.isNotNull(in.getManageIds())&&StringUtils.isNotBlank(in.getManageId())){
            sql.append(" and au.apply_manage_id in :applyManageIds");
        }
        Query query=businessEntity.createNativeQuery(sql.toString());
        if(ListUntils.isNotNull(in.getStatusList())){
            query.setParameter("statusList",in.getStatusList());
        }
        if(StringUtils.isNotBlank(in.getIdCard())){
            query.setParameter("idCard",in.getIdCard()+"%");
        }
        if(StringUtils.isNotBlank(in.getRelName())){
            query.setParameter("relName",in.getRelName()+"%");
        }
        if(StringUtils.isNotBlank(in.getTelephone())){
            query.setParameter("telephone",in.getTelephone()+"%");
        }
        if(ListUntils.isNotNull(in.getClientIds())){
            query.setParameter("clientIds", in.getClientIds());
        }
        if(ListUntils.isNotNull(in.getManageIds())){
            query.setParameter("applyManageIds",in.getManageIds());
        }
        Object res= query.getSingleResult();
        if(null==res){
            return 0L;
        }
        return StringUtils.toLong(res);
    }

    /**
     * 备案否决
     * @param
     * @return
     */
    @Override
    @Transactional
    @Modifying
    public OutMessageDto auditAdminVeto(InManageDto in) throws Exception {
        OutMessageDto res=new OutMessageDto();
        //获取当前状态
        List<String> applyUserStatuses=applyUserBasicResources.queryStatuses(in.getItemIds());
        boolean flag=false;
        for(String s:applyUserStatuses){
            if(s.equals("2")){
                flag=true;
                break;
            }
        }
        if(flag) {
            if (in.getItemIds().size() == 1) {
                res.setFlag("fail");
                res.setMessage("本次操作人员已通过，请确认所选人员");
                return res;
            } else {
                res.setFlag("fail");
                res.setMessage("部分操作人员已通过，请确认所选人员");
                return res;
            }
        }
        //获取审批人姓名
        User user=userResources.findOne(in.getUserId());
        if(applyUserBasicResources.auditAdminVeto(in.getItemIds(),in.getRerson(),in.getUserId(),new Date(),user.getName())>0){
            res.setFlag("success");
            res.setMessage("处理成功");
        }else{
            res.setFlag("fail");
            res.setMessage("处理失败");
        }
        return res;
    }
}
