package sharedInformation;

import java.lang.reflect.Method;
import java.util.Arrays;

public class CapabilitiesEdge {
	
	private CapabilitiesNode parent;
	private CapabilitiesNode child;
	private int weight;
	
	private Object activeObject;
	private Method activeMethod;
	private Integer[] activeParameters;
	
	private boolean controllability;
	private boolean observability;
	
	private Object deactiveObject;
	private Method deactiveMethod;
	private Object[] deactiveParameters;	

	/**
	 * @param agent
	 * @param parent
	 * @param child
	 * @param activeMethod
	 * @param parameter
	 * @param weight
	 */
	public CapabilitiesEdge(Object agent, CapabilitiesNode parent, CapabilitiesNode child, Method activeMethod, Integer parameter, int weight){
		this.parent = parent;
		this.child = child;
		
		this.activeObject = agent;
		this.activeMethod = activeMethod;
		
		if (parameter !=null){
			this.activeParameters = new Integer[]{parameter};
		}
		else{
			this.activeParameters = null;}
		
		this.deactiveObject = null;
		this.deactiveMethod = null;
		this.deactiveParameters = null;
		
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
		
	public Method getActiveMethod() {
		return this.activeMethod;
	}
	
	public Object getActiveObject() {
		return this.activeObject;
	}
	
	public Object[] getActiveParams() {
		return this.activeParameters;
	}
	
	//================================================================================
    // Deactivate Edge
    //================================================================================
	
	/**
	 * @param deactiveObject the deactiveObject to set
	 */
	public void setDeactiveObject(Object deactiveObject) {
		this.deactiveObject = deactiveObject;
	}


	/**
	 * @param deactiveMethod the deactiveMethod to set
	 */
	public void setDeactiveMethod(Method deactiveMethod) {
		this.deactiveMethod = deactiveMethod;
	}

	/**
	 * @param deactiveParameters the deactiveParameters to set
	 */
	public void setDeactiveParameters(Object[] deactiveParameters) {
		this.deactiveParameters = deactiveParameters;
	}
	
	public Method getDeactiveMethod() {
		return null;
	}
	
	public Object getDeactiveObject() {
		return null;
	}
	
	public Object[] getDeactiveParams() {
		return null;
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
		if (this.activeParameters!=null){
			edge = new CapabilitiesEdge(this.activeObject, parent, child, activeMethod, this.activeParameters[0], this.weight);
		}
		else{
			edge = new CapabilitiesEdge(this.activeObject, parent, child, activeMethod, null, this.weight);
		}
			
		edge.setControllability(this.controllability);
		edge.setObservability(this.observability);
		
		return edge;
	}
	
	@Override
	public String toString(){
		String activeParametersString = "";
	
	if (this.activeParameters != null){activeParametersString = this.activeParameters[0].toString();}
	
	return "Edge with weight " + this.weight + " goes from " + parent.getLocation().x + "," + parent.getLocation().y + " to " + child.getLocation().x + "," + child.getLocation().y + ".\n"
			+ "Makes " + parent.getProcessCompleted() + " to " + child.getProcessCompleted() + ".\n"
			+ " Activated by: " + this.activeObject + " using " + this.activeMethod.getName() + "(" + activeParametersString + ")\n"
			+ " Controllable: " + this.controllability + ", Observable: " + this.observability;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activeMethod == null) ? 0 : activeMethod.hashCode());
		result = prime * result + ((activeObject == null) ? 0 : activeObject.hashCode());
		result = prime * result + Arrays.hashCode(activeParameters);
		result = prime * result + ((child == null) ? 0 : child.hashCode());
		result = prime * result + (controllability ? 1231 : 1237);
		result = prime * result + ((deactiveObject == null) ? 0 : deactiveObject.hashCode());
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
		if (activeMethod == null) {
			if (other.activeMethod != null) {
				return false;
			}
		} else if (!activeMethod.equals(other.activeMethod)) {
			return false;
		}
		if (activeObject == null) {
			if (other.activeObject != null) {
				return false;
			}
		} else if (!activeObject.equals(other.activeObject)) {
			return false;
		}
		if (!Arrays.equals(activeParameters, other.activeParameters)) {
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
		if (deactiveObject == null) {
			if (other.deactiveObject != null) {
				return false;
			}
		} else if (!deactiveObject.equals(other.deactiveObject)) {
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