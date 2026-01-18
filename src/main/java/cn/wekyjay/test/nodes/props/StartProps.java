package cn.wekyjay.test.nodes.props;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StartProps extends NodeProps {
    private List<JsonNode> formPerms;
}
