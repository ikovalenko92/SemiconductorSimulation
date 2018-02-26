package intelligentProduct;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.Transformer;

import Part.Part;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import resourceAgent.ResourceAgent;
import sharedInformation.ResourceEvent;
import sharedInformation.ProductState;
import sharedInformation.PhysicalProperty;

/**
 * @author ikoval
 *
 */
public class ProductAgentInstance implements ProductAgent{
	

	@Override
	public void initializeProductionPlan() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initializeExitPlan() {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void informEvent(DirectedSparseGraph<ProductState, ResourceEvent> systemOutput, ProductState currentState,
			ArrayList<ResourceEvent> occuredEvents) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateEdge(ResourceEvent rescheduleEdge) {
		// TODO Auto-generated method stub
		
	}
	
	private String partName;
	private int PANumber = -1;
	private int priority;
	
	private boolean startSchedulingMethod;
	
	private ProductionPlan productionPlan;
	private ExitPlan exitPlan;
	private ProductHistory productHistory;
	private AgentBeliefModel beliefModel;
	private PAPlan agentPlan;
	
	private ResourceEvent queriedEdge;
	private ResourceAgent lastResourceAgent;
	
	
	public ProductAgentInstance(Part part, ArrayList<String> processesNeeded, ResourceAgent startingResource, 
			ProductState startingNode, int priority){
		this.partName = part.toString();
		this.priority = priority;
		
		//Initialize the Knowledge Base
		this.productionPlan = new ProductionPlan(this);
		this.exitPlan = new ExitPlan(this);
		this.productHistory = new ProductHistory(this,startingNode);
		this.beliefModel = new AgentBeliefModel(this,startingNode);
		this.agentPlan = new PAPlan(this);
		
		//Don't need to start the bidding process
		this.startSchedulingMethod = false;
		
		//Set the starting node in the belief model
		this.beliefModel.setCurrentNode(startingNode);
		
		//No edge queried
		this.queriedEdge = null;		
	}
	
	public void setPANumber(int number){
		this.PANumber = number;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PA" + this.PANumber + " for " + this.partName;
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
	public void submitBid(DirectedSparseGraph<ProductState, ResourceEvent> bid) {
		
		//Start scheduling 1 tick after the first bid comes in (other bids should come in at the same tick)
		if (!this.startSchedulingMethod){
			this.startSchedulingMethod = true;
			ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
			
			//Schedule the Resource Scheduling Method one tick in the future
			schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount()+1), this, "startScheduling");
		}	
		
		//Add all of the edges in the bid to the agent belief model
		this.beliefModel.addEdges(edgeList);
		
		//Add the last node as a desired node to the agent belief model
		this.beliefModel.addDesiredNode(edgeList.get(edgeList.size()-1).getChild());
	}
	
	public void updateEdge(ResourceEvent oldEdge, ResourceEvent newEdge){
		
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

	public void informEvent(ResourceEvent edge) {
		
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		
		//Save the last resource
		this.lastResourceAgent = edge.getEventAgent();
		
		//If there is no next action to do (new part or finished with current desired task), ask for more bids 
		if (this.agentPlan.getNextAction(edge, (int) schedule.getTickCount()) == null){
			
			for (PhysicalProperty property : edge.getChild().getPhysicalProperties()){
				//if (this.processesNeeded.contains(property)){
					//this.processesDone.add(property);
				//}
			}
	
			startBidding(lastResourceAgent, beliefModel.getCurrentNode());
		}
		
		//If the event is part of your local environmnet
		else{			
			//Update the belief model of the current node
			this.beliefModel.setCurrentNode(edge.getChild());
			
			//Find the next planned action to request from the RA
			ResourceEvent nextEdge = this.agentPlan.getNextAction(queriedEdge, (int) schedule.getTickCount());			
			
			//If the next action matches the current state of the part, then do it.
			if (nextEdge.getParent().equals(this.beliefModel.getCurrentNode())){
				schedule.schedule(ScheduleParameters.createOneTime(this.agentPlan.getTimeofAction(nextEdge,(int) schedule.getTickCount())), 
						this, "queryResource", new Object[]{nextEdge.getEventAgent(),nextEdge});
			}
			
			// Else, reschedule based on the current agent belief model
			else{
//TODO reschedule for all methods
				//this.startScheduling();
			}
		}
	}
	
	/** Query an action from a resource
	 * @param resourceAgent
	 * @param edge
	 */
	public void queryResource(ResourceAgent resourceAgent, ResourceEvent edge){
		//Set the queried edge		
		this.queriedEdge = edge;
		boolean queried = resourceAgent.query(edge, this);
		
		if (!queried){
			System.out.println("" + this + " query did not work for " + resourceAgent + " " + edge);
			//this.startBidding(this.lastResourceAgent, this.beliefModel.getCurrentNode());
		}
	}
	
	//================================================================================
    // Internal decision - helper methods
    //================================================================================
	
	/** Finding the "best" (according to the weight transformer function) path
	 * @return A list of Capabilities Edges that correspond to the best path
	 */
	private List<ResourceEvent> getBestPath(){
		//Find the shortest path
		DijkstraShortestPath<ProductState, ResourceEvent> shortestPathGetter = 
				new DijkstraShortestPath<ProductState, ResourceEvent>(this.beliefModel, getWeightTransformer());
		shortestPathGetter.reset();
		
		//Set initial values
		int dist = 999999999; //a very large number
		ProductState desiredNodeFinal = null;
		
		//Find the desired distance with the shortest distance
		for (ProductState desiredNode: this.beliefModel.getDesiredNodes()){
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
	private Transformer<ResourceEvent, Integer> getWeightTransformer(){
		return new Transformer<ResourceEvent,Integer>(){
			public Integer transform(ResourceEvent edge) {
				return edge.getEventTime(); // Transformer takes just the edge of weight and weighs it as one (can change)
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
	private void startBidding(ResourceAgent resource, ProductState startingNode) {
		this.beliefModel.clear();
		
		//For the next needed process, request bids
		PhysicalProperty property = this.getDesiredProperty();
		int currentTime = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		resource.teamQuery(this, property, startingNode, currentTime, this.getBidTime(), new ArrayList<ResourceAgent>(), new ArrayList<ResourceEvent>());
	}
	            
	/** The desired property for the product agent
	 * @return
	 */
	private PhysicalProperty getDesiredProperty() {
		//for (PhysicalProperty property : this.processesNeeded){
			//if (!this.processesDone.contains(property)){
			//	return property;
		//	}
		//}
		
		return new PhysicalProperty("End");
	}

	/**
	 * Called by the scheduling method
	 */
	public void startScheduling(){
		List<ResourceEvent> bestPath = this.getBestPath(); //Use the best bid
		
		//Schedule 1 tick in the future
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		double startTime = schedule.getTickCount();
		int futureScheduleTime = (int) (startTime+1);
		
		//Flag if scheduling doesn't work
		boolean badPathFlag = false;
		ArrayList<ResourceAgent> scheduledPathAgents = new ArrayList<ResourceAgent>();
		ArrayList<Integer> scheduledPathTimes = new ArrayList<Integer>();
		
		//For each edge in the path, request to schedule the action with the desired RA
		for (ResourceEvent edge : bestPath){			
			//int edgeOffset = edge.getWeight() - edge.getActiveAgent().getCapabilities().findEdge(edge.getParent(),edge.getChild()).getWeight();
			if (!edge.getEventAgent().requestScheduleTime(this, edge, futureScheduleTime, futureScheduleTime + edge.getEventTime())){
				badPathFlag = true;
				break;
			}
			this.agentPlan.addAction(edge, futureScheduleTime);
	
			//Keep track of the schedule so that we can remove it if there is a bad path
			scheduledPathTimes.add(futureScheduleTime);
			scheduledPathAgents.add(edge.getEventAgent());
			
			futureScheduleTime += edge.getEventTime();	
		}
		if (!badPathFlag){
			//Scheduling method was finished
			this.startSchedulingMethod = false;
			scheduledPathTimes = null;
			scheduledPathAgents = null;
				
			//Start the querying method (1 tick in the future)
			ResourceEvent queryEdge = agentPlan.getActionatNextTime((int) startTime+1);
			schedule.schedule(ScheduleParameters.createOneTime(this.agentPlan.getTimeofAction(queryEdge,(int) startTime)), this, "queryResource", 
					new Object[]{queryEdge.getEventAgent(), queryEdge});			
		}
		else{
			//For each edge in the path, request to remove the scheduled actions with the desired RA
			for (int index = 0; index<scheduledPathAgents.size();index++){
				scheduledPathAgents.get(index).removeScheduleTime(this, scheduledPathTimes.get(index));
			}
			
			//Garbage collecting
			scheduledPathTimes = null;
			scheduledPathAgents = null;
			
			//Ask for biddings again
			startBidding(lastResourceAgent, beliefModel.getCurrentNode());
		}
	}

	
}