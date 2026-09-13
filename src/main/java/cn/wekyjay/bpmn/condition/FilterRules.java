package cn.wekyjay.test.condition;

import lombok.Data;

import java.util.List;

/**
 * 筛选规则
 */
@Data
public class FilterRules {
    private Boolean logic;
    private List<Condition> conditions;
    private List<FilterRules> groups;
}
