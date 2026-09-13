package cn.wekyjay.test.nodes.props;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApprovalProps extends NodeProps {
    private String mode;
    private String ruleType;
    private Map<String,Object> taskMode;
    private Boolean needSign;
    private List<String> assignUser;
    private Map<String,Object>  rootSelect;
    private Map<String,Object>  leader;
    private Map<String,Object>  leaderTop;
    private Map<String,Object>  assignDept;
    private Map<String,Object>  assignRole;
    private Map<String,Object>  noUserHandler;
    private Map<String,Object>  sameRoot;
    private Map<String,Object>  timeout;

}
