package Testing;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.sun.javafx.geom.Edge;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import Buffer.BufferLLC;
import Part.Part;
import initializingAgents.PartCreatorforBuffer;
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
public class TestingNewProductType {

	private Point exitPoint = new Point (142,60);
	private Point exitHumanPointPlace = new Point (6,10);
	
	private Grid<Object> physicalGrid;
	private Context<Object> cyberContext;
	private Context<Object> physicalContext;
	private PartCreatorforBuffer partCreator;

	public TestingNewProductType(Grid<Object> physicalGrid, Context<Object> cyberContext, Context<Object> physicalContext,
			PartCreatorforBuffer partCreator, int[] injectTime, int endTime, Point exitPoint,Point exitHumanPointPlace) {	
		this.physicalGrid = physicalGrid;
		this.cyberContext = cyberContext;
		this.physicalContext = physicalContext;
		this.partCreator = partCreator;
		
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		schedule.schedule(ScheduleParameters.createOneTime(injectTime[0],-145), this, "newProductVariety");		
		schedule.schedule(ScheduleParameters.createOneTime(injectTime[1],-145), this, "oldProductVariety");		
		schedule.schedule(ScheduleParameters.createOneTime(endTime,-145), this, "runTest");		
	}
	
	public void newProductVariety(){
		partCreator.setPartType('b');
	}
	
	public void oldProductVariety(){
		partCreator.setPartType('a');
	}
	
	public void runTest() {	
		String outputS1 = "";
		String outputS2 = "";
		String outputS3 = "";
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
			
			System.out.println(object);
			
			//Find the parts
			if (object.toString().contains("art")){
				

				//Find the Agent associated with the physical part
				for (Object PAinst : cyberContext.getObjects(desiredType)){
					ProductAgentInstance PA = (ProductAgentInstance) PAinst;
					
					if (PA.toString().contains(object.toString())){
						//Obtain its product history based on specified format
						
						if (PA.getPartName().contains("b")){
							outputS3 = outputS3+", "+object.toString(); //for output to console if part b
						}
						else{
							outputS1 = outputS1+", "+object.toString(); //for output to console
						}
						
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
		System.out.println("Completed new: " + outputS3);
		

		try {
			PrintWriter out = new PrintWriter("outFile3.txt");
			out.println(PAHistory);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
