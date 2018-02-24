package Part;

import java.awt.Point;
import java.util.ArrayList;

import Buffer.Buffer;
import intelligentProduct.ProductAgent;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.space.grid.Grid;
import resourceAgent.BufferAgent;
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;
import sharedInformation.PhysicalProperty;

public class PartCreatorforBuffer {

	private Context<Object> cyberContext;
	private BufferAgent bufferAgent;
	private Buffer buffer;
	private Context<Object> physicalContext;
	private Grid<Object> physicalGrid;
	private double startTime;
	private double intervalTime;
	
	private int PAnumber;
	
	public PartCreatorforBuffer(Buffer buffer, BufferAgent bufferAgent, Grid<Object> physicalGrid, Context<Object> physicalContext, Context<Object> cyberContext) {
		this.startTime = 5;
		this.intervalTime = 250;
		
		this.buffer = buffer;
		this.bufferAgent = bufferAgent;
		
		this.physicalGrid = physicalGrid;
		this.physicalContext = physicalContext;
		this.cyberContext = cyberContext;
				
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
		
		// Create a new Product Agent and inform the part that it was created
		CapabilitiesNode startingNode = new CapabilitiesNode(buffer, null, new PhysicalProperty(storagePoint));
		ProductAgent productAgent = new ProductAgent(part, getDesiredList(partType), bufferAgent, startingNode, 0);
		productAgent.informEvent(new CapabilitiesEdge(bufferAgent, null, startingNode, null, 0));
		cyberContext.add(productAgent);

		productAgent.setPANumber(this.PAnumber);
	}
	
	/**
	 * @param partType
	 * @return the list of desired processes for this part type
	 */
	public ArrayList<String> getDesiredList(char partType){
		ArrayList<String> desiredList = new ArrayList<String>();
		
		if (partType == 'a'){		
			desiredList.add("S1");
			desiredList.add("S2");
			desiredList.add("S3");
			desiredList.add("S4");
			desiredList.add("S5");
			desiredList.add("S6");
		}
		
		return desiredList;
	}
}
