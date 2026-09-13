package cn.wekyjay.test.nodes;

import cn.wekyjay.test.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.FlowElement;

import java.util.ArrayList;
import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class EndNode extends Node {

    @Override
    public List<FlowElement> convert() {
        ArrayList<FlowElement> elements = new ArrayList<>();
        // 结束节点
        EndEvent endEvent = new EndEvent();
        endEvent.setId(this.getId());
        endEvent.setName(this.getName());
        // 添加执行监听器
        endEvent.setExecutionListeners(this.buidEventListener());
        elements.add(endEvent);
        return elements;
    }
}