package Robot;

import java.awt.Point;
import java.lang.reflect.Method;

public class RobotProgram {
	private Method method;
	private Robot robot;
	
	private String stringParams;
	private Point pointParams;
	private RobotInput robotInput;

	/**
	 * @param robot
	 * @param method
	 */
	public RobotProgram(Robot robot, Method m){
		this.robot = robot;
		this.method = m;
		this.robotInput = new RobotInput();
	}
	
	@Override
	public String toString() {
		if(this.pointParams != null){
			return "RobotProgram: " + robot + method.getName() + this.pointParams;
		}
		else if (this.stringParams != null){
			return "RobotProgram: " + robot + method.getName() + this.stringParams;
		}
		
		return "RobotProgram: " + robot + method.getName();
	}

	/**
	 * @param robot
	 * @param method
	 * @param params - string of object type
	 */
	public RobotProgram(Robot robot, Method m, String params){
		this.robot = robot;
		this.method = m;
		this.robotInput = new RobotInput(params);
	}
	

	/**
	 * @param robot
	 * @param method
	 * @param params point to move it to
	 */
	public RobotProgram(Robot robot, Method m, Point params){
		this.robot = robot;
		this.method = m;
		this.robotInput = new RobotInput(params);
	}

	public Robot getRobot() {
		return robot;
	}

	public Method getMethod() {
		return method;
	}

	public RobotInput getRobotInput() {
		return this.robotInput;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result
				+ ((pointParams == null) ? 0 : pointParams.hashCode());
		result = prime * result + ((robot == null) ? 0 : robot.hashCode());
		result = prime * result
				+ ((stringParams == null) ? 0 : stringParams.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RobotProgram other = (RobotProgram) obj;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (pointParams == null) {
			if (other.pointParams != null)
				return false;
		} else if (!pointParams.equals(other.pointParams))
			return false;
		if (robot == null) {
			if (other.robot != null)
				return false;
		} else if (!robot.equals(other.robot))
			return false;
		if (stringParams == null) {
			if (other.stringParams != null)
				return false;
		} else if (!stringParams.equals(other.stringParams))
			return false;
		return true;
	}

}
