package journalPaperSimulation;


import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import Buffer.Buffer;
import Buffer.BufferLLC;
import Machine.Machine;
import Machine.MachineLLC;
import Part.Part;
import Part.RFIDTag;
import Robot.Robot;
import Robot.RobotLLC;
import intelligentProduct.LotProductAgent;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.BouncyBorders;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import resourceAgent.BufferAgent;
import resourceAgent.MachineAgent;
import resourceAgent.ResourceAgentInterface;
import resourceAgent.RobotAgent;
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;
import sharedInformation.PhysicalProperty;


public class SimulationContextBuilder implements ContextBuilder<Object> {
	//Overall dimensions of the area
	private final int XDIM = 160;
	private final int YDIM = 120;
	
	
    //================================================================================
    // Machine
    //================================================================================
	
	/**
	 * List of all of Machine controllers
	 */
	private ArrayList<MachineLLC> listMachineLLC = new ArrayList<MachineLLC>();

	/**
	 * List of all Machine agents
	 */
	private ArrayList<MachineAgent> listMachineAgent = new ArrayList<MachineAgent>();
	
	//Machine locations
	private Point machineTAPoint = new Point (30,100);
	private Point machineTBPoint = new Point (50,90);
	private Point machineTCPoint = new Point (30,80);
	private Point machineTDPoint = new Point (50,40);
	private Point machineTEPoint = new Point (30,30);
	private Point machineTFPoint = new Point (50,20);
	private Point machineTGPoint = new Point (70,100);
	private Point machineTHPoint = new Point (70,80);
	private Point machineTIPoint = new Point (90,100);
	private Point machineTJPoint = new Point (90,80);
	private Point machineTKPoint = new Point (70,40);
	private Point machineTLPoint = new Point (70,20);
	private Point machineTMPoint = new Point (90,40);
	private Point machineTNPoint = new Point (90,20);
	private Point machineTOPoint = new Point (110,100);
	private Point machineTPPoint = new Point (130,90);
	private Point machineTQPoint = new Point (110,80);
	private Point machineTRPoint = new Point (130,40);
	private Point machineTSPoint = new Point (110,30);
	private Point machineTTPoint = new Point (130,20);
	
	private final Point[] machineLocations = new Point[]{machineTAPoint, machineTBPoint, 
			machineTCPoint, machineTDPoint, machineTEPoint, machineTFPoint, machineTGPoint, 
			machineTHPoint, machineTIPoint, machineTJPoint, machineTKPoint, machineTLPoint, 
			machineTMPoint, machineTNPoint, machineTOPoint, machineTPPoint, machineTQPoint, 
			machineTRPoint, machineTSPoint, machineTTPoint};
	
	//Machine Times: negative numbers indicate that that process can't be performed by the specific machine
	//e.g. Machine TA can performa process 1 and 5 in 225 and 255 ticks, but can't perform other processes
	private int[] machineTATime = new int[]{225,-1,-1,-1, 255,-1};
	private int[] machineTBTime = new int[]{225,-1,-1,-1, 255,-1};
	private int[] machineTCTime = new int[]{225,-1,-1,-1, 255,-1};
	private int[] machineTDTime = new int[]{225,-1,-1,-1, 255,-1};
	private int[] machineTETime = new int[]{225,-1,-1,-1, 255,-1};
	private int[] machineTFTime = new int[]{225,-1,-1,-1, 255,-1};
	private int[] machineTGTime = new int[]{225,-1,-1,-1, 255,-1};
	private int[] machineTHTime = new int[]{225,-1,-1,-1, 255,-1};
	private int[] machineTITime = new int[]{-1,30,-1,50,-1,-1};
	private int[] machineTJTime = new int[]{-1,30,-1,50,-1,-1};
	private int[] machineTKTime = new int[]{-1,30,-1,50,-1,-1};
	private int[] machineTLTime = new int[]{-1,30,-1,50,-1,-1};
	private int[] machineTMTime = new int[]{-1,-1,55,-1,-1,10};
	private int[] machineTNTime = new int[]{-1,-1,55,-1,-1,10};
	private int[] machineTOTime = new int[]{-1,-1,55,-1,-1,10};
	private int[] machineTPTime = new int[]{-1,-1,55,-1,-1,10};
	private int[] machineTQTime = new int[]{-1,-1,55,-1,-1,10};
	private int[] machineTRTime = new int[]{-1,-1,55,-1,-1,10};
	private int[] machineTSTime = new int[]{-1,-1,55,-1,-1,10};
	private int[] machineTTTime = new int[]{-1,-1,55,-1,-1,10};

	
	private int[][] machineTimes = new int[][]{machineTATime, machineTBTime, machineTCTime,
		machineTDTime, machineTETime, machineTFTime, machineTGTime, machineTHTime, machineTITime,
		machineTJTime, machineTKTime, machineTLTime, machineTMTime, machineTNTime, machineTOTime,
		machineTPTime, machineTQTime, machineTRTime, machineTSTime, machineTTTime};
 

    //================================================================================
    // Storage
    //================================================================================
	
	/**
	 * List of all of Buffer Controllers
	 */
	private ArrayList<BufferLLC> listBufferLLC = new ArrayList<BufferLLC>();

	/**
	 * List of all Buffer agents
	 */
	private ArrayList<BufferAgent> listBufferAgent = new ArrayList<BufferAgent>();
		
	//Storage points for bay buffers
	private Point enterPointStorage = new Point (18,60);
	private Point exitPointStorage = new Point (142,60);
	private Point depositB1Point = new Point (40,70);
	private Point depositB2Point = new Point (40,50);
	private Point depositB3Point = new Point (80,70);
	private Point depositB4Point = new Point (80,50);
	private Point depositB5Point = new Point (120,70);
	private Point depositB6Point = new Point (120,50);
	private Point buffer1Point = new Point (60,60);
	private Point buffer2Point = new Point (100,60);
		
	//Enter points for bay buffers
	private Point enterPoint = new Point (20,60);
	private Point depositB1PointEnter = new Point (40,65);
	private Point depositB2PointEnter = new Point (40,55);
	private Point depositB3PointEnter = new Point (80,65);
	private Point depositB4PointEnter = new Point (80,55);
	private Point depositB5PointEnter = new Point (120,65);
	private Point depositB6PointEnter = new Point (120,55);
	private Point buffer1PointEnter = new Point (55,60);
	private Point buffer2PointEnter = new Point (95,60);

	//Exit points for bay buffers
	private Point exitPoint = new Point (140,60);
	private Point depositB1PointExit = new Point (40,75);
	private Point depositB2PointExit = new Point (40,45);
	private Point depositB3PointExit = new Point (80,75);
	private Point depositB4PointExit = new Point (80,45);
	private Point depositB5PointExit = new Point (120,75);
	private Point depositB6PointExit = new Point (120,45);
	private Point buffer1PointExit = new Point (65,60);
	private Point buffer2PointExit = new Point (105,60);
	
	private final Point[] bufferLocations = new Point[]{enterPointStorage, exitPointStorage, depositB1Point, depositB2Point,
			depositB3Point,depositB4Point, depositB5Point, depositB6Point, buffer1Point, buffer2Point};
			
	private final Point[][] bufferEnterLocations = new Point[][]{{enterPoint}, {exitPoint}, {depositB1PointEnter, depositB1PointExit},
		{depositB2PointEnter, depositB2PointExit}, {depositB3PointEnter, depositB3PointExit}, {depositB4PointEnter, depositB4PointExit},
		{depositB5PointEnter, depositB5PointExit},{depositB6PointEnter, depositB6PointExit},{buffer1PointEnter, buffer1PointExit},{buffer2PointEnter, buffer2PointExit}};
	
	//BUffer names
	private final String[] bufferNames = new String[]{"EnterBuffer", "ExitBuffer", "depositB1",
			"depositB2", "depositB3", "depositB4", "depositB5", "depositB6", "buffer1234", "buffer3456"};
		
	//================================================================================
    // Robots
    //================================================================================
	
	//Speed of the robots
	final int robotSpeed = 5;

	/**
	 * List of all of Robot Controllers
	 */
	private ArrayList<RobotLLC> listRobotLLC = new ArrayList<RobotLLC>();

	/**
	 * List of all robot agents
	 */
	private ArrayList<RobotAgent> listRobotAgent = new ArrayList<RobotAgent>();;    
	
	private final Point robotB1Point = new Point(40,90);
	private final Point robotB2Point = new Point(40,30);
	private final Point robotB3Point = new Point(80,90);
	private final Point robotB4Point = new Point(80,30);
	private final Point robotB5Point = new Point(120,90);
	private final Point robotB6Point = new Point(120,30);
	private final Point robotM12Point = new Point(40,60);
	private final Point robotM34Point = new Point(80,60);
	private final Point robotM56Point = new Point(120,60);
		
	//Robot names
	private final String[] robotNames = new String[]{"RobotB1", "RobotB2", "RobotB3",
			"RobotB4", "RobotB5", "RobotB6", "RobotM12", "RobotM34", "RobotM56"};
	
	//Points where the robots should go
	private final Point[] robotLocations = new Point[]{robotB1Point, robotB2Point, robotB3Point,
			robotB4Point, robotB5Point, robotB6Point, robotM12Point, robotM34Point, robotM56Point};
	
	// Points that the robots can move between
	private final Point[] robotB1PointMove = new Point[]{depositB1PointExit, machineTAPoint,
			machineTBPoint, machineTCPoint};
	private final Point[] robotB2PointMove = new Point[]{depositB2PointExit, machineTDPoint,
			machineTEPoint, machineTFPoint};
	private final Point[] robotB3PointMove = new Point[]{depositB3PointExit, machineTGPoint,
			machineTHPoint, machineTIPoint, machineTJPoint};
	private final Point[] robotB4PointMove = new Point[]{depositB4PointExit, machineTKPoint,
			machineTLPoint, machineTMPoint, machineTNPoint};
	private final Point[] robotB5PointMove = new Point[]{depositB5PointExit, machineTOPoint,
			machineTPPoint, machineTQPoint};
	private final Point[] robotB6PointMove = new Point[]{depositB6PointExit, machineTRPoint,
			machineTSPoint, machineTTPoint};
	private final Point[] robotM12PointMove = new Point[]{enterPoint, depositB1PointEnter,
			depositB2PointEnter, buffer1PointEnter};
	private final Point[] robotM34PointMove = new Point[]{buffer1PointExit, depositB3PointEnter,
			depositB4PointEnter, buffer2PointEnter};
	private final Point[] robotM56PointMove = new Point[]{buffer2PointExit, depositB5PointEnter,
			depositB6PointEnter, exitPoint};
	
	//Where the robots can move things between
	private final Point[][] robotNeighborLocations = new Point[][]{robotB1PointMove, robotB2PointMove, robotB3PointMove,
		robotB4PointMove, robotB5PointMove, robotB6PointMove, robotM12PointMove, robotM34PointMove, robotM56PointMove};
		
		
	//================================================================================
    // Neighborhoods
    //================================================================================
		
	//The index of the MACHINE neighbors for each of the robots
	private final int[] robotB1MachineIndex = new int[]{0, 1, 2};
	private final int[] robotB2MachineIndex = new int[]{3, 4, 5};
	private final int[] robotB3MachineIndex = new int[]{6, 7, 8, 9};
	private final int[] robotB4MachineIndex = new int[]{10, 11, 12, 13};
	private final int[] robotB5MachineIndex = new int[]{14, 15, 16};
	private final int[] robotB6MachineIndex = new int[]{17, 18, 19};
	private final int[] robotM12MachineIndex = new int[]{};
	private final int[] robotM34MachineIndex = new int[]{};
	private final int[] robotM56MachineIndex = new int[]{};
		
	// The index of the MACHINE neighbors for all of the robots
	private final int[][] robotMachineNeighborIndices = new int[][]{robotB1MachineIndex, robotB2MachineIndex, robotB3MachineIndex, robotB4MachineIndex, robotB5MachineIndex, robotB6MachineIndex, robotM12MachineIndex, robotM34MachineIndex, robotM56MachineIndex};

	//The index of the BUFFER neighbors for each of the robots
	private final int[] robotB1BufferIndex = new int[]{2};
	private final int[] robotB2BufferIndex = new int[]{3};
	private final int[] robotB3BufferIndex = new int[]{4};
	private final int[] robotB4BufferIndex = new int[]{5};
	private final int[] robotB5BufferIndex = new int[]{6};
	private final int[] robotB6BufferIndex = new int[]{7};
	private final int[] robotM12BufferIndex = new int[]{0, 2, 3, 8};
	private final int[] robotM34BufferIndex = new int[]{4, 5, 8, 9};
	private final int[] robotM56BufferIndex = new int[]{1, 6, 7, 9};
			
	//The index of the BUFFER neighbors for all of the robots
	private final int[][] robotBufferNeighborIndices = new int[][]{robotB1BufferIndex, robotB2BufferIndex, robotB3BufferIndex,	robotB4BufferIndex, robotB5BufferIndex, robotB6BufferIndex, robotM12BufferIndex, robotM34BufferIndex, robotM56BufferIndex};
	private HashMap<Point, Object> tableLocationObject;

			
//================================================================================
// START OF METHODS
//================================================================================
	@Override
	public Context<Object> build(Context<Object> context) {
		
		resetVariables();
		
		context.setId("JournalPaperSimulation");
		
		//Physical System Floor
		Context<Object> physicalContext = new DefaultContext<Object>("physicalContext");
		context.addSubContext(physicalContext);
		
		//Cyber System
				Context<Object> cyberContext = new DefaultContext<Object>("cyberContext");
				context.addSubContext(cyberContext);
		
		//Physical grid
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> physicalGrid =  gridFactory.createGrid ("physicalGrid", physicalContext, new GridBuilderParameters<Object>(new BouncyBorders(),
						new SimpleGridAdder<Object>(), true, XDIM, YDIM));
		
		buildProductionControl(physicalContext, physicalGrid, cyberContext);
		
		buildAgentNetwork(cyberContext);
		
		//Testing
		cyberContext.add(new Testing(this.listBufferLLC, this.bufferLocations, this.listBufferAgent, cyberContext));
		
		return context;
	}

	private void buildProductionControl(Context<Object> physicalContext, Grid<Object> physicalGrid, Context<Object> cyberContext) {
		
		this.tableLocationObject = new HashMap<Point, Object>();
		
		//================================================================================
	    // Machines
	    //================================================================================
		
		//Create new machines using the initialized information
		for (int index = 0; index < this.machineLocations.length; index++){
			char nameSuffix = (char) (65+index);
			Machine machine = new Machine("machineT" + nameSuffix, this.machineLocations[index], physicalGrid, 0, this.machineTimes[index]);
			physicalContext.add(machine);
			physicalGrid.moveTo(machine, machine.getCenter().x,machine.getCenter().y);
			
			MachineLLC machineLLC = new MachineLLC(machine);
			this.listMachineLLC.add(machineLLC);
			
			this.tableLocationObject.put(this.machineLocations[index], machine);
		}
		
		//================================================================================
	    // Buffers
	    //================================================================================
		
		for (int index = 0; index < this.bufferLocations.length; index++){
			Buffer buffer = new Buffer(this.bufferNames[index], this.bufferEnterLocations[index], 
					this.bufferLocations[index], physicalGrid);
			physicalContext.add(buffer);
			physicalGrid.moveTo(buffer, buffer.getStoragePoint().x, buffer.getStoragePoint().y);
			
			BufferLLC bufferLLC = new BufferLLC(buffer);
			this.listBufferLLC.add(bufferLLC);
			
			this.tableLocationObject.put(this.bufferLocations[index], buffer);
			for (Point point : this.bufferEnterLocations[index]){
				this.tableLocationObject.put(point, buffer);
			}
		}
		
		//================================================================================
	    // Robots
	    //================================================================================
		
		for (int index = 0; index < this.robotLocations.length; index++){
			//Build the physical robot
			Robot robot = new Robot(this.robotNames[index], this.robotLocations[index], this.robotSpeed, physicalGrid, 25);
			physicalContext.add(robot);
			physicalGrid.moveTo(robot, robot.getCenter().x, robot.getCenter().y);
			
			//Build the lower level controller
			RobotLLC robotLLC = new RobotLLC(robot);
			int locationAmount = this.robotNeighborLocations[index].length;
			for (int j = 0; j < locationAmount; j++){
				for (int k = 0; k < locationAmount; k++){
					if(j!=k){
						//Write the move programs for all of the points that the robot can move the part between
						robotLLC.writeMoveObjectProgram("move"+j, this.robotNeighborLocations[index][j],
								this.robotNeighborLocations[index][k], "Part");
					}
				}
			}
			
			this.listRobotLLC.add(robotLLC);
			cyberContext.add(robotLLC);		
			
			this.tableLocationObject.put(this.robotLocations[index], robot);
		}
				
		//================================================================================
	    // Parts
	    //================================================================================
		
		Part part = new Part(new RFIDTag('a'));
		physicalContext.add(part);
		physicalGrid.moveTo(part, 18, 60);	
	}
	
	private void buildAgentNetwork(Context<Object> cyberContext) {
		//================================================================================
	    // Robots
	    //================================================================================
		
		for (RobotLLC robotLLC : this.listRobotLLC){
			RobotAgent robotAgent = new RobotAgent(robotLLC.getRobot().toString()+" Agent", robotLLC, this.tableLocationObject);
			cyberContext.add(robotAgent);
			
			this.listRobotAgent.add(robotAgent);
		}
		
		//================================================================================
	    // Machines
	    //================================================================================
		
		for (MachineLLC machineLLC : this.listMachineLLC){
			MachineAgent machineAgent = new MachineAgent(machineLLC.getMachine().toString()+" Agent", machineLLC);
			cyberContext.add(machineAgent);
			
			this.listMachineAgent.add(machineAgent);
		}
		
		//================================================================================
	    // Buffer
	    //================================================================================
		
		for (BufferLLC bufferLLC : this.listBufferLLC){
			BufferAgent bufferAgent = new BufferAgent(bufferLLC.getBuffer().toString()+" Agent", bufferLLC);
			cyberContext.add(bufferAgent);
			
			this.listBufferAgent.add(bufferAgent);
		}
		
		//================================================================================
	    // Resource agent neighbors
	    //================================================================================
		
		//For resource agents, populate 
		for(int index = 0; index < this.robotLocations.length; index++){
			//find the robot agent and the neighbor indices
			RobotAgent robotAgent = this.listRobotAgent.get(index);
			int[] robotMachineIndices= this.robotMachineNeighborIndices[index];
			int[] robotBufferIndices = this.robotBufferNeighborIndices[index];
			
			//Machine agents neighbor population
			for (int machineIndex : robotMachineIndices){
				MachineAgent machineAgent = this.listMachineAgent.get(machineIndex);
				robotAgent.addNeighbor(machineAgent);
				machineAgent.addNeighbor(robotAgent);
			}
			
			//Buffer agents neighbor population
			for (int bufferIndex : robotBufferIndices){
				BufferAgent bufferAgent = this.listBufferAgent.get(bufferIndex);
				robotAgent.addNeighbor(bufferAgent);
				bufferAgent.addNeighbor(robotAgent);
			}
		}		
		
		//================================================================================
	    // Parts
	    //================================================================================
		
	}
	
	/**
	 * Makes sure all of the variables are reset whenever we reset the simulation
	 */
	private void resetVariables() {
		
		ArrayList<Object> clearLists = new ArrayList<Object>();
		clearLists.addAll(listMachineLLC);
		clearLists.addAll(listRobotLLC);
		clearLists.addAll(listBufferLLC);
		clearLists.addAll(listMachineAgent);
		clearLists.addAll(listRobotAgent);
		clearLists.addAll(listBufferAgent);
		
		for (@SuppressWarnings("unused") Object object : clearLists){
			object = null;
		}
		
		this.listMachineLLC.clear();   
		this.listRobotLLC.clear();
		this.listBufferLLC.clear();
		
		this.listMachineAgent.clear();
		this.listRobotAgent.clear();
		this.listBufferAgent.clear();
	}
	
}