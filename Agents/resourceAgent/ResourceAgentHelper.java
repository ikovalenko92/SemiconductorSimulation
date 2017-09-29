package resourceAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections15.Transformer;

import Part.Part;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import intelligentProduct.ProductAgent;
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;
import sharedInformation.PhysicalProperty;
import sharedInformation.RASchedule;
import sharedInformation.ResourceAgent;

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
			int currentTime, int maxTime, ArrayList<ResourceAgent> teamList, ArrayList<ResourceAgent> neighbors, HashMap<ResourceAgent, CapabilitiesNode> tableNeighborNode,
				DirectedSparseGraph<CapabilitiesNode,CapabilitiesEdge> capabilities, Transformer<CapabilitiesEdge, Integer> weightTransformer) {
		
		ArrayList<ResourceAgent> newTeamList = new ArrayList<ResourceAgent>(teamList);
		
		DijkstraShortestPath<CapabilitiesNode, CapabilitiesEdge> shortestPathGetter = 
				new DijkstraShortestPath<CapabilitiesNode, CapabilitiesEdge>(capabilities, weightTransformer);
		shortestPathGetter.reset();
		
		// If the desired property is a point
		if (desiredProperty.getPoint() != null){
			for (CapabilitiesNode vertex : capabilities.getVertices()){
				for(PhysicalProperty vertexProperty : vertex.getPhysicalProperties()){
					
					//If the location fits the desired property
					if (vertexProperty.equals(desiredProperty)){
						//Find the shortest path
						List<CapabilitiesEdge> shortestPathCandidateList = shortestPathGetter.getPath(currentNode, vertex);
						
						//Calculate the bid
						int bidTime = currentTime;
						for (CapabilitiesEdge pathNode : shortestPathCandidateList){
							bidTime = bidTime + pathNode.getWeight();
						}
						
						//Submit the bid to the product agent
						if (bidTime < maxTime){
							newTeamList.add(resourceAgent); // Add to the team
							productAgent.submitBid(teamList, bidTime);
						}
					}
				}
			}
		}
		
		//If it's not a point, then push this bid to neighbors
		for (ResourceAgent neighbor: neighbors){
			
			//If the neighbor isn't part of the team
			if (!teamList.contains(neighbor)){
				newTeamList.clear();
				newTeamList.addAll(teamList);
				
				CapabilitiesNode neighborNode = tableNeighborNode.get(neighbor);

				//Find the shortest path
				List<CapabilitiesEdge> shortestPathCandidateList = shortestPathGetter.getPath(currentNode, neighborNode);
				
				//Calculate the bid
				int bidTime = currentTime;
				for (CapabilitiesEdge pathNode : shortestPathCandidateList){
					bidTime = bidTime + pathNode.getWeight();
				}
				
				//Push the bid to the resource agent
				if (bidTime < maxTime){
					newTeamList.add(resourceAgent); // Add to the team
					neighbor.teamQuery(productAgent, desiredProperty, neighborNode, bidTime, maxTime, newTeamList);
				}
			}
		}
	}
}
