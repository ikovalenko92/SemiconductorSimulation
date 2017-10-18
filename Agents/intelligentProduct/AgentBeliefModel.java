package intelligentProduct;

import java.util.ArrayList;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;

@SuppressWarnings("serial")
public class AgentBeliefModel extends DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge>{

	private CapabilitiesNode currentNode = null;
	private ArrayList<CapabilitiesNode> desiredNodes = new ArrayList<CapabilitiesNode>();
	private CapabilitiesNode dummyEmptyNode;
	private CapabilitiesEdge startingEdge;
	
	public AgentBeliefModel(){
		this.dummyEmptyNode = new CapabilitiesNode(null, null, null);
		this.currentNode = new CapabilitiesNode(null, null, null);
		this.startingEdge = new CapabilitiesEdge(null, dummyEmptyNode, currentNode, null, 0);
		this.addEdge(startingEdge, dummyEmptyNode, currentNode);
	}
	
	/** Add a list of edges
	 * @param edgeList
	 */
	public void addEdges(ArrayList<CapabilitiesEdge> edgeList) {
		for (CapabilitiesEdge edge:edgeList){
			this.addEdge(edge, edge.getParent(), edge.getChild());
		}
	}
	
	public void setCurrentNode(CapabilitiesNode currentNode){
		this.currentNode = currentNode;
	}
	
	public CapabilitiesNode getCurrentNode() {
		return this.currentNode;
	}
	
	public void addDesiredNode(CapabilitiesNode desiredNode){
		this.desiredNodes.add(desiredNode);
	}
	
	public void clearDesiredNodes(){
		this.desiredNodes.clear();
	}

	public ArrayList<CapabilitiesNode> getDesiredNodes() {
		return this.desiredNodes;
	}
	
	/**
	 * @return checks if the current node is one of the desired nodes
	 */
	public boolean checkCurrentDesired(){
		if (this.desiredNodes.contains(currentNode)){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Clears everything except the current node and the dummy first node (edge that indicates current state)
	 */
	public void clear() {
		this.clearDesiredNodes();
		
		//keep the current node
		for (CapabilitiesNode node : getVertices()){
			if (!this.currentNode.equals(node) && !this.currentNode.equals(dummyEmptyNode)){
				this.removeVertex(node);
			}
		}
		
		//keep the starting edge
		for (CapabilitiesEdge edge: getEdges()){
			if (!this.startingEdge.equals(edge)){
				this.removeEdge(edge);
			}
		}
		
	}
}
