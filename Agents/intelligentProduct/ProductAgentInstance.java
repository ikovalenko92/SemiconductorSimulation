package intelligentProduct;

import initializingAgents.ExitPlan;
import initializingAgents.ProductionPlan;

import java.util.ArrayList;
import java.util.HashSet;
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
	
	private String partName;
	private int PANumber = -1;
	private int priority;
	
	private ProductionPlan productionPlan;
	private ExitPlan exitPlan;
	private ProductHistory productHistory;
	private EnvironmentModel environmentModel;
	private PAPlan plan;
	
	private ResourceEvent queriedEdge;
	private ResourceAgent lastResourceAgent;
	
	private ISchedule simulationSchedule;
	
	private EnvironmentModel newEnvironmentModel;
	
	public ProductAgentInstance(Part part, ResourceAgent startingResource, ProductState startingNode, int priority,
			ProductionPlan productionPlan, ExitPlan exitPlan){
		this.partName = part.toString();
		this.priority = priority;
		
		//Initialize the Knowledge Base
		this.productionPlan = productionPlan; // Production Plan
		this.exitPlan = exitPlan; 
		this.productHistory = new ProductHistory(this,startingNode); //Product History
		this.environmentModel = new EnvironmentModel(this,startingNode); // Environment Model
		this.plan = new PAPlan(this); //Agent Plan
		
		//Set the starting node in the belief model
		this.environmentModel.update(startingNode);
		
		//No edge queried
		this.queriedEdge = null;
		
		this.simulationSchedule = RunEnvironment.getInstance().getCurrentSchedule();
	}
	
	public void setPANumber(int number){
		this.PANumber = number;
	}
	
	@Override
	public String toString() {
		return "PA" + this.PANumber + " for " + this.partName;
	}

	public String getPartName(){
		return this.partName;
	}
	
	//================================================================================
    // Decision Director - Working with KB
    //================================================================================
	
	/** Update the internal environment model
	 * @param currentState
	 */
	private void updateEnvironmentModelState(ProductState currentState) {
		this.environmentModel.update(currentState);
	}
	
	/** Update the internal product history
	 * @param currentState
	 */
	private void updateProductHistory(DirectedSparseGraph<ProductState, ResourceEvent> systemOutput,
			ProductState currentState, ArrayList<ResourceEvent> occuredEvents) {
		this.productHistory.update(systemOutput,currentState,occuredEvents);	
	}
	
	
	
	//================================================================================
    // Decision Director - Intelligence
    //================================================================================
	
	/**
	 * The execution components are requesting a new plan
	 * Start of decision director's process
	 */
	public void requestNewPlan() {
		startExploration(this.productHistory,this.getDesiredProperties());
		
		//Wait for one time step and then check the output of the exploration
		int nextTimeStep = (int) this.simulationSchedule.getTickCount()+1;
		this.simulationSchedule.schedule(ScheduleParameters.createOneTime(nextTimeStep), 
				this, "checkPlanning", new Object[]{});
	}
	
	/**
	 * Check whether the exploration found a feasible model and move onto planning
	 */
	public void checkPlanning() {
		
		//Check if the exploration built a model
		if (!this.newEnvironmentModel.isEmpty()){
			//Replace the environment model
			this.environmentModel.clear();
			this.environmentModel.update(newEnvironmentModel,newEnvironmentModel.getCurrentState());
			this.newEnvironmentModel.clear();
			
			startPlanning(this.getDesiredProperties(),this.environmentModel,this.plan);
		}
		//If not send the exit plan to the execution
		else{
			sendExitPlan();
		}
	}

	/**
	 * Send the exit plan and start executing
	 */
	public void sendExitPlan() {
		int currentTime = (int) this.simulationSchedule.getTickCount();
		PAPlan exitPAPlan = new PAPlan(this);
		
		//Populate with exit events
		for (int i=0;i<3;i++){
			exitPAPlan.addEvent(this.exitPlan.getExitEvent(), currentTime+i, currentTime+i+1);
		}
		
		//Start executing the exit PAPlan
		startExecution(exitPAPlan);
	}
	
	//================================================================================
    // Exploring/Bidding (RA Communication)
    //================================================================================

	public void startExploration(ProductHistory productHistory, ArrayList<PhysicalProperty> desiredProperties){
		ProductState currentState = productHistory.getCurrentState();
		this.newEnvironmentModel = new EnvironmentModel(this,currentState); //New environment model
		
		int bidTime = getStartBidTime();
		
		//Keep the bidding until enough bids are received (more than 0) or the max bid time is reached
		while (newEnvironmentModel.isEmpty() && bidTime <= this.getMaxBidTime()){
			ResourceAgent contactRA = productHistory.getLastEvent().getEventAgent();
			int nextTimeQuery = (int) this.simulationSchedule.getTickCount()+2;  // ask for bids for two ticks in the future
			
			//For each desired property, send bid requests
			for (PhysicalProperty desiredProperty:desiredProperties){
				DirectedSparseGraph<ProductState,ResourceEvent> bid = new DirectedSparseGraph<ProductState,ResourceEvent>();
				bid.addVertex(currentState);
				contactRA.teamQuery(this, desiredProperty, currentState, bidTime, bid, nextTimeQuery);
			}
			bidTime+=this.getBidTimeChange();
		}
		
	}
	
	@Override
	public void submitBid(DirectedSparseGraph<ProductState, ResourceEvent> bid) {
		this.newEnvironmentModel.update(bid);
	}
	
	/** The function for the product agent to set the starting bid time
	 * @return
	 */
	private int getStartBidTime() {
		return 200;
	}
	
	/** Function to increase the bid time
	 * @return
	 */
	private int getBidTimeChange() {
		return 400;
	}
	
	/** The function for the product agent to set the max bid time
	 * @return
	 */
	private int getMaxBidTime() {
		return 1800;
	}
	
	//================================================================================
    // Planning/Scheduling (RA Communication)
    //================================================================================

	
	private void startPlanning(ArrayList<PhysicalProperty> desiredProperties,
			EnvironmentModel environmentModel, PAPlan plan) {
		 //Solve the optimization problem to find the best path
		List<ResourceEvent> bestPath = this.getBestPath(desiredProperties,environmentModel);
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
			this.plan.addEvent(edge, futureScheduleTime, futureScheduleTime+ edge.getEventTime());
	
			//Keep track of the schedule so that we can remove it if there is a bad path
			scheduledPathTimes.add(futureScheduleTime);
			scheduledPathAgents.add(edge.getEventAgent());
			
			futureScheduleTime += edge.getEventTime();	
		}
		if (!badPathFlag){
			//Scheduling method was finished
			//this.startSchedulingMethod = false;
			scheduledPathTimes = null;
			scheduledPathAgents = null;
				
			//Start the querying method (1 tick in the future)
			int actionIndex = this.plan.getIndexOfNextEvent((int) startTime+1);
			ResourceEvent queryEdge = plan.getIndexEvent(actionIndex);
			schedule.schedule(ScheduleParameters.createOneTime(this.plan.getIndexStartTime(actionIndex)), this, "queryResource", 
					new Object[]{queryEdge.getEventAgent(), queryEdge});			
		}
		else{
			//For each edge in the path, request to remove the scheduled actions with the desired RA
			for (int index = 0; index<scheduledPathAgents.size();index++){
				//scheduledPathAgents.get(index).removeScheduleTime(this, scheduledPathTimes.get(index));
			}
			
			//Garbage collecting
			scheduledPathTimes = null;
			scheduledPathAgents = null;
			
			//Ask for biddings again
			//startBidding(lastResourceAgent, environmentModel.getCurrentNode());
		}
	}
	
	@Override
	public void updateEdge(ResourceEvent rescheduleEdge) {
		// TODO Auto-generated method stub
	}
	
	public void updateEdge(ResourceEvent oldEdge, ResourceEvent newEdge){
		
		//If the model has the edge, update it
		if (this.environmentModel.containsEdge(oldEdge)){
			this.environmentModel.removeEdge(oldEdge);
			if (newEdge != null){
				this.environmentModel.addEdge(newEdge, newEdge.getParent(), newEdge.getChild());
			}
		}
		
		//Trying to replace wrong edge
		else{
			System.out.println(this + " doesn't have " + oldEdge + " in updateEdge()");
		}
	}	
	
	/** Finding the "best" (according to the weight transformer function) path
	 * @param environmentModel
	 * @param desiredProperties 
	 * @return A list of Capabilities Edges that correspond to the best path
	 */
	private List<ResourceEvent> getBestPath(ArrayList<PhysicalProperty> desiredProperties, EnvironmentModel environmentModel){
		//Find the shortest path
		DijkstraShortestPath<ProductState, ResourceEvent> shortestPathGetter = 
				new DijkstraShortestPath<ProductState, ResourceEvent>(environmentModel, getWeightTransformer());
		shortestPathGetter.reset();
		
		//Set initial values
		int dist = 999999999; //a very large number
		ProductState desiredNodeFinal = null;
		
		//Find the desired distance with the shortest distance
		
		ArrayList<ProductState> desiredNodes = new ArrayList<ProductState>();
		
		//Find the desired nodes in the environment model
		for (PhysicalProperty property: desiredProperties){
			for (ProductState node : environmentModel.getVertices()){
				if (node.getPhysicalProperties().contains(property)){
					desiredNodes.add(node);
				}
			}
		}
		
		//Find the fastest path to one of the desired nodes
		for (ProductState desiredNode: desiredNodes){
			int compareDist = shortestPathGetter.getDistanceMap(environmentModel.getCurrentState()).get(desiredNode).intValue();
			if (compareDist < dist){
				dist = compareDist;
				desiredNodeFinal = desiredNode;
			}
		}
		
		return shortestPathGetter.getPath(environmentModel.getCurrentState(), desiredNodeFinal);
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
	
	//================================================================================
    // Execution (RA Communication)
    //================================================================================
	
	@Override
	public void informEvent(DirectedSparseGraph<ProductState, ResourceEvent> systemOutput, ProductState currentState, 
			ArrayList<ResourceEvent> occuredEvents){
		
		//Update the models
		updateEnvironmentModelState(currentState);
		updateProductHistory(systemOutput,currentState,occuredEvents);

		//If there is no next action, find a new plan
		if (this.plan.isEmpty((int) simulationSchedule.getTickCount())){
			requestNewPlan();
		}
		else{
			startExecution(this.plan);
		}
	}
	
	/**
	 * Execute the next action by calling queryResource during the scheduled time
	 */
	public void startExecution(PAPlan plan){
		int nextIndex = plan.getIndexOfNextEvent((int) simulationSchedule.getTickCount());
		ResourceEvent nextAction = plan.getIndexEvent(nextIndex);
		
		//Find the event time
		int nextEventTime = plan.getIndexStartTime(nextIndex);

		//Schedule querying the resource for the next action
		this.simulationSchedule.schedule(ScheduleParameters.createOneTime(nextEventTime), 
				this, "queryResource", new Object[]{nextAction.getEventAgent(),nextAction});
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
		}
	}
	
	//================================================================================
    // Helper algorithms/methods
    //================================================================================
	            
	/** Compare the product history to the production plan to obtain a desired set of physical states
	 * @return The desired set of properties for the product agent
	 */
	private ArrayList<PhysicalProperty> getDesiredProperties() {
		
		ArrayList<PhysicalProperty> incompleteProperties = new ArrayList<PhysicalProperty>();
		
		//Obtain the states that have occurred on the physical product using the product history
		ArrayList<ProductState> checkStates = new ArrayList<ProductState>();
		for (ResourceEvent event: this.productHistory.getOccurredEvents()){
			checkStates.add(event.getChild());
		}
		
		//Compare the production plan to the occurred states
		for(HashSet<PhysicalProperty> set:this.productionPlan.getSetList()){
			int highestIndex = -1; // the highest index when a desired property occurred
						
			//For each desired physical property
			for (PhysicalProperty desiredProperty : set){
				//Check if it has occurred
				boolean propertyComplete = false;
				for (int index = 0; index<checkStates.size()-1;index++){
					if (checkStates.get(index).getPhysicalProperties().equals(desiredProperty)){
						//If it's occurred, overwrite the highest index, if appropriate
						if (index>highestIndex){
							highestIndex = index;
						}
						propertyComplete = true;
						break;
					}
				}
				
				//Add to incomplete properties if the property hasn't previously occurred in the product history
				if(!propertyComplete){
					incompleteProperties.add(desiredProperty);
				}
			}
			
			//If there are incomplete properties, then return these
			if (!incompleteProperties.isEmpty()){
				break;
			}
			//If there aren't incomplete properties, then go onto the next set of properties
			//Note: need to remove all of the properties that are associated with the previous set to continue
			else{
				for (int j=0;j<highestIndex;j++){
					checkStates.remove(j);
				}
			}
		}
		
		return incompleteProperties;
	}

	//================================================================================
    // PA communication
    //================================================================================
	
	@Override
	public int getPriority() {
		return this.priority;
	}

	@Override
	public void rescheduleRequest(ResourceAgent resourceAgent, int startTime) {
		// TODO Auto-generated method stub
		
	}
	
}