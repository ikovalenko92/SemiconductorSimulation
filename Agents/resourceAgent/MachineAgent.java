package resourceAgent;

import java.util.ArrayList;

import Machine.MachineLLC;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import repast.simphony.engine.schedule.ScheduledMethod;
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;
import sharedInformation.ResourceAgent;

public class MachineAgent implements ResourceAgent{

	private String name;
	private MachineLLC machine;
	private boolean working;
	private int program;
	private DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> cncGraph;
	private DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> cncOutputGraph;
	//private ArrayList<ProductAgent> listeningParts;
	private CapabilitiesEdge runningEdge;

	public MachineAgent(String name, MachineLLC machine){
		this.name = name;
		this.machine = machine;
		this.working = false;
		
		//this.cncGraph = cncGraph;
		//this.cncOutputGraph = new DirectedSparseGraph<SystemVertex, SystemEdge>();
		
		//createOutputGraph();
	}

	@Override
	public DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> getCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean scheduleGraph() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void notify(Object object, CapabilitiesEdge edge) {
		// TODO Auto-generated method stub
		
	}
	
	//================================================================================
    // Testing
    //================================================================================
	
	/*@ScheduledMethod (start = 30)
	public void testRobot(){
		this.machine.runProgram(this.machine.getProgramList().remove(0));
	}*/
}