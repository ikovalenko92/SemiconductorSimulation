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
}
