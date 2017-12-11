package intelligentProduct;

import sharedInformation.CapabilitiesEdge;

import java.util.ArrayList;

public class PAPlan {
	private ProductAgent productAgent;
	private ArrayList<String> actions;
	private ArrayList<Integer> startTimes;
	private ArrayList<CapabilitiesEdge> edges;
	
	/**
	 * Part agents, start times array lists must be the same size
	 * 
	 * @param resourceAgent
	 * @param partAgents (Array List) The part agents that occupy the resource agent
	 * @param startTimes (Array List)
	 * @param endTimes (Array List)
	 */
	public PAPlan(ProductAgent productAgent) {
		this.productAgent = productAgent;
		this.actions = new ArrayList<String>();
		this.startTimes = new ArrayList<Integer>();
		
		this.edges = new ArrayList<CapabilitiesEdge>();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		String total = "";
		for (int i = 0; i<this.actions.size();i++){
			total = total + " "+ this.actions.get(i) + " [" + this.startTimes.get(i) + "];";		
		}
		return productAgent + "Schedule:" + total;
	}
	
	/** Add a desired action to the schedule
	 * @param action
	 * @param startTime
	 * @return
	 */
	public boolean addAction(CapabilitiesEdge edge, Integer startTime){
		String action = changeToAction(edge);
		
		if (startTime < 0){
			System.out.println("Start time is wrong for " + this.productAgent + " for " + action);
			return false;
		}
		
		//If the new resource agent is the first resource agent to be added
		if (actions.size() == 0){
			actions.add(action);
			startTimes.add(startTime);
			this.edges.add(edge);
			return true;
		}
		
		Integer checkStartTime;

		//Check to see if RA can be scheduled
		for(int i = 0; i < startTimes.size(); i++){
			checkStartTime = startTimes.get(i);
				if (startTime < checkStartTime){
					this.actions.add(i,action);
					startTimes.add(i,startTime);
					this.edges.add(edge);
					return true;
				}
				else if (checkStartTime == startTime){
					System.out.println("Product busy " + action + " for " + this.productAgent);
					return false;
				}
		}
		
		//If the new part agent is scheduled last and the resource is free
		actions.add(action);
		startTimes.add(startTime);
		this.edges.add(edge);
		return true;
	}
	
	/** Removes a product agent from the plan
	 * @param action
	 * @param startTime
	 */
	public void removeAction(CapabilitiesEdge edge){
		String action = changeToAction(edge);
		
		int index = actions.indexOf(action);
		if (index != -1){
			actions.remove(index);
			startTimes.remove(index);
			this.edges.remove(edge);
		}
		else{
			System.out.println("No " + action + " in " + this.productAgent);
		}
	}
	
	/** Returns the next action that is supposed to happen for this product agent
	 * @param action
	 * @return
	 */
	public CapabilitiesEdge getNextAction(CapabilitiesEdge edge, int currentTime){
		String action = changeToAction(edge);
		
		//Don't include the last edge
		for (int index = 0; index<(actions.size()-1);index++){
			if(action.equals(actions.get(index))){
				if(currentTime<=startTimes.get(index+1)){
					return changeToEdge(actions.get(index+1));
				}
			}
		}
		
		/*int index = actions.indexOf(action);
		if (index != -1){
			if (actions.size()-1 == index){return null;}
			return changeToEdge(actions.get(index+1));
		}*/
		
		//System.out.println("No " + action + " planned for " + this.productAgent);
		return null;
	}
	
	/** Returns the time of the specified action that is planned for this product agent
	 * @param d 
	 * @param action
	 * @return
	 */
	public Integer getTimeofAction(CapabilitiesEdge edge, double currentTime){
		String action = changeToAction(edge);
		
		for (int index = 0; index<actions.size();index++){
			if(action.equals(actions.get(index))){
				if(currentTime<=startTimes.get(index)){
					return startTimes.get(index);
				}
			}
		}
		
		System.out.println("No " + action + " planned for " + this.productAgent);
		return null;
	}
	
	/** Returns the action at that time
	 * @param time
	 * @return
	 */
	public CapabilitiesEdge getActionatNextTime(int time){
		for (int i=0;i<this.startTimes.size();i++){
			Integer startTime = this.startTimes.get(i);
			if (startTime>=time){
				return changeToEdge(actions.get(i));
			}
		}
		
		System.out.println("No action starts at or after " + time + " for " + this.productAgent);
		return null;
	}
	
	public String changeToAction(CapabilitiesEdge edge){
		return "" + edge.getActiveAgent() + "," + edge.getActiveMethod();
	}
	
	
	/** Reutrn the Capabilities Edge with this hash code
	 * @param string
	 * @return
	 */
	public CapabilitiesEdge changeToEdge(String name){
		for (CapabilitiesEdge edge : this.edges){
			if (changeToAction(edge).equals(name)){
				return edge;
			}
		}
		return null;
	}
	
	
	
}