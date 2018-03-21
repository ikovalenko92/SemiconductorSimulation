package resourceAgent;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.collections15.Transformer;

import Machine.MachineLLC;
import Part.Part;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import groovy.ui.SystemOutputInterceptor;
import intelligentProduct.ProductAgent;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import sharedInformation.ResourceEvent;
import sharedInformation.ProductState;
import sharedInformation.PhysicalProperty;
import sharedInformation.RASchedule;

public class MachineAgent implements ResourceAgent{

	private String name;
	private MachineLLC machine;
	private boolean working;
	private int program;
	
	private ResourceEvent runningEdge;
	private DirectedSparseGraph<ProductState, ResourceEvent> machineCapabilities;
	
	private ArrayList<ResourceAgent> neighbors;
	private HashMap<ResourceAgent, ProductState> tableNeighborNode;
	private Transformer<ResourceEvent, Integer> weightTransformer;
	private RASchedule RAschedule;

	/**
	 * @param name
	 * @param Machine LLC
	 */
	public MachineAgent(String name, MachineLLC machine){
		this.name = name;
		this.machine = machine;
		this.working = false;
		this.machineCapabilities = new DirectedSparseGraph<ProductState, ResourceEvent>();
		
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
	public void teamQuery(ProductAgent productAgent, PhysicalProperty desiredProperty, ProductState currentNode, 
			int maxTime, DirectedSparseGraph<ProductState,ResourceEvent> bid, int currentTime) {
		
		if (this.machine.getTimeLeft()>30000){
			ResourceAgentHelper rah = new ResourceAgentHelper();
			
			DirectedSparseGraph<ProductState, ResourceEvent> machineCapabilitiesBroken = rah.copyGraph(machineCapabilities);
			
			for (ResourceEvent edge:machineCapabilitiesBroken.getEdges()){
				if (!(edge.getActiveMethod() == "Reset" || edge.getActiveMethod() == "Hold")){
					edge.setWeight(this.machine.getTimeLeft());
				}
			}
			
			rah.teamQuery(productAgent, desiredProperty, currentNode, maxTime, bid,
			currentTime, this, neighbors, tableNeighborNode, machineCapabilitiesBroken, weightTransformer);
		}
		
		else{
		new ResourceAgentHelper().teamQuery(productAgent, desiredProperty, currentNode, maxTime, bid,
				currentTime, this, neighbors, tableNeighborNode, machineCapabilities, weightTransformer);
		}
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
		
		int edgeOffset = edge.getEventTime() - this.getCapabilities().findEdge(edge.getParent(),edge.getChild()).getEventTime();

		if (edge.getActiveMethod() == "Reset"){
				endTime = startTime+edgeOffset;
		}
		
		return this.RAschedule.addPA(productAgent, startTime+edgeOffset, endTime, false);
	}	

	@Override
	public boolean removeScheduleTime(ProductAgent productAgent, int startTime, int endTime) {
		return this.RAschedule.removePA(productAgent, startTime, endTime);
	}
	
	//================================================================================
    // Product agent communication
    //================================================================================
	
	@Override
	public DirectedSparseGraph<ProductState, ResourceEvent> getCapabilities() {
		return this.machineCapabilities;
	}
	
	@Override
	public boolean query(ResourceEvent queriedEdge, ProductAgent productAgent) {

		//Find the desired edge
		ResourceEvent desiredEdge = null;
		for (ResourceEvent edge : this.getCapabilities().getEdges()){
			if (edge.getActiveMethod().equals(queriedEdge.getActiveMethod())){
				desiredEdge = edge;
				break;
			}
		}
		
		//Find the offset between the queried edge and when the actual program should be run
		int edgeOffset = queriedEdge.getEventTime() - this.getCapabilities().findEdge(queriedEdge.getParent(),queriedEdge.getChild()).getEventTime();
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		double startTime = schedule.getTickCount()+edgeOffset;
		
		//If the product agent is scheduled for this time, run the desired program
		if (desiredEdge!=null &&  this.RAschedule.checkPATime(productAgent, (int) startTime, (int) startTime+desiredEdge.getEventTime())){
			if (desiredEdge.getActiveMethod() == "Hold"){
				this.machine.doNothing();
				informPA(productAgent, desiredEdge);
				return true;
			}
			else if (desiredEdge.getActiveMethod() == "Reset"){
				this.machine.doNothing();
				informPA(productAgent, desiredEdge);
				return true;
			}
			else{
				//Schedule it for the future
				schedule.schedule(ScheduleParameters.createOneTime(startTime), 
						this.machine, "runProgram", new Object[]{queriedEdge.getActiveMethod(),productAgent.getPartName()});
				this.informPA(productAgent, queriedEdge);
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
		
		DirectedSparseGraph<ProductState, ResourceEvent> systemOutput = new DirectedSparseGraph<ProductState, ResourceEvent>();
		ArrayList<ResourceEvent> occuredEvents = new ArrayList<ResourceEvent>();

		
		if (!(edge.getActiveMethod() == "Hold")){
			systemOutput.addEdge(edge, edge.getParent(),edge.getChild());
			occuredEvents.add(edge);
			
			for (ResourceEvent edgeResetFind:this.machineCapabilities.getIncidentEdges(edge.getChild())){
				if (edgeResetFind.getActiveMethod().contains("Reset")){
					systemOutput.addEdge(edgeResetFind, edgeResetFind.getParent(),edgeResetFind.getChild());
					occuredEvents.add(edgeResetFind);
				}
			}
			
			schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount()+edge.getEventTime()), productAgent,
					"informEvent", new Object[]{systemOutput,occuredEvents.get(occuredEvents.size()-1).getChild(),occuredEvents});
		}
		else{
			systemOutput.addEdge(edge, edge.getParent(),edge.getChild());
			occuredEvents.add(edge);
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
		ProductState startNode = new ProductState(this.machine.getMachine(), null, machineLocation);
		
		//Create the self-loop to hold it at this cnc
		ResourceEvent selfEdge = new ResourceEvent(this, startNode, startNode, "Hold", 1);
		this.machineCapabilities.addEdge(selfEdge, startNode, startNode);	
		
		//Nodes that represent machining operations performed on the part
		for (String program : this.machine.getProgramList()){
			//Create the node representing this program being complete
			PhysicalProperty programName = new PhysicalProperty(program.substring(program.length()-2));
			ProductState programNode = new ProductState(this.machine.getMachine(), programName, machineLocation);
		
			//Create the edge to go to this node
			ResourceEvent programEdge = new ResourceEvent(this, startNode, programNode, program, this.machine.getProgramTime(program));
			this.machineCapabilities.addEdge(programEdge, startNode, programNode);
			
			//Create the edge to come back to the original position
			ResourceEvent programReset = new ResourceEvent(this, programNode, startNode, "Reset", 0);
			this.machineCapabilities.addEdge(programReset, programNode, startNode);
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
			for (ProductState node : this.machineCapabilities.getVertices()){
				if (neighbor.getCapabilities().containsVertex(node)){
					//Assume only one node can be shared between neighbors
					this.tableNeighborNode.put(neighbor, node);
				};
			}
		}
	}
	
	//================================================================================
    // TestingNormalOperation
    //================================================================================
	
	/*@ScheduledMethod (start = 30)
	public void testRobot(){
		this.machine.runProgram(this.machine.getProgramList().remove(0));
	}*/
}