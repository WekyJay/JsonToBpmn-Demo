package cn.wekyjay.bpmn;

import cn.wekyjay.bpmn.utils.NodeConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;

/**
 * SDK 门面：将前端流程设计器导出的 JSON 转换为 Flowable BPMN 模型 / XML。
 *
 * 转换管线：
 * <ol>
 *     <li>JSON 解析为 {@link JsonNode}</li>
 *     <li>{@link NodeConnector} 自动串联节点 parentId / childId（含网关分支递归）</li>
 *     <li>Jackson 多态反序列化为 {@link ProcessModel}</li>
 *     <li>装配 {@link BpmnModel}（无结束节点时自动补充，自动布局）</li>
 * </ol>
 *
 * 与运行时的变量约定（由部署方保证）：
 * <ul>
 *     <li>审批节点多实例集合并变量：{@code ${节点id}Collection}，元素变量：{@code 节点id}Item</li>
 *     <li>抄送节点委派表达式：{@code ${ccDelegate}}</li>
 * </ul>
 *
 * @author WekyJay
 * @Github: <a href="https://github.com/WekyJay">https://github.com/WekyJay</a>
 */
public class BpmnConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NodeConnector nodeConnector = new NodeConnector();

    /**
     * JSON 流程定义 → BPMN 2.0 XML 字节数组（含自动布局）
     *
     * @param json 前端设计器导出的流程定义 JSON
     * @return BPMN 2.0 XML 字节数组（UTF-8）
     */
    public byte[] convertToXml(String json) throws JsonProcessingException {
        return new BpmnXMLConverter().convertToXML(convertToModel(json));
    }

    /**
     * JSON 流程定义 → Flowable {@link BpmnModel}（可继续编程加工后再导出 XML）
     *
     * @param json 前端设计器导出的流程定义 JSON
     */
    public BpmnModel convertToModel(String json) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(json);
        JsonNode connected = nodeConnector.connect(jsonNode);
        ProcessModel processModel = objectMapper.treeToValue(connected, ProcessModel.class);
        return processModel.toBpmnModel();
    }
}
