package intelligentProduct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections15.Transformer;

import Part.Part;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import resourceAgent.ResourceAgent;
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;
import sharedInformation.PhysicalProperty;

/**
 * @author ikoval
 *
 */
public class LotProductAgent implements ProductAgent{
	
	private String partName;
	private int priority;
	
	private ArrayList<String> processDone;
	private ArrayList<PhysicalProperty> processesNeeded;
	
	private AgentBeliefModel beliefModel;
	private HashMap<CapabilitiesEdge, ResourceAgent> edgeAgentMap;

	private boolean startSchedulingMethod;
	private PAPlan agentPlan;
	private CapabilitiesEdge queriedEdge;
	
	public LotProductAgent(Part part, ArrayList<String> processesNeeded, ResourceAgent startingResource, 
			CapabilitiesNode startingNode, int priority){
		this.partName = part.toString();
		this.priority = priority;
		
		// Populate the desired physical properties
		this.processesNeeded = new ArrayList<PhysicalProperty>();
		for (String process : processesNeeded){
			this.processesNeeded.add(new PhysicalProperty(process));
		}
		
		//No processes done in the beginning
		this.processDone = new ArrayList<String>();
		
		//Create an empty agent belief model
		this.beliefModel = new AgentBeliefModel();
		
		//No bids have been set
		this.edgeAgentMap = new HashMap<CapabilitiesEdge, ResourceAgent>();
		
		//No actions have been planned
		this.agentPlan = new PAPlan(this);
		
		//Don't need to start the bidding process
		this.startSchedulingMethod = false;
		
		this.beliefModel.setCurrentNode(startingNode);
		
		this.queriedEdge = null;		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Product Agent " + this.partName;
	}


	/* (non-Javadoc)
	 * @see intelligentProduct.ProductAgent#getPartName()
	 */
	public String getPartName(){
		return this.partName;
	}

	//================================================================================
    // Part communication
    //================================================================================
	
	@Override
	public int getPriority() {
		return this.priority;
	}

	@Override
	public void rescheduleRequest(ResourceAgent resourceAgent, int startTime) {
		// TODO Auto-generated method stub
		
	}

	//================================================================================
    // Communication from Resources
    //================================================================================

	@Override
	public void submitBid(ArrayList<ResourceAgent> resourceList, int bidTime, ArrayList<CapabilitiesEdge> edgeList) {
		
		if (!this.startSchedulingMethod){
			startSchedulingMethod = true;
			ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
			
			//Schedule the Resource Scheduling Method one tick in the future
			schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount()+1), this, "startScheduling");
		}	
		
		//Populate the edge to agent map for easier mapping of edges to RAs
		for (ResourceAgent resource : resourceList){
			for (CapabilitiesEdge edge : edgeList){
				if(resource.getCapabilities().containsEdge(edge)){
					this.edgeAgentMap.put(edge, resource);
				}
			}
		}
		
		//Add all of the edges in the bid to the agent belief model
		this.beliefModel.addEdges(edgeList);
		
		//Add the last node as a desired node to the agent belief model
		this.beliefModel.addDesiredNode(edgeList.get(edgeList.size()-1).getChild());
	}
	
	@Override
	public void updateEdge(CapabilitiesEdge oldEdge, CapabilitiesEdge newEdge){
		
		//If the model has the edge, update it
		if (this.beliefModel.containsEdge(oldEdge)){
			this.beliefModel.removeEdge(oldEdge);
			if (newEdge != null){
				this.beliefModel.addEdge(newEdge, newEdge.getParent(), newEdge.getChild());
			}
		}
		
		//Trying to replace wrong edge
		else{
			System.out.println(this + " doesn't have " + oldEdge + " in updateEdge()");
		}
	}
	
	@Override
	public void informEvent(CapabilitiesEdge edge) {
		if (this.agentPlan.getNextAction(edge) == null){
			startBidding((ResourceAgent) edge.getActiveObject(),beliefModel.getCurrentNode());
		}
		else{
			ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
			
			this.beliefModel.setCurrentNode(edge.getChild());
			
			if(this.agentPlan.getNextAction(queriedEdge).equals(edge)){
				schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount()+1), this, "queryResource", new Object[]{});
			}
			else{
//TODO reschedule for all methods
				this.startScheduling();
			}
		}
	}
	
	public void queryResource(ResourceAgent resourceAgent, CapabilitiesEdge edge){
		this.queriedEdge = edge;
		resourceAgent.query(edge.getActiveMethod(), this);
	}
	
	//================================================================================
    // Internal decision - helper methods
    //================================================================================
	
	/** Finding the "best" (according to the weight transformer function) path
	 * @return A list of Capabilities Edges that correspond to the best path
	 */
	private List<CapabilitiesEdge> getBestPath(){
		//Find the shortest path
		DijkstraShortestPath<CapabilitiesNode, CapabilitiesEdge> shortestPathGetter = 
				new DijkstraShortestPath<CapabilitiesNode, CapabilitiesEdge>(this.beliefModel, getWeightTransformer());
		shortestPathGetter.reset();
		
		//Set initial values
		int dist = 999999999; //a very large number
		CapabilitiesNode desiredNodeFinal = null;
		
		//Find the desired distance with the shortest distance
		for (CapabilitiesNode desiredNode: this.beliefModel.getDesiredNodes()){
			int compareDist = shortestPathGetter.getDistanceMap(this.beliefModel.getCurrentNode()).get(desiredNode).intValue();
			if (compareDist < dist){
				dist = compareDist;
				desiredNodeFinal = desiredNode;
			}
		}
		
		return shortestPathGetter.getPath(this.beliefModel.getCurrentNode(), desiredNodeFinal);
	}
	
	
	/** The transformer for the capabilities of the resource agent to the desires of the product agent
	 * @return 
	 */
	private Transformer<CapabilitiesEdge, Integer> getWeightTransformer(){
		return new Transformer<CapabilitiesEdge,Integer>(){
			public Integer transform(CapabilitiesEdge edge) {
				return edge.getWeight(); // Transformer takes just the edge of weight and weighs it as one (can change)
			}
		};
	}
	
	
	/** The function for the product agent to set the bid time
	 * @return
	 */
	private int getBidTime() {
		return 100;
	}
	
	//================================================================================
    // Communication sequence - helper methods
    //================================================================================
	
	/**
	 * Start a bidding process using the starting resource
	 * @param resource
	 * @param startingNode 
	 */
	private void startBidding(ResourceAgent resource, CapabilitiesNode startingNode) {
		this.beliefModel.clear();
		this.edgeAgentMap.clear();
		
		//For the next needed process, request bids
		PhysicalProperty property = this.processesNeeded.get(0);
		resource.teamQuery(this, property, startingNode, 0, this.getBidTime(), new ArrayList<ResourceAgent>(), new ArrayList<CapabilitiesEdge>());
	}
	
	/**
	 * Called by the scheduling method
	 */
	public void startScheduling(){
		List<CapabilitiesEdge> bestPath = this.getBestPath(); //Use the best bid
		
		//Schedule 1 tick in the future
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		double startTime = schedule.getTickCount();
		int futureScheduleTime = (int) (startTime+1);
		
		//For each edge in the path, request to schedule the action with the desired RA
		for (CapabilitiesEdge edge : bestPath){
			this.edgeAgentMap.get(edge).requestScheduleTime(this, futureScheduleTime, futureScheduleTime + edge.getWeight());
			this.agentPlan.addAction(edge, futureScheduleTime);
			futureScheduleTime += edge.getWeight();
		}
		
		//Scheduling method was finished
		this.startSchedulingMethod = false;
		
			
		//Start the querying method
		CapabilitiesEdge queryEdge = agentPlan.getActionatTime((int) startTime+1);
		schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount()+1), this, "queryResource", 
				new Object[]{this.edgeAgentMap.get(queryEdge), queryEdge});
	}
	
}