package resourceAgent;

import intelligentProduct.ProductAgent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.collections15.Transformer;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import sharedInformation.PhysicalProperty;
import sharedInformation.ProductState;
import sharedInformation.RASchedule;
import sharedInformation.ResourceEvent;
import Part.Part;
import Robot.Robot;
import Robot.RobotLLC;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class ExitAgent implements ResourceAgent{

	private Grid<Object> grid;
	private String exitEventName;
	private Robot robot;
	
	/**
	 * @param name
	 * @param physicalGrid 
	 * @param tableLocationObject - table of physical points and the corresponding physical object
	 * @param robotLLC
	 */
	public ExitAgent(String name, Robot robot, String exitEventName, Grid<Object> physicalGrid){
		this.robot = robot;
		this.grid = physicalGrid;
		this.exitEventName = exitEventName;
	}

	@Override
	public boolean query(ResourceEvent exitEventName, ProductAgent productAgent) {
		
		//The queried event must have the word exit
		if (exitEventName.getActiveMethod().contains("exit")){
			for (Object object: grid.getObjects())
				if (object.toString().contains(productAgent.getPartName())){
					GridPoint point = this.grid.getLocation(object);
					// Move the person to this point
					this.grid.moveTo(robot,point.getX()+2,point.getY());
					
					//In the next time-step move the object and the person to the "failed" parts location
					ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
					schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount()+1),this.grid,
							"moveTo", new Object[]{object,new int[]{this.robot.getCenter().x-4,this.robot.getCenter().y}});
					schedule.schedule(ScheduleParameters.createOneTime(schedule.getTickCount()+1),this.grid,
							"moveTo", new Object[]{this.robot,new int[]{this.robot.getCenter().x,this.robot.getCenter().y}});
			}
		}
		return true;
	}
	
	public String getExitEventName() {
		return exitEventName;
	}

	@Override
	public String toString() {
		return "Exit Agent";
	}

	
	//================================================================================
    // Product agent scheduling
    //================================================================================
	
	@Override
	public RASchedule getSchedule() {
		return null;
	}

	@Override
	public boolean requestScheduleTime(ProductAgent productAgent, ResourceEvent edge, int startTime, int endTime) {
		return true;
	}


	@Override
	public boolean removeScheduleTime(ProductAgent productAgent, int startTime, int endTime) {
		return true;
	}
	
	//================================================================================
    // Product agent communication
    //================================================================================

	/**
	 * @return
	 */
	@Override
	public DirectedSparseGraph<ProductState, ResourceEvent> getCapabilities() {
		return null;
	}

	@Override
	public void addNeighbor(ResourceAgent neighbor) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public ArrayList<ResourceAgent> getNeighbors() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void teamQuery(ProductAgent productAgent,
			PhysicalProperty desiredProperty, ProductState currentNode,
			int maxTime, DirectedSparseGraph<ProductState, ResourceEvent> bid,
			int currentTime) {
		
	}


}