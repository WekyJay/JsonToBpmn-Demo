package cn.wekyjay.bpmn.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * @title: 流程节点连接器
 * @Description: 用于解析JSON流程结构，自动串联节点的 parentId 和 childId
 *
 *
 * @author WekyJay
 * @Github: <a href="https://github.com/WekyJay">https://github.com/WekyJay</a>
 * @Date: 2026/1/18 22:31
 */
public class NodeConnector {

    private static final String KEY_PROCESS = "process";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ID = "id";
    private static final String KEY_CHILD_ID = "childId";
    private static final String KEY_PARENT_ID = "parentId";
    private static final String KEY_PROPS = "props";
    private static final String KEY_BRANCH = "branch";
    private static final String TYPE_GATEWAY = "Gateway";
    private static final String NODE_END = "node_end";

    /**
     * 流程节点Id自动串联子父节点
     * @param jsonNode
     * @return
     * @throws JsonProcessingException
     */
    public JsonNode connect(JsonNode jsonNode) throws JsonProcessingException {
        if (jsonNode.isObject() && jsonNode.has(KEY_PROCESS)) {
            ArrayNode process = jsonNode.withArray(KEY_PROCESS);
            connectNodeList(process, null);
        }
        return jsonNode;
    }

    /**
     * 连接节点列表
     * @param nodes 节点列表
     * @param finalNode 列表结束后的汇聚节点（如果为null则表示是主流程结束）
     * @throws JsonProcessingException
     */
    private void connectNodeList(ArrayNode nodes, JsonNode finalNode) throws JsonProcessingException {
        for (int i = 0; i < nodes.size(); i++) {
            JsonNode currentNode = nodes.get(i);
            ObjectNode currentObj = (ObjectNode) currentNode;
            // 获取当前节点ID（预留，当前逻辑中未使用）
            // String currentId = currentNode.get(KEY_ID).asText();

            // 1. 设置 parentId (除了第一个节点)
            if (i > 0) {
                String prevId = nodes.get(i - 1).get(KEY_ID).asText();
                currentObj.put(KEY_PARENT_ID, prevId);
            }

            // 2. 处理网关节点
            if (TYPE_GATEWAY.equalsIgnoreCase(currentNode.path(KEY_TYPE).asText())) {
                // 网关的汇聚节点是列表中的下一个节点，或者是传入的 finalNode
                JsonNode nextNode = (i < nodes.size() - 1) ? nodes.get(i + 1) : finalNode;
                connectGatewayBranches(currentNode, nextNode);
                // 网关节点本身不需要设置 childId，因为流程流向了分支
                // 但为了保持链条完整性，有时也会指向下一个节点，视具体业务逻辑而定
                // 这里保持原逻辑，网关本身不设置 childId 指向 nextNode，而是由分支末端指向 nextNode
                continue;
            }

            // 3. 设置 childId (除了最后一个节点)
            if (i < nodes.size() - 1) {
                String nextId = nodes.get(i + 1).get(KEY_ID).asText();
                currentObj.put(KEY_CHILD_ID, nextId);
            } else {
                // 最后一个节点
                if (finalNode != null) {
                    currentObj.put(KEY_CHILD_ID, finalNode.get(KEY_ID).asText());
                } else {
                    // 主流程结束，或者分支结束且无汇聚点，指向结束节点
                    currentObj.put(KEY_CHILD_ID, NODE_END);
                }
            }
        }
    }

    /**
     * 处理网关分支连接
     * @param gatewayNode 网关节点
     * @param finalNode 网关汇聚后的节点
     * @throws JsonProcessingException
     */
    private void connectGatewayBranches(JsonNode gatewayNode, JsonNode finalNode) throws JsonProcessingException {
        if (!gatewayNode.has(KEY_PROPS) || !gatewayNode.has(KEY_BRANCH)) {
            return;
        }

        // 条件节点列表 (props.branch)
        ArrayNode conditionNodes = gatewayNode.get(KEY_PROPS).withArray(KEY_BRANCH);
        // 分支具体流程列表 (branch)
        ArrayNode branchFlows = gatewayNode.withArray(KEY_BRANCH);

        String gatewayId = gatewayNode.get(KEY_ID).asText();
        String finalId = (finalNode != null) ? finalNode.get(KEY_ID).asText() : NODE_END;

        for (int i = 0; i < conditionNodes.size(); i++) {
            ObjectNode conditionNode = (ObjectNode) conditionNodes.get(i);
            ArrayNode branchFlow = (ArrayNode) branchFlows.get(i);

            // 1. 条件节点连接到网关 (Parent = Gateway)
            conditionNode.put(KEY_PARENT_ID, gatewayId);

            // 2. 如果分支为空，直接连接到汇聚节点
            if (branchFlow.isEmpty()) {
                conditionNode.put(KEY_CHILD_ID, finalId);
                continue;
            }

            // 3. 条件节点连接到分支第一个节点
            conditionNode.put(KEY_CHILD_ID, branchFlow.get(0).get(KEY_ID).asText());

            // 4. 处理分支内的节点链
            // 分支内的第一个节点的 parentId 是条件节点
            ObjectNode firstNodeInBranch = (ObjectNode) branchFlow.get(0);
            firstNodeInBranch.put(KEY_PARENT_ID, conditionNode.get(KEY_ID).asText());
            
            // 递归处理分支列表
            // 注意：connectNodeList 内部会处理 parentId，但它假设 parent 是 list[i-1]。
            // 对于 list[0]，我们需要手动处理（上面已经设置了），或者修改 connectNodeList 让它支持传入 parentId。
            // 为了复用 connectNodeList，我们可以让它只处理 i>0 的 parentId，或者我们在外部处理好 list[0] 的 parentId 后，
            // connectNodeList 的 i>0 逻辑会覆盖正确的 parentId 吗？不会，因为 i>0 时 parent 是 list[i-1]，这是对的。
            // 唯一的问题是 list[0] 的 parentId 在 connectNodeList 中不会被设置，所以我们在外面设置是对的。
            
            connectNodeList(branchFlow, finalNode);
        }
    }
}
