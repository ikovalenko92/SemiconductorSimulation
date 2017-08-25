package resourceAgent;


import java.awt.Point;
import java.lang.reflect.Method;
import java.util.ArrayList;

import Robot.RobotLLC;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import intelligentProduct.ProductAgent;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import repast.simphony.ui.probe.ProbedProperty;
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;
import sharedInformation.PhysicalProperty;
import sharedInformation.RASchedule;
import sharedInformation.ResourceAgent;

public class RobotAgent implements ResourceAgent {
	
	private RobotLLC robot;
	private boolean working;
	private String program;
	private DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> robotCapabilities;
	private CapabilitiesEdge runningEdge;
	private ArrayList<ResourceAgent> neighbors;


	/**
	 * @param name
	 * @param robotLLC
	 * @param neighbors
	 */
	public RobotAgent(String name, RobotLLC robot, ArrayList<ResourceAgent> neighbors){
		this.robot = robot;
		this.working = false;
		this.robotCapabilities = new DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge>();
		
		this.runningEdge = null;
		
		this.neighbors = neighbors;
		
		createOutputGraph();
	}
	
	@Override
	public String toString() {
		return "Robot Agent for " + this.robot.toString();
	}

	//================================================================================
    // Product agent communication
    //================================================================================

	@Override
	public DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> getCapabilities() {
		return this.robotCapabilities;
	}


	@Override
	public boolean query(String program) {
		// TODO Auto-generated method stub
		return false;
	}

	//================================================================================
    // Product agent scheduling
    //================================================================================
	
	@Override
	public RASchedule getSchedule() {
		// TODO Auto-generated method stub
		return null;
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
    // Product/resource team formation
    //================================================================================

	@Override
	public void teamQuery(ProductAgent productAgent, PhysicalProperty desiredProperty, int currentTime, int maxTime,
			ArrayList<ResourceAgent> teamList) {
		// TODO Auto-generated method stub
		
	}
	
	//================================================================================
    // Helper methods
    //================================================================================
	
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
			CapabilitiesNode startNode = new CapabilitiesNode(this.robot.getRobot(), null, startLocation);  
			CapabilitiesNode endNode = new CapabilitiesNode(this.robot.getRobot(), null, endLocation);
			
			//Estimate the weight (time it takes to move between two points) using the manhattan distance 
			int weight = Math.abs(start.x-end.x) + Math.abs(start.y-end.y);
			
			//Create and add the edge to the capabilities
			CapabilitiesEdge programEdge = new CapabilitiesEdge(this, startNode, endNode, program, weight);
			this.robotCapabilities.addEdge(programEdge, startNode, endNode);
		}
	}

	//================================================================================
    // Testing
    //================================================================================
}