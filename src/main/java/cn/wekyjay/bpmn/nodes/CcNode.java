package cn.wekyjay.bpmn.nodes;

import cn.wekyjay.bpmn.Node;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.ImplementationType;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.ServiceTask;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: Cc抄送节点
 *
 * @author WekyJay
 * @Github: <a href="https://github.com/WekyJay">https://github.com/WekyJay</a>
 * @Date: 2026/1/18 22:26
 */
public class CcNode extends AssigneeNode{

    @Override
    public List<FlowElement> convert() {
        ArrayList<FlowElement> elements = new ArrayList<>();
        // 创建服务节点
        ServiceTask serviceTask = new ServiceTask();
        serviceTask.setId(this.getId());
        serviceTask.setName(this.getName());
        // serviceTask.setAsynchronous(true); // 是否异步执行

        // 设置委派任务
        serviceTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
        serviceTask.setImplementation("${ccDelegate}");
        elements.add(serviceTask);

        // 下一个节点的连线
        SequenceFlow sequenceFlow = this.buildSequence(this.getChildId());
        elements.add(sequenceFlow);

        return elements;
    }
}
