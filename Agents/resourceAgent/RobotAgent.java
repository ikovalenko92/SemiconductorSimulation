package resourceAgent;


import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.collections15.Transformer;

import Part.Part;
import Robot.RobotLLC;
import bsh.This;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import intelligentProduct.ProductAgent;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import sharedInformation.ResourceEvent;
import sharedInformation.ProductState;
import sharedInformation.PhysicalProperty;
import sharedInformation.RASchedule;

public class RobotAgent implements ResourceAgent {
	
	private RobotLLC robot;
	private boolean working;
	private String program;
	
	// Capabilities Graph
	private DirectedSparseGraph<ProductState, ResourceEvent> robotCapabilities;
	private ResourceEvent runningEdge;
	private Transformer<ResourceEvent, Integer> weightTransformer;
	
	//Neighbors
	private ArrayList<ResourceAgent> neighbors;
	private HashMap<ResourceAgent, ProductState> tableNeighborNode;
	private HashMap<Point, Object> tableLocationObject;
	
	//Scheduling
	private RASchedule RAschedule;
	

	/**
	 * @param name
	 * @param robot
	 * @param tableLocationObject - Table of correlations between location and the object at that location (for robot neighbors)
	 */
	public RobotAgent(String name, RobotLLC robot, HashMap<Point, Object> tableLocationObject){
		this.robot = robot;
		this.working = false;
		this.robotCapabilities = new DirectedSparseGraph<ProductState, ResourceEvent>();
		
		//Edge that is currently running
		this.runningEdge = null;
		//Transformer for shortest path
		this.weightTransformer = new Transformer<ResourceEvent,Integer>(){
	       	public Integer transform(ResourceEvent edge) {return edge.getEventTime();}
		};

		this.neighbors = new ArrayList<ResourceAgent>();
		
		//Table of correlations between location and the object at that location
		this.tableLocationObject = tableLocationObject;
		
		createOutputGraph();
		
		this.RAschedule = new RASchedule(this);
	}
	
	/*@ScheduledMethod ( start = 1 , interval = 1, priority = -50000)
	public void adf(){
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		double startTime = schedule.getTickCount();
		if (this.toString().contains("M12")){
			System.out.println(this.getSchedule()+ ""+startTime);}
	}*/
	
	@Override
	public String toString() {
		return "Robot Agent for " + this.robot.toString();
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
				currentTime, this, neighbors, tableNeighborNode, robotCapabilities, weightTransformer);
	}
	
	//================================================================================
    // Product agent scheduling
    //================================================================================
	
	@Override
	public RASchedule getSchedule() {
		return this.RAschedule;
	}

	@Override
	public boolean requestScheduleTime(ProductAgent productAgent, ResourceEvent edge, int startTime, int endTime) {
		int edgeOffset = edge.getEventTime() - this.getCapabilities().findEdge(edge.getParent(),edge.getChild()).getEventTime();
		return this.RAschedule.addPA(productAgent, startTime+edgeOffset, endTime, false);
	}


	@Override
	public boolean removeScheduleTime(ProductAgent productAgent, int startTime, int endTime) {
		return this.RAschedule.removePA(productAgent, startTime, endTime);
	}
	
	//================================================================================
    // Product agent communication
    //================================================================================

	/**
	 * @return
	 */
	@Override
	public DirectedSparseGraph<ProductState, ResourceEvent> getCapabilities() {
		return this.robotCapabilities;
	}

	@Override
	public boolean query(ResourceEvent queriedEdge, ProductAgent productAgent) {
		//Find the desired edge
		ResourceEvent desiredEdge = null;
		for (ResourceEvent robotEdge : this.getCapabilities().getEdges()){
			if (robotEdge.getActiveMethod().equals(queriedEdge.getActiveMethod())){
				desiredEdge = robotEdge;
				break;
			}
		}
		
		//Find the offset between the queried edge and when the actual program should be run
		int edgeOffset = queriedEdge.getEventTime() - this.getCapabilities().findEdge(queriedEdge.getParent(),queriedEdge.getChild()).getEventTime();
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		double startTime = schedule.getTickCount()+edgeOffset;
		
		//If the product agent is scheduled for this time, run the desired program at that time;
		if (desiredEdge!=null &&  this.RAschedule.checkPATime(productAgent, (int) startTime, (int) startTime+desiredEdge.getEventTime())){
			//Schedule it for the future
			schedule.schedule(ScheduleParameters.createOneTime(startTime), 
					this.robot, "runMoveObjectProgram", new Object[]{queriedEdge.getActiveMethod(),productAgent.getPartName()});
			this.informPA(productAgent, queriedEdge);
			return true;
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
		schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount()+edge.getEventTime()), productAgent,
				"informEvent", new Object[]{edge});
	}

	//================================================================================
    // Helper methods
    //================================================================================
	
	/**
	 * Helper method to create the capabilities graph for the part
	 */
	@ScheduledMethod (start = 1, priority = 6000)
	private void createOutputGraph() {
		
		//For each program in the Robot LLC, create an edge 
		for(String program : this.robot.getProgramList()){
			Point[] endpoints = this.robot.getProgramEndpoints(program);
			Point start = endpoints[0];
			Point end = endpoints[1];
			Point center = this.robot.getRobot().getCenter();
			
			//Create the physical property of being in each location
			PhysicalProperty startLocation = new PhysicalProperty(start);
			PhysicalProperty endLocation = new PhysicalProperty(end);
			
			//Create the capability nodes
			ProductState startNode = new ProductState(this.tableLocationObject.get(startLocation.getPoint()), null, startLocation);  
			ProductState endNode = new ProductState(this.tableLocationObject.get(endLocation.getPoint()), null, endLocation);
			
			//Estimate the weight (time it takes to move between two points) using the manhattan distance.
			//Multiplier to give more time to the robot to perform the action
			int distTravel = (int) (Math.abs(center.x-start.x) + Math.abs(center.y-start.y) + Math.abs(start.x-end.x) + Math.abs(start.y-end.y)
					+ Math.abs(center.x-end.x) + Math.abs(center.y-end.y));
			int pickPlaceOffset = 14;
			int weight = (int) ((distTravel)/this.robot.getRobot().getVelocity()) + pickPlaceOffset;
			
			//Create and add the edge to the capabilities
			ResourceEvent programEdge = new ResourceEvent(this, startNode, endNode, program, weight);
			this.robotCapabilities.addEdge(programEdge, startNode, endNode);
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
			for (ProductState node : this.robotCapabilities.getVertices()){
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