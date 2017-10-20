package sharedInformation;

import java.lang.reflect.Method;
import java.util.Arrays;

import resourceAgent.ResourceAgent;

public class CapabilitiesEdge {
	
	private CapabilitiesNode parent;
	private CapabilitiesNode child;
	private int weight;
	
	private ResourceAgent activeAgent;
	private String activeMethod;
	
	private boolean controllability;
	private boolean observability;
	
	/**
	 * @param agent
	 * @param parent
	 * @param child
	 * @param activeMethod (string)
	 * @param weight
	 */
	public CapabilitiesEdge(ResourceAgent agent, CapabilitiesNode parent, CapabilitiesNode child, String activeMethod, int weight){
		this.parent = parent;
		this.child = child;
		
		this.activeAgent = agent;
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
	
	public ResourceAgent getActiveAgent() {
		return this.activeAgent;
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
		edge = new CapabilitiesEdge(this.activeAgent, parent, child, activeMethod, this.weight);
			
		edge.setControllability(this.controllability);
		edge.setObservability(this.observability);
		
		return edge;
	}
	
	@Override
	public String toString(){
	
	return "Edge with weight " + this.weight + " goes from " + parent.getLocation().x + "," + parent.getLocation().y + " to " + child.getLocation().x + "," + child.getLocation().y + ".\n"
			+ "Makes " + child.getProcessCompleted() + ".\n"
			+ " Activated by: " + this.activeAgent + " using " + this.activeMethod + "\n";//.getName() + "(" + activeParametersString + ")\n"
			//+ " Controllable: " + this.controllability + ", Observable: " + this.observability;
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activeAgent == null) ? 0 : activeAgent.hashCode());
		result = prime * result + ((activeMethod == null) ? 0 : activeMethod.hashCode());
		result = prime * result + ((child == null) ? 0 : child.hashCode());
		result = prime * result + (controllability ? 1231 : 1237);
		result = prime * result + (observability ? 1231 : 1237);
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + weight;
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CapabilitiesEdge)) {
			return false;
		}
		CapabilitiesEdge other = (CapabilitiesEdge) obj;
		if (activeAgent == null) {
			if (other.activeAgent != null) {
				return false;
			}
		} else if (!activeAgent.equals(other.activeAgent)) {
			return false;
		}
		if (activeMethod == null) {
			if (other.activeMethod != null) {
				return false;
			}
		} else if (!activeMethod.equals(other.activeMethod)) {
			return false;
		}
		if (child == null) {
			if (other.child != null) {
				return false;
			}
		} else if (!child.equals(other.child)) {
			return false;
		}
		if (controllability != other.controllability) {
			return false;
		}
		if (observability != other.observability) {
			return false;
		}
		if (parent == null) {
			if (other.parent != null) {
				return false;
			}
		} else if (!parent.equals(other.parent)) {
			return false;
		}
		if (weight != other.weight) {
			return false;
		}
		return true;
	}
	
	

}