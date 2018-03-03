package initializingAgents;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import Buffer.Buffer;
import Part.Part;
import Part.RFIDTag;
import intelligentProduct.ProductAgentInstance;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.space.grid.Grid;
import resourceAgent.BufferAgent;
import resourceAgent.ExitAgent;
import sharedInformation.ResourceEvent;
import sharedInformation.ProductState;
import sharedInformation.PhysicalProperty;

public class PartCreatorforBuffer {

	private BufferAgent bufferAgent;
	private Buffer buffer;
	private ResourceEvent exitEvent;
	private ExitAgent exitRA;
	
	private Grid<Object> physicalGrid;
	private Context<Object> cyberContext;
	private Context<Object> physicalContext;

	private double startTime;
	private double intervalTime;
	
	private int PAnumber;
	
	/**
	 * @param buffer
	 * @param bufferAgent
	 * @param exitRA
	 * @param physicalGrid
	 * @param physicalContext
	 * @param cyberContext
	 */
	public PartCreatorforBuffer(Buffer buffer, BufferAgent bufferAgent, ExitAgent exitRA, Grid<Object> physicalGrid, Context<Object> physicalContext, Context<Object> cyberContext) {
		this.startTime = 5;
		this.intervalTime = 300;
		
		this.buffer = buffer;
		this.bufferAgent = bufferAgent;
		
		this.physicalGrid = physicalGrid;
		this.physicalContext = physicalContext;
		this.cyberContext = cyberContext;
		
		this.exitRA = exitRA;
		this.exitEvent = new ResourceEvent(exitRA, null, null, exitRA.getExitEventName(), 1);
				
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		schedule.schedule(ScheduleParameters.createRepeating(startTime,intervalTime), this, "startPartAgentCreation");
		
		this.PAnumber = -1;
	}
	
	/**
	 * Creates a physical part and the agent
	 */
	public void startPartAgentCreation(){
		//set the part type
		char partType = 'a';	
		this.PAnumber = this.PAnumber+1;
		
		//Part and agent will be based on the storage of the bufer
		Point storagePoint = this.buffer.getStoragePoint();
		
		// Create a new physical part
		Part part = new Part(new RFIDTag(PAnumber+"",partType));
		physicalContext.add(part);
		physicalGrid.moveTo(part, storagePoint.x, storagePoint.y);
		
		// Create a new Product Agent 
		ProductState startingNode = new ProductState(buffer, null, new PhysicalProperty(storagePoint));
		ProductAgentInstance productAgentInstance = new ProductAgentInstance(part, bufferAgent, startingNode, 0, 
				getProductionPlan(partType), getExitPlan(partType));
		
		// Inform the PA that it was created
		DirectedSparseGraph<ProductState,ResourceEvent> bid = new DirectedSparseGraph<ProductState,ResourceEvent>();
		ResourceEvent newEvent = new ResourceEvent(bufferAgent, startingNode, startingNode, null, 0);
		bid.addEdge(newEvent,newEvent.getParent(),newEvent.getChild());
		ArrayList<ResourceEvent> eventList = new ArrayList<ResourceEvent>();
		eventList.add(newEvent);
		productAgentInstance.informEvent(bid,newEvent.getChild(), eventList);

		//Set the PA number
		productAgentInstance.setPANumber(this.PAnumber);
		
		cyberContext.add(productAgentInstance);
	}
	
	/**
	 * @param partType
	 * @return the list of desired processes for this part type
	 */
	public ProductionPlan getProductionPlan(char partType){
		ProductionPlan productionPlan = new ProductionPlan();
		
		if (partType == 'a'){
			productionPlan.add(new PhysicalProperty("S1"));
			
			HashSet<PhysicalProperty> set2 = new HashSet<PhysicalProperty>();
			set2.add(new PhysicalProperty("S2"));
			productionPlan.addNewSet(set2);
			
			HashSet<PhysicalProperty> set3 = new HashSet<PhysicalProperty>();
			set3.add(new PhysicalProperty("S3"));
			productionPlan.addNewSet(set3);
			
			HashSet<PhysicalProperty> set4 = new HashSet<PhysicalProperty>();
			set4.add(new PhysicalProperty("S4"));
			productionPlan.addNewSet(set4);
			
			HashSet<PhysicalProperty> set5 = new HashSet<PhysicalProperty>();
			set5.add(new PhysicalProperty("S5"));
			productionPlan.addNewSet(set5);
			
			HashSet<PhysicalProperty> set6 = new HashSet<PhysicalProperty>();
			set6.add(new PhysicalProperty("S6"));
			productionPlan.addNewSet(set6);
			
			HashSet<PhysicalProperty> end = new HashSet<PhysicalProperty>();
			end.add(new PhysicalProperty("End"));
			productionPlan.addNewSet(end);
		}
		
		return productionPlan;
	}
	
	public ExitPlan getExitPlan(char partType){
		return new ExitPlan(this.exitRA,this.exitEvent);
	}
}
