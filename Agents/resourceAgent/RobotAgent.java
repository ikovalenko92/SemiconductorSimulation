package resourceAgent;


import java.util.ArrayList;

import Robot.RobotLLC;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import repast.simphony.ui.probe.ProbedProperty;
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;
import sharedInformation.ResourceAgent;

public class RobotAgent implements ResourceAgent {
	
	private RobotLLC robot;
	private boolean working;
	private String program;
	private DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> robotGraph;
	private DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> robotOutputGraph;
	private CapabilitiesEdge runningEdge;
	private Object holdingObject;
	private Grid<Object> grid;


	/**
	 * @param name
	 * @param robotLLC
	 * @param robotGraph
	 * @param physicalGrid
	 */
	public RobotAgent(String name, RobotLLC robot){
		this.robot = robot;
		this.working = false;
		this.robotOutputGraph = new DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge>();
		
		this.runningEdge = null;
		
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
	
	/*	@ScheduledMethod (start = 5)
	public void testRobot(){
		this.robot.runMoveObjectProgram(this.robot.getProgramList().remove(0));
	}
	
	@ScheduledMethod (start = 75)
	public void testRobot2(){
		this.robot.runMoveObjectProgram(this.robot.getProgramList().remove(1));
	}
	
	@ScheduledMethod (start = 140)
	public void testRobot3(){
		this.robot.runMoveObjectProgram(this.robot.getProgramList().remove(2));
	}*/
}