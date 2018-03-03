package intelligentProduct;

import java.awt.Point;
import java.util.ArrayList;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import sharedInformation.PhysicalProperty;
import sharedInformation.ProductState;
import sharedInformation.ResourceEvent;

public class ProductHistory  extends DirectedSparseGraph<ProductState, ResourceEvent>{

	private static final long serialVersionUID = 1L; //Eclipse wanted me to add this
	
	private ProductState currentState = null;
	private ProductState dummyEmptyNode; // Empty node needed to represent the "parent" of the initial state.
	private ResourceEvent startingEdge;
	private ArrayList<ResourceEvent> occurredEvents;

	public ProductHistory(ProductAgentInstance productAgentInstance,
			ProductState currentState) {

		this.dummyEmptyNode = new ProductState(null, null, new PhysicalProperty(new Point(0,0)));
		this.currentState = currentState;
		this.startingEdge = new ResourceEvent(null, dummyEmptyNode, currentState, null, 0);
		this.addEdge(startingEdge, dummyEmptyNode, currentState);
		
		this.occurredEvents = new ArrayList<ResourceEvent>();
		this.occurredEvents.add(startingEdge);
	}
	
	public void update(DirectedSparseGraph<ProductState, ResourceEvent> systemOutput,
			ProductState currentState, ArrayList<ResourceEvent> occuredEvents) {
		
		//Update both the graph and the current state
		this.update(currentState);
		this.update(systemOutput, occuredEvents);
		
	}
	
	public void update(DirectedSparseGraph<ProductState, ResourceEvent> systemOutput,
			ArrayList<ResourceEvent> occuredEvents){
		
		//Update directed graph
		for (ResourceEvent event : systemOutput.getEdges()){
			this.addEdge(event,event.getParent(),event.getChild());
		}
		for (ProductState state : systemOutput.getVertices()){
	        this.addVertex(state);
		}
		
		//Update list of occurred events
		for (ResourceEvent occurredEvent : occuredEvents){
			this.occurredEvents.add(occurredEvent);
		}
	}
	
	public void update(ProductState currentState){
		addVertex(currentState);
		this.currentState = currentState;
	}
	
	public ProductState getCurrentState() {
		return this.currentState;
	}
	
	public ArrayList<ResourceEvent> getOccurredEvents(){
		return this.occurredEvents;
	}

	public ResourceEvent getLastEvent() {
		return this.occurredEvents.get(this.occurredEvents.size()-1);
	}

}
