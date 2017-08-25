package sharedInformation;

import java.lang.reflect.Method;
import java.util.Arrays;

public class CapabilitiesEdge {
	
	private CapabilitiesNode parent;
	private CapabilitiesNode child;
	private int weight;
	
	private Object activeObject;
	private String activeMethod;
	
	private boolean controllability;
	private boolean observability;
	
	/**
	 * @param agent
	 * @param parent
	 * @param child
	 * @param activeMethod
	 * @param weight
	 */
	public CapabilitiesEdge(Object agent, CapabilitiesNode parent, CapabilitiesNode child, String activeMethod, int weight){
		this.parent = parent;
		this.child = child;
		
		this.activeObject = agent;
		this.activeMethod = activeMethod;
		
		this.weight = weight;
		
		this.controllability = false;
		this.observability = false;
	}

	
	public CapabilitiesNode getParent(){
		return this.parent;
	}
	
	public CapabilitiesNode getChild(){
		return this.child;
	}
	
	public int getWeight(){
		return this.weight;
	}
		
	public String getActiveMethod() {
		return this.activeMethod;
	}
	
	public Object getActiveObject() {
		return this.activeObject;
	}
	
	//================================================================================
    // Setters
    //================================================================================
	
	public void setWeight(int weight){
		this.weight = weight;
	}
	
	public void setChild(CapabilitiesNode child) {
		this.child = child;
	}
	
	//================================================================================
    // Controllability and observability
    //================================================================================
	
	public void setControllability(boolean controllability) {
		this.controllability = controllability;
	}
	
	public void setObservability(boolean observability) {
		this.observability = observability;
	}

	public boolean getControllability() {
		return this.controllability;
	}

	public boolean getObservability() {
		return this.observability;
	}
	
	//================================================================================
    // Controllability and observability
    //================================================================================
	
	/**
	 * @return a copy of this edge
	 */
	public CapabilitiesEdge copy(){
		CapabilitiesEdge edge = null;
		edge = new CapabilitiesEdge(this.activeObject, parent, child, activeMethod, this.weight);
			
		edge.setControllability(this.controllability);
		edge.setObservability(this.observability);
		
		return edge;
	}
	
	@Override
	public String toString(){
	
	return "Edge with weight " + this.weight + " goes from " + parent.getLocation().x + "," + parent.getLocation().y + " to " + child.getLocation().x + "," + child.getLocation().y + ".\n"
			+ "Makes " + child.getProcessCompleted() + ".\n"
			+ " Activated by: " + this.activeObject + " using " + this.activeMethod;//.getName() + "(" + activeParametersString + ")\n"
			//+ " Controllable: " + this.controllability + ", Observable: " + this.observability;
	}

}