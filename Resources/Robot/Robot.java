package Robot;

import java.awt.Point;
import java.util.ArrayList;

import Part.Part;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class Robot {

	private String name;
	private Grid<Object> grid;
	private int vel;
	private int radius;
	private Point center;
	
	private Point end;
	private ArrayList<Point> pathFollow;
	private boolean working;
	private boolean holdingObject;
	private Object heldObject;

	/**
	 * @param name The name of this robot
	 * @param center The location of the center
	 * @param velocity How fast the robot can move
	 * @param grid
	 * @param radius that it can operate
	 */
	public Robot(String name, Point center, int vel, Grid<Object> grid, int radius) {
		this.name = name;
		this.grid = grid;
		this.vel = vel;
		this.radius = radius;
		this.center = center;
		
		this.end = center; //End effector is the center
		this.working = false; //Not working
		this.pathFollow = new ArrayList<Point>(); //Initialize the pathFollow to empty
		
		//It isn't holding anything at first
		this.holdingObject = false;
		this.heldObject = null;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
	
    //================================================================================
    // System Input
    //================================================================================
	
	
	/** Moves the end effector toward a desired location
	 * @param input The desired location of the end effector
	 * @return if the command was run
	 */
	public boolean moveTo(RobotInput input){		
		if (input.getDestination() == null || input.getObjectType() != null ){
			System.out.println("For " + this + " moveTo method doesn't have the right robot input");
			return false;
		}
		
		Point desired = input.getDestination();
		
		if(desired.distance(center) > radius || desired.distance(center) > radius){
			System.err.println("The point given to writeProgram in robot are out of bounds");
			return false;
		}
		
		//if the robot isn't working, set the path of the robot
		if (this.working == false){
			this.pathFollow = findPath(end,desired);
		}
		
		return true;
	}
	
	/**
	 * Homes the robot
	 * @return if the command was run
	 */
	public boolean home(RobotInput input){
		if (input.getDestination() != null || input.getObjectType() != null ){
			System.out.println("For " + this + " home method doesn't have the right robot input");
			return false;
		}
		
		//If the robot isn't working, set the desired path to the home
		if (!this.working){
			moveTo(new RobotInput(this.center));
			return true;
		}
		
		return false;
	}
	
	/**
	 * @param objectType The string associated with the object class
	 * @return if the command was run
	 */
	public boolean pickUp(RobotInput input){
		if (input.getDestination() != null || input.getObjectType() == null ){
			System.out.println("For " + this + " pickUp method doesn't have the right robot input");
			return false;
		}
		
		String objectType = input.getObjectType();
		
		//If the robot isn't working
		if (!this.working){
			
			//Find all objects at the end effector location
			Iterable<Object> objectsHere = grid.getObjectsAt(end.x,end.y);
			
			Object pickObject = null;
			
			//If it is the desired object type, pick up that object
			for (Object object : objectsHere){
				if (object.getClass().getSimpleName().contains(objectType)){
					pickObject = object;
					break;
				}
			}
			
			//Let the console know that nothing was picked up
			if (pickObject == null){
				System.out.println("No object at " + end.x +"," + end.y + " for " + this + " to pick up.");
				return false;
			}
			//Set the associated object information
			else{
				this.holdingObject = true;
				this.heldObject = pickObject;
			}
			return true;
		}
		
		return false;
	}
	
	/**
	 * Leaves whatever object the robot is holding wherever the end effector of the robot is located
	 * @return if the command was run
	 */
	public boolean placeObject(RobotInput input){
		
		if (input.getDestination() != null || input.getObjectType() != null ){
			System.out.println("For " + this + " placeObject method doesn't have the right robot input");
			return false;
		}
		
		if (!this.working){
			this.holdingObject = false;
			this.heldObject = null;
			return true;
		}
		return false;
	}
	
	/**
	 * Check to see if the robot is free
	 * @return if the robot is free
	 */
	public boolean getStatus(){
		return !this.working;
	}	
	
	public String getPartHere(){
		Iterable<Object> objectsHere = grid.getObjectsAt(end.x,end.y);
		for (Object object : objectsHere){
			if (object.getClass().getName().contains("Part")){
				return ((Part) object).toString();
			}
		}
		return null;
	}
	

	///////////////////////////////////////////////////////
	/////  The following are helper functions		//////
	/////////////////////////////////////////////////////
	/**
	 * @param current point where the robot is currently
	 * @param desired point where the robot wants to be
	 * @return an array list of points 1 unit away from each other that signify the robot trajectory
	 */
	
	
	/** Moves the robot end effector toward whatever is in the pathFollow array
	 * 
	 */
	@ScheduledMethod ( start = 1 , interval = 1, priority = 150)
	public void move(){

		if (pathFollow.size() == 0){
			this.working = false;
			return;
		}
		
		//Move the end of the robot arm with the specified velocity 
		for (int ind = 0; ind<this.vel;ind++){
			if (pathFollow.size() != 0){
				this.working = true;
				end = pathFollow.remove(0);
			}
			else{
				break;
			}
		}
		
		//Move the object held with the end (if there is an object)
		if (this.holdingObject){
			grid.moveTo(this.heldObject,end.x, end.y);
		}
	}
	
	/** Finds a path between two points
	 * @param current The current location
	 * @param desired The desired location
	 * @return The array list of points to follow
	 */
	private ArrayList<Point> findPath(Point current, Point desired){
		ArrayList<Point> path= new ArrayList<Point>();
		
		int x_val = current.x;
		int y_val = current.y;
		
		int x_add = 0;
		int y_add = 0;
		
		//Find out the sign of the value to add for x and y
		if (desired.x-x_val>0){x_add = 1;}
		else {x_add = -1;}
		
		if (desired.y-y_val>0){y_add = 1;}
		else {y_add = -1;}
		
		//Add the value to the current point until you reach a desired location
		
		//Go diagonally
		while (x_val != desired.x && y_val != desired.y){
			x_val = x_val + x_add;
			y_val = y_val + y_add;
			path.add(new Point(x_val,y_val));
		}
		
		//Go horizontally
		while (x_val != desired.x){
			x_val = x_val + x_add;
			path.add(new Point(x_val,y_val));
		}
		
		//Go vertically
		while (y_val != desired.y){
			y_val = y_val + y_add;
			path.add(new Point(x_val,y_val));
		}
		
		return path;
	}
	
	///////////////////////////////////////////////////////
	// The following is used for building the graphics	//
	/////////////////////////////////////////////////////

	
	/** Adds the center X, to indicate the home l
	 * 
	 */
	@SuppressWarnings("unchecked")
	@ScheduledMethod (start = 1)
	public void addCenter(){
		RobotCenterGraphics robotCenterGraphics = new RobotCenterGraphics(this.center);
		
		@SuppressWarnings("rawtypes")
		Context context = ContextUtils.getContext(this);
		context.add(robotCenterGraphics);
		
		grid.moveTo(robotCenterGraphics, robotCenterGraphics.getCenter().x,robotCenterGraphics.getCenter().y);
	}
	
	/** Creates the L-shaped path
	 *
	 */
	public ArrayList<Point> getPath() {
		ArrayList<Point> totalPath = new ArrayList<Point>();
		
		//Path is an L shape, first going in the x direction, then going in the y direction
		totalPath.add(new Point(0,0));
		totalPath.add(new Point(end.x - center.x, 0));
		totalPath.add(new Point(end.x - center.x, end.y - center.y));
		
		return totalPath;
	}
	
	public Point getCenter() {
		return this.center;
	}
	

	///////////////////////////////////////////////////////
	// The following is used for building equals and hash code to compare robots
	/////////////////////////////////////////////////////
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((center == null) ? 0 : center.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + radius;
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
		Robot other = (Robot) obj;
		if (center == null) {
			if (other.center != null)
				return false;
		} else if (!center.equals(other.center))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (radius != other.radius)
			return false;
		return true;
	}

	public int getVelocity() {
		return this.vel;
	}
	
}