package intelligentProduct;

import sharedInformation.ResourceEvent;

import java.util.ArrayList;
import java.util.Collections;

public class PAPlan {
	private ProductAgent productAgent;
	private ArrayList<ResourceEvent> plannedString;
	private ArrayList<Integer> startTimes;
	private ArrayList<Integer> endTimes;
	
	public PAPlan(ProductAgent productAgent) {
		this.productAgent = productAgent;
		
		this.plannedString = new ArrayList<ResourceEvent>();
		
		this.startTimes = new ArrayList<Integer>();
		this.endTimes = new ArrayList<Integer>();
	}

	@Override
	public String toString() {
		
		String total = "";
		for (int i = 0; i<this.plannedString.size();i++){
			total = total + " "+ this.plannedString.get(i) + " [" + this.startTimes.get(i) + "];";		
		}
		return productAgent + "Schedule:" + total;
	}
	
	
	/** Add a desired action to the schedule
	 * @param action
	 * @param startTime
	 * @return
	 */
	public boolean addEvent(ResourceEvent event, Integer startTime, Integer endTime){
		if (startTime < 0){
			System.out.println("Start time is wrong for " + this.productAgent + " for " + event);
			return false;
		}
		
		//If the new resource agent is the first resource agent to be added
		if (this.plannedString.size() == 0){
			this.startTimes.add(startTime);
			this.endTimes.add(endTime);
			this.plannedString.add(event);
			return true;
		}
		
		Integer checkStartTime;
		
		//If the new part agent is scheduled after the first one and it doesn't allow multiple scheduled together
		
		//Check to see if event can be scheduled
		for(int i = 0; i < startTimes.size(); i++){
			checkStartTime = startTimes.get(i);
			
			if (checkStartTime < startTime){
					this.plannedString.add(i,event);
					this.startTimes.add(i,startTime);
					this.endTimes.add(i,endTime);
					sortByStartTime();return true;
			}
		}
		
		//If the new part agent is scheduled last and the resource is free
		this.plannedString.add(event);
		this.startTimes.add(startTime);
		this.endTimes.add(endTime);
		sortByStartTime();return true;
	}
	
	private void sortByStartTime() {
		ArrayList<Integer> newStartList = new ArrayList<Integer>(this.startTimes);
		ArrayList<Integer> newEndList = new ArrayList<Integer>(this.endTimes);
		ArrayList<ResourceEvent> newPlannedString = new ArrayList<ResourceEvent>(this.plannedString);
		
		Collections.sort(newStartList);
		for(int sortIndex = 0;sortIndex<newStartList.size();sortIndex++){
			int startTimeSorted = newStartList.get(sortIndex);
			int index = this.startTimes.indexOf(startTimeSorted);
			newEndList.set(sortIndex, this.endTimes.get(index));
			newPlannedString.set(sortIndex, this.plannedString.get(index));
		}
		
		this.startTimes = newStartList;
		this.endTimes = newEndList;
		this.plannedString = newPlannedString;
	}

	/** Removes a product agent from the plan
	 * @param action
	 * @param startTime
	 */
	public boolean removeEvent(ResourceEvent event, Integer startTime, Integer endTime){
		for(int index = 0; index < this.startTimes.size(); index++){
			//Find if there is an event scheduled for the proposed time to remove it
			if (startTime >= this.startTimes.get(index) && endTime< this.endTimes.get(index) && event.equals(this.plannedString.get(index))){
				//Remove the event if the proposed scheduled agent is found
				this.startTimes.remove(index);
				
				//Check if the desired end time is after the indexed end time
				if (endTime>=this.endTimes.get(index)){
					this.endTimes.remove(index);
					this.plannedString.remove(index);
				}
				//If it is not, just shorten the length of the event by moving the start time to the proposed endTime
				else{
					this.startTimes.add(index, endTime);
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	/** Returns the index of the next action that is supposed to happen for this product agent
	 * @param action
	 * @return
	 */
	public int getIndexOfNextEvent(ResourceEvent event, int currentTime){
		//Don't include the last edge
		for (int index = 0; index<(this.plannedString.size()-1);index++){
			if(plannedString.equals(this.plannedString.get(index))){
				//If the current time is between the last start time and the next start time
				if(currentTime>=startTimes.get(index) && currentTime <=startTimes.get(index+1)){
					return index+1;
				}
			}
		}
		
		System.out.println("No " + event + " planned for " + this.productAgent);
		return -1;
	}

	/** Returns the index of next action
	 * @param time
	 * @return
	 */
	public int getIndexOfNextEvent(int time){
		for (int i=0;i<this.startTimes.size();i++){
			Integer startTime = this.startTimes.get(i);
			if (startTime>=time){
				return i;
			}
		}
		
		//System.out.println("No action starts at or after " + time + " for " + this.productAgent);
		return -1;
	}
	
	public ResourceEvent getIndexEvent(int index){
		if (index>=0 && index<this.plannedString.size()){
			return this.plannedString.get(index);
		}
		return null;
	}
	
	public int getIndexStartTime(int index){
		if (index>=0){
			return this.startTimes.get(index);
		}
		return -1;
	}
	
	public int getIndexEndTime(int index){

		if (index>=0){
		return this.endTimes.get(index);
		}
		return -1;
	}

	/**
	 * @return if there are any events in the environment model
	 */
	public boolean isEmpty(int time){
		if (this.getIndexOfNextEvent(time)==-1){
			return true;
		}
		return false;
	}
}