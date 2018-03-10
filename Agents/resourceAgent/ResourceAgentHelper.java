package resourceAgent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections15.Transformer;

import Part.Part;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import intelligentProduct.ProductAgent;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import sharedInformation.ResourceEvent;
import sharedInformation.ProductState;
import sharedInformation.PhysicalProperty;
import sharedInformation.RASchedule;

public class ResourceAgentHelper {

	/** Used in the resource agents to create team queries
	 * @param resourceAgent
	 * @param productAgent
	 * @param desiredProperty
	 * @param bidPartState
	 * @param existingBidTime
	 * @param maxTime
	 * @param teamList
	 * @param neighbors
	 * @param tableNeighborNode
	 * @param capabilities
	 * @param weightTransformer
	 */
	public void teamQuery(ProductAgent productAgent, PhysicalProperty desiredProperty,	ProductState bidPartState, int maxTimeAllowed,
			DirectedSparseGraph<ProductState,ResourceEvent> existingBid, int existingBidTime,
			// Resource Specific Information below. This is necessary because the ResourceAgentHelper method is outside the RA.
			// If the teamQuery() was inside the RA, the below information could be accessed locally and doesn't need to be passed in the method.
			ResourceAgent resourceAgent, ArrayList<ResourceAgent> neighbors, HashMap<ResourceAgent, ProductState> tableNeighborNode,
			DirectedSparseGraph<ProductState,ResourceEvent> RAcapabilities, Transformer<ResourceEvent, Integer> weightTransformer) {
		
		DirectedSparseGraph<ProductState,ResourceEvent> updatedCapabilities = copyGraph(RAcapabilities); //need to update based on current schedule
		//Copy graphs to not mess with pointers
		DirectedSparseGraph<ProductState,ResourceEvent> bid = copyGraph(existingBid);
		DirectedSparseGraph<ProductState,ResourceEvent> searchGraph = copyGraph(existingBid);
		
		// 1. Update events in capabilities based on current schedule
		// 2. Create new full graph (capabilities + bid)
		Iterator<ResourceEvent> itr = updatedCapabilities.getEdges().iterator();
		while (itr.hasNext()){
			// Find the edge and update it based on current schedule
			ResourceEvent edge = itr.next();
			
			int bidOffset = resourceAgent.getSchedule().getNextFreeTime(existingBidTime,edge.getEventTime())-existingBidTime;

			edge.setWeight(edge.getEventTime()+bidOffset);
			
			//Add to entire graph
			searchGraph.addEdge(edge, edge.getParent(), edge.getChild());
		}
				
		DijkstraShortestPath<ProductState, ResourceEvent> shortestPathGetter = 
				new DijkstraShortestPath<ProductState, ResourceEvent>(searchGraph, weightTransformer);
		shortestPathGetter.reset();
		
		
		//Check if a node in the capabilities graph satisfies a desired property
		boolean flag = false;
		ProductState desiredVertex = null;
		for (ProductState vertex : updatedCapabilities.getVertices()){
			if(vertex.getPhysicalProperties().contains(desiredProperty)){
				flag = true;
				desiredVertex = vertex;
				break;
			}
		}
		
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		double currentTime = schedule.getTickCount(); 
		
		// If a vertex satisfied a desired property
		if (flag){
			
			//Find the shortest path
			List<ResourceEvent> shortestPathCandidateList = shortestPathGetter.getPath(bidPartState, desiredVertex);
			
			//Calculate the bid
			int bidTime = existingBidTime;
			for (ResourceEvent path : shortestPathCandidateList){
				bidTime = bidTime + path.getEventTime();
				bid.addEdge(path, path.getParent(), path.getChild());
				bidPartState = path.getChild();
			}
			
			//Submit the bid to the product agent
			if (bidTime < currentTime + maxTimeAllowed){
				productAgent.submitBid(bid);
			}	
		}
		//Push the bid negotiation to a neighbor
		else{ //Note: if a desired node is found, don't push it to the neighbor (can be turned off)
			for (ResourceAgent neighbor: neighbors){
				
				ProductState neighborNode = tableNeighborNode.get(neighbor);
				
				//Find the shortest path
				List<ResourceEvent> shortestPathCandidateList = shortestPathGetter.getPath(bidPartState, neighborNode);
				
				//If the current state is the 1st neighbor state -> pass the bid on to the resource's neighbor
				if(shortestPathCandidateList.isEmpty() && bid.getEdgeCount()==0){
					ResourceEvent selfEdge = new ResourceEvent(resourceAgent, neighborNode, neighborNode, "Hold", 0);
					bid.addEdge(selfEdge, selfEdge.getParent(), selfEdge.getChild());
					neighbor.teamQuery(productAgent, desiredProperty, bidPartState, maxTimeAllowed, bid, existingBidTime);
				}
				else
				//Don't revisit the same edges
				if(!bid.getEdges().containsAll(shortestPathCandidateList)){	
					//Calculate the bid
					int bidTime = existingBidTime; //Reset bid time
					for (ResourceEvent path : shortestPathCandidateList){
						bidTime = bidTime + path.getEventTime();
						bid.addEdge(path, path.getParent(), path.getChild());
					}
					
					ProductState newBidPartState = shortestPathCandidateList.get(shortestPathCandidateList.size()-1).getChild();
					
					//Push the bid to the resource agent
					if (bidTime < currentTime + maxTimeAllowed && bidTime >= existingBidTime){
						neighbor.teamQuery(productAgent, desiredProperty, newBidPartState, maxTimeAllowed, bid, bidTime);
					}
				}
			}
		}
		
		//For garbage collector
		clearGraph(updatedCapabilities);
		updatedCapabilities = null;
		clearGraph(bid);
		bid = null;
		clearGraph(searchGraph);
		searchGraph = null;
	}

	/** Copies the graph
	 * @param oldgraph
	 * @return
	 */
	private DirectedSparseGraph<ProductState, ResourceEvent> copyGraph(
			DirectedSparseGraph<ProductState, ResourceEvent> oldgraph) {
		DirectedSparseGraph<ProductState, ResourceEvent> graph = new DirectedSparseGraph<ProductState, ResourceEvent>();
		
		for (ResourceEvent e : oldgraph.getEdges()){
			ResourceEvent newEdge = e.copy();
			graph.addEdge(newEdge,newEdge.getParent(),newEdge.getChild());
		}
		
		return graph;
	}
 
	/** Clears the graph
	 * @param graph
	 */
	private void clearGraph(DirectedSparseGraph<ProductState, ResourceEvent> graph) {
		for (ResourceEvent e : graph.getEdges()){
			e = null;
		}
		
		for (ProductState v : graph.getVertices()){
	        v = null;
		}    
	}
}
