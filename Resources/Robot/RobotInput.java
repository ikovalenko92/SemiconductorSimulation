package Robot;

import java.awt.Point;

public class RobotInput {

	private Point destination;
	private String objectType;

	//Create a point input
	
	public RobotInput(Point destination){
		this.destination = destination;
		this.objectType = null;
	}
	
	public Point getDestination() {
		return destination;
	}
	
	//Create a string input
	
	public RobotInput(String objectType){
		this.objectType = objectType;
		this.destination = null;
	}

	public String getObjectType() {
		return objectType;
	}
	
	//When there is no extra input
	
	public RobotInput() {
		this.objectType = null;
		this.destination = null;
	}

}
