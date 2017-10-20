package intelligentProduct;

import java.util.ArrayList;

import resourceAgent.ResourceAgent;
import sharedInformation.CapabilitiesEdge;

public interface ProductAgent {

	public String getPartName();
	
	//================================================================================
    // Part agent communication
    //================================================================================
	
	public int getPriority();
	
	public void rescheduleRequest(ResourceAgent resourceAgent, int startTime);
	
	//================================================================================
    // Resource agent communication
    //================================================================================
	
	public void informEvent(CapabilitiesEdge edge);
	
	public void submitBid(ArrayList<ResourceAgent> resourceList, ArrayList<CapabilitiesEdge> edgeList);
	
	public void updateEdge(CapabilitiesEdge oldEdge, CapabilitiesEdge newEdge);
}
