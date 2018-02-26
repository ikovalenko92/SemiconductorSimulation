package intelligentProduct;

import java.awt.Point;
import java.util.ArrayList;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import sharedInformation.ResourceEvent;
import sharedInformation.ProductState;
import sharedInformation.PhysicalProperty;

public class AgentBeliefModel extends DirectedSparseGraph<ProductState, ResourceEvent>{

	private ProductState currentNode = null;
	private ArrayList<ProductState> desiredNodes = new ArrayList<ProductState>();
	private ProductState dummyEmptyNode;
	private ResourceEvent startingEdge;
	
	public AgentBeliefModel(ProductState currentNode){
		this.dummyEmptyNode = new ProductState(null, null, new PhysicalProperty(new Point(0,0)));
		this.currentNode = currentNode;
		this.startingEdge = new ResourceEvent(null, dummyEmptyNode, currentNode, null, 0);
		this.addEdge(startingEdge, dummyEmptyNode, currentNode);
	}
	
	public AgentBeliefModel(ProductAgentInstance productAgentInstance,
			ProductState startingNode) {
		// TODO Auto-generated constructor stub
	}

	/** Add a list of edges
	 * @param edgeList
	 */
	public void addEdges(ArrayList<ResourceEvent> edgeList) {
		for (ResourceEvent edge:edgeList){
			this.addEdge(edge, edge.getParent(), edge.getChild());
		}
	}
	
	public void setCurrentNode(ProductState currentNode){
		addVertex(currentNode);
		this.currentNode = currentNode;
	}
	
	public ProductState getCurrentNode() {
		return this.currentNode;
	}
	
	public void addDesiredNode(ProductState desiredNode){
		if (this.containsVertex(desiredNode)){
			this.desiredNodes.add(desiredNode);
		}
	}
	
	public void clearDesiredNodes(){
		this.desiredNodes.clear();
	}

	public ArrayList<ProductState> getDesiredNodes() {
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
		//Clear desired nodes
		this.clearDesiredNodes();
		
		ArrayList<ProductState> removeVertices = new ArrayList<ProductState>();
		ArrayList<ResourceEvent> removeEdges = new ArrayList<ResourceEvent>();
		
		//Find all the vertices to remove
		for (ProductState node : getVertices()){
			// Keep the current and dummy nodes (starting)
			if (!this.currentNode.equals(node) && !this.currentNode.equals(dummyEmptyNode)){
				removeVertices.add(node);
			}
		}
		
		//Find all the edges to remove
		for (ResourceEvent edge: getEdges()){
			// Keep the starting edge
			if (!this.startingEdge.equals(edge)){
				removeEdges.add(edge);
			}
		}
		
		for (ProductState node : removeVertices){this.removeVertex(node);}
		for (ResourceEvent edge: removeEdges){this.removeEdge(edge);}
		
		// Clean up
		removeVertices.clear();
		removeEdges.clear();
		removeVertices = null;
		removeEdges = null;
	}
	
}
