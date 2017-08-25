package intelligentProduct;

import java.util.ArrayList;

import sharedInformation.CapabilitiesEdge;
import sharedInformation.ResourceAgent;

public interface ProductAgent {

	//================================================================================
    // Part agent communication
    //================================================================================
	
	public int getPriority();
	
	public void rescheduleRequest(ResourceAgent resourceAgent, int startTime);
	
	//================================================================================
    // Resource agent communication
    //================================================================================
	
	public void informEvent(CapabilitiesEdge edge);
	
	public void submitBid(ArrayList<ResourceAgent> resourceList, int bidTime);
}
