package journalPaperSimulation;

import java.awt.Point;
import java.util.ArrayList;

import Buffer.BufferLLC;
import intelligentProduct.LotProductAgent;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import resourceAgent.BufferAgent;
import sharedInformation.CapabilitiesNode;
import sharedInformation.PhysicalProperty;

public class Testing {


	private ArrayList<BufferLLC> listBufferLLC;
	private Point[] bufferLocations;
	private Context<Object> cyberContext;
	private ArrayList<BufferAgent> listBufferAgent;

	public Testing(ArrayList<BufferLLC> listBufferLLC, Point[] bufferLocations, ArrayList<BufferAgent> listBufferAgent, Context<Object> cyberContext) {
		this.listBufferLLC = listBufferLLC;
		this.listBufferAgent = listBufferAgent;
		this.bufferLocations = bufferLocations;
		this.cyberContext = cyberContext;
	}

	@ScheduledMethod (start = 2)
	public void start(){
		ArrayList<String> desiredList = new ArrayList<String>();
		desiredList.add("S2");
		CapabilitiesNode startingNode = new CapabilitiesNode(this.listBufferLLC.get(0).getBuffer(), null, new PhysicalProperty(this.bufferLocations[0]));
		LotProductAgent productAgent = new LotProductAgent("testAgent", desiredList, this.listBufferAgent.get(0), startingNode);
		cyberContext.add(productAgent);
	}
}
