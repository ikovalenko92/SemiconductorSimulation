package intelligentProduct;

import java.util.ArrayList;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import repast.simphony.engine.schedule.ScheduledMethod;

public class ProductAgent {
	
	private DirectedSparseGraph<SystemVertex, SystemEdge> systemGraph;
	private String tagName;
	private AgentBeliefModel beliefModel;
	private ArrayList<SystemVertex> allStates;
	private ArrayList<String> processDone;
	private ArrayList<String> processNeed;
	private Transformer<SystemEdge, Integer> weightTransformer;
	private boolean ranRequestCommand;
	private int tagNumber;
	private ArrayList<CollectionAgent> collectionAgentList;
	
	public PartController(int i, String tagName, ArrayList<MaterialHandlingAgent> materialHandlingList, ArrayList<ManufacturingProcessAgent> manufacturingList,
			 ArrayList<CollectionAgent> collectionAgentList, ArrayList<String> processNeed){
		this.tagNumber = i;
		this.tagName = tagName;
		this.materialHandlingList = materialHandlingList;
		this.manufacturingList = manufacturingList;
		this.collectionAgentList = collectionAgentList;

		this.processDone = new ArrayList<String>();
		this.processNeed = processNeed;
		
		this.weightTransformer = new Transformer<SystemEdge,Integer>(){
	       	public Integer transform(SystemEdge edge) {return edge.getWeight();}
	     };
		
		this.systemGraph = new DirectedSparseGraph<SystemVertex, SystemEdge>();
	     	
		for (MaterialHandlingAgent agent : materialHandlingList){
			systemGraph = Controllers_GraphHelper.addGraph(systemGraph, agent.getGraph(0));
			agent.addListeningPart(this);
		}
		
		for (ManufacturingProcessAgent agent : manufacturingList){
			systemGraph = Controllers_GraphHelper.addGraph(systemGraph, agent.getGraph(0));
			agent.addListeningPart(this);
		}
		
		for (CollectionAgent agent : collectionAgentList){
			systemGraph = Controllers_GraphHelper.addGraph(systemGraph, agent.getGraph(0));
			agent.addListeningPart(this);
		}
		
		controllabilityGraph = Controllers_GraphHelper.controllabilityGraph(systemGraph);
		observabilityGraph = Controllers_GraphHelper.observabilityGraph(systemGraph);
		
		//Uncomment if you want to create a text file of the compiled graph vertices and edges
		Controllers_GraphHelper.createTextFile(controllabilityGraph, "_controllabilityGraph.txt","Controllability Graph");
		//Controllers_GraphHelper.createTextFile(observabilityGraph, "_observabilityGraph.txt","Observability Graph");
		//Controllers_GraphHelper.createTextFile(cleanSystemGraph, "_cleanPartGraph.txt","Part Clean Graph");
		Controllers_GraphHelper.createTextFile(systemGraph, "_partGraph.txt","Part Total Graph");
	}
}