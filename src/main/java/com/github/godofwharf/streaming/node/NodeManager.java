package com.github.godofwharf.streaming.node;

import java.util.List;

public class NodeManager {

    private List<Node> nodes;

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public Node getNode(int id) {
        return nodes.get(id);
    }

    public String getNodeType(int id) {
        return nodes.get(id).getType();
    }

    public void addNode(Node n) {
        nodes.add(n);
    }
}
