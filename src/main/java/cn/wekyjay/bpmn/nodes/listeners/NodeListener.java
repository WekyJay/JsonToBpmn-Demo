package cn.wekyjay.bpmn.nodes.listeners;

import lombok.Data;

@Data
public class NodeListener {
    private String event;
    private String implementation;
    private String implementationType;
}