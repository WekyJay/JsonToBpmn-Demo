package cn.wekyjay.test;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ArraySerializerBase;
import com.fasterxml.jackson.databind.ser.std.ObjectArraySerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import cn.wekyjay.test.nodes.EndNode;
import lombok.Data;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProcessModel {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private List<Node> process;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;
    private String remark;

    public BpmnModel toBpmnModel() {
        BpmnModel bpmnModel = new BpmnModel();
        // 命名空间
        bpmnModel.setTargetNamespace("https://flowable.org/bpmn20");
        // 创建一个流程
        Process process = new Process();
        // 设置一个流程id
        process.setId(this.id+"");
        // 设置流程的name
        process.setName(this.name);
        // 设置流程的文档
        process.setDocumentation(this.remark);
    
        // 遍历构建所有节点
        List<Node> processList = this.getProcess();
        List<FlowElement> flowElementList = new ArrayList<>();

        // 遍历所有节点
        processList.forEach(flowElement -> {
            List<FlowElement> elements = flowElement.convert();
            flowElementList.addAll(elements);
        });

        // 判断是否存在结束节点
        boolean hasEndNode = processList.stream().anyMatch(e -> e instanceof EndNode);

        if (!hasEndNode) {
            // 如果不存在结束节点，则添加一个默认的结束节点
            System.out.println("不存在结束节点,添加默认结束节点 ");
            EndNode endNode = new EndNode();
            endNode.setId("node_end");
            endNode.setName("流程结束");
            flowElementList.addAll(endNode.convert());
        }else{
            System.out.println("存在结束节点,不添加默认结束节点 ");
        }

        for (FlowElement flowElement : flowElementList) {
            process.addFlowElement(flowElement);
        }
        

        // 设置流程
        bpmnModel.addProcess(process);
        // 自动布局
        new BpmnAutoLayout(bpmnModel).execute();

        return bpmnModel;
    }
}
