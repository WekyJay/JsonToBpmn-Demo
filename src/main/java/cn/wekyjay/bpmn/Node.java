package cn.wekyjay.bpmn;

import cn.wekyjay.bpmn.nodes.*;
import cn.wekyjay.bpmn.nodes.listeners.NodeListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowableListener;
import org.flowable.bpmn.model.SequenceFlow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,include = JsonTypeInfo.As.PROPERTY,property = "type",defaultImpl = Node.class,visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StartNode.class,name = "Start"),
        @JsonSubTypes.Type(value = ApprovalNode.class, name = "Approval"),
        @JsonSubTypes.Type(value = GatewayNode.class, name = "Gateway"),
        @JsonSubTypes.Type(value = ConditionNode.class, name = "Exclusive"),
        @JsonSubTypes.Type(value = CcNode.class, name = "Cc"),
        @JsonSubTypes.Type(value = EndNode.class, name = "End")
})
@Getter
@Setter
public abstract class Node implements Serializable {

    private String id;          // 节点id
    private String parentId;    // 父节点id
    private String name;        // 节点名称
    private String type;        // 节点类型
    private String childId;     // 子节点
    private List<NodeListener> executionListeners;  // 执行监听器
    private JsonNode props;    // 属性配置
    @JsonIgnore
    private String branchId;    // 分支id

    public abstract List<FlowElement> convert();


    /**
     * 构建序列流
     * @param childId
     * @return
     */
    public SequenceFlow buildSequence(String childId) {
        String sourceRef;
        String targetRef;
        if (StringUtils.isNotBlank(childId)) {
            sourceRef = getId();
            targetRef = childId;
        } else { // Try to find branch
            if (StringUtils.isNotBlank(this.branchId)) {
                sourceRef = this.id;
                targetRef = this.branchId;
            } else {
                throw new RuntimeException(String.format("节点 %s 的下一个节点不能为空", this.id));
            }
        }
        // Build SequenceFlow
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setId(String.format("%s-%s", sourceRef, targetRef));
        sequenceFlow.setSourceRef(sourceRef);
        sequenceFlow.setTargetRef(targetRef);
        return sequenceFlow;
    }

    /**
     * 构建执行监听器
     * @return
     */
    public List<FlowableListener> buidEventListener() {
        // 过滤空实现
        if (this.executionListeners != null && !this.executionListeners.isEmpty()) {
            return this.executionListeners.stream().filter(l -> StringUtils.isNotBlank(l.getImplementation())).map(listener -> {
                // 构建标准执行监听器
                FlowableListener executionListener = new FlowableListener();
                executionListener.setEvent(listener.getEvent());
                executionListener.setImplementationType(listener.getImplementationType());
                executionListener.setImplementation(listener.getImplementation());
                return executionListener;
            }).collect(Collectors.toList());
        }
        // 无执行监听器，返回空列表
        return new ArrayList<>();
    }

}
