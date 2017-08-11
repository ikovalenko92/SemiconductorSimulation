package sharedInformation;

import java.lang.reflect.Method;

public interface SystemEdge {
	
	public SystemEdge copy();
	
	//Standard edge information for a directed weighted graph
	public SystemVertex getParent();
	public SystemVertex getChild();
	public int getWeight();
	
	/**
	 * @return The method to invoke 
	 */
	public Method getActiveMethod();
	/**
	 * @return The object that needs to invoke the method
	 */
	public Object getActiveObject();
	
	/**
	 * @return The parameters that need to be called with the method
	 */
	public Object[] getActiveParams();
	
	/**
	 * @return The method to invoke to deactivate edge
	 */
	public Method getDeactiveMethod();
	/**
	 * @return The object that needs to invoke the method to deactivate edge
	 */
	public Object getDeactiveObject();
	
	/**
	 * @return The parameters that need to be called with the method to deactivate edge
	 */
	public Object[] getDeactiveParams();

	public void setWeight(int Weight);
	
	public void setChild(SystemVertex child);
	
	public String toString();
	
	public void setControllability(boolean b);
	
	public void setObservability(boolean b);
	
	public boolean getControllability();
	
	boolean getObservability();
}