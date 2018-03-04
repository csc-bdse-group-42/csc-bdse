package ru.csc.bdse.kv;

import org.springframework.stereotype.Component;
import ru.csc.bdse.util.NodeOperationException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class NodeRepository {
    private Map<String, NodeInfo> nodes;

    NodeRepository() {
        this.nodes = new HashMap<String, NodeInfo>();
        NodeInfo defaultNode = new NodeInfo("DEFAULT", NodeStatus.UP);
        nodes.put("DEFAULT", defaultNode);
    }

    public void addNode(String nodeName) throws NodeOperationException {
        if (nodes.containsKey(nodeName)) {
            throw new NodeOperationException("Node with name " + nodeName + " already exists.");
        }

        nodes.put(nodeName, new NodeInfo(nodeName, NodeStatus.UP));
    }

    public void removeNode(String nodeName) throws NodeOperationException {
        if (!nodes.containsKey(nodeName)) {
            throw new NodeOperationException("Node with name " + nodeName + " not found.");
        }

        nodes.remove(nodeName);
    }

    /**
     * @return names of all registered nodes
     */
    public Set<String> getNodeNames() {
        return nodes.keySet();
    }

    /**
     * @return info about all registered nodes
     */
    public Set<NodeInfo> getNodesInfo() {
        return new HashSet<>(nodes.values());
    }

    /**
     * @return default node.
     */
    public NodeInfo getNode() {
        return nodes.get("DEFAULT");
    }

    /**
     * @param name - nodeName
     * @return node with specified name
     */
    public NodeInfo getNode(String name) {
        return nodes.get(name);
    }
}
