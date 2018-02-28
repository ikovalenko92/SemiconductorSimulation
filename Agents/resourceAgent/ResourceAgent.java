package resourceAgent;

import intelligentProduct.ProductAgent;
import sharedInformation.ResourceEvent;
import sharedInformation.ProductState;
import sharedInformation.PhysicalProperty;
import sharedInformation.RASchedule;

import java.lang.reflect.Method;
import java.util.ArrayList;

import Part.Part;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public interface ResourceAgent {
	
	//================================================================================
    // Setting up local neighbors
    //================================================================================
	
	public void addNeighbor(ResourceAgent neighbor);
	public ArrayList<ResourceAgent> getNeighbors();
	
	//================================================================================
    // PA to RA Communication
    //================================================================================
	
	/**
	 * Bidding propagation to find resource agent teammates to complete a requested task
	 * @param productAgent The product agent sending the request
	 * @param desiredProperty The product agent sending the request
	 * @param currentTime The current time
	 * @param maxTime The max allowed time
	 * @param teamList The current team list
	 * @param edgeList The current list of capabilities edges for each resource
	 */
	public void teamQuery(ProductAgent productAgent, PhysicalProperty desiredProperty, ProductState currentNode, 
			int maxTime, DirectedSparseGraph<ProductState,ResourceEvent> bid, int currentTime);
	
	/** From the PA's scheduling
	 * @param productAgent the product agent to schedule
	 * @param edge the start time time of request
	 * @param endTime 
	 * @return If the part was successfully scheduled
	 */
	public boolean requestScheduleTime(ProductAgent productAgent, ResourceEvent edge, int startTime, int endTime);
	
	/** From the PA's scheduling
	 * @param productAgent the product agent to remove
	 * @param startTime the start time time of request
	 * @return If the part was successfully removed from the schedule
	 */
	public boolean removeScheduleTime(ProductAgent productAgent, int startTime, int endTime);
	
	/** From the PA's event translator
	 * @param desiredEdge
	 * @param productAgent
	 * @return 
	 */
	public boolean query(ResourceEvent desiredEdge, ProductAgent productAgent);
	
	//================================================================================
    // RA to RA communication
    //================================================================================
	
	public RASchedule getSchedule();
	
	/** API for the PA Input Translator method
	 * @return The capabilities graph of the RA
	 */
	public DirectedSparseGraph<ProductState, ResourceEvent> getCapabilities();
} 