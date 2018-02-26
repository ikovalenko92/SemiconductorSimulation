package sharedInformation;

import resourceAgent.ResourceAgent;

public class ResourceEvent {
	
	private ProductState parent;
	private ProductState child;
	private int eventTime;
	
	private ResourceAgent eventAgent;
	private String activeMethod;
	
	private boolean controllability;
	private boolean observability;
	
	/**
	 * @param agent
	 * @param parent
	 * @param child
	 * @param activeMethod (string)
	 * @param eventTime
	 */
	public ResourceEvent(ResourceAgent agent, ProductState parent, ProductState child, String activeMethod, int eventTime){
		this.parent = parent;
		this.child = child;
		
		this.eventAgent = agent;
		this.activeMethod = activeMethod;
		
		this.eventTime = eventTime;
		
		this.controllability = false;
		this.observability = false;
	}
	
	public ProductState getParent(){
		return this.parent;
	}
	
	public ProductState getChild(){
		return this.child;
	}
	
	public int getEventTime(){
		return this.eventTime;
	}
		
	public String getActiveMethod() {
		return this.activeMethod;
	}
	
	public ResourceAgent getEventAgent() {
		return this.eventAgent;
	}
	
	//================================================================================
    // Setters
    //================================================================================
	
	public void setWeight(int weight){
		this.eventTime = weight;
	}
	
	public void setChild(ProductState child) {
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
	public ResourceEvent copy(){
		ResourceEvent edge = null;
		edge = new ResourceEvent(this.eventAgent, parent, child, activeMethod, this.eventTime);
			
		edge.setControllability(this.controllability);
		edge.setObservability(this.observability);
		
		return edge;
	}
	
	@Override
	public String toString(){
	
	return "Edge with weight " + this.eventTime + " goes from " + parent.getLocation().x + "," + parent.getLocation().y + " to " + child.getLocation().x + "," + child.getLocation().y + ".\n"
			+ "Makes " + child.getProcessCompleted() + ".\n"
			+ " Activated by: " + this.eventAgent + " using " + this.activeMethod + "\n";//.getName() + "(" + activeParametersString + ")\n"
			//+ " Controllable: " + this.controllability + ", Observable: " + this.observability;
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventAgent == null) ? 0 : eventAgent.hashCode());
		result = prime * result + ((activeMethod == null) ? 0 : activeMethod.hashCode());
		result = prime * result + ((child == null) ? 0 : child.hashCode());
		result = prime * result + (controllability ? 1231 : 1237);
		result = prime * result + (observability ? 1231 : 1237);
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + eventTime;
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
		if (!(obj instanceof ResourceEvent)) {
			return false;
		}
		ResourceEvent other = (ResourceEvent) obj;
		if (eventAgent == null) {
			if (other.eventAgent != null) {
				return false;
			}
		} else if (!eventAgent.equals(other.eventAgent)) {
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
		if (eventTime != other.eventTime) {
			return false;
		}
		return true;
	}
	
	

}