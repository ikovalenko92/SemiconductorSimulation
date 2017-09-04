package resourceAgent;


import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.collections15.Transformer;

import Robot.RobotLLC;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import intelligentProduct.ProductAgent;
import repast.simphony.engine.schedule.ScheduledMethod;
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
	private Transformer<CapabilitiesEdge, Integer> weightTransformer;
	
	private ArrayList<ResourceAgent> neighbors;
	private HashMap<ResourceAgent, CapabilitiesNode> tableNeighborNode;
	
	/**
	 * @param name
	 * @param robotLLC
	 * @param neighbors
	 */
	public RobotAgent(String name, RobotLLC robot, ArrayList<ResourceAgent> neighbors){
		this.robot = robot;
		this.working = false;
		this.robotCapabilities = new DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge>();
		
		//Edge that is currently running
		this.runningEdge = null;
		//Transformer for shortest path
		this.weightTransformer = new Transformer<CapabilitiesEdge,Integer>(){
	       	public Integer transform(CapabilitiesEdge edge) {return edge.getWeight();}
		};
		
		this.neighbors = neighbors;
		
		createOutputGraph();
	}
	
	@Override
	public String toString() {
		return "Robot Agent for " + this.robot.toString();
	}

	//================================================================================
    // Product/resource team formation
    //================================================================================

	@Override
	public void teamQuery(ProductAgent productAgent, PhysicalProperty desiredProperty, CapabilitiesNode currentNode,
			int currentTime, int maxTime, ArrayList<ResourceAgent> teamList) {
		
		ArrayList<ResourceAgent> newTeamList = new ArrayList<ResourceAgent>(teamList);
		
		DijkstraShortestPath<CapabilitiesNode, CapabilitiesEdge> shortestPathGetter = 
				new DijkstraShortestPath<CapabilitiesNode, CapabilitiesEdge>(this.robotCapabilities, this.weightTransformer);
		shortestPathGetter.reset();
		
		// If the desired property is a point
		if (desiredProperty.getPoint() != null){
			for (CapabilitiesNode vertex : this.robotCapabilities.getVertices()){
				for(PhysicalProperty vertexProperty : vertex.getPhysicalProperties()){
					
					//If the location fits the desired property
					if (vertexProperty.equals(desiredProperty)){
						//Find the shortest path
						List<CapabilitiesEdge> shortestPathCandidateList = shortestPathGetter.getPath(currentNode, vertex);
						
						//Calculate the bid
						int bidTime = currentTime;
						for (CapabilitiesEdge pathNode : shortestPathCandidateList){
							bidTime = bidTime + pathNode.getWeight();
						}
						
						//Submit the bid to the product agent
						if (bidTime < maxTime){
							newTeamList.add(this); // Add to the team
							productAgent.submitBid(teamList, bidTime);
						}
					}
				}
			}
		}
		
		//If it's not a point, then push this bid to neighbors
		for (ResourceAgent neighbor: this.neighbors){
			
			//If the neighbor isn't part of the team
			if (!teamList.contains(neighbor)){
				newTeamList.clear();
				newTeamList.addAll(teamList);
				
				CapabilitiesNode neighborNode = this.tableNeighborNode.get(neighbor);
				
				//Find the shortest path
				List<CapabilitiesEdge> shortestPathCandidateList = shortestPathGetter.getPath(currentNode, neighborNode);
				
				//Calculate the bid
				int bidTime = currentTime;
				for (CapabilitiesEdge pathNode : shortestPathCandidateList){
					bidTime = bidTime + pathNode.getWeight();
				}
				
				//Push the bid to the resource agent
				if (bidTime < maxTime){
					newTeamList.add(this); // Add to the team
					neighbor.teamQuery(productAgent, desiredProperty, neighborNode, bidTime, maxTime, newTeamList);
				}
			}
		}
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
	
	/**
	 * Finds at which nodes the neighbors are connected
	 */
	@ScheduledMethod (start = 1, priority = 5000)
	public void findNeighborNodes(){
		this.tableNeighborNode = new HashMap<ResourceAgent, CapabilitiesNode>();
		
		//Fill the look up table that matches the neighbor with the node
		for (ResourceAgent neighbor : this.neighbors){
			for (CapabilitiesNode node : this.robotCapabilities.getVertices()){
				neighbor.getCapabilities().containsVertex(node);
				//Assume only one node can be shared between neighbors
				this.tableNeighborNode.put(neighbor, node);
			}
		}
	}

	//================================================================================
    // Testing
    //================================================================================
}