package cn.wekyjay.bpmn.nodes.props;


import cn.wekyjay.bpmn.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Data
public class GatewayProps extends NodeProps {
    private String type;
    private List<Node> branch;
}
