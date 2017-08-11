package Robot;

import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import repast.simphony.engine.schedule.ScheduledMethod;

public class RobotLLC{
	private Robot robot;
	
	private ArrayList<Program> programList;
	private Method moveToMethod;
	private Method homeMethod;
	private Method pickUpMethod;
	private Method placeObjectMethod;
	
	private ArrayList<RobotProgram> programRun;
	private boolean working;
	

	public RobotLLC(String name, Robot robot) {
		this.robot = robot;
		this.programList = new ArrayList<Program>();
		
		this.programRun = new ArrayList<RobotProgram>();
		this.working = false;
		
		populateMethods();
	}
	
	@Override
	public String toString() {
		return "Controller " + this.robot;
	}
	
	//================================================================================
    // Input - Setup a program
    //================================================================================
	
	/** Move an object between point a and point b
	 * @param a where the object is currently located
	 * @param b where the object needs to go
	 * @param obj the object to be moved
	 */
	public void writeMoveObjectProgram(String name, Point a, Point b, String objectType){
		Program program = new Program(name, a, b);
		
		//Move to where the object is
		RobotProgram moveTo1 = new RobotProgram(this.robot, this.moveToMethod, a);
		program.add(moveTo1);
		
		//Pick up the object
		RobotProgram pickObject = new RobotProgram(this.robot, this.pickUpMethod, objectType);
		program.add(pickObject);
		
		//Move back
		RobotProgram moveCenter = new RobotProgram(this.robot, this.homeMethod);
		program.add(moveCenter);
		
		//Move where you want the object to be
		RobotProgram moveto2 = new RobotProgram(this.robot, this.moveToMethod, b);
		program.add(moveto2);
		
		//Place the object
		RobotProgram place = new RobotProgram(this.robot, this.placeObjectMethod);
		program.add(place);
		
		//Move back
		program.add(moveCenter);
		
		programList.add(program);
	}
	
	//================================================================================
    // Input from HLC
    //================================================================================
	
	/**
	 * @param programInput The name (String) of the program (e.g. "ConveyorToCNC")
	 * @return if this program was queried for the controller
	 */
	public boolean runMoveObjectProgram(String programInput){
		for (Program prog : this.programList){
			if (prog.getName().equals(programInput) && !this.working){
				this.working = true;
				programRun.addAll(prog.getProgramList());
				return true;
			}
		}
		
		return false;
	}
	
	//================================================================================
    // Output to HLC (agent)
    //================================================================================
	
	public boolean getFree(){
		return !this.working;
	}
	
	public ArrayList<String> getProgramList(){
		ArrayList<String> programString = new ArrayList<String>(); 
		
		for (Program program : programList){
			programString.add(program.getName());
		}
		return programString;
	}
	
	public Point[] getProgramEndpoints(String programName){
		Point start = null;
		Point end = null;
		
		for (Program program : programList){
			if (program.getName().equals(programName)){
				start = program.getStart();
				end = program.getEnd();
			}
		}
		
		return new Point[]{start,end};
	}
	
	public String getHoldingObject() {
		return this.robot.getPartHere();
	}	
	
	//================================================================================
    // Timed helper functions
    //================================================================================
	
	/** Updates the status of the robot controller every tick
	 */
	@ScheduledMethod ( start = 1 , interval = 1, priority = -10000)
	public void statusUpdate(){
		if (programRun.size() == 0){
			this.working = false;
		}
		else{
			this.working = true;
		}
	}
		
	/** Moves the robot based on the communication with the physical component
	 * Scan time = every other tick
	 */
	@ScheduledMethod ( start = 1 , interval = 2)
	public void move(){
		if (this.robot.getStatus() && this.working == true){
			//Find the next program
			RobotProgram runProgram = programRun.remove(0);
			
			//Try running the program on the robot
			try {
				runProgram.getMethod().invoke(runProgram.getRobot(),runProgram.getRobotInput());
			}
			catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				System.err.println("Check RobotLLC.move() methods");
			}
		}
	}
	
	//================================================================================
    // Untimed helper functions
    //================================================================================
	
	/** Creates a handle for the moveTo, home, pickUp, and placeObject methods in the physical robot
	 * 
	 */
	private void populateMethods() {
		//moveTo Method
		try {
			this.moveToMethod = robot.getClass().getMethod("moveTo", new Class[] {RobotInput.class});
		}
		catch (NoSuchMethodException | SecurityException e){
			System.err.println(this + " Check moveTo");
		}
		
		//home Method
		try {
			this.homeMethod = robot.getClass().getMethod("home",  new Class[] {RobotInput.class});
		}
		catch (NoSuchMethodException | SecurityException e){
			System.err.println(this + " Check home");
		}
		
		//pickUp Method
		try {
			this.pickUpMethod = robot.getClass().getMethod("pickUp", new Class[] {RobotInput.class});
		}
		catch (NoSuchMethodException | SecurityException e){
			System.err.println(this + " Check pickUp");
		}
		
		//place Method
		try {
			this.placeObjectMethod = robot.getClass().getMethod("placeObject",  new Class[] {RobotInput.class});
		}
		catch (NoSuchMethodException | SecurityException e){
			System.err.println(this + "Check place");
		}
	}
	
	//================================================================================
    // Helper classes
    //================================================================================
	
	/** Controller program
	 * @author ikovalenko
	 */
	private class Program{
		private String name;
		private ArrayList<RobotProgram> subProgramList;
		private Point start;
		private Point end;
		
		/**
		 * @param name
		 * @param start
		 * @param end
		 */
		public Program(String name, Point start, Point end){
			this.name = name;
			this.start = start;
			this.end = end;
			
			this.subProgramList = new ArrayList<RobotProgram>();
		}

		/** Add a robot program to the controller program
		 * @param robotProgram
		 */
		public void add(RobotProgram robotProgram){
			subProgramList.add(robotProgram);
		}

		public String getName() {
			return name;
		}
		
		public Point getStart() {
			return this.start;
		}
		
		public Point getEnd() {
			return this.end;
		}

		public ArrayList<RobotProgram> getProgramList() {
			return subProgramList;
		}
	}

	public int getVelocity() {
		return this.robot.getVelocity();
	}

	public void setWorking(boolean b) {
		this.working = b;
	}

}
