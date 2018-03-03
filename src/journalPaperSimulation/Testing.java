package journalPaperSimulation;

import java.awt.Point;
import java.util.ArrayList;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import Buffer.BufferLLC;
import Part.Part;
import intelligentProduct.ProductAgentInstance;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import resourceAgent.BufferAgent;
import resourceAgent.ResourceAgent;
import sharedInformation.ResourceEvent;
import sharedInformation.ProductState;
import sharedInformation.PhysicalProperty;



//NOT USING RIGHT NOW
public class Testing {

	private Point exitPoint = new Point (142,60);
	private Point exitHumanPointPlace = new Point (6,10);
	
	private Grid<Object> physicalGrid;
	private Context<Object> cyberContext;

	public Testing(Grid<Object> physicalGrid, Context<Object> cyberContext) {	
		this.physicalGrid = physicalGrid;
		this.cyberContext = cyberContext;
	}
	
	@ScheduledMethod ( start = 1 , interval = 3000, priority = -150)
	public void runTest() {	
		String outputS1 = "";
		String outputS2 = "";
		
		Class desiredType = null;
		for (Class typee :cyberContext.getAgentTypes()){
			if (typee.toString().contains("ProductAgent")){
				 desiredType = typee;
			}
		}
		
		for (Object object:this.physicalGrid.getObjectsAt(exitPoint.x,exitPoint.y)){
			if (object.toString().contains("art")){
				outputS1 = outputS1+", "+object.toString();
				for (Object PA : cyberContext.getObjects(desiredType)){
					//System.out.println(((ProductAgentInstance) PA).getProductHistory());
					int a =5;
				}
				
			}
		}
		
		for (Object object:this.physicalGrid.getObjectsAt(exitHumanPointPlace.x,exitHumanPointPlace.y)){
			if (object.toString().contains("art")){
				outputS2 = outputS2+", "+object.toString();
			}
		}
		
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		System.out.println(schedule.getTickCount());
		System.out.println("Completed: " + outputS1);
		System.out.println("Exited: " + outputS2);

	}
	
}
