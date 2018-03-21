package Testing;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import com.sun.javafx.geom.Edge;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import Buffer.BufferLLC;
import Machine.Machine;
import Part.Part;
import intelligentProduct.ProductAgentInstance;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import resourceAgent.BufferAgent;
import resourceAgent.MachineAgent;
import resourceAgent.ResourceAgent;
import sharedInformation.ResourceEvent;
import sharedInformation.ProductState;
import sharedInformation.PhysicalProperty;

public class TestingBrokenMachine {

	private Point exitPoint = new Point (142,60);
	private Point exitHumanPointPlace = new Point (6,10);
	
	private Grid<Object> physicalGrid;
	private Context<Object> cyberContext;
	private Context<Object> physicalContext;
	private ArrayList<Machine> brokenMachines;
	
	public String prefix;

	public TestingBrokenMachine(Grid<Object> physicalGrid, Context<Object> cyberContext, Context<Object> physicalContext,
			int startTime, int midTime, int endTime, Point exitPoint,Point exitHumanPointPlace, String prefix) {	
		this.physicalGrid = physicalGrid;
		this.cyberContext = cyberContext;
		this.physicalContext = physicalContext;
		
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		schedule.schedule(ScheduleParameters.createOneTime(startTime,-145), this, "setMachineBroken");		
		schedule.schedule(ScheduleParameters.createOneTime(midTime,-145), this, "runTest",new Object[]{"3"});	
		schedule.schedule(ScheduleParameters.createOneTime(endTime,-145), this, "runTest",new Object[]{"4"});			
		//schedule.schedule(ScheduleParameters.createOneTime(endTime,-145), this, "fixMachine");		
		
		this.prefix = prefix;
	
	}
	
	public void setMachineBroken(){
		Class desClass = null;
		for (Object clas:this.physicalContext.getAgentTypes()){
			if(clas.toString().contains("Machine")){
				desClass = (Class) clas;
				break;
			}
		}
		
		this.brokenMachines = new ArrayList<Machine>();
		for (Object object:this.physicalContext.getObjects(desClass)){
			if(object.toString().contains("TM") || object.toString().contains("TN")){
				((Machine) object).setBroken();
				brokenMachines.add((Machine) object);
			}
		}
	}
	
	public void runTest(String suffix) {	
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
		
		ArrayList<Object> cyberContextRemove = new ArrayList<Object>();
	
	
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
						cyberContextRemove.add(PAinst);
					}
				}
			}
		}
		
		
		this.cyberContext.removeAll(cyberContextRemove);
		
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
			PrintWriter out = new PrintWriter(this.prefix+"outFile"+suffix+".txt");
			out.println(PAHistory);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void fixMachine(){
		for (Machine machine:this.brokenMachines){
			machine.fix();
		}
	}
	
}
