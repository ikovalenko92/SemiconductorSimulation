package resourceAgent;


import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.collections15.Transformer;

import Part.Part;
import Robot.RobotLLC;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import intelligentProduct.ProductAgent;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;
import sharedInformation.PhysicalProperty;
import sharedInformation.RASchedule;

public class RobotAgent implements ResourceAgentInterface {
	
	private RobotLLC robot;
	private boolean working;
	private String program;
	
	// Capabilities Graph
	private DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> robotCapabilities;
	private CapabilitiesEdge runningEdge;
	private Transformer<CapabilitiesEdge, Integer> weightTransformer;
	
	//Neighbors
	private ArrayList<ResourceAgentInterface> neighbors;
	private HashMap<ResourceAgentInterface, CapabilitiesNode> tableNeighborNode;
	private HashMap<Point, Object> tableLocationObject;
	
	//Scheduling
	private RASchedule schedule;
	
	/**
	 * @param name
	 * @param tableLocationObject - table of physical points and the corresponding physical object
	 * @param robotLLC
	 */
	public RobotAgent(String name, RobotLLC robot, HashMap<Point, Object> tableLocationObject){
		this.robot = robot;
		this.working = false;
		this.robotCapabilities = new DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge>();
		
		//Edge that is currently running
		this.runningEdge = null;
		//Transformer for shortest path
		this.weightTransformer = new Transformer<CapabilitiesEdge,Integer>(){
	       	public Integer transform(CapabilitiesEdge edge) {return edge.getWeight();}
		};

		this.neighbors = new ArrayList<ResourceAgentInterface>();
		
		//Table of correlations between location and the object at that location
		this.tableLocationObject = tableLocationObject;
		
		createOutputGraph();
	}
	
	@Override
	public String toString() {
		return "Robot Agent for " + this.robot.toString();
	}
	
	//================================================================================
    // Adding/getting neighbors for this resource
    //================================================================================
	
	public void addNeighbor(ResourceAgentInterface neighbor){
		this.neighbors.add(neighbor);
	}
	
	public ArrayList<ResourceAgentInterface> getNeighbors(){
		return this.neighbors;
	}

	//================================================================================
    // Product/resource team formation
    //================================================================================

	@Override
	public void teamQuery(ProductAgent productAgent, PhysicalProperty desiredProperty, CapabilitiesNode currentNode,
			int currentTime, int maxTime, ArrayList<ResourceAgentInterface> teamList) {
		
		new ResourceAgentHelper().teamQuery(this, productAgent, desiredProperty, currentNode,
				currentTime, maxTime, teamList, neighbors, tableNeighborNode, robotCapabilities, weightTransformer);
	}
	
	//================================================================================
    // Product agent scheduling
    //================================================================================
	
	@Override
	public RASchedule getSchedule() {
		return this.schedule;
	}


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
		return this.robotCapabilities;
	}


	@Override
	public boolean query(String program, Part part) {
		// TODO Auto-generated method stub
		return false;
	}

	//================================================================================
    // Helper methods
    //================================================================================
	
	@ScheduledMethod (start = 1, priority = 6000)
	private void createOutputGraph() {
		
		//For each program in the Robot LLC, create an edge 
		for(String program : this.robot.getProgramList()){
			Point[] endpoints = this.robot.getProgramEndpoints(program);
			Point start = endpoints[0];
			Point end = endpoints[1];
			
			//Create the physical property of being in each location
			PhysicalProperty startLocation = new PhysicalProperty(start);
			PhysicalProperty endLocation = new PhysicalProperty(end);
			
			//Create the capability nodes
			CapabilitiesNode startNode = new CapabilitiesNode(this.tableLocationObject.get(startLocation.getPoint()), null, startLocation);  
			CapabilitiesNode endNode = new CapabilitiesNode(this.tableLocationObject.get(endLocation.getPoint()), null, endLocation);
			
			//Estimate the weight (time it takes to move between two points) using the manhattan distance 
			int weight = (Math.abs(start.x-end.x) + Math.abs(start.y-end.y))/this.robot.getRobot().getVelocity();
			
			//Create and add the edge to the capabilities
			CapabilitiesEdge programEdge = new CapabilitiesEdge(this, startNode, endNode, program, weight);
			this.robotCapabilities.addEdge(programEdge, startNode, endNode);
		}
	}
	
	/**
	 * Finds at which nodes the neighbors are connected
	 */
	@ScheduledMethod (start = 1, priority = 5000)
	public void findNeighborNodes(){
		this.tableNeighborNode = new HashMap<ResourceAgentInterface, CapabilitiesNode>();
		
		//Fill the look up table that matches the neighbor with the node
		for (ResourceAgentInterface neighbor : this.neighbors){
			for (CapabilitiesNode node : this.robotCapabilities.getVertices()){
				if(neighbor.getCapabilities().containsVertex(node)){
					//Assume only one node can be shared between neighbors
					this.tableNeighborNode.put(neighbor, node);
				};
				
			}
		}
	}

	//================================================================================
    // Testing
    //================================================================================
}