package com.ido85.party.aaaa.mgmt.business.application.impl;

import com.ido85.party.aaaa.mgmt.business.application.RuleApplication;
import com.ido85.party.aaaa.mgmt.business.domain.BusinessRole;
import com.ido85.party.aaaa.mgmt.business.domain.Condition;
import com.ido85.party.aaaa.mgmt.business.domain.Permission;
import com.ido85.party.aaaa.mgmt.business.dto.rule.ConditionDto;
import com.ido85.party.aaaa.mgmt.business.dto.rule.PermissionRuleDto;
import com.ido85.party.aaaa.mgmt.business.resources.BusinessRoleResources;
import com.ido85.party.aaaa.mgmt.business.resources.ConditionResource;
import com.ido85.party.aaaa.mgmt.security.utils.ListUntils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Administrator
 * @date 2017/12/25
 */
@Named
public class RuleApplicationImpl implements RuleApplication{

    @Inject
    private BusinessRoleResources businessRoleResources;

    @PersistenceContext(unitName = "business")
    private EntityManager entityManager;

    @Inject
    private RedisTemplate redisTemplate;

    @Inject
    private ConditionResource conditionResource;

    /**
     * 根据角色id判断所有权限是否合法
     * @param roleId
     * @return
     */
    @Override
    public Map<String, String> checkPermissionRule(Long roleId, String manageType, String manageLevel, String manageId, String manageCode) {
        Map<String,String> map = new HashMap<>();
        //特殊处理角色 TODO
        if((-4942560878455402496L == roleId.longValue() || -4939082210620207104L == roleId.longValue()) && null == manageLevel){
            map.put("flag","success");
            return map;
        }
        List<Permission> permissionList = null;
        List<Long> permissionIds = null;
        //根据角色id查询出所有权限
        BusinessRole businessRole = businessRoleResources.getRoleById(roleId);
        if(null != businessRole){
            permissionIds = new ArrayList<>();
            permissionList = businessRole.getPermissions();
            //遍历权限找到所有需要权限控制的
            for(Permission permission:permissionList){
                if(permission.isLimit()){
                    permissionIds.add(permission.getPermissionId());
                }
            }
        }
        //查询出权限需要控制的规则
        List<PermissionRuleDto> ruleList = this.getRule(permissionIds);
        //规则为单个条件||规则为组合条件,开始判断
        if(ListUntils.isNotNull(ruleList)){
            //将所有可能需要比较的字段封装成map
            Map<String,String> fieldMap = new HashMap<>();
            fieldMap.put("level",manageLevel);
            fieldMap.put("type",manageType);
            fieldMap.put("manageCode",manageCode);
            fieldMap.put("manageId",manageId);
            return this.judgeRule(ruleList, fieldMap);
        }else{
            map.put("flag","success");
            return map;
        }
    }

    /**
     * 条件判断
     * 1.包含(值存放在enum_value)
     * 2.正则匹配(匹配规则存放在reg_value)
     * @param ruleList
     * @return
     */
    private Map<String,String> judgeRule(List<PermissionRuleDto> ruleList, Map<String,String> filedMap) {
        Map<String,String> map = new HashMap<>();
        boolean flag = false;
        String errDes = null;
        String isCom = null;
        String permissionDes = null;
        Long permissionId = null;
        String ruleCode = null;
        String andOrTag = null;
        for(PermissionRuleDto dto:ruleList){
            errDes = dto.getErrDes();
            isCom = dto.getIsCom();
            andOrTag = dto.getAndOrTag();
            permissionDes = dto.getPermissionDes();
            permissionId = dto.getPermissionId();
            ruleCode = dto.getRuleCode();
            //规则为单个条件
            if("0".equals(isCom)){
                ConditionDto conditionDto = this.getCondition(ruleCode);
                if(null == conditionDto){
                    map.put("flag","fail");
                    map.put("message","系统规则异常，请联系管理员！");
                    return map;
                }
                String filed = conditionDto.getField();
                String regValue = conditionDto.getRegValue();
                //单个条件校验
                flag = judgeByCondition(conditionDto,filedMap);
                if(!flag){
                    map.put("flag","fail");
                    map.put("message",errDes);
                    return map;
                }
            }
            //规则为组合条件
            if("1".equals(isCom)){
                List<ConditionDto> conditionDtoList = this.getComContion(ruleCode);
                if(ListUntils.isNotNull(conditionDtoList)){
                    //组合条件为and的情形
                    if("0".equals(andOrTag)){
                        flag = true;
                        for(ConditionDto conditionDto:conditionDtoList){
                            if(null == conditionDto){
                                map.put("flag","fail");
                                map.put("message","系统规则异常，请联系管理员！");
                                return map;
                            }
                            flag = flag && judgeByCondition(conditionDto,filedMap);
                        }
                        if(!flag){
                            map.put("flag","fail");
                            map.put("message",errDes);
                            return map;
                        }
                    }
                    //组合条件为or的情形
                    if("1".equals(andOrTag)){
                        flag = false;
                        for(ConditionDto conditionDto:conditionDtoList){
                            flag = flag || judgeByCondition(conditionDto,filedMap);
                            if(flag){
                                break;
                            }
                        }
                        if(!flag){
                            map.put("flag","fail");
                            map.put("message",errDes);
                            return map;
                        }
                    }
                }
            }
        }
        map.put("flag","success");
        return map;
    }

    /**
     * 获取组合条件
     * @param ruleCode
     * @return
     */
    private List<ConditionDto> getComContion(String ruleCode) {
        List<ConditionDto> conditionDtos = null;
        ConditionDto conditionDto = null;
        //先从缓存获取
        String redisKey = "comrule-"+ruleCode;
        if(redisTemplate.hasKey(redisKey)){
            conditionDtos = (List<ConditionDto>) redisTemplate.opsForValue().get(redisKey);
            if(ListUntils.isNotNull(conditionDtos)){
                return conditionDtos;
            }
        }else{
            //缓存不存在在从数据库获取，同时加入缓存
            List<Condition> conditions = conditionResource.getComConditionByRuleCode(ruleCode);
            if(ListUntils.isNotNull(conditions)){
                conditionDtos = new ArrayList<>();
                for(Condition condition:conditions){
                    conditionDto = new ConditionDto();
                    conditionDto.setEnumValue(condition.getEnumValue());
                    conditionDto.setField(condition.getFiled());
                    conditionDto.setJudgeMethod(condition.getJudgeMethod());
                    conditionDto.setMaxValue(condition.getMaxValue());
                    conditionDto.setMinValue(condition.getMinValue());
                    conditionDto.setRegValue(condition.getRegValue());
                    conditionDto.setValueType(condition.getValueType());
                    conditionDtos.add(conditionDto);
                }
                redisTemplate.opsForValue().set(redisKey,conditionDtos);
                return conditionDtos;
            }
        }
        return null;
    }

    /**
     * 获取单个条件（如果更新规则，需要去redis清除该缓存）
     * @param ruleCode
     * @return
     */
    private ConditionDto getCondition(String ruleCode) {
        ConditionDto conditionDto = null;
        //先从缓存获取
        String redisKey = "rule-"+ruleCode;
        if(redisTemplate.hasKey(redisKey)){
            conditionDto = (ConditionDto) redisTemplate.opsForValue().get(redisKey);
            if(null != conditionDto){
                return conditionDto;
            }
        }else{
            //缓存不存在在从数据库获取，同时加入缓存
            Condition condition = conditionResource.getConditionByRuleCode(ruleCode);
            if(null != condition){
                conditionDto = new ConditionDto();
                conditionDto.setEnumValue(condition.getEnumValue());
                conditionDto.setField(condition.getFiled());
                conditionDto.setJudgeMethod(condition.getJudgeMethod());
                conditionDto.setMaxValue(condition.getMaxValue());
                conditionDto.setMinValue(condition.getMinValue());
                conditionDto.setRegValue(condition.getRegValue());
                conditionDto.setValueType(condition.getValueType());
                redisTemplate.opsForValue().set(redisKey,conditionDto);
                return conditionDto;
            }
        }
        return null;
    }

    /**
     * 判断一个简单条件
     * 1：枚举包含（包括等于的情形）  2：正则表达式
     * ps：可进行扩展，扩展的规则需要在下面写好逻辑
     * @return
     */
    private boolean judgeByCondition(ConditionDto conditionDto,Map<String,String> filedMap) {
        boolean flag = false;
        String filed = conditionDto.getField();
        //包含(值存放在enum_value)
        if("1".equals(conditionDto.getJudgeMethod())){
            String enumValue = conditionDto.getEnumValue();
            String[] strs = enumValue.split(",");
            for (String str: strs) {
                //检测到有符合的立刻返回true
                if (str.equals(filedMap.get(filed))){
                    flag = true;
                    break;
                }
            }
            return flag;
        }
        //正则匹配(匹配规则存放在reg_value)
        if("2".equals(conditionDto.getJudgeMethod())){
            // 编译正则表达式
            Pattern pattern = Pattern.compile(conditionDto.getRegValue());
            // 忽略大小写的写法
            // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(filedMap.get(filed));
            // 查找字符串中是否有匹配正则表达式的字符/字符串
            flag = matcher.find();
            return flag;
        }
        //不包含(值存放在enum_value)
        if("3".equals(conditionDto.getJudgeMethod())){
            flag = true;
            String enumValue = conditionDto.getEnumValue();
            String[] strs = enumValue.split(",");
            for (String str: strs) {
                //检测到存在不符合列表的立刻返回false
                if (str.equals(filedMap.get(filed))){
                    flag = false;
                    break;
                }
            }
            return flag;
        }
        return false;
    }

    /**
     * 查询出权限需要控制的规则
     * @param permissionIds
     * @return
     */
    private List<PermissionRuleDto> getRule(List<Long> permissionIds) {
        //根据权限id集查询出所有权限需要的控制规则
        if(ListUntils.isNotNull(permissionIds)){
            StringBuffer sql = new StringBuffer("select new com.ido85.party.aaaa.mgmt.business.dto.rule.PermissionRuleDto(p.permissionId,n.permissionDescription,p.ruleCode,r.isCom,r.errDes,r.andOrTag) " +
                    "from PermissionRule p,Permission n,Rule r where p.ruleCode = r.ruleCode and p.permissionId = n.permissionId and p.delFlag = '0'" +
                    " and r.delFlag = '0' ");
            sql.append(" and p.permissionId in :permissionIds");
            Query query = entityManager.createQuery(sql.toString(),PermissionRuleDto.class);
            query.setParameter("permissionIds",permissionIds);
            List<PermissionRuleDto> dtoList = query.getResultList();
            return dtoList;
        }
        return null;
    }
}
