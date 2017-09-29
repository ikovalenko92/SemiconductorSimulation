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
import sharedInformation.ResourceAgent;

public class BufferAgent implements ResourceAgent {

	private BufferLLC buffer;
	private DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> bufferCapabilities;
	private ArrayList<ResourceAgent> neighbors;
	
	private HashMap<ResourceAgent, CapabilitiesNode>  tableNeighborNode;
	private Transformer<CapabilitiesEdge, Integer> weightTransformer;

	public BufferAgent(String name, BufferLLC buffer){
		this.buffer = buffer;
		this.bufferCapabilities = new DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge>();
			
		createOutputGraph();
		//Transformer for shortest path
		this.weightTransformer = new Transformer<CapabilitiesEdge,Integer>(){
	       	public Integer transform(CapabilitiesEdge edge) {return edge.getWeight();}
		};
		
		this.neighbors = new ArrayList<ResourceAgent>();
	}
	
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
			int currentTime, int maxTime, ArrayList<ResourceAgent> teamList) {

		new ResourceAgentHelper().teamQuery(this, productAgent, desiredProperty, currentNode,
				currentTime, maxTime, teamList, neighbors, tableNeighborNode, bufferCapabilities, weightTransformer);
	}

	@Override
	public RASchedule getSchedule() {
		// TODO Auto-generated method stub
		return null;
	}

	//================================================================================
    // Product agent scheduling
    //================================================================================
	
	@Override
	public boolean requestScheduleTime(ProductAgent productAgent, int startTime) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeScheduleTime(ProductAgent productAgent, int startTime) {
		// TODO Auto-generated method stub
		return false;
	}

	//================================================================================
    // Product agent communication
    //================================================================================
	
	@Override
	public DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> getCapabilities() {
		return this.bufferCapabilities;
	}

	@Override
	public boolean query(String program, Part part) {
		// TODO Auto-generated method stub
		char programType = program.charAt(0);
		String point = program.substring(1);
		
		String[] tokens = point.split(",");
		
		return false;
	}
	
	//================================================================================
    // Helper methods
    //================================================================================
	
	private void createOutputGraph() {
		
		//Create the physical property of being in storage
		PhysicalProperty storageLocation = new PhysicalProperty(this.buffer.getStoragePoint());
		CapabilitiesNode storageNode = new CapabilitiesNode(this.buffer.getBuffer(), null, storageLocation);  
		
		for (Point enterPoints : this.buffer.getEnterPoints()){
			//Create the node for being at the enter point
			PhysicalProperty enterLocation = new PhysicalProperty(enterPoints);
			CapabilitiesNode enterNode = new CapabilitiesNode(this.buffer.getBuffer(), null, enterLocation);
			
			//Program to move it FROM storage. Format: Rx,y
			CapabilitiesEdge programOutEdge = new CapabilitiesEdge(this, storageNode, enterNode, "F" + enterNode.getLocation().x + "," + enterNode.getLocation().y, 1);
			this.bufferCapabilities.addEdge(programOutEdge, storageNode, enterNode);
			
			//Program to move TO from storage. Format: Sx,y
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
				neighbor.getCapabilities().containsVertex(node);
				//Assume only one node can be shared between neighbors
				this.tableNeighborNode.put(neighbor, node);
			}
		}
	}

}
