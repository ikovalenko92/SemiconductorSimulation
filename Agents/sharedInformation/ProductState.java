package sharedInformation;

import java.awt.Point;
import java.util.ArrayList;

public class ProductState {
	
	private Object holdingObject;
	private PhysicalProperty location;
	private PhysicalProperty processCompleted;
	
	/**
	 * @param holdingObject
	 * @param processCompleted
	 * @param location
	 */
	public ProductState(Object holdingObject, PhysicalProperty processCompleted, PhysicalProperty location){
		this.holdingObject = holdingObject;
		this.location = location;
		this.processCompleted = processCompleted;
	}
	
	public Point getLocation() {
		return location.getPoint();
	}

	public String getProcessCompleted() {
		if (this.processCompleted==null || this.processCompleted.getProcessCompleted()==null) {return "nothing completed";}
		
		return this.processCompleted.getProcessCompleted();
	}
	
	public ArrayList<PhysicalProperty> getPhysicalProperties(){
		ArrayList<PhysicalProperty> list = new ArrayList<PhysicalProperty>();
		
		if (this.location != null){
			list.add(location);
		}
		
		if (this.processCompleted != null){
			list.add(processCompleted);
		}
		
		return list;		
	}
	
	@Override
	public String toString(){
		return this.holdingObject + " at " + getLocation().x + "," + getLocation().y + " process done: " + this.processCompleted;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((holdingObject == null) ? 0 : holdingObject.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((processCompleted == null) ? 0 : processCompleted.hashCode());
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
		if (!(obj instanceof ProductState)) {
			return false;
		}
		ProductState other = (ProductState) obj;
		if (holdingObject == null) {
			if (other.holdingObject != null) {
				return false;
			}
		} else if (!holdingObject.equals(other.holdingObject)) {
			return false;
		}
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (processCompleted == null) {
			if (other.processCompleted != null) {
				return false;
			}
		} else if (!processCompleted.equals(other.processCompleted)) {
			return false;
		}
		return true;
	}
}