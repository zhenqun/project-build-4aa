package com.ido85.party.aaaa.mgmt.business.resources;

import com.ido85.party.aaaa.mgmt.business.domain.Condition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/12/26.
 */
public interface ConditionResource extends JpaRepository<Condition,Long>{

    /**
     * 根据规则编码查询单个条件
     * @param ruleCode
     * @return
     */
    @Query("select c from Condition c where c.delFlag = '0' and c.ruleCode = :ruleCode")
    Condition getConditionByRuleCode(@Param("ruleCode") String ruleCode);

    /**
     * 根据规则编码查询组合条件
     * @param ruleCode
     * @return
     */
    @Query("select c from Condition c,com.ido85.party.aaaa.mgmt.business.domain.ComCondition t where c.id = t.subConditionId and c.delFlag = '0' and t.delFlag = '0' and t.ruleCode = :ruleCode")
    List<Condition> getComConditionByRuleCode(@Param("ruleCode") String ruleCode);
}
