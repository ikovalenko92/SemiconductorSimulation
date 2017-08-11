package systemSimulation;

import java.awt.Point;

import Robot.Robot;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.grid.BouncyBorders;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;


public class _TestbedContextBuilder implements ContextBuilder<Object> {
	//Overall dimensions of the area
	private final int XDIM = 160;
	private final int YDIM = 120;
	
	//================================================================================
    // Robots
    //================================================================================
	
	private Point robotB1Point = new Point(40,90);
	private Point robotB2Point = new Point(40,30);
	private Point robotB3Point = new Point(80,90);
	private Point robotB4Point = new Point(80,30);
	private Point robotB5Point = new Point(120,90);
	private Point robotB6Point = new Point(120,30);
	private Point robotM12Point = new Point(40,60);
	private Point robotM34Point = new Point(80,60);
	private Point robotM56Point = new Point(120,60);
	
	//Robot2 is slightly faster
	//private Data_RobotSystem robot1Data = new Data_RobotSystem("robot1", 8, 20, robot1Point);
	//private Data_RobotSystem robot2Data = new Data_RobotSystem("robot2", 10, 20, robot2Point);
	//private Data_RobotSystem robot3Data = new Data_RobotSystem("robot3", 8, 20, robot3Point);
	//private Data_RobotSystem[] robotData= new Data_RobotSystem[]{robot1Data,robot2Data,robot3Data};	

    //================================================================================
    // Machine
    //================================================================================
	
	private Point machineTAPoint = new Point (30,100);
	private Point machineTBPoint = new Point (50,90);
	private Point machineTCPoint = new Point (30,80);
	private Point machineTDPoint = new Point (50,40);
	private Point machineTEPoint = new Point (30,30);
	private Point machineTFPoint = new Point (50,20);
	private Point machineTGPoint = new Point (70,100);
	private Point machineTHPoint = new Point (90,90);
	private Point machineTIPoint = new Point (70,80);
	private Point machineTJPoint = new Point (90,40);
	private Point machineTKPoint = new Point (70,30);
	private Point machineTLPoint = new Point (90,20);
	private Point machineTMPoint = new Point (110,100);
	private Point machineTNPoint = new Point (130,100);
	private Point machineTOPoint = new Point (110,80);
	private Point machineTPPoint = new Point (130,80);
	private Point machineTQPoint = new Point (110,40);
	private Point machineTRPoint = new Point (130,40);
	private Point machineTSPoint = new Point (110,20);
	private Point machineTTPoint = new Point (130,20);

    //================================================================================
    // Storage
    //================================================================================
	
	private Point enterPoint = new Point (20,60);
	private Point exitPoint = new Point (140,60);
	private Point depositB1Point = new Point (40,70);
	private Point depositB2Point = new Point (40,50);
	private Point depositB3Point = new Point (80,70);
	private Point depositB4Point = new Point (80,50);
	private Point depositB5Point = new Point (120,70);
	private Point depositB6Point = new Point (120,50);
	private Point buffer1Point = new Point (60,60);
	private Point buffer2Point = new Point (90,60);
	
//================================================================================
// START OF METHODS
//================================================================================
	
//------------------------------------------------------------------------------------
// Initailizing Everything
//------------------------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	@Override
	public Context build(Context<Object> context) {
		context.setId("ManufacturingTestbed");
		
		//Physical System Floor
		Context<Object> physicalContext = new DefaultContext<Object>("physicalContext");
		context.addSubContext(physicalContext);
		
		//Physical grid
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> physicalGrid =  gridFactory.createGrid ("physicalGrid", physicalContext, new GridBuilderParameters<Object>(new BouncyBorders(),
						new SimpleGridAdder<Object>(), true, XDIM, YDIM));
		
		buildManufacturingSystem(physicalContext, physicalGrid);
		
		return context;
	}

	private void buildManufacturingSystem(Context<Object> physicalContext, Grid<Object> physicalGrid) {
		Robot robot = new Robot("TestRobot", new Point(50,50), 1, physicalGrid, 10);
		physicalContext.add(robot);
		physicalGrid.moveTo(robot, robot.getCenter().x, robot.getCenter().y);
		
	}
}