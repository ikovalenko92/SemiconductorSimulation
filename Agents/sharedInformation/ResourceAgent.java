package sharedInformation;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

public interface ResourceAgent {
	public DirectedSparseGraph<SystemVertex, SystemEdge> getGraph(int time);
	public boolean scheduleGraph(Object object);

	public void notify(Object object, SystemEdge edge);
	
	//public void addListeningPart(PartController partController);
	//public void removeListeningPart(PartController partController);
} 