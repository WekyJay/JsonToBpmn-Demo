package cn.wekyjay.bpmn.nodes;

import cn.wekyjay.bpmn.Node;
import cn.wekyjay.bpmn.condition.FilterRules;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.SequenceFlow;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Setter
@Getter
@ToString(callSuper = true)
public class ConditionNode extends Node {
    @JsonIgnore
    private Boolean def;
    @JsonIgnore
    private Map<String, String> operatorMap = new HashMap<>();

    {
        // 等于
        operatorMap.put("eq", "var:eq(%s, %s)");
        // 不等于
        operatorMap.put("ne", "var:notEquals(%s, %s)");
        // 包含
        operatorMap.put("in", "var:containsAny(%s, %s)");
        // 不包含
        operatorMap.put("ni", "var:notContainsAny(%s, %s)");
        // 为空
        operatorMap.put("ul", "var:isNull(%s)");
        // 不为空
        operatorMap.put("nu", "var:isNotNull(%s)");
        // 字符包含
        operatorMap.put("lk", "var:contains(%s, %s)");
        // 大于
        operatorMap.put("gt", "var:gt(%s, %s)");
        // 小于
        operatorMap.put("lt", "var:lt(%s, %s)");
        // 小于或等于
        operatorMap.put("le", "var:lte(%s, %s)");
        // 大于或等于
        operatorMap.put("ge", "var:gte(%s, %s)");
    }

    /**
     * 格式化表达式值
     * @param val
     * @return ‘val’
     */
    protected String stringVal(Object val) {
        if (val instanceof String) {
            // 字符串需要添加单引号
            return String.format("'%s'", val);
        } else {
            return String.valueOf(val);
        }
    }

    /**
     * 格式化比较值
     * @param val
     * @return
     */
    private Object formatCompareValue(Object val) {
        if (val instanceof Collection) {
            return ((Collection<?>) val).stream()
                    .map(this::stringVal)
                    .collect(Collectors.joining(","));
        } else if (val instanceof Object[]) {
            return Arrays.stream((Object[]) val)
                    .map(this::stringVal)
                    .collect(Collectors.joining(","));
        } else if (val instanceof String) {
            return String.format("'%s'", val);
        }
        return val;
    }

    /**
     * 转换为条件表达式
     * @param filterRules
     * @return
     */
    public String toConditionExpression(FilterRules filterRules) {
        String expression = "";
        // 遍历条件 并格式化表达式
        if (filterRules.getConditions() != null) {
            expression = filterRules.getConditions().stream().map(e -> {
                String operator = operatorMap.get(e.getCompare().toLowerCase()); // 转换为小写 确保匹配
                if (StringUtils.isNotBlank(operator)) {
                    Object formattedVal = formatCompareValue(e.getCompareVal());
                    e.setCompareVal(formattedVal);
                    return String.format(operator,
                            e.getSymbol(),
                            formattedVal
                    );
                } else {
                    return "";
                }
            }).collect(Collectors.joining(filterRules.getLogic() ? " && " : " ||")); // logic为true意为“与”，否则为“或”
        }
        // 如果filterRules下没有组 直接返回该表达式 否则进入子组递归
        if (filterRules.getGroups() == null || filterRules.getGroups().isEmpty()) {
            return expression;
        } else {
            String collect = filterRules
                    .getGroups()
                    .stream()
                    .map(this::toConditionExpression)
                    .collect(Collectors.joining(filterRules.getLogic() ? " && " : " || "));
            // 合并子组表达式
            if (StringUtils.isNotBlank(expression)) {
                return String.format("(%s) %s (%s)", expression, filterRules.getLogic() ? " && " : " || ", collect);
            } else {
                return collect;
            }
        }
    }

    @Override
    public List<FlowElement> convert() {
        ArrayList<FlowElement> elements = new ArrayList<>();
        // 条件节点
        SequenceFlow sequenceFlow = this.buildSequence(this.getChildId());
        sequenceFlow.setSourceRef(this.getParentId());

        ObjectMapper objectMapper = new ObjectMapper();
        FilterRules filterRules = new FilterRules();
        filterRules.setLogic(false);
        // 解析条件组
        ArrayNode gateWayProps = this.getProps().withArray("groups");

        // 嵌套解析条件组
        List<FilterRules> list = gateWayProps.valueStream().map(sub -> {
            try {
                // 解析子组条件，内部会递归解析子组
                return objectMapper.treeToValue(sub, FilterRules.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        // 设置条件组
        filterRules.setGroups(list);

        // 获取条件表达式
        String expression = this.toConditionExpression(filterRules);

        // 如果条件表达式不为空 则设置为条件表达式
        if (StringUtils.isNotBlank(expression)) {
            sequenceFlow.setConditionExpression(String.format("${%s}", expression));
        }
        elements.add(sequenceFlow);
        return elements;
    }
}
