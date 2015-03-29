package Node;

import java.util.List;

import ch.epfl.arni.ncutils.CodingVectorDecoder;

public class NodeManager {
	
	private List<Node> nodes;	
	
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	
	public List<Node> getNodes() {
		return nodes;
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
