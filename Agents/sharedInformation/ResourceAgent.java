package sharedInformation;

import intelligentProduct.ProductAgent;

import java.util.ArrayList;

import Part.Part;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public interface ResourceAgent {

	//================================================================================
    // Part agent communication
    //================================================================================
	
	public DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> getStatus();
	public void inform(Part part, CapabilitiesEdge edge);
	public RASchedule getSchedule();
	public boolean query(Method method, )
	
	//public void addListeningPart(ProductAgent productAgent);
	//public void removeListeningPart(ProductAgent productAgent);
	
	//================================================================================
    // Team Formation request
    //================================================================================
	
	public void teamQuery(ProductAgent part, CapabilitiesNode desiredNode, int maxNeighborhood, ArrayList<ResourceAgent> teamList);
	
} 