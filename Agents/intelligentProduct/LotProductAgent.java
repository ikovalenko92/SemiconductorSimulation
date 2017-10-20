package intelligentProduct;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.Transformer;

import Part.Part;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
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
	
	private ArrayList<PhysicalProperty> processesDone;
	private ArrayList<PhysicalProperty> processesNeeded;
	
	private AgentBeliefModel beliefModel;

	private boolean startSchedulingMethod;
	private PAPlan agentPlan;
	private CapabilitiesEdge queriedEdge;
	private ResourceAgent lastResourceAgent;
	
	public static int PAnumber = 0;
	
	public LotProductAgent(Part part, ArrayList<String> processesNeeded, ResourceAgent startingResource, 
			CapabilitiesNode startingNode, int priority){
		
		PAnumber +=1;
		
		this.partName = part.toString();
		this.priority = priority;
		
		// Populate the desired physical properties
		this.processesNeeded = new ArrayList<PhysicalProperty>();
		for (String process : processesNeeded){
			this.processesNeeded.add(new PhysicalProperty(process));
		}
		
		//No processes done in the beginning
		this.processesDone = new ArrayList<PhysicalProperty>();
		
		//Create an empty agent belief model
		this.beliefModel = new AgentBeliefModel(startingNode);
		
		//No actions have been planned
		this.agentPlan = new PAPlan(this);
		
		//Don't need to start the bidding process
		this.startSchedulingMethod = false;
		
		//Set the starting node in the belief model
		this.beliefModel.setCurrentNode(startingNode);
		
		//No edge queried
		this.queriedEdge = null;		
	}
	
	public void resetPANumber(){
		LotProductAgent.PAnumber = 0;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PA" + this.PAnumber + " for " + this.partName;
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
	public void submitBid(ArrayList<ResourceAgent> resourceList, ArrayList<CapabilitiesEdge> edgeList) {
		
		//Start scheduling 1 tick after the first bid comes in (other bids should come in at the same tick)
		if (!this.startSchedulingMethod){
			startSchedulingMethod = true;
			ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
			
			//Schedule the Resource Scheduling Method one tick in the future
			schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount()+1), this, "startScheduling");
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
		this.lastResourceAgent = edge.getActiveAgent();
		
		//If there is no next action to do (new part or finished with current desired task), ask for more bids 
		if (this.agentPlan.getNextAction(edge) == null){
			
			for (PhysicalProperty property : edge.getChild().getPhysicalProperties()){
				if (this.processesNeeded.contains(property)){
					this.processesDone.add(property);
				}
			}
	
			startBidding(lastResourceAgent, beliefModel.getCurrentNode());
		}
		
		//If the event is part of your local environmnet
		else{
			ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
			
			//Update the belief model of the current node
			this.beliefModel.setCurrentNode(edge.getChild());
			
			//Find the next planned action to request from the RA
			CapabilitiesEdge nextEdge = this.agentPlan.getNextAction(queriedEdge);
			
			//If the next action matches the current state of the part, then do it.
			if (nextEdge.getParent().equals(this.beliefModel.getCurrentNode())){
				schedule.schedule(ScheduleParameters.createOneTime(this.agentPlan.getTimeofAction(nextEdge)), 
						this, "queryResource", new Object[]{nextEdge.getActiveAgent(),nextEdge});
			}
			
			// Else, reschedule based on the current agent belief model
			else{
//TODO reschedule for all methods
				this.startScheduling();
			}
		}
	}
	
	/** Query an action from a resource
	 * @param resourceAgent
	 * @param edge
	 */
	public void queryResource(ResourceAgent resourceAgent, CapabilitiesEdge edge){
		//Set the queried edge		
		this.queriedEdge = edge;
		boolean queried = resourceAgent.query(edge.getActiveMethod(), this);
		
		if (!queried){
			System.out.println("" + this + " query did not work for " + resourceAgent + " " + edge);
		}
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
		return 3000;
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
		
		//For the next needed process, request bids
		PhysicalProperty property = this.getDesiredProperty();
		int currentTime = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		resource.teamQuery(this, property, startingNode, currentTime, this.getBidTime(), new ArrayList<ResourceAgent>(), new ArrayList<CapabilitiesEdge>());
	}
	
	/** The desired property for the product agent
	 * @return
	 */
	private PhysicalProperty getDesiredProperty() {
		for (PhysicalProperty property : this.processesNeeded){
			if (!this.processesDone.contains(property)){
				return property;
			}
		}
		
		return new PhysicalProperty("End");
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
		
		//Flag if scheduling doesn't work
		boolean badPathFlag = false;
		ArrayList<ResourceAgent> scheduledPathAgents = new ArrayList<ResourceAgent>();
		ArrayList<Integer> scheduledPathTimes = new ArrayList<Integer>();
		
		//For each edge in the path, request to schedule the action with the desired RA
		for (CapabilitiesEdge edge : bestPath){			
			int edgeOffset = edge.getWeight() - edge.getActiveAgent().getCapabilities().findEdge(edge.getParent(),edge.getChild()).getWeight();
			if (!edge.getActiveAgent().requestScheduleTime(this, futureScheduleTime + edgeOffset, futureScheduleTime + edge.getWeight())){
				badPathFlag = true;
				break;
			}
			this.agentPlan.addAction(edge, futureScheduleTime + edgeOffset);
	
			//Keep track of the schedule so that we can remove it if there is a bad path
			scheduledPathTimes.add(futureScheduleTime + edgeOffset);
			scheduledPathAgents.add(edge.getActiveAgent());
			
			futureScheduleTime += edge.getWeight();	
		}
		
		if (!badPathFlag){
			//Scheduling method was finished
			this.startSchedulingMethod = false;
			scheduledPathTimes = null;
			scheduledPathAgents = null;
				
			//Start the querying method (1 tick in the future)
			CapabilitiesEdge queryEdge = agentPlan.getActionatTime((int) startTime+1);			
			schedule.schedule(ScheduleParameters.createOneTime(this.agentPlan.getTimeofAction(queryEdge)), this, "queryResource", 
					new Object[]{queryEdge.getActiveAgent(), queryEdge});			
		}
		else{
			//For each edge in the path, request to remove the scheduled actions with the desired RA
			for (int index = 0; index<scheduledPathAgents.size();index++){
				scheduledPathAgents.get(index).removeScheduleTime(this, scheduledPathTimes.get(index));
			}
			
			//Garbage collecting
			scheduledPathTimes = null;
			scheduledPathAgents = null;
			
			//Ask for biddings
			startBidding(lastResourceAgent, beliefModel.getCurrentNode());
		}
	}
	
}