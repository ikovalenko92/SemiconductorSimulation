package intelligentProduct;

import java.util.ArrayList;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import resourceAgent.ResourceAgent;
import sharedInformation.ProductState;
import sharedInformation.ResourceEvent;

public interface ProductAgent {

	public String getPartName();
	
	//================================================================================
    // PA to PA communication
    //================================================================================
	
	public int getPriority();
	
	public void rescheduleRequest(ResourceAgent resourceAgent, int startTime);
	
	//================================================================================
    // Initializing agent communication
    //================================================================================
	
	public void initializeProductionPlan();
	
	public void initializeExitPlan();
	
	//================================================================================
    // RA to PA communication
    //================================================================================
	
	/** Input to the event translator
	 * @param systemOutput
	 * @param currentState
	 * @param occuredEvents
	 */
	public void informEvent(DirectedSparseGraph<ProductState,ResourceEvent> systemOutput, ProductState currentState, ArrayList<ResourceEvent> occuredEvents);
	
	/**  Input to the bid translator
	 * @param bid DirectedSparseGraph
	 */
	public void submitBid(DirectedSparseGraph<ProductState,ResourceEvent> bid);
	
	/** Input to the scheduling translator
	 * @param rescheduleEdge
	 */
	public void updateEdge(ResourceEvent rescheduleEdge);
}
