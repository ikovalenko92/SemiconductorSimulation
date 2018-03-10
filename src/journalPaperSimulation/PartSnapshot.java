package journalPaperSimulation;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;

public class PartSnapshot {

	private Point exitPoint = new Point (142,60);
	private Point exitHumanPointPlace = new Point (6,10);
	
	private Grid<Object> physicalGrid;
	private Context<Object> physicalContext;
	int[][] heatMap;
	private Hashtable<Object, Point> lastPlaceMap;

	public PartSnapshot(Grid<Object> physicalGrid, Context<Object> physicalContext) {	
		this.physicalGrid = physicalGrid;
		this.physicalContext = physicalContext;
		
		this.heatMap = new int[this.physicalGrid.getDimensions().getWidth()]
				[this.physicalGrid.getDimensions().getHeight()];
		this.lastPlaceMap = new Hashtable<Object,Point>();
	}
	
	@ScheduledMethod ( start = 1, interval = 100, priority = -500)
	public void gatherSnapshots(){		
		int partCount = 0;
		for (Object object:this.physicalGrid.getObjects()){
			if (object.getClass().toString().contains("Part")){
				int x_coor = this.physicalGrid.getLocation(object).getX();
				int y_coor = this.physicalGrid.getLocation(object).getY();
				if(!((x_coor == this.exitPoint.x && y_coor == this.exitPoint.y) || 
						(x_coor == this.exitHumanPointPlace.x && y_coor == this.exitHumanPointPlace.y))){
					partCount++;
				}
			}
		}
		System.out.println(partCount);
	}

}
