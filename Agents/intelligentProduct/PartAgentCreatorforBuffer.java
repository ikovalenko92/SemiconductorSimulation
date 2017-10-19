package intelligentProduct;

import java.awt.Point;
import java.util.ArrayList;

import Buffer.Buffer;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import resourceAgent.BufferAgent;
import resourceAgent.ResourceAgent;
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;
import sharedInformation.PhysicalProperty;

public class PartAgentCreatorforBuffer {

	private Context<Object> cyberContext;
	private BufferAgent bufferAgent;
	private Buffer buffer;
	private ArrayList<String> desiredList;

	public PartAgentCreatorforBuffer(Buffer buffer, BufferAgent bufferAgent, Context<Object> cyberContext) {
		this.desiredList = new ArrayList<String>();
		desiredList.add("S1");
		desiredList.add("S2");
		desiredList.add("S3");
		desiredList.add("S4");
		desiredList.add("S5");
		desiredList.add("S6");
		
		this.buffer = buffer;
		this.bufferAgent = bufferAgent;
		this.cyberContext = cyberContext;
		
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		schedule.schedule(ScheduleParameters.createOneTime(3.0), this, "startPartAgentCreation");
	}
	
	public void startPartAgentCreation(){
		CapabilitiesNode startingNode = new CapabilitiesNode(buffer, null, new PhysicalProperty(buffer.getStoragePoint()));
		LotProductAgent productAgent = new LotProductAgent(buffer.getPartatStorage(), desiredList, bufferAgent, startingNode, 0);
		
		productAgent.informEvent(new CapabilitiesEdge(bufferAgent, null, startingNode, null, 0));
		cyberContext.add(productAgent);
	}
}
