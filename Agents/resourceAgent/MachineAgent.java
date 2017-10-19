package resourceAgent;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.collections15.Transformer;

import Machine.MachineLLC;
import Part.Part;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import intelligentProduct.ProductAgent;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;
import sharedInformation.PhysicalProperty;
import sharedInformation.RASchedule;

public class MachineAgent implements ResourceAgent{

	private String name;
	private MachineLLC machine;
	private boolean working;
	private int program;
	
	private CapabilitiesEdge runningEdge;
	private DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> machineCapabilities;
	
	private ArrayList<ResourceAgent> neighbors;
	private HashMap<ResourceAgent, CapabilitiesNode> tableNeighborNode;
	private Transformer<CapabilitiesEdge, Integer> weightTransformer;
	private RASchedule schedule;

	/**
	 * @param name
	 * @param Machine LLC
	 */
	public MachineAgent(String name, MachineLLC machine){
		this.name = name;
		this.machine = machine;
		this.working = false;
		this.machineCapabilities = new DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge>();
		
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
		return "MachineAgent for " + this.machine.toString();
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
				currentTime, maxTime, teamList, edgeList, neighbors, tableNeighborNode, machineCapabilities, weightTransformer);
	}
	
	//================================================================================
    // Product agent scheduling
    //================================================================================

	@Override
	public RASchedule getSchedule() {
		return this.schedule;
	}

	@Override
	public boolean requestScheduleTime(ProductAgent productAgent, int startTime, int endTime) {
		return this.schedule.addPA(productAgent, startTime, endTime,false);
	}	

	@Override
	public boolean removeScheduleTime(ProductAgent productAgent, int startTime) {
		return this.removeScheduleTime(productAgent, startTime);
	}
	
	//================================================================================
    // Product agent communication
    //================================================================================
	
	@Override
	public DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> getCapabilities() {
		return this.machineCapabilities;
	}
	
	@Override
	public boolean query(String program, ProductAgent productAgent) {
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		double startTime = schedule.getTickCount();
		
		//Find the desired edge
		CapabilitiesEdge desiredEdge = null;
		for (CapabilitiesEdge edge : this.getCapabilities().getEdges()){
			if (edge.getActiveMethod().equals(program)){
				desiredEdge = edge;
				break;
			}
		}
		
		//If the product agent is scheduled for this time, run the desired program
		if (desiredEdge!=null &&  this.schedule.checkPATime(productAgent, (int) startTime, (int) startTime+desiredEdge.getWeight())){
			if (desiredEdge.getActiveMethod() == "Hold"){
				this.machine.doNothing();
				informPA(productAgent, desiredEdge);
				return true;
			}
			else if (desiredEdge.getActiveMethod() == "Reset"){
				return false;
			}
			else{
				if (this.machine.runProgram(desiredEdge.getActiveMethod())){
					informPA(productAgent, desiredEdge);
					return true;
				}
			}
		}		
		return false;
	}
	
	/** Check when the edge is done and inform the product agent
	 * @param productAgent
	 * @param edge
	 */
	private void informPA(ProductAgent productAgent, CapabilitiesEdge edge){
		//Using the edge of the weight (might need to check with Robot LLC in the future)
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount()+edge.getWeight()), productAgent,
				"informEvent", new Object[]{edge});
		
		// If this is not a hold method (i.e. one of the programs was run)
		// Inform the product agent that the machine was "reset" and new programs can be run on it
		if (!(edge.getActiveMethod() == "Hold")){
			for (CapabilitiesEdge resetEdge : this.machineCapabilities.getIncidentEdges(edge.getChild())){
				if (resetEdge.getActiveMethod()=="Reset"){
					schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount()+edge.getWeight()), productAgent,
							"informEvent", new Object[]{resetEdge});
				}
			}
			
		}
	}
	
	//================================================================================
    // Helper methods
    //================================================================================
	
	/**
	 * Creates a graph that represents the capabilities of this machine
	 */
	private void createOutputGraph() {
		//Node representing the part located in the CNC
		PhysicalProperty machineLocation = new PhysicalProperty(this.machine.getLocation());
		CapabilitiesNode startNode = new CapabilitiesNode(this.machine.getMachine(), null, machineLocation);
		
		//Create the self-loop to hold it at this cnc
		CapabilitiesEdge selfEdge = new CapabilitiesEdge(this, startNode, startNode, "Hold", 1);
		this.machineCapabilities.addEdge(selfEdge, startNode, startNode);	
		
		//Nodes that represent machining operations performed on the part
		for (String program : this.machine.getProgramList()){
			//Create the node representing this program being complete
			PhysicalProperty programName = new PhysicalProperty(program.substring(program.length()-2));
			CapabilitiesNode programNode = new CapabilitiesNode(this.machine.getMachine(), programName, machineLocation);
		
			//Create the edge to go to this node
			CapabilitiesEdge programEdge = new CapabilitiesEdge(this, startNode, programNode, program, this.machine.getProgramTime(program));
			this.machineCapabilities.addEdge(programEdge, startNode, programNode);
			
			//Create the edge to come back to the original position
			CapabilitiesEdge programReset = new CapabilitiesEdge(this, programNode, startNode, "Reset", 0);
			this.machineCapabilities.addEdge(programReset, programNode, startNode);
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
			for (CapabilitiesNode node : this.machineCapabilities.getVertices()){
				if (neighbor.getCapabilities().containsVertex(node)){
					//Assume only one node can be shared between neighbors
					this.tableNeighborNode.put(neighbor, node);
				};
			}
		}
	}
	
	//================================================================================
    // Testing
    //================================================================================
	
	/*@ScheduledMethod (start = 30)
	public void testRobot(){
		this.machine.runProgram(this.machine.getProgramList().remove(0));
	}*/
}