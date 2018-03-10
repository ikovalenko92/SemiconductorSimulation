package intelligentProduct;

import java.awt.Point;
import java.util.ArrayList;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import sharedInformation.ResourceEvent;
import sharedInformation.ProductState;
import sharedInformation.PhysicalProperty;

public class EnvironmentModel extends DirectedSparseGraph<ProductState, ResourceEvent>{
	
	private static final long serialVersionUID = 1L; //Eclipse wanted me to add this
	
	private ProductState currentState = null;
	private ProductState dummyEmptyNode; // Empty node needed to represent the "parent" of the initial state.
	private ResourceEvent startingEdge;
	
	/** This extends the DirectedSparseGraph<ProductState, ResourceEvent>
	 * @param productAgentInstance
	 * @param startingNode
	 */
	public EnvironmentModel(ProductAgentInstance productAgentInstance, ProductState currentState) {
		
		this.dummyEmptyNode = new ProductState(null, null, new PhysicalProperty(new Point(18,60))); //random point
		this.currentState = currentState;
		this.startingEdge = new ResourceEvent(null, this.dummyEmptyNode, currentState, null, 0);
		this.addEdge(startingEdge, dummyEmptyNode, currentState);
	}
	

	public void update(DirectedSparseGraph<ProductState, ResourceEvent> systemOutput,
			ProductState currentState) {
		
		//Update both the graph and the current state
		this.update(currentState);
		this.update(systemOutput);
	}
	
	public void update(DirectedSparseGraph<ProductState, ResourceEvent> systemOutput){
		
		//Update directed graph
		for (ResourceEvent event : systemOutput.getEdges()){
			this.addEdge(event,event.getParent(),event.getChild());
		}
		for (ProductState state : systemOutput.getVertices()){
	        this.addVertex(state);
		}
	}
	
	public void update(ProductState currentState){
		addVertex(currentState);
		this.currentState = currentState;
	}
	
	public ProductState getCurrentState() {
		return this.currentState;
	}

	/**
	 * Clears everything except the current node and the dummy first node (edge that indicates current state)
	 */
	public void clear() {
		
		ArrayList<ProductState> removeVertices = new ArrayList<ProductState>();
		ArrayList<ResourceEvent> removeEdges = new ArrayList<ResourceEvent>();
		
		//Find all the vertices to remove
		for (ProductState node : getVertices()){
			// Keep the current and dummy nodes (starting)
			if (!this.currentState.equals(node) && !this.currentState.equals(dummyEmptyNode)){
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
	
	/**
	 * @return if there are any events in the environment model
	 */
	public boolean isEmpty(){
		boolean flag = true;
		
		for (ResourceEvent edge: getEdges()){
			// Keep the starting edge
			if (!this.startingEdge.equals(edge)){
				flag = false;
				break;
			}
		}
		
		return flag;
	}
	
}
