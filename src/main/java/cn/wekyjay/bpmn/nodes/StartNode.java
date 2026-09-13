package cn.wekyjay.bpmn.nodes;

import cn.wekyjay.bpmn.Node;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;

import java.util.ArrayList;
import java.util.List;


public class StartNode extends Node {


    @Override
    public List<FlowElement> convert() {
        ArrayList<FlowElement> elements = new ArrayList<>();
        // 创建开始节点
        StartEvent startEvent = new StartEvent();
        startEvent.setId(this.getId());
        startEvent.setName(this.getName());
        startEvent.setExecutionListeners(this.buidEventListener()); 
        // 添加节点
        elements.add(startEvent);
        // 构建SequenceFlow
        SequenceFlow sequenceFlow = this.buildSequence(this.getChildId());
        elements.add(sequenceFlow);
        return elements;
    }

}
