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
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;
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
	public void teamQuery(ResourceAgent resourceAgent, ProductAgent productAgent, PhysicalProperty desiredProperty, CapabilitiesNode currentNode,
			int currentTime, int maxTimeAllowed, ArrayList<ResourceAgent> teamList, ArrayList<CapabilitiesEdge> edgeList, ArrayList<ResourceAgent> neighbors,
			HashMap<ResourceAgent, CapabilitiesNode> tableNeighborNode,	DirectedSparseGraph<CapabilitiesNode,CapabilitiesEdge> RAcapabilities, Transformer<CapabilitiesEdge, Integer> weightTransformer) {
		
		//Set-up. New lists are created for pointer purposes
		ArrayList<ResourceAgent> newTeamList = new ArrayList<ResourceAgent>(teamList);
		ArrayList<CapabilitiesEdge> newEdgeList = new ArrayList<CapabilitiesEdge>(edgeList);
		DirectedSparseGraph<CapabilitiesNode,CapabilitiesEdge> capabilities = copyGraph(RAcapabilities);
				
		DijkstraShortestPath<CapabilitiesNode, CapabilitiesEdge> shortestPathGetter = 
				new DijkstraShortestPath<CapabilitiesNode, CapabilitiesEdge>(capabilities, weightTransformer);
		shortestPathGetter.reset();
		
		
		//Check if a vertex satisfies a desired property
		boolean flag = false;
		CapabilitiesNode desiredVertex = null;
		for (CapabilitiesNode vertex : capabilities.getVertices()){
			if(vertex.getPhysicalProperties().contains(desiredProperty)){
				flag = true;
				desiredVertex = vertex;
				break;
			}
		}
		
		// If a vertex satisfied a desired property
		if (flag){
			
			//Find the shortest path
			List<CapabilitiesEdge> shortestPathCandidateList = shortestPathGetter.getPath(currentNode, desiredVertex);
			int pathValue = shortestPathGetter.getDistanceMap(currentNode).get(desiredVertex).intValue();
			
			//Find the time when the resource is available and add it to the bid
			int nextTimeAvailable = resourceAgent.getSchedule().getNextFreeTime(currentTime,pathValue);	
			int timeOffset = nextTimeAvailable-currentTime;
			
			for (CapabilitiesEdge edge:shortestPathCandidateList){
				edge.setWeight(edge.getWeight()+timeOffset);
			}
			
			//Calculate the bid
			int bidTime = currentTime;
			for (CapabilitiesEdge pathNode : shortestPathCandidateList){
				bidTime = bidTime + pathNode.getWeight();
				newEdgeList.add(pathNode);
			}
	
			//Submit the bid to the product agent
			if (bidTime < currentTime + maxTimeAllowed){
				newTeamList.add(resourceAgent); // Add to the team
				productAgent.submitBid(newTeamList, newEdgeList);
			}	
		}
		
		//If the agent can't do the desired property, push the bid negotiation to a neighbor
		else{
			//Push it to a neighbor
			for (ResourceAgent neighbor: neighbors){
				
				//If the neighbor isn't already part of the team
				if (!teamList.contains(neighbor)){
					newTeamList.clear(); //Reset previous instance of the newTeamList
					newTeamList.addAll(teamList); //Add the initial team list
					
					CapabilitiesNode neighborNode = tableNeighborNode.get(neighbor);
	
					//Find the shortest path
					List<CapabilitiesEdge> shortestPathCandidateList = shortestPathGetter.getPath(currentNode, neighborNode);
					int pathValue = shortestPathGetter.getDistanceMap(currentNode).get(neighborNode).intValue();
					
					//Find the time when the resource is available and add it to the bid
					int nextTimeAvailable = resourceAgent.getSchedule().getNextFreeTime(currentTime,pathValue);
					int timeOffset = nextTimeAvailable-currentTime;
										
					for (CapabilitiesEdge edge:shortestPathCandidateList){
						edge.setWeight(edge.getWeight()+timeOffset);
					}
					
					//Calculate the bid
					int bidTime = currentTime; //Reset bid time
					newEdgeList.clear(); //Reset previous instance of the newTeamList
					newEdgeList.addAll(edgeList); //Add the initial team list
					for (CapabilitiesEdge pathNode : shortestPathCandidateList){
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
		}
		
		//For garbage collector
		clearGraph(capabilities);
		capabilities = null;
	}

	/** Copies the graph
	 * @param oldgraph
	 * @return
	 */
	private DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> copyGraph(
			DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> oldgraph) {
		DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> graph = new DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge>();
		
		for (CapabilitiesEdge e : oldgraph.getEdges()){
			CapabilitiesEdge newEdge = e.copy();
			graph.addEdge(newEdge,newEdge.getParent(),newEdge.getChild());
		}
		
		return graph;
	}
 
	/** Clears the graph
	 * @param graph
	 */
	private void clearGraph(DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> graph) {
		for (CapabilitiesEdge e : graph.getEdges()){
			e = null;
		}
		
		for (CapabilitiesNode v : graph.getVertices()){
	        v = null;
		}    
	}
}
