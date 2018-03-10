package Testing;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.sun.javafx.geom.Edge;

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
public class Testing1 {

	private Point exitPoint = new Point (142,60);
	private Point exitHumanPointPlace = new Point (6,10);
	
	private Grid<Object> physicalGrid;
	private Context<Object> cyberContext;
	private Context<Object> physicalContext;

	public Testing1(Grid<Object> physicalGrid, Context<Object> cyberContext, Context<Object> physicalContext) {	
		this.physicalGrid = physicalGrid;
		this.cyberContext = cyberContext;
		this.physicalContext = physicalContext;
	}
	
	@ScheduledMethod ( start = 50000, priority = -149)
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
		
		for (Object o:removeList){
			this.physicalContext.remove(o);
		}
	}
	
	@ScheduledMethod ( start = 100000, priority = -150)
	public void runTest() {	
		String outputS1 = "";
		String outputS2 = "";
		String PAHistory = "";
		
		Class desiredType = null;
		//Find an example product agent to get its class;;
		for (Class typee :cyberContext.getAgentTypes()){
			if (typee.toString().contains("ProductAgent")){
				 desiredType = typee;
			}
		}
	
		//For each object at the end
		for (Object object:this.physicalGrid.getObjectsAt(exitPoint.x,exitPoint.y)){
			//Find the parts
			if (object.toString().contains("art")){
				outputS1 = outputS1+", "+object.toString(); //for output to console

				//Find the Agent associated with the physical part
				for (Object PAinst : cyberContext.getObjects(desiredType)){
					ProductAgentInstance PA = (ProductAgentInstance) PAinst;
					if (PA.toString().contains(object.toString())){
						//Obtain its product history based on specified format
						for (ResourceEvent edge:(PA.getProductHistory())){
							String points = "" + edge.getParent().getLocation().x+ "," + edge.getParent().getLocation().y+"," + 
									edge.getChild().getLocation().x+","+edge.getChild().getLocation().y;
							
							//Remove initial edge
							if (edge.getEventAgent()!=null){
								int edgeTrueTime = edge.getEventAgent().getCapabilities().
										findEdge(edge.getParent(),edge.getChild()).getEventTime();
								String waitingTimeAtParent = String.valueOf(edge.getEventTime()-edgeTrueTime);
								String eventTime = String.valueOf(edgeTrueTime);
								PAHistory = PAHistory+ points + ","+waitingTimeAtParent+ ","+ eventTime +"\n";
							}
						}
					}
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
		

		try {
			PrintWriter out = new PrintWriter("outFile1.txt");
			out.println(PAHistory);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
