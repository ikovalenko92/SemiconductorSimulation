package intelligentProduct;

import java.util.ArrayList;

import resourceAgent.ResourceAgentInterface;
import sharedInformation.CapabilitiesEdge;

public interface ProductAgent {

	//================================================================================
    // Part agent communication
    //================================================================================
	
	public int getPriority();
	
	public void rescheduleRequest(ResourceAgentInterface resourceAgentInterface, int startTime);
	
	//================================================================================
    // Resource agent communication
    //================================================================================
	
	public void informEvent(CapabilitiesEdge edge);
	
	public void submitBid(ArrayList<ResourceAgentInterface> resourceList, int bidTime);
}
