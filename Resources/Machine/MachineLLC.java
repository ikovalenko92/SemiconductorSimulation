package Machine;

import java.awt.Point;
import java.util.ArrayList;

import repast.simphony.engine.schedule.ScheduledMethod;

public class MachineLLC {
	private Machine machine;
	private String currentPart;
	private boolean started;
	private boolean done;
	private ArrayList<Program> programList;
	
	/**
	 * @param machine
	 */
	public MachineLLC(Machine machine) {
		this.machine = machine;
		this.started = false;
		this.done = false;
		this.currentPart = null;
		
		this.programList = new ArrayList<Program>();
		
		for (int i=1; i<=6; i++){
			Boolean processEnabled = machine.getProcessesEnabled()[i-1];
			if (processEnabled){
				programList.add(new Program("S" + i, i-1, machine.getProcessTime(i-1)));
			}
		}
	}
	
	@Override
	public String toString() {
		return "Controller " + this.machine;
	}
	
	
	//================================================================================
    // Input from HLC
    //================================================================================
	
	/**
	 * @param manProcess
	 * @param 0: change color to cyan
	 * @param 1: reduces size by half
	 * @param 2: changes the shape to a triangle
	 * @param 3: adds a triangle
	 * @return if the program was run
	 */
	public boolean runProgram(String manProcess){
		if (manProcess.equals("S1")){
			if (machine.S1()){this.started = true; return true;}
		}
		else if (manProcess.equals("S2")){
			if (machine.S2()){this.started = true; return true;}
		}
		else if (manProcess.equals("S3")){
			if (machine.S3()){this.started = true; return true;}
		}
		else if (manProcess.equals("S4")){
			if (machine.S4()){this.started = true; return true;}
		}
		else if (manProcess.equals("S5")){
			if (machine.S5()){this.started = true; return true;}
		}
		else if (manProcess.equals("S6")){
			if (machine.S6()){this.started = true; return true;}
		}
		else if(manProcess.equals("Hold")){
			if(!this.machine.getStatus()){return true;};
		}
		
		return false;
	}
	
	public void doNothing(){
		//Do nothing
	}
	
	//================================================================================
    // Output to HLC (agent)
    //================================================================================
	
	public int getTimeLeft() {
		return this.machine.getTimeLeft();
	}
	
	/**
	 * @return true if the machine is working
	 */
	public boolean queryWorking(){
		return machine.getStatus();
	}
	
	/**
	 * @return true if the machine is done
	 */
	public boolean queryDone(){	
		this.done = (machine.getPartHere() !=null) && !this.queryWorking() && this.started;
		return this.done;
	}
	
	/**
	 * Resets the started variable (if part is moved)
	 */
	public void resetStarted(){
		this.started = false;
	}
	
	@ScheduledMethod(start=1 , interval=1)
	public void checkPresence(){
		//If there is no part here, it hasn't started working
		if (machine.getPartHere() == null){
			this.started = false;
			return;
		}
		//If the part here doesn't equal the previous part, it hasn't started working
		else if (!machine.getPartHere().equals(this.currentPart)){
			this.currentPart = machine.getPartHere();
			this.started = false;
		};
	}
	
	/**
	 * @return The string of program names
	 */
	public ArrayList<String> getProgramList(){
		ArrayList<String> programString = new ArrayList<String>(); 
		
		for (Program program : programList){
			programString.add(program.getProgramName());
		}
		return programString;
	}
	
	/**
	 * @param programName The string corresponding to the program name
	 * @return The integer time of the program (-1 if can't be run)
	 */
	public int getProgramTime(String programName){
		for (Program program : programList){
			if (program.getProgramName().equals(programName)){
				return program.getProcessTime();
			}
		}
		//Return -1 if this program can't be run
		return -1;
	}
	
	//================================================================================
    // Helper functions
    //================================================================================
	
	/** Controller program
	 * @author ikovalenko
	 */
	private class Program{
		private String programName;
		private int processIndex;
		private int processTime;
		
		/**
		 * @param name
		 * @param start
		 * @param end
		 */
		public Program(String programName, int processIndex, int processTime){
			this.programName = programName;
			this.processIndex = processIndex;
			this.processTime = processTime;
		}

		/**
		 * @return the programName
		 */
		public String getProgramName() {
			return programName;
		}

		/**
		 * @return the processIndex
		 */
		@SuppressWarnings("unused")
		public int getProcessIndex() {
			return processIndex;
		}

		/**
		 * @return the processTime
		 */
		public int getProcessTime() {
			return processTime;
		}		
	}

	public Machine getMachine() {
		return this.machine;
	}

	public Point getLocation() {
		return this.machine.getCenter();
	}
	
	
}