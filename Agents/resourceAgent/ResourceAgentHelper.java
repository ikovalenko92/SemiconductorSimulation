package resourceAgent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
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
	 * @param currentNode
	 * @param currentTime
	 * @param maxTime
	 * @param teamList
	 * @param neighbors
	 * @param tableNeighborNode
	 * @param capabilities
	 * @param weightTransformer
	 */
	public void teamQuery(ResourceAgent resourceAgent, ProductAgent productAgent, PhysicalProperty desiredProperty, ProductState currentNode,
			int currentTime, int maxTimeAllowed, ArrayList<ResourceAgent> teamList, ArrayList<ResourceEvent> edgeList, ArrayList<ResourceAgent> neighbors,
			HashMap<ResourceAgent, ProductState> tableNeighborNode,	DirectedSparseGraph<ProductState,ResourceEvent> RAcapabilities, Transformer<ResourceEvent, Integer> weightTransformer) {
		
		//Set-up. New lists are created for pointer purposes
		ArrayList<ResourceAgent> newTeamList = new ArrayList<ResourceAgent>(teamList);
		ArrayList<ResourceEvent> newEdgeList = new ArrayList<ResourceEvent>(edgeList);
		DirectedSparseGraph<ProductState,ResourceEvent> capabilities = copyGraph(RAcapabilities);
				
		DijkstraShortestPath<ProductState, ResourceEvent> shortestPathGetter = 
				new DijkstraShortestPath<ProductState, ResourceEvent>(capabilities, weightTransformer);
		shortestPathGetter.reset();
		
		
		//Check if a vertex satisfies a desired property
		boolean flag = false;
		ProductState desiredVertex = null;
		for (ProductState vertex : capabilities.getVertices()){
			if(vertex.getPhysicalProperties().contains(desiredProperty)){
				flag = true;
				desiredVertex = vertex;
				break;
			}
		}
		
		// If a vertex satisfied a desired property
		if (flag){
			
			//Find the shortest path
			List<ResourceEvent> shortestPathCandidateList = shortestPathGetter.getPath(currentNode, desiredVertex);
			int pathValue = shortestPathGetter.getDistanceMap(currentNode).get(desiredVertex).intValue();
			
			//Find the time when the resource is available and add it to the bid
			int nextTimeAvailable = resourceAgent.getSchedule().getNextFreeTime(currentTime,pathValue);	
			int timeOffset = nextTimeAvailable-currentTime;
			
			for (ResourceEvent edge:shortestPathCandidateList){
				edge.setWeight(edge.getWeight()+timeOffset);
			}
			
			//Calculate the bid
			int bidTime = currentTime;
			for (ResourceEvent pathNode : shortestPathCandidateList){
				bidTime = bidTime + pathNode.getWeight();
				newEdgeList.add(pathNode);
			}
	
			//Submit the bid to the product agent
			if (bidTime < currentTime + maxTimeAllowed){
				newTeamList.add(resourceAgent); // Add to the team
				productAgent.submitBid(newTeamList, newEdgeList);
			}	
		}
		
		//Push the bid negotiation to a neighbor
		for (ResourceAgent neighbor: neighbors){
			//If the neighbor isn't already part of the team
			if (!teamList.contains(neighbor)){
				newTeamList.clear(); //Reset previous instance of the newTeamList
				newTeamList.addAll(teamList); //Add the initial team list
				
				ProductState neighborNode = tableNeighborNode.get(neighbor);

				//Find the shortest path
				List<ResourceEvent> shortestPathCandidateList = shortestPathGetter.getPath(currentNode, neighborNode);
				int pathValue = shortestPathGetter.getDistanceMap(currentNode).get(neighborNode).intValue();
				
				//Find the time when the resource is available and add it to the bid
				int nextTimeAvailable = resourceAgent.getSchedule().getNextFreeTime(currentTime,pathValue);
				int timeOffset = nextTimeAvailable-currentTime;
									
				for (ResourceEvent edge:shortestPathCandidateList){
					edge.setWeight(edge.getWeight()+timeOffset);
				}
				
				//Calculate the bid
				int bidTime = currentTime; //Reset bid time
				newEdgeList.clear(); //Reset previous instance of the newTeamList
				newEdgeList.addAll(edgeList); //Add the initial team list
				for (ResourceEvent pathNode : shortestPathCandidateList){
					bidTime = bidTime + pathNode.getWeight();
					newEdgeList.add(pathNode);
				}
				
				//Push the bid to the resource agent
				if (bidTime < currentTime + maxTimeAllowed){
					newTeamList.add(resourceAgent); // Add to the team
					neighbor.teamQuery(productAgent, desiredProperty, neighborNode, bidTime, maxTimeAllowed, newTeamList, newEdgeList);
				}
			}
		}
		
		//For garbage collector
		clearGraph(capabilities);
		capabilities = null;
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
