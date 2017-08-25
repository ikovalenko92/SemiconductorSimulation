package resourceAgent;

import java.lang.reflect.Method;
import java.util.ArrayList;

import Machine.MachineLLC;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import intelligentProduct.ProductAgent;
import repast.simphony.engine.schedule.ScheduledMethod;
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;
import sharedInformation.PhysicalProperty;
import sharedInformation.RASchedule;
import sharedInformation.ResourceAgent;

public class MachineAgent implements ResourceAgent{

	private String name;
	private MachineLLC machine;
	private boolean working;
	private int program;
	
	private CapabilitiesEdge runningEdge;
	private DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> machineCapabilities;

	public MachineAgent(String name, MachineLLC machine){
		this.name = name;
		this.machine = machine;
		this.working = false;
		
		createOutputGraph();
	}

	//================================================================================
    // Product agent communication
    //================================================================================

	@Override
	public DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> getCapabilities() {
		return this.machineCapabilities;
	}


	@Override
	public boolean query(String program) {
		// TODO Auto-generated method stub
		return false;
	}

	//================================================================================
    // Product agent scheduling
    //================================================================================
	
	@Override
	public RASchedule getSchedule() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean requestScheduleTime(ProductAgent productAgent, int startTime) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean removeScheduleTime(ProductAgent productAgent, int startTime) {
		// TODO Auto-generated method stub
		return false;
	}

	//================================================================================
    // Product/resource team formation
    //================================================================================

	@Override
	public void teamQuery(ProductAgent productAgent, PhysicalProperty desiredProperty, int currentTime, int maxTime,
			ArrayList<ResourceAgent> teamList) {
		// TODO Auto-generated method stub
		
	}
	
	//================================================================================
    // Helper methods
    //================================================================================
	
	private void createOutputGraph() {
		
		
	}
	
	//================================================================================
    // Testing
    //================================================================================
	
	/*@ScheduledMethod (start = 30)
	public void testRobot(){
		this.machine.runProgram(this.machine.getProgramList().remove(0));
	}*/
}