package resourceAgent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.collections15.Transformer;

import Buffer.BufferLLC;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import intelligentProduct.ProductAgent;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import sharedInformation.ResourceEvent;
import sharedInformation.ProductState;
import sharedInformation.PhysicalProperty;
import sharedInformation.RASchedule;

public class BufferAgent implements ResourceAgent {

	private BufferLLC buffer;
	private DirectedSparseGraph<ProductState, ResourceEvent> bufferCapabilities;
	private ArrayList<ResourceAgent> neighbors;
	
	private HashMap<ResourceAgent, ProductState>  tableNeighborNode;
	private Transformer<ResourceEvent, Integer> weightTransformer;
	private RASchedule RAschedule;

	public BufferAgent(String name, BufferLLC buffer){
		this.buffer = buffer;
		this.bufferCapabilities = new DirectedSparseGraph<ProductState, ResourceEvent>();
			
		createOutputGraph();
		//Transformer for shortest path
		this.weightTransformer = new Transformer<ResourceEvent,Integer>(){
	       	public Integer transform(ResourceEvent edge) {return edge.getEventTime();}
		};
		
		this.neighbors = new ArrayList<ResourceAgent>();
		
		this.RAschedule = new RASchedule(this);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BufferAgent for " + this.buffer.toString();
	}
	
	//================================================================================
    // Adding/getting neighbors for this resource
    //================================================================================
	
	public void addNeighbor(ResourceAgent neighbor){
		this.neighbors.add(neighbor);
	}
	
	public ArrayList<ResourceAgent> getNeighbors(){
		return this.neighbors;
	}

	//================================================================================
    // Product/resource team formation
    //================================================================================
	

	@Override
	public void teamQuery(ProductAgent productAgent, PhysicalProperty desiredProperty, ProductState currentNode, 
			int maxTime, DirectedSparseGraph<ProductState,ResourceEvent> bid, int currentTime) {
		
		new ResourceAgentHelper().teamQuery(productAgent, desiredProperty, currentNode, maxTime, bid,
				currentTime, this, neighbors, tableNeighborNode, bufferCapabilities, weightTransformer);
	}

	//================================================================================
    // Product agent scheduling
    //================================================================================
	
	
	@Override
	public RASchedule getSchedule() {
		return this.RAschedule;
	}
	
	@Override
	public boolean requestScheduleTime(ProductAgent productAgent,ResourceEvent edge, int startTime, int endTime) {
		//int edgeOffset = edge.getWeight() - this.getCapabilities().findEdge(edge.getParent(),edge.getChild()).getWeight();
		return true;
	}

	@Override
	public boolean removeScheduleTime(ProductAgent productAgent, int startTime, int endTime) {
		return true;
	}

	//================================================================================
    // Product agent communication
    //================================================================================
	
	/* (non-Javadoc)
	 * @see resourceAgent.ResourceAgent#getCapabilities()
	 */
	@Override
	public DirectedSparseGraph<ProductState, ResourceEvent> getCapabilities() {
		return this.bufferCapabilities;
	}

	/* (non-Javadoc)
	 * @see resourceAgent.ResourceAgent#query(java.lang.String, Part.Part)
	 */
	@Override
	public boolean query(ResourceEvent queriedEdge, ProductAgent productAgent) {
		// No need to check if the part is on RAschedule since it's a buffer
		String program = queriedEdge.getActiveMethod();
		
		//If the queried program is true, 
		if (program == "End"){
			return true;
		}
		
		//Find the desired edge
		ResourceEvent desiredEdge = null;
		for (ResourceEvent edge : this.getCapabilities().getEdges()){
			if (edge.getActiveMethod().equals(program)){
				desiredEdge = edge;
				break;
			}
		}
		
		//Find the relevant position
		String point = program.substring(1);
		
		String[] tokens = point.split(",");
		
		int x = Integer.parseInt(tokens[0]);
		int y = Integer.parseInt(tokens[1]);
		
		//Obtain the program
		char programType = program.charAt(0);
		
		//Run the corresponding program
		if (programType == 'F'){
			if (this.buffer.moveFromStorage(productAgent.getPartName(), new Point(x,y)) == true){
				//Let the part know that the edge is done
				this.informPA(productAgent, desiredEdge);
				return true;
			}
			
		}
		else if (programType == 'T'){
			if (this.buffer.moveToStorage(productAgent.getPartName(), new Point(x,y))){
				//Let the part know that the edge is done
				this.informPA(productAgent, desiredEdge);
				return true;
			}
		}
		
		return false;
	}
	
	/** Check when the edge is done and inform the product agent
	 * @param productAgent
	 * @param edge
	 */
	private void informPA(ProductAgent productAgent, ResourceEvent edge){
		//Using the edge of the weight (might need to check with Robot LLC in the future)
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount()+edge.getEventTime()), productAgent, "informEvent", new Object[]{edge});
	}
	
	//================================================================================
    // Helper methods
    //================================================================================
	
	/**
	 * Helper method to create the capabilities graph for the part
	 */
	private void createOutputGraph() {
		
		//Create the physical property of being in storage
		PhysicalProperty storageLocation = new PhysicalProperty(this.buffer.getStoragePoint());
		ProductState storageNode = new ProductState(this.buffer.getBuffer(), null, storageLocation);  
		
		for (Point enterPoints : this.buffer.getEnterPoints()){
			//Create the node for being at the enter point
			PhysicalProperty enterLocation = new PhysicalProperty(enterPoints);
			ProductState enterNode = new ProductState(this.buffer.getBuffer(), null, enterLocation);
			
			//Program to move it FROM storage. Format: Fx,y
			ResourceEvent programOutEdge = new ResourceEvent(this, storageNode, enterNode, "F" + enterNode.getLocation().x + "," + enterNode.getLocation().y, 1);
			this.bufferCapabilities.addEdge(programOutEdge, storageNode, enterNode);
			
			//Program to move TO storage. Format: Tx,y
			ResourceEvent programInEdge = new ResourceEvent(this, enterNode, storageNode, "T" + enterNode.getLocation().x + "," + enterNode.getLocation().y, 1);
			this.bufferCapabilities.addEdge(programInEdge, enterNode, storageNode);
		}
	}
	
	/**
	 * Finds at which nodes the neighbors are connected
	 */
	@ScheduledMethod (start = 1, priority = 5000)
	public void findNeighborNodes(){
		this.tableNeighborNode = new HashMap<ResourceAgent, ProductState>();
		
		//Fill the look up table that matches the neighbor with the node
		for (ResourceAgent neighbor : this.neighbors){
			for (ProductState node : this.bufferCapabilities.getVertices()){
				if(neighbor.getCapabilities().containsVertex(node)){
					//Assume only one node can be shared between neighbors
					this.tableNeighborNode.put(neighbor, node);
				};
			}
		}
	}

}
