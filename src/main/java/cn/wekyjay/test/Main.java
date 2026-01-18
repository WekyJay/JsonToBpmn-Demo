package cn.wekyjay.test;

import cn.wekyjay.test.utils.BufferUtils;
import cn.wekyjay.test.utils.NodeConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;

import java.io.*;
import java.nio.Buffer;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        // Set the JSON string
        String jsonString = "{\"name\":\"请假流程\",\"process\":[{\"id\":\"node_root\",\"type\":\"Start\",\"name\":\"发起人\",\"parentId\":null,\"childId\":null,\"props\":{}},{\"id\":\"node_17628242120246176\",\"type\":\"Approval\",\"name\":\"用户申请\",\"parentId\":null,\"childId\":null,\"props\":{\"mode\":\"USER\",\"ruleType\":\"ROOT_SELF\",\"taskMode\":{\"type\":\"AND\",\"percentage\":100},\"needSign\":false,\"assignUser\":[],\"rootSelect\":{\"multiple\":false},\"leader\":{\"level\":1,\"emptySkip\":false},\"leaderTop\":{\"level\":0,\"toEnd\":false,\"emptySkip\":false},\"assignDept\":{\"dept\":[],\"type\":\"LEADER\"},\"assignRole\":[],\"noUserHandler\":{\"type\":\"TO_NEXT\",\"assigned\":[]},\"sameRoot\":{\"type\":\"TO_SELF\",\"assigned\":[]},\"timeout\":{\"enable\":false,\"time\":1,\"timeUnit\":\"M\",\"type\":\"TO_PASS\"}}},{\"id\":\"node_17685476393335780_fork\",\"type\":\"Gateway\",\"name\":\"网关节点\",\"parentId\":null,\"childId\":null,\"props\":{\"type\":\"Exclusive\",\"branch\":[{\"id\":\"node_17685476393333937\",\"type\":\"Exclusive\",\"name\":\"大于1000\",\"parentId\":null,\"childId\":null,\"props\":{\"logic\":true,\"groups\":[{\"logic\":true,\"conditions\":[{\"group\":\"FORM\",\"type\":\"NumberInput\",\"symbol\":\"NumberInput_hvccvk7m\",\"name\":[\"表单\",\"金额\"],\"valueType\":null,\"compare\":\"GT\",\"compareVal\":[\"1000\"]}]}]}},{\"id\":\"node_17685476393339974\",\"type\":\"Exclusive\",\"name\":\"1000以内（默认）\",\"parentId\":null,\"childId\":null,\"props\":{\"logic\":true,\"groups\":[{\"logic\":true,\"conditions\":[]}]}}]},\"branch\":[[{\"id\":\"node_17685476615368380\",\"type\":\"Approval\",\"name\":\"校长\",\"parentId\":null,\"childId\":null,\"props\":{\"mode\":\"USER\",\"ruleType\":\"LEADER\",\"taskMode\":{\"type\":\"AND\",\"percentage\":100},\"needSign\":false,\"assignUser\":[],\"rootSelect\":{\"multiple\":false},\"leader\":{\"level\":2,\"emptySkip\":false},\"leaderTop\":{\"level\":1,\"toEnd\":false,\"emptySkip\":false},\"assignDept\":{\"dept\":[],\"type\":\"LEADER\"},\"assignRole\":[],\"noUserHandler\":{\"type\":\"TO_NEXT\",\"assigned\":[]},\"sameRoot\":{\"type\":\"TO_SELF\",\"assigned\":[]},\"timeout\":{\"enable\":false,\"time\":1,\"timeUnit\":\"M\",\"type\":\"TO_PASS\"}}}],[{\"id\":\"node_17685476640969365\",\"type\":\"Approval\",\"name\":\"学部校长\",\"parentId\":null,\"childId\":null,\"props\":{\"mode\":\"USER\",\"ruleType\":\"LEADER\",\"taskMode\":{\"type\":\"AND\",\"percentage\":100},\"needSign\":false,\"assignUser\":[],\"rootSelect\":{\"multiple\":false},\"leader\":{\"level\":1,\"emptySkip\":false},\"leaderTop\":{\"level\":0,\"toEnd\":false,\"emptySkip\":false},\"assignDept\":{\"dept\":[],\"type\":\"LEADER\"},\"assignRole\":[],\"noUserHandler\":{\"type\":\"TO_NEXT\",\"assigned\":[]},\"sameRoot\":{\"type\":\"TO_SKIP\",\"assigned\":[]},\"timeout\":{\"enable\":false,\"time\":1,\"timeUnit\":\"M\",\"type\":\"TO_PASS\"}}}]]}]}";


        ObjectMapper objectMapper = new ObjectMapper();

        // 先转换为JsonNode，再转换为实体类
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        JsonNode node = new NodeConnector().connect(jsonNode);


        ProcessModel processModel = objectMapper.treeToValue(node, ProcessModel.class);

        // 转换为Bpmn实体类
        BpmnModel bpmnModel = processModel.toBpmnModel();

        // 转换为XML字节数组
        byte[] xmlBytes = new BpmnXMLConverter().convertToXML(bpmnModel);

        // 写入XML文件
        BufferedOutputStream outputStream = BufferUtils.getBufferedOutputStream(xmlBytes);
        outputStream.close();
    }


}