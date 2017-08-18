package sharedInformation;

import java.awt.Point;

public class CapabilitiesNode {
	
	private Object holdingObject;
	private Point location;
	private String processCompleted;
	
	/**
	 * @param holdingObject
	 * @param processCompleted
	 * @param location
	 */
	public CapabilitiesNode(Object holdingObject, String processCompleted, Point location){
		this.holdingObject = holdingObject;
		this.location = location;
		this.processCompleted = processCompleted;
	}

	public Point getLocation() {
		return location;
	}

	public Object getObject() {
		return holdingObject;
	}
	
	public String getProcessCompleted() {
		return this.processCompleted;
	}
	
	@Override
	public String toString(){
		return this.holdingObject + " at " + this.location.x + "," + this.location.y + " process done: " + this.processCompleted;
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
		if (!(obj instanceof CapabilitiesNode)) {
			return false;
		}
		CapabilitiesNode other = (CapabilitiesNode) obj;
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