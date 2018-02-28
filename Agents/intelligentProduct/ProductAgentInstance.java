package intelligentProduct;

import initializingAgents.ExitPlan;
import initializingAgents.ProductionPlan;

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
	
	private String partName;
	private int PANumber = -1;
	private int priority;
	
	private boolean startSchedulingMethod;
	
	private ProductionPlan productionPlan;
	private ExitPlan exitPlan;
	private ProductHistory productHistory;
	private EnvironmentModel beliefModel;
	private PAPlan plan;
	
	private ResourceEvent queriedEdge;
	private ResourceAgent lastResourceAgent;
	
	private ISchedule simulationSchedule;
	
	public ProductAgentInstance(Part part, ResourceAgent startingResource, ProductState startingNode, int priority,
			ProductionPlan productionPlan, ExitPlan exitPlan){
		this.partName = part.toString();
		this.priority = priority;
		
		//Initialize the Knowledge Base
		this.productionPlan = productionPlan; // Production Plan
		this.exitPlan = exitPlan; 
		this.productHistory = new ProductHistory(this,startingNode); //Product History
		this.beliefModel = new EnvironmentModel(this,startingNode); // Environment Model
		this.plan = new PAPlan(this); //Agent Plan
		
		//Don't need to start the bidding process
		this.startSchedulingMethod = false;
		
		//Set the starting node in the belief model
		this.beliefModel.setCurrentNode(startingNode);
		
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
    // Decision Director
    //================================================================================

	private void updateEnvironmentModel(DirectedSparseGraph<ProductState, ResourceEvent> systemOutput,
			ProductState currentState, ArrayList<ResourceEvent> occuredEvents) {
		// TODO Auto-generated method stub
	}
	
	private ResourceEvent requestNewPlan() {
		// TODO Auto-generated method stub
		return null;
	}
	
	//================================================================================
    // Exploring/Bidding (RA Communication)
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
		//this.beliefModel.addEdges(edgeList);
		
		//Add the last node as a desired node to the agent belief model
		//this.beliefModel.addDesiredNode(edgeList.get(edgeList.size()-1).getChild());
	}
	
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
		//resource.teamQuery(this, property, startingNode, currentTime, this.getBidTime(), new ArrayList<ResourceAgent>(), new ArrayList<ResourceEvent>());
	}
	
	
	/** The function for the product agent to set the bid time
	 * @return
	 */
	private int getBidTime() {
		return 3000;
	}
	//================================================================================
    // Planning/Scheduling (RA Communication)
    //================================================================================

	@Override
	public void updateEdge(ResourceEvent rescheduleEdge) {
		// TODO Auto-generated method stub
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
			this.plan.addAction(edge, futureScheduleTime);
	
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
			ResourceEvent queryEdge = plan.getActionatNextTime((int) startTime+1);
			schedule.schedule(ScheduleParameters.createOneTime(this.plan.getTimeofAction(queryEdge,(int) startTime)), this, "queryResource", 
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
			startBidding(lastResourceAgent, beliefModel.getCurrentNode());
		}
	}
	
	//================================================================================
    // Execution (RA Communication)
    //================================================================================
	
	@Override
	public void informEvent(DirectedSparseGraph<ProductState, ResourceEvent> systemOutput, ProductState currentState,
			ArrayList<ResourceEvent> occuredEvents) {
		updateEnvironmentModel(systemOutput,currentState,occuredEvents);
		
		ResourceEvent nextAction = this.plan.getNextAction(occuredEvents.get(occuredEvents.size()-1), (int) simulationSchedule.getTickCount());
		
		if (nextAction == null){
			nextAction = requestNewPlan();
		}
		int nextEventTime = this.plan.getTimeofAction(nextAction,(int) simulationSchedule.getTickCount());
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
    // Decision Maker - helper methods
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
	
	//================================================================================
    // Communication sequence - helper methods
    //================================================================================
	            
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