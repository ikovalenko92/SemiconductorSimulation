package journalPaperSimulation;


import initializingAgents.PartCreatorforBuffer;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import Buffer.Buffer;
import Buffer.BufferLLC;
import Machine.Machine;
import Machine.MachineLLC;
import Robot.Robot;
import Robot.RobotLLC;
import Testing.PartSnapshot;
import Testing.RemoveExitAndEnd;
import Testing.TestingNormalOperation;
import Testing.TestingBrokenMachine;
import Testing.TestingNewProductType;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.BouncyBorders;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import resourceAgent.BufferAgent;
import resourceAgent.ExitAgent;
import resourceAgent.MachineAgent;
import resourceAgent.RobotAgent;
import sharedInformation.ResourceEvent;
import sharedInformation.ProductState;
import sharedInformation.PhysicalProperty;


public class SimulationContextBuilder implements ContextBuilder<Object> {
	//Overall dimensions of the area
	private final int XDIM = 160;
	private final int YDIM = 120;
	
//================================================================================
// START OF VARIABLE INITIALIZING
//================================================================================	
	
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
	
	int scale = 2;
	//Time for processes
/*	int S1time = 70*scale;
	int S2time = 45*scale;
	int S3time = 55*scale;
	int S4time = 50*scale;
	int S5time = 60*scale;
	int S6time = 10*scale;*/
	int S1time = 75*scale;
	int S2time = 30*scale;
	int S3time = 55*scale;
	int S4time = 50*scale;
	int S5time = 85*scale;
	int S6time = 10*scale;

	
	//Machine Times: negative numbers indicate that that process can't be performed by the specific machine
	//e.g. Machine TA can performa process 1 and 5 in 225 and 255 ticks, but can't perform other processes
	private int[] machineTATime = new int[]{S1time,-1,-1,-1, S5time,-1};
	private int[] machineTBTime = new int[]{S1time,-1,-1,-1,S5time,-1};
	private int[] machineTCTime = new int[]{S1time,-1,-1,-1,S5time,-1};
	private int[] machineTDTime = new int[]{S1time,-1,-1,-1,S5time,-1};
	private int[] machineTETime = new int[]{S1time,-1,-1,-1,S5time,-1};
	private int[] machineTFTime = new int[]{S1time,-1,-1,-1,S5time,-1};
	private int[] machineTGTime = new int[]{S1time,-1,-1,-1,S5time,-1};
	private int[] machineTHTime = new int[]{S1time,-1,-1,-1,S5time,-1};
	private int[] machineTITime = new int[]{-1,-1,S3time,-1,-1,S6time};
	private int[] machineTJTime = new int[]{-1,-1,S3time,-1,-1,S6time};
	private int[] machineTKTime = new int[]{-1,-1,S3time,-1,-1,S6time};
	private int[] machineTLTime = new int[]{-1,-1,S3time,-1,-1,S6time};
	private int[] machineTMTime = new int[]{-1,S2time,-1,S4time,-1,-1};
	private int[] machineTNTime = new int[]{-1,S2time,-1,S4time,-1,-1};
	private int[] machineTOTime = new int[]{-1,S2time,-1,S4time,-1,-1};
	private int[] machineTPTime = new int[]{-1,S2time,-1,S4time,-1,-1};
	private int[] machineTQTime = new int[]{-1,S2time,-1,S4time,-1,-1};
	private int[] machineTRTime = new int[]{-1,S2time,-1,S4time,-1,-1};
	private int[] machineTSTime = new int[]{-1,S2time,-1,S4time,-1,-1};
	private int[] machineTTTime = new int[]{-1,S2time,-1,S4time,-1,-1};

	
	private int[][] machineTimes = new int[][]{machineTATime, machineTBTime, machineTCTime,
		machineTDTime, machineTETime, machineTFTime, machineTGTime, machineTHTime, machineTITime,
		machineTJTime, machineTKTime, machineTLTime, machineTMTime, machineTNTime, machineTOTime,
		machineTPTime, machineTQTime, machineTRTime, machineTSTime, machineTTTime};
 

    //================================================================================
    // Buffers
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
	private Point exitPoint = new Point (18,60);
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
	private Point depositB1PointEnter = new Point (40,68);
	private Point depositB2PointEnter = new Point (40,52);
	private Point depositB3PointEnter = new Point (80,68);
	private Point depositB4PointEnter = new Point (80,52);
	private Point depositB5PointEnter = new Point (120,68);
	private Point depositB6PointEnter = new Point (120,52);
	private Point buffer1PointEnter = new Point (58,60);
	private Point buffer2PointEnter = new Point (98,60);

	//Exit points for bay buffers
	private Point exitBufferEnter = new Point (140,60);
	private Point depositB1PointExit = new Point (40,72);
	private Point depositB2PointExit = new Point (40,48);
	private Point depositB3PointExit = new Point (80,72);
	private Point depositB4PointExit = new Point (80,48);
	private Point depositB5PointExit = new Point (120,72);
	private Point depositB6PointExit = new Point (120,48);
	private Point buffer1PointExit = new Point (62,60);
	private Point buffer2PointExit = new Point (102,60);
	
	private final Point[] bufferLocations = new Point[]{exitPoint, exitPointStorage, depositB1Point, depositB2Point,
			depositB3Point,depositB4Point, depositB5Point, depositB6Point, buffer1Point, buffer2Point};
			
	private final Point[][] bufferEnterLocations = new Point[][]{{enterPoint}, {exitBufferEnter}, {depositB1PointEnter, depositB1PointExit},
		{depositB2PointEnter, depositB2PointExit}, {depositB3PointEnter, depositB3PointExit}, {depositB4PointEnter, depositB4PointExit},
		{depositB5PointEnter, depositB5PointExit},{depositB6PointEnter, depositB6PointExit},{buffer1PointEnter, buffer1PointExit},{buffer2PointEnter, buffer2PointExit}};
	
	//BUffer names
	private final String[] bufferNames = new String[]{"EnterBuffer", "ExitBuffer", "depositB1",
			"depositB2", "depositB3", "depositB4", "depositB5", "depositB6", "buffer1234", "buffer3456"};
		
	//================================================================================
    // Robots
    //================================================================================
	
	//Speed of the robots
	int robotSpeed = 20;

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
			depositB6PointEnter, exitBufferEnter};
	
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
    // Exit Agent
    //================================================================================

	private final Point exitHumanLocation = new Point(10,10);
	private final Point exitHumanPoint = new Point(6,10);
	private ExitAgent exitHumanAgent;
	private PartCreatorforBuffer partCreatora;
	
//================================================================================
// START OF METHODS
//================================================================================
	
	
	@Override
	public Context<Object> build(Context<Object> context) {
		
		resetVariables();
		
		context.setId("JournalPaperSimulation");
		
		//Machines, robots, and parts interact in the physical context
		Context<Object> physicalContext = new DefaultContext<Object>("physicalContext");
		context.addSubContext(physicalContext);
		
		//Agents live and interact in the Cyber Context
		Context<Object> cyberContext = new DefaultContext<Object>("cyberContext");
		context.addSubContext(cyberContext);
		
		//Physical grid
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> physicalGrid =  gridFactory.createGrid ("physicalGrid", physicalContext, new GridBuilderParameters<Object>(new BouncyBorders(),
						new SimpleGridAdder<Object>(), true, XDIM, YDIM));
		
		//Creates the machines, robots, and buffers
		buildProductionControl(physicalContext, physicalGrid, cyberContext);
		
		//Builds the agents for each resource
		buildAgentNetwork(cyberContext);
		
		//Puts a physical part into the system at some interval
		buildPartCreator(physicalContext, physicalGrid, cyberContext);
		
		//Builds the environment for recording some various scenarios. 
		buildTesting(physicalContext, physicalGrid, cyberContext);
		
		return context;
	}

	/**
	 * Creates a physical part (rectangle) at a buffer. This simulates putting a part into the system.
	 * @param physicalContext
	 * @param physicalGrid
	 * @param cyberContext
	 */
	private void buildPartCreator(Context<Object> physicalContext, Grid<Object> physicalGrid, Context<Object> cyberContext) {
		
		//Some of the parameters for the part creator
		int startTime = 5;
		int partCreatingInterval = 120;
		Buffer startingBuffer = this.listBufferLLC.get(0).getBuffer();
		BufferAgent startingBufferAgent = this.listBufferAgent.get(0);
		
		
		//Create and add this part creator to the cyber context
		this.partCreatora = new PartCreatorforBuffer(startingBuffer, startingBufferAgent,
				exitHumanAgent, physicalGrid, physicalContext, cyberContext, startTime, partCreatingInterval);
		cyberContext.add(partCreatora);
	}
	

	/**
	 * Creates some of the scenarios (e.g. shut down a machine) and the recordkeeping for it
	 * @param physicalContext
	 * @param physicalGrid
	 * @param cyberContext
	 */
	private void buildTesting(Context<Object> physicalContext, Grid<Object> physicalGrid, Context<Object> cyberContext) {
		
		//Just the prefix for collecting the data (T1 = Trial1, etc.)
		Parameters params = RunEnvironment.getInstance ().getParameters();
		String prefix = params.getString("prefix");
		
		//Clears all of the accumulated parts at the exit location and end buffer at the specified intervals
		RemoveExitAndEnd clearEnds = new RemoveExitAndEnd(physicalGrid, cyberContext, physicalContext,
				new int[]{50001,100001,150001,200001}, this.exitPointStorage, this.exitHumanPoint);
		cyberContext.add(clearEnds);
			
		//Obtain a Snapshot of all of the parts every 50000 ticks
		int startTime = 50000;
		int intervalTime = 50000;
		
		PartSnapshot partSnap = new PartSnapshot(startTime, intervalTime,
				physicalGrid,physicalContext, prefix);
		cyberContext.add(partSnap);

		//================================================================================
	    // Build the objects that simulate normal operation of the system for the first "endTick" ticks
	    //================================================================================	
		
		int endTick = 50000;
		TestingNormalOperation test = new TestingNormalOperation(physicalGrid,cyberContext,physicalContext, 
				endTick, this.exitPointStorage, this.exitHumanPoint, prefix);
		cyberContext.add(test);
		
		//================================================================================
	    // Build the objects that simulate a variety of parts in the system (a,b,c type parts)
		// Agents.initalizingAgents.PartCreatorforBuffer contains the specific processes in those plans
	    //================================================================================
		
		//Start times for each of the creators
		int bufferStartTimea = 50001;
		int bufferStartTimeb = 50121;
		int bufferStartTimec = 50241;
		
		//Initialize and add all of the part creators (a,b, and c) to the cyber context
		PartCreatorforBuffer partCreatora_new = new PartCreatorforBuffer(this.listBufferLLC.get(0).getBuffer(), this.listBufferAgent.get(0),
				exitHumanAgent, physicalGrid, physicalContext, cyberContext,bufferStartTimea,360);	
		PartCreatorforBuffer partCreatorb = new PartCreatorforBuffer(this.listBufferLLC.get(0).getBuffer(), this.listBufferAgent.get(0),
				exitHumanAgent, physicalGrid, physicalContext, cyberContext,bufferStartTimeb,360);
		PartCreatorforBuffer partCreatorc = new PartCreatorforBuffer(this.listBufferLLC.get(0).getBuffer(), this.listBufferAgent.get(0),
				exitHumanAgent, physicalGrid, physicalContext, cyberContext,bufferStartTimec,360);
		cyberContext.add(partCreatora_new);
		cyberContext.add(partCreatorb);
		cyberContext.add(partCreatorc);
		
		//Start up all of the new products
		TestingNewProductType test3b = new TestingNewProductType(physicalGrid,cyberContext,physicalContext,
				partCreatora_new, partCreatorb, partCreatorc,
				bufferStartTimea,bufferStartTimeb-1, bufferStartTimec-1, 100000 , this.exitPointStorage, this.exitHumanPoint, prefix);
		cyberContext.add(test3b);
		
		//================================================================================
	    // Build the objects that simulate a machine failure
	    //================================================================================	
		
		//Creates a new part creator
		PartCreatorforBuffer partCreatora_new2 = new PartCreatorforBuffer(this.listBufferLLC.get(0).getBuffer(), this.listBufferAgent.get(0),
				exitHumanAgent, physicalGrid, physicalContext, cyberContext,100001,120);
		cyberContext.add(partCreatora_new2);
		//Start up the broken machine test
		TestingBrokenMachine test2 = new TestingBrokenMachine(physicalGrid,cyberContext,physicalContext,
				100001, 102000, 150000, this.exitPointStorage, this.exitHumanPoint, prefix);
		cyberContext.add(test2);
		
	}

	/**
	 * Creates the machines, robots, and buffers
	 * @param physicalContext
	 * @param physicalGrid
	 * @param cyberContext
	 */
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
						robotLLC.writeMoveObjectProgram("move"+j + "" + k, this.robotNeighborLocations[index][j],
								this.robotNeighborLocations[index][k], "Part");
					}
				}
			}
			
			this.listRobotLLC.add(robotLLC);
			cyberContext.add(robotLLC);		
			
			this.tableLocationObject.put(this.robotLocations[index], robot);			
		}
		
		//================================================================================
	    // Exit Robot (and Agent)
	    //================================================================================
		
		Robot exitHumanRobot = new Robot("exitHuman", exitHumanLocation, 200, physicalGrid, 200);
		physicalContext.add(exitHumanRobot);
		physicalGrid.moveTo(exitHumanRobot, exitHumanLocation.x,exitHumanLocation.y);
		this.exitHumanAgent = new ExitAgent("exitHumanAgent", exitHumanRobot, "exit", physicalGrid);
		cyberContext.add(exitHumanRobot);
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
			
			//DoneProperty for end buffer agent//
			//Adds a Done physical property to the exit buffer agent
			if (bufferLLC.getBuffer().getStoragePoint().equals(exitPointStorage)){				
				ProductState n = null;
				for (ProductState node : bufferAgent.getCapabilities().getVertices()){
					for (PhysicalProperty property : node.getPhysicalProperties()){
						if (property.getPoint().equals(exitPointStorage)){n = node;}
					}
				}
				ProductState addNode = new ProductState(bufferLLC.getBuffer(), new PhysicalProperty("End"), new PhysicalProperty(n.getLocation()));
				ResourceEvent addEdge = new ResourceEvent(bufferAgent, n, addNode, "End", 0);
				bufferAgent.getCapabilities().addEdge(addEdge, n, addNode);}
			//DoneProperty for end buffer agent//
		}
		
		//================================================================================
	    // Resource agents neighbors
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
		
	}

	/**
	 * Makes sure all of the variables are reset whenever we reset the simulation
	 * (Like a garbage collector)
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