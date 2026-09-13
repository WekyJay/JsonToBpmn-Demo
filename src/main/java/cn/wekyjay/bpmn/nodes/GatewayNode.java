package cn.wekyjay.bpmn.nodes;

import cn.wekyjay.bpmn.Node;
import cn.wekyjay.bpmn.ProcessModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.FlowElement;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class GatewayNode extends Node {
    private List<List<Node>> branch;

    @Override
    public List<FlowElement> convert() {
        ArrayList<FlowElement> elements = new ArrayList<>();
        // 独占分支
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setId(this.getId());
        exclusiveGateway.setName(this.getName());
        // 获取props中的条件分支
        ArrayNode conditionBranch = this.getProps().withArray("branch");

        List<ConditionNode> branches = conditionBranch.valueStream().map(v -> {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.treeToValue(v, ConditionNode.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        // 设置默认条件节点
        ConditionNode defNode = branches.get(branches.size() - 1);
        String defId = defNode.getId()+"-"+defNode.getChildId();
        exclusiveGateway.setDefaultFlow(defId);

        elements.add(exclusiveGateway);
        // 转换条件节点
        branches.forEach(conditionNode -> elements.addAll(conditionNode.convert()));

        // 收集子节点Convert
        branch.forEach(sub->sub.forEach(branchNode -> elements.addAll(branchNode.convert())));

        return elements;
    }
}
