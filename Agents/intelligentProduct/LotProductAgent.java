package intelligentProduct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.collections15.Transformer;

import Part.Part;
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
	
	private ArrayList<String> processDone;
	private ArrayList<PhysicalProperty> processesNeeded;
	
	private AgentBeliefModel beliefModel;

	private ArrayList<ResourceBid> biddingResources;

	private boolean startSchedulingMethod;
	
	public LotProductAgent(Part part, ArrayList<String> processesNeeded, ResourceAgent startingResource, CapabilitiesNode startingNode){
		this.partName = part.toString();
		
		// Populate the desired physical properties
		this.processesNeeded = new ArrayList<PhysicalProperty>();
		for (String process : processesNeeded){
			this.processesNeeded.add(new PhysicalProperty(process));
		}
		
		//No processes done in the beginning
		this.processDone = new ArrayList<String>();
		
		//Create an empty agent belief model
		this.beliefModel = new AgentBeliefModel();
		
		this.biddingResources = new ArrayList<ResourceBid>();
		startBidding(startingResource, startingNode);		
		
		this.startSchedulingMethod = false;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void rescheduleRequest(ResourceAgent resourceAgent, int startTime) {
		// TODO Auto-generated method stub
		
	}

	//================================================================================
    // Communication from Resources
    //================================================================================
	
	@Override
	public void informEvent(CapabilitiesEdge edge) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void submitBid(ArrayList<ResourceAgent> resourceList, int bidTime, ArrayList<CapabilitiesEdge> edgeList) {
		
		if (!this.startSchedulingMethod){
			startSchedulingMethod = true;
			ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
			
			//Schedule the Resource Scheduling Method one tick in the future
			schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount()+1), this, "startScheduling");
		}		
		
		this.biddingResources.add(new ResourceBid(resourceList, bidTime, edgeList));
		
		//Compare the bids and sort
		Collections.sort(biddingResources, new Comparator<ResourceBid>(){
            public int compare(ResourceBid rb1, ResourceBid rb2){
                return rb1.getBidTime()-rb2.getBidTime();}});
	}
	

	
	//================================================================================
    // Helper method
    //================================================================================
	
	/**
	 * Start a bidding process using the starting resource
	 * @param resource
	 * @param startingNode 
	 */
	private void startBidding(ResourceAgent resource, CapabilitiesNode startingNode) {
		PhysicalProperty property = this.processesNeeded.get(0);
		resource.teamQuery(this, property, startingNode, 0, this.getBidTime(), new ArrayList<ResourceAgent>(), new ArrayList<CapabilitiesEdge>());
	}
	
	/**
	 * Called by the scheduling method
	 */
	public void startScheduling(){
		//Use the best bid
		ResourceBid bestBid = this.biddingResources.get(0);
		int futureScheduleTime = 1;
		
		for (ResourceAgent resource:bestBid.getResourceList()){
			DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> resourceCapabilities = resource.getCapabilities();
			
			for (CapabilitiesEdge edge : bestBid.getEdgeList()){
				if(resourceCapabilities.containsEdge(edge)){
					resource.requestScheduleTime(this, futureScheduleTime, futureScheduleTime + edge.getWeight());
					futureScheduleTime += edge.getWeight();
				}
			}
		}
		this.startSchedulingMethod = false;
	}
	
	
	
	/** The function for the product agent to set the bid time
	 * @return
	 */
	private int getBidTime() {
		return 100;
	}
	
	//================================================================================
    // Helper class
    //================================================================================
	
	private class ResourceBid {
		private ArrayList<ResourceAgent> resourceList;
		private int bidTime;
		private ArrayList<CapabilitiesEdge> edgeList;

		public ResourceBid(ArrayList<ResourceAgent> resourceList, int bidTime, ArrayList<CapabilitiesEdge> edgeList){
			this.resourceList = resourceList;
			this.bidTime = bidTime;
			this.edgeList = edgeList;
		}

		/**
		 * @return the resourceAgent
		 */
		public ArrayList<ResourceAgent> getResourceList() {
			return resourceList;
		}

		/**
		 * @return the bidTime
		 */
		public int getBidTime() {
			return bidTime;
		}
		
		public ArrayList<CapabilitiesEdge> getEdgeList(){
			return this.edgeList;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			String printString = "";
			for (ResourceAgent resource : resourceList){
				printString = printString + resource.toString() + " ";
			}
			return "" + resourceList + " Bid:" + bidTime;
		}		
	}
	
}