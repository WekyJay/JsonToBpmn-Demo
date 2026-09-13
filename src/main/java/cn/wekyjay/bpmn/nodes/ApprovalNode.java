package cn.wekyjay.test.nodes;

import cn.wekyjay.test.Node;
import cn.wekyjay.test.enums.ApprovalMultiEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class ApprovalNode extends AssigneeNode{

    private ApprovalMultiEnum multi;    // 多人审批方式
    private BigDecimal multiPercent;    // 多人会签通过百分比

    @Override
    public List<FlowElement> convert() {

        // 所有节点集合
        ArrayList<FlowElement> elements = new ArrayList<>();
        // 用户节点
        UserTask userTask = new UserTask();
        userTask.setId(this.getId());
        userTask.setName(this.getName());
        // 审批人
        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
        if (this.getMulti() == ApprovalMultiEnum.SEQUENTIAL) { // 多人会签
            multiInstanceLoopCharacteristics.setSequential(true);
        } else if (this.getMulti() == ApprovalMultiEnum.JOINT) { // 多人会签（或签）
            multiInstanceLoopCharacteristics.setSequential(false);
            if (Objects.nonNull(this.getMultiPercent()) && this.getMultiPercent().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percent = this.getMultiPercent().divide(new BigDecimal(100), 2, RoundingMode.DOWN);
                multiInstanceLoopCharacteristics.setCompletionCondition(String.format("${nrOfCompletedInstances/nrOfInstances >= %s}", percent));
            }
        } else if (this.getMulti() == ApprovalMultiEnum.SINGLE) { // 正常签名
            multiInstanceLoopCharacteristics.setSequential(false);
            multiInstanceLoopCharacteristics.setCompletionCondition("${nrOfCompletedInstances > 0}");
        }

        String variable = String.format("%sItem", this.getId());
        multiInstanceLoopCharacteristics.setElementVariable(variable);
        multiInstanceLoopCharacteristics.setInputDataItem(String.format("${%sCollection}", this.getId()));
        userTask.setLoopCharacteristics(multiInstanceLoopCharacteristics);
        userTask.setAssignee(String.format("${%s}", variable));
        elements.add(userTask);

        // 下一个节点的连线
        SequenceFlow sequenceFlow = this.buildSequence(this.getChildId());
        elements.add(sequenceFlow);

        return elements;
    }
}
