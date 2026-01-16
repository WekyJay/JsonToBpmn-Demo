package cn.wekyjay.test.nodes.props;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class NodeProps implements Serializable {
    private String mode;
    private String ruleType;
    private Map<String,Object> taskMode;
    private Boolean needSign;
    private List<String> assignUser;
    private Map<String,Object>  rootSelect;
}
