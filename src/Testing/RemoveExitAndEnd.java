package Testing;

import intelligentProduct.ProductAgentInstance;

import java.awt.Point;
import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.space.grid.Grid;

public class RemoveExitAndEnd {

	private Grid<Object> physicalGrid;
	private Context<Object> cyberContext;
	private Context<Object> physicalContext;
	private Point exitPoint;
	private Point exitHumanPointPlace;

	/**
	 * @param physicalGrid
	 * @param cyberContext
	 * @param physicalContext
	 * @param startTimes
	 * @param exitPoint
	 * @param exitHumanPointPlace
	 */
	public RemoveExitAndEnd(Grid<Object> physicalGrid, Context<Object> cyberContext, Context<Object> physicalContext,
			int[] startTimes, Point exitPoint,Point exitHumanPointPlace) {	
		this.physicalGrid = physicalGrid;
		this.cyberContext = cyberContext;
		this.physicalContext = physicalContext;
		
		this.exitPoint = exitPoint;
		this.exitHumanPointPlace = exitHumanPointPlace;
		
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();

		for(int startTime:startTimes){
			schedule.schedule(ScheduleParameters.createOneTime(startTime,-145), this, "removeFinal");		
		}
	}
	
	
	/**
	 * Removes the objects at both the exit and end of the system
	 */
	public void removeFinal(){
		ArrayList<Object> removeList = new ArrayList<Object>();
		
		for (Object object:this.physicalGrid.getObjects()){
			if (object.getClass().toString().contains("Part")){
				int x_coor = this.physicalGrid.getLocation(object).getX();
				int y_coor = this.physicalGrid.getLocation(object).getY();
				
				if (x_coor == exitPoint.x && y_coor == exitPoint.y){
					removeList.add(object);
				}
				if (x_coor == exitHumanPointPlace.x && y_coor == exitHumanPointPlace.y){
					removeList.add(object);
				}
			}
		}
		
		Class desiredType = null;
		//Find an example product agent to get its class;;
		for (Class typee :cyberContext.getAgentTypes()){
			if (typee.toString().contains("ProductAgent")){
				 desiredType = typee;
			}
		}
		
		ArrayList<Object> cyberContextRemove = new ArrayList<Object>();
		
		//For each object at the end
		for (Object object:this.physicalGrid.getObjectsAt(exitPoint.x,exitPoint.y)){
			//Find the parts
			if (object.toString().contains("art")){
				//Find the Agent associated with the physical part
				for (Object PAinst : cyberContext.getObjects(desiredType)){
					ProductAgentInstance PA = (ProductAgentInstance) PAinst;
					if (PA.toString().contains(object.toString())){
						cyberContextRemove.add(PAinst);
					}
				}
			}
		}
		
		//For each object at the exit
		for (Object object:this.physicalGrid.getObjectsAt(exitHumanPointPlace.x,exitHumanPointPlace.y)){
			//Find the parts
			if (object.toString().contains("art")){
				//Find the Agent associated with the physical part
				for (Object PAinst : cyberContext.getObjects(desiredType)){
					ProductAgentInstance PA = (ProductAgentInstance) PAinst;
					if (PA.toString().contains(object.toString())){
						cyberContextRemove.add(PAinst);
					}
				}
			}
		}
		
		for (Object o:removeList){
			this.physicalContext.remove(o);
			o = null;
		}
		
		for (Object object:cyberContextRemove){
			this.cyberContext.remove(object);
			object = null;
		}
	}
}
