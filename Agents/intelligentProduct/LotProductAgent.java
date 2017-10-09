package intelligentProduct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import repast.simphony.engine.schedule.ScheduledMethod;
import resourceAgent.ResourceAgentInterface;
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;
import sharedInformation.PhysicalProperty;

public class LotProductAgent implements ProductAgent{
	
	private String name;
	
	private ArrayList<String> processDone;
	private ArrayList<PhysicalProperty> processesNeeded;
	
	private AgentBeliefModel beliefModel;

	private ArrayList<ResourceBid> biddingResources;
	
	public LotProductAgent(String name, ArrayList<String> processesNeeded, ResourceAgentInterface startingResource, CapabilitiesNode startingNode){
		this.name = name;
		
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
	public void rescheduleRequest(ResourceAgentInterface resourceAgentInterface, int startTime) {
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
	public void submitBid(ArrayList<ResourceAgentInterface> resourceList, int bidTime) {
		this.biddingResources.add(new ResourceBid(resourceList,bidTime));
		
		Collections.sort(biddingResources, new Comparator<ResourceBid>(){
            public int compare(ResourceBid rb1, ResourceBid rb2){
                return rb1.getBidTime()-rb2.getBidTime();}});
		
		String printString = "\n";
		for (ResourceBid resource : biddingResources){
			printString = printString + resource.toString() + " \n";
		}
		System.out.println("" + printString);
	}
	
	//================================================================================
    // Helper method
    //================================================================================
	
	/**
	 * Start a bidding process using the starting resource
	 * @param resource
	 * @param startingNode 
	 */
	private void startBidding(ResourceAgentInterface resource, CapabilitiesNode startingNode) {
		PhysicalProperty property = this.processesNeeded.get(0);
		resource.teamQuery(this, property, startingNode, 0, this.getBidTime(), new ArrayList<ResourceAgentInterface>());
	}

	private int getBidTime() {
		return 100;
	}
	
	//================================================================================
    // Helper class
    //================================================================================
	
	private class ResourceBid {
		private ArrayList<ResourceAgentInterface> resourceList;
		private int bidTime;

		public ResourceBid(ArrayList<ResourceAgentInterface> resourceList, int bidTime){
			this.resourceList = resourceList;
			this.bidTime = bidTime;
		}

		/**
		 * @return the resourceAgentInterface
		 */
		public ArrayList<ResourceAgentInterface> getResourceList() {
			return resourceList;
		}

		/**
		 * @return the bidTime
		 */
		public int getBidTime() {
			return bidTime;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			String printString = "";
			for (ResourceAgentInterface resource : resourceList){
				printString = printString + resource.toString() + " ";
			}
			return "" + resourceList + " Bid:" + bidTime;
		}		
	}
	
}