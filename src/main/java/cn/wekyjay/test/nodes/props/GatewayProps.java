package cn.wekyjay.test.nodes.props;


import cn.wekyjay.test.Node;
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
