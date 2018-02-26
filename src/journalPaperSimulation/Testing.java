package journalPaperSimulation;

import java.awt.Point;
import java.util.ArrayList;

import Buffer.BufferLLC;
import Part.Part;
import intelligentProduct.ProductAgentInstance;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import resourceAgent.BufferAgent;
import resourceAgent.ResourceAgent;
import sharedInformation.ResourceEvent;
import sharedInformation.ProductState;
import sharedInformation.PhysicalProperty;

public class Testing {


	private ArrayList<BufferLLC> listBufferLLC;
	private Point[] bufferLocations;
	private Context<Object> cyberContext;
	private ArrayList<BufferAgent> listBufferAgent;
	private Part part;

	public Testing(ArrayList<BufferLLC> listBufferLLC, Point[] bufferLocations, ArrayList<BufferAgent> listBufferAgent,
			Context<Object> cyberContext, Part part) {
		this.listBufferLLC = listBufferLLC;
		this.listBufferAgent = listBufferAgent;
		this.bufferLocations = bufferLocations;
		this.cyberContext = cyberContext;
		this.part = part;
	}

	@ScheduledMethod (start = 2)
	public void start(){
		ArrayList<String> desiredList = new ArrayList<String>();
		desiredList.add("S1");
		desiredList.add("S2");
		desiredList.add("S3");
		desiredList.add("S4");
		desiredList.add("S5");
		desiredList.add("S6");
		ProductState startingNode = new ProductState(this.listBufferLLC.get(0).getBuffer(), null, new PhysicalProperty(this.bufferLocations[0]));
		ProductAgentInstance productAgentInstance = new ProductAgentInstance(this.part, desiredList, this.listBufferAgent.get(0), startingNode, 0);
		
		productAgentInstance.informEvent(new ResourceEvent(this.listBufferAgent.get(0), null, startingNode, null, 0));
		cyberContext.add(productAgentInstance);
	}
	
	
	public void check() throws ClassNotFoundException{
		//Iterable<Class> a = this.cyberContext.getAgentTypes();
		for (Object resourceAgent : this.cyberContext.getObjects(Class.forName("resourceAgent.ResourceAgent"))){
			System.out.println(((ResourceAgent) resourceAgent).getSchedule());
		}
	}
}
