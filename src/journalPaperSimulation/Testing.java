package journalPaperSimulation;

import java.awt.Point;
import java.util.ArrayList;

import Buffer.BufferLLC;
import Part.Part;
import intelligentProduct.LotProductAgent;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import resourceAgent.BufferAgent;
import resourceAgent.ResourceAgent;
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;
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
		desiredList.add("S2");
		CapabilitiesNode startingNode = new CapabilitiesNode(this.listBufferLLC.get(0).getBuffer(), null, new PhysicalProperty(this.bufferLocations[0]));
		LotProductAgent productAgent = new LotProductAgent(this.part, desiredList, this.listBufferAgent.get(0), startingNode, 0);
		
		productAgent.informEvent(new CapabilitiesEdge(this.listBufferAgent.get(0), null, startingNode, null, 0));
		cyberContext.add(productAgent);
	}
	
	@ScheduledMethod (start = 6)
	public void check() throws ClassNotFoundException{
		//Iterable<Class> a = this.cyberContext.getAgentTypes();
		for (Object resourceAgent : this.cyberContext.getObjects(Class.forName("resourceAgent.ResourceAgent"))){
			System.out.println(((ResourceAgent) resourceAgent).getSchedule());
		}
	}
}
