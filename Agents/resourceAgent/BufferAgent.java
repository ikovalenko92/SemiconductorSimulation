package resourceAgent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.collections15.Transformer;

import Buffer.BufferLLC;
import Part.Part;
import Robot.RobotLLC;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import intelligentProduct.ProductAgent;
import repast.simphony.engine.schedule.ScheduledMethod;
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;
import sharedInformation.PhysicalProperty;
import sharedInformation.RASchedule;

public class BufferAgent implements ResourceAgent {

	private BufferLLC buffer;
	private DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> bufferCapabilities;
	private ArrayList<ResourceAgent> neighbors;
	
	private HashMap<ResourceAgent, CapabilitiesNode>  tableNeighborNode;
	private Transformer<CapabilitiesEdge, Integer> weightTransformer;
	private RASchedule schedule;

	public BufferAgent(String name, BufferLLC buffer){
		this.buffer = buffer;
		this.bufferCapabilities = new DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge>();
			
		createOutputGraph();
		//Transformer for shortest path
		this.weightTransformer = new Transformer<CapabilitiesEdge,Integer>(){
	       	public Integer transform(CapabilitiesEdge edge) {return edge.getWeight();}
		};
		
		this.neighbors = new ArrayList<ResourceAgent>();
		
		this.schedule = new RASchedule(this);
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
	public void teamQuery(ProductAgent productAgent, PhysicalProperty desiredProperty, CapabilitiesNode currentNode,
			int currentTime, int maxTime, ArrayList<ResourceAgent> teamList, ArrayList<CapabilitiesEdge> edgeList) {

		new ResourceAgentHelper().teamQuery(this, productAgent, desiredProperty, currentNode,
				currentTime, maxTime, teamList, edgeList, neighbors, tableNeighborNode, bufferCapabilities, weightTransformer);
	}

	@Override
	public RASchedule getSchedule() {
		return this.schedule;
	}

	//================================================================================
    // Product agent scheduling
    //================================================================================
	
	@Override
	public boolean requestScheduleTime(ProductAgent productAgent, int startTime, int endTime) {
		this.schedule.addPA(productAgent, startTime, endTime, true);
		return true;
	}

	@Override
	public boolean removeScheduleTime(ProductAgent productAgent, int startTime) {
		return true;
	}

	//================================================================================
    // Product agent communication
    //================================================================================
	
	/* (non-Javadoc)
	 * @see resourceAgent.ResourceAgent#getCapabilities()
	 */
	@Override
	public DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> getCapabilities() {
		return this.bufferCapabilities;
	}

	/* (non-Javadoc)
	 * @see resourceAgent.ResourceAgent#query(java.lang.String, Part.Part)
	 */
	@Override
	public boolean query(String program, ProductAgent productAgent) {
		// No need to check if the part is on schedule since it's a buffer
		
		//Find the relevant position
		String point = program.substring(1);
		
		String[] tokens = point.split(",");
		
		int x = Integer.parseInt(tokens[0]);
		int y = Integer.parseInt(tokens[1]);
		
		//Obtain the program
		char programType = program.charAt(0);
		
		//Run the corresponding program
		if (programType == 'F'){
			this.buffer.moveFromStorage(productAgent.getPartName(), new Point(x,y));
		}
		else{
			this.buffer.moveToStorage(productAgent.getPartName(), new Point(x,y));
		}
		
		return false;
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
		CapabilitiesNode storageNode = new CapabilitiesNode(this.buffer.getBuffer(), null, storageLocation);  
		
		for (Point enterPoints : this.buffer.getEnterPoints()){
			//Create the node for being at the enter point
			PhysicalProperty enterLocation = new PhysicalProperty(enterPoints);
			CapabilitiesNode enterNode = new CapabilitiesNode(this.buffer.getBuffer(), null, enterLocation);
			
			//Program to move it FROM storage. Format: Fx,y
			CapabilitiesEdge programOutEdge = new CapabilitiesEdge(this, storageNode, enterNode, "F" + enterNode.getLocation().x + "," + enterNode.getLocation().y, 1);
			this.bufferCapabilities.addEdge(programOutEdge, storageNode, enterNode);
			
			//Program to move TO storage. Format: Tx,y
			CapabilitiesEdge programInEdge = new CapabilitiesEdge(this, enterNode, storageNode, "T" + enterNode.getLocation().x + "," + enterNode.getLocation().y, 1);
			this.bufferCapabilities.addEdge(programInEdge, enterNode, storageNode);
		}
	}
	
	/**
	 * Finds at which nodes the neighbors are connected
	 */
	@ScheduledMethod (start = 1, priority = 5000)
	public void findNeighborNodes(){
		this.tableNeighborNode = new HashMap<ResourceAgent, CapabilitiesNode>();
		
		//Fill the look up table that matches the neighbor with the node
		for (ResourceAgent neighbor : this.neighbors){
			for (CapabilitiesNode node : this.bufferCapabilities.getVertices()){
				if(neighbor.getCapabilities().containsVertex(node)){
					//Assume only one node can be shared between neighbors
					this.tableNeighborNode.put(neighbor, node);
				};
			}
		}
	}

}
