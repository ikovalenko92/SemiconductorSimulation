package Machine;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.GeneralPath;

import Part.Part;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;

public class Machine {
	private String name;
	private Point location;
	private Point processLocation;
	private Grid<Object> grid;
	
	private boolean working;
	private int waitTimer;
	
	private float partSize;
	private Color partColor;
	private int windingRule;
	private GeneralPath partShape;
	private float rotation;
	
	private boolean[] processesEnabled;
	private int[] processTimes;
	private String partName;

	/**
	 * @param name
	 * @param location Where the machining takes place
	 * @param grid
	 * @param rotation For the graphics
	 * @param processTimes Array of size 6 that represents the process times for each operation (must be a positive number)
	 */
	public Machine(String name, Point location, Grid<Object> grid, float rotation, int[] processTimes){
		this.name = name;
		this.location = location;
		this.processLocation = new Point(location.x, location.y-3);
		this.grid = grid;
		
		this.working = false;		
		this.processTimes = processTimes;
		this.waitTimer = 0;
		
		processesEnabled = new boolean[6];

		this.rotation = rotation;
		
		//Must have process times for all 6 stages of processing (if time is -1, process cannot be done)
		if (processTimes.length != 6)
	    {throw new IllegalArgumentException("Number of process times are wrong for " + this.toString());}
		
		else{
			//Fill out the processes enabled array
			for (int i = 0; i < 6; i++){
				int processTime = processTimes[i];
				if (processTime <= 0){
					processesEnabled[i] = false;
				}
				else{
					processesEnabled[i] = true;
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}

	
	public void setBroken(){
		this.working = true;
		this.waitTimer = 100000;
	}
	

	//================================================================================
    // System Output
    //================================================================================
	
	/**
	 * Check to see if the cnc is finished with the program
	 * @return if the cnc is done working with the program
	 */
	public boolean getStatus(){
		return this.working;
	}
	
	public String getPartHere(){
		Iterable<Object> objectsHere = grid.getObjectsAt(location.x,location.y);
		for (Object object : objectsHere){
			if (object.getClass().getName().contains("Part")){
				return ((Part) object).toString();
			}
		}
		return null;
	}
	
	public boolean[] getProcessesEnabled(){
		return this.processesEnabled;
	}
	
	public int getProcessTime(int i) {
		return this.processTimes[i];
	}
	
	public int getTimeLeft() {
		return this.waitTimer;
	}
	
	
	//================================================================================
    // System Input
    //================================================================================
	
	//Processes that can be run on the machine
	
	//@ScheduledMethod (start = 1560)
	public boolean S1(String partName){
		
		if (!this.processesEnabled[0] || getPartHere()==null){
			return false;
		}
		
		//Puts a black box on the part in the top left
		GeneralPath topleft = new GeneralPath();
		topleft.moveTo(-1,1);
		topleft.lineTo(-1,0);
		/*topleft.moveTo(-1.5,1);
		topleft.lineTo(-0.5,1);
		topleft.lineTo(-0.5,0);
		topleft.lineTo(-1.5,0);
		topleft.lineTo(-1.5,1);
		topleft.closePath();
		topleft.setWindingRule(0);*/

		this.partName = partName;
		boolean methodCalled = appendShape(new MachineInput(topleft));
		
		if (methodCalled){
			waitTimer = this.processTimes[0];
			move(partName,this.location,this.processLocation);
			return true;
		}
		
		return false;
	}
	
	//@ScheduledMethod (start = 1560)
	public boolean S2(String partName){
		
		if (!this.processesEnabled[1] || getPartHere()==null){
			return false;
		}
		
		//Puts a black box on the part in the top center
		GeneralPath topcenter = new GeneralPath();
		topcenter.moveTo(0,1);
		topcenter.lineTo(0,0);
		/*
		topcenter.moveTo(-0.5,1);
		topcenter.lineTo(0.5,1);
		topcenter.lineTo(0.5,0);
		topcenter.lineTo(-0.5,0);
		topcenter.lineTo(-0.5,1);
		topcenter.closePath();
		topcenter.setWindingRule(0);*/
		
		this.partName = partName;
		boolean methodCalled = appendShape(new MachineInput(topcenter));
		
		if (methodCalled){
			waitTimer = this.processTimes[1];
			move(partName,this.location,this.processLocation);
			return true;
		}
		
		return false;
	}
		
	//@ScheduledMethod (start = 1560)
	public boolean S3(String partName){
		
		if (!this.processesEnabled[2] || getPartHere()==null){
			return false;
		}
		
		//Puts a black box on the part in the top right
		GeneralPath topright = new GeneralPath();
		topright.moveTo(1,1);
		topright.lineTo(1,0);
		/*
		topright.moveTo(0.5,1);
		topright.lineTo(1.5,1);
		topright.lineTo(1.5,0);
		topright.lineTo(0.5,0);
		topright.lineTo(0.5,1);
		topright.closePath();
		topright.setWindingRule(0)*/
		
		this.partName = partName;
		boolean methodCalled = appendShape(new MachineInput(topright));
		
		if (methodCalled){
			waitTimer = this.processTimes[2];
			move(partName,this.location,this.processLocation);
			return true;
		}
		
		return false;
	}
		
	//@ScheduledMethod (start = 1560)
	public boolean S4(String partName){
		
		if (!this.processesEnabled[3] || getPartHere()==null){
			return false;
		}
		
		//Puts a black box on the part in the bottom left
		GeneralPath bottomleft = new GeneralPath();
		bottomleft.moveTo(-1,0);
		bottomleft.lineTo(-1,-1);
		/*
		bottomleft.moveTo(-1.5,0);
		bottomleft.lineTo(-0.5,0);
		bottomleft.lineTo(-0.5,-1);
		bottomleft.lineTo(-1.5,-1);
		bottomleft.lineTo(-1.5,0);
		bottomleft.closePath();
		bottomleft.setWindingRule(0);*/
		
		this.partName = partName;
		boolean methodCalled = appendShape(new MachineInput(bottomleft));
		
		if (methodCalled){
			waitTimer = this.processTimes[3];
			move(partName,this.location,this.processLocation);
			return true;
		}
		
		return false;
	}
		
	//@ScheduledMethod (start = 1560)
	public boolean S5(String partName){
		
		if (!this.processesEnabled[4] || getPartHere()==null){
			return false;
		}
		
		//Puts a black box on the part in the bottom center
		GeneralPath bottomcenter = new GeneralPath();
		bottomcenter.moveTo(0,0);
		bottomcenter.lineTo(0,-1);
		/*
		bottomcenter.moveTo(-0.5,0);
		bottomcenter.lineTo(0.5,0);
		bottomcenter.lineTo(0.5,-1);
		bottomcenter.lineTo(-0.5,-1);
		bottomcenter.lineTo(-0.5,0);
		bottomcenter.closePath();
		bottomcenter.setWindingRule(0);*/
		
		this.partName = partName;
		boolean methodCalled = appendShape(new MachineInput(bottomcenter));
		
		if (methodCalled){
			waitTimer = this.processTimes[4];
			move(partName,this.location,this.processLocation);
			return true;
		}
		
		return false;
	}
		
	//@ScheduledMethod (start = 1560)
	public boolean S6(String partName){
		
		if (!this.processesEnabled[5] || getPartHere()==null){
			return false;
		}
		
		//Puts a black box on the part in the bottom right
		GeneralPath bottomright = new GeneralPath();
		bottomright.moveTo(1,0);
		bottomright.lineTo(1,-1);
		/*
		bottomright.moveTo(0.5,0);
		bottomright.lineTo(1.5,0);
		bottomright.lineTo(1.5,-1);
		bottomright.lineTo(0.5,-1);
		bottomright.lineTo(0.5,0);
		bottomright.closePath();
		bottomright.setWindingRule(0);*/

		this.partName = partName;
		boolean methodCalled = appendShape(new MachineInput(bottomright));
		
		if (methodCalled){
			waitTimer = this.processTimes[5];
			move(partName,this.location,this.processLocation);
			return true;
		}
		
		return false;
	}
	
	/** Adds to the current shape of the physicalComponent_Part
	 * @param path
	 */
	private boolean appendShape(MachineInput input){
		GeneralPath path = input.getShape();
		
		if (!this.working){
			Iterable<Object> objectsHere = grid.getObjectsAt(location.x,location.y);
			
			//Check the object is a physicalComponent_Part 
			for (Object object : objectsHere){
				if (object.getClass().getName().contains("Part") && object.toString().contains(this.partName)){
					
					//Keep everything else the same
					savePartProperties((Part) object);
					
					//Set the new shape			
					path.append(partShape, false);
					this.partShape = path;
					
				}
			}
			
			//waitTimer = calculateShapeTime(input);
			this.working = true;
			return true;
		}
		
		return false;
	}
	
	//================================================================================
    // Helper functions
    //================================================================================
	
	/**
	 * @param input The input to the Machine
	 * @return how long it takes 
	 */
	/*private int calculateShapeTime(MachineInput input) {
		int randomNum = (int)(Math.random()*11)+5;
		return randomNum;
	}*/
	
	/** Helper method to save the physicalComponent_Part's properties
	 * @param physicalComponent_Part
	 */
	private void savePartProperties(Part part){
		this.windingRule = part.getWindingRule();
		this.partColor = part.getColor();
		this.partSize = part.getSize();
		this.partShape = part.getShape();
	}
	
	/** Not an actual schedule method...this should work now though
	 * 
	 */
	@ScheduledMethod (start = 1, interval = 1, priority = 250)
	public void waiting(){
		if (this.waitTimer != 0){
			this.waitTimer--;
			if (this.waitTimer == 0){
				makeChanges();
			}
		}
	}

	/** The method that changes the physicalComponent_Part (after waiting)
	 * 
	 */
	private void makeChanges() {
		Iterable<Object> objectsHere = grid.getObjectsAt(this.processLocation.x,this.processLocation.y);
		
		//Check the object is a physicalComponent_Part 
		for (Object object : objectsHere){
			if (object.getClass().getName().contains("Part")){
				
				//Set everything for the physicalComponent_Part
				((Part) object).setSize(this.partSize);
				((Part) object).setColor(this.partColor);
				((Part) object).setShape(this.partShape);
				((Part) object).setWindingRule(this.windingRule);
				
				break;
			}
		}
		
		//Reset all of the variables to make sure they don't get saved;
		this.partSize = -1;
		this.partShape = null;
		this.windingRule = -1;
		this.partColor = null;
		
		//Finish working
		this.working = false;
		
		move(this.partName,this.processLocation,this.location);
	}
	
	private void move(String partName, Point location, Point destination) {
		Iterable<Object> objectsHere = grid.getObjectsAt(location.x,location.y);
		
		Object desiredObject = null;
		for (Object object : objectsHere){
			if (object.getClass().getName().contains("Part") && object.toString().contains(this.partName)){
				desiredObject = object;
				break;
			}
		}
		
		grid.moveTo(desiredObject, destination.x, destination.y);
	}
	
	//================================================================================
    // For the graphics
    //================================================================================


	public Point getCenter() {
		return this.location;
	}
	
	public float getRotation() {
		return this.rotation;
	}
}