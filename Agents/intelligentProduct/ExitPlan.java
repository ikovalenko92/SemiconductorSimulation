package intelligentProduct;

import resourceAgent.ResourceAgent;
import sharedInformation.ResourceEvent;

public class ExitPlan {
	
	private ProductAgentInstance productAgentInstance;
	private ResourceAgent RAexit;
	private ResourceEvent exitEvent;
	

	public ExitPlan(ProductAgentInstance productAgentInstance) {
		this.productAgentInstance = productAgentInstance;
		
	}
	
	@Override
	public String toString() {
		return "Exit Plan for " + this.productAgentInstance + ": " + this.RAexit + " " + this.exitEvent;
	}
	
	public void setRAExit(ResourceAgent RA){
		this.RAexit = RA;
	}
	
	public void setEventExit(ResourceEvent exit){
		this.exitEvent = exit;
	}
}
