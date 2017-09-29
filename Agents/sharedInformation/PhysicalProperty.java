package sharedInformation;

import java.awt.Point;

public class PhysicalProperty {

	private Point point;
	private String processCompleted;

	public PhysicalProperty(Point point){
		this.point = point;
		this.processCompleted = null;
	}
	
	public PhysicalProperty(String processCompleted){
		this.point = null;
		this.processCompleted = processCompleted;
	}

	/**
	 * @return the point
	 */
	public Point getPoint() {
		return point;
	}

	/**
	 * @return the processCompleted
	 */
	public String getProcessCompleted() {
		return processCompleted;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((point == null) ? 0 : point.hashCode());
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
		if (!(obj instanceof PhysicalProperty)) {
			return false;
		}
		PhysicalProperty other = (PhysicalProperty) obj;
		if (point == null) {
			if (other.point != null) {
				return false;
			}
		} else if (!point.equals(other.point)) {
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
