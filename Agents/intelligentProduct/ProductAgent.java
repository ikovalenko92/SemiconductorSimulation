package intelligentProduct;

import java.util.ArrayList;

import resourceAgent.ResourceAgent;
import sharedInformation.ResourceEvent;

public interface ProductAgent {

	public String getPartName();
	
	//================================================================================
    // Part agent communication
    //================================================================================
	
	public int getPriority();
	
	public void rescheduleRequest(ResourceAgent resourceAgent, int startTime);
	
	//================================================================================
    // Initializing agent communication
    //================================================================================
	
	public void initializeProductionPlan();
	
	public void initializeExitPlan();
	
	//================================================================================
    // Resource agent communication
    //================================================================================
	
	public void informEvent(ResourceEvent edge);
	
	public void submitBid(ArrayList<ResourceAgent> resourceList, ArrayList<ResourceEvent> edgeList);
	
	public void updateEdge(ResourceEvent oldEdge, ResourceEvent newEdge);
}
