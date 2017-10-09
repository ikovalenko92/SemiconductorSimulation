package resourceAgent;

import intelligentProduct.ProductAgent;
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;
import sharedInformation.PhysicalProperty;
import sharedInformation.RASchedule;

import java.lang.reflect.Method;
import java.util.ArrayList;

import Part.Part;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public interface ResourceAgentInterface {

	public void addNeighbor(ResourceAgentInterface neighbor);
	public ArrayList<ResourceAgentInterface> getNeighbors();
	
	//================================================================================
    // Product/resource team formation
    //================================================================================
	
	/**
	 * Bidding propogation to find resource agent teammates to complete a requested task
	 * @param productAgent The product agent sending the request
	 * @param desiredNode The desired capabilities node that the PA wants to achieve
	 * @param maxNeighborhood The max allowed neighborhood of the PA
	 * @param teamList The current team list
	 */
	/**
	 * Bidding propogation to find resource agent teammates to complete a requested task
	 * @param productAgent The product agent sending the request
	 * @param desiredProperty The product agent sending the request
	 * @param currentTime The current time
	 * @param maxTime The max allowed time
	 * @param teamList The current team list
	 */
	public void teamQuery(ProductAgent productAgent, PhysicalProperty desiredProperty, CapabilitiesNode currentNode,
			int currentTime, int maxTime, ArrayList<ResourceAgentInterface> teamList);
	
	//================================================================================
    // Product agent scheduling
    //================================================================================
	
	/**
	 * API for one of the PA Schedule Manager method
	 * @return The schedule of this resource agent
	 */
	public RASchedule getSchedule();
	
	/**
	 * API for one of the PA Schedule Manager method
	 * @return If the part was successfully scheduled
	 * 
	 */
	/**
	 * @param productAgent the product agent to schedule
	 * @param startTime the start time time of request
	 * @return If the part was successfully scheduled
	 */
	public boolean requestScheduleTime(ProductAgent productAgent, int startTime);
	
	/**
	 * API for one of the PA Schedule Manager method
	 * @param productAgent the product agent to remove
	 * @param startTime the start time time of request
	 * @return If the part was successfully removed from the schedule
	 */
	public boolean removeScheduleTime(ProductAgent productAgent, int startTime);
	
	//================================================================================
    // Product agent communication
    //================================================================================
	
	/** API for the PA Input Translator method
	 * @return The capabilities graph of the RA
	 */
	public DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> getCapabilities();
	
	/**
	 * API for the PA Action Requester method
	 * @param method
	 * @param parameter
	 * @return If the method was accepted by the resource
	 */
	public boolean query(String program, Part part);
} 