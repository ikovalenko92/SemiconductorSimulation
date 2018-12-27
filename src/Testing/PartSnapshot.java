package Testing;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import Part.Part;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;

public class PartSnapshot {

	private Point exitPoint = new Point (142,60);
	private Point exitHumanPointPlace = new Point (6,10);
	
	private Grid<Object> physicalGrid;
	private Context<Object> physicalContext;
	int[][] heatMap;
	private Hashtable<Object, Point> lastPlaceMap;
	private String prefix;

	/**
	 * @param startTime
	 * @param intervalTime
	 * @param physicalGrid
	 * @param physicalContext
	 * @param prefix - (name for saving the file)
	 */
	public PartSnapshot(int startTime, int intervalTime, Grid<Object> physicalGrid, Context<Object> physicalContext, String prefix) {	
		this.physicalGrid = physicalGrid;
		this.physicalContext = physicalContext;
		
		this.heatMap = new int[this.physicalGrid.getDimensions().getWidth()]
				[this.physicalGrid.getDimensions().getHeight()];
		this.lastPlaceMap = new Hashtable<Object,Point>();
		
		this.prefix = prefix;
		
		//Run the gather snapshot method based on some interval
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		schedule.schedule(ScheduleParameters.createRepeating(startTime,intervalTime),
				this, "gatherSnapshots");
	}
	
	/**
	 * Takes a snapshot of the existing "Part" objects in the system 
	 */
	public void gatherSnapshots(){		
		int completedPartCounta = 0;
		int completedPartCountb = 0;
		int completedPartCountc = 0;
		int exitedPartCounta = 0;
		int exitedPartCountb = 0;
		int exitedPartCountc = 0;
		int livePartCount = 0;
		
		for (Object object:this.physicalGrid.getObjects()){
			if (object.getClass().toString().contains("Part")){
				int x_coor = this.physicalGrid.getLocation(object).getX();
				int y_coor = this.physicalGrid.getLocation(object).getY();
				
				if (x_coor == this.exitPoint.x && y_coor == this.exitPoint.y){
					if(((Part) object).getRFIDTag().getType() == 'b'){
						completedPartCountb++;
					}
					else if(((Part) object).getRFIDTag().getType() == 'c'){
						completedPartCountc++;
					}
					else{
						completedPartCounta++;
					}
				}
				else if (x_coor == this.exitHumanPointPlace.x && y_coor == this.exitHumanPointPlace.y){
					if(((Part) object).getRFIDTag().getType() == 'b'){
						exitedPartCountb++;
					}
					else if(((Part) object).getRFIDTag().getType() == 'c'){
						exitedPartCountc++;
					}
					else{
						exitedPartCounta++;
					}
				}
				//See how many parts are live
				else{
					livePartCount++;
				}
			}
		}
		
		
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		try {
			PrintWriter out = new PrintWriter(this.prefix+"outFile" + schedule.getTickCount() +".txt");
			out.println("Completed Part Count (a): " + completedPartCounta);
			out.println("Exited Part Count (a): " + exitedPartCounta);
			
			if(completedPartCountb!=0){
				out.println("Completed Part Count (b): " + completedPartCountb);
			}
			
			if (exitedPartCountb!=0){
				out.println("Exited Part Count (b): " + exitedPartCountb);
			}
			
			if(completedPartCountc!=0){
				out.println("Completed Part Count (c): " + completedPartCountc);
			}
			
			if (exitedPartCountc!=0){
				out.println("Exited Part Count (c): " + exitedPartCountc);
			}
			
			//out.println("Live Part Count: " + completedPartCount);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		//System.out.println(livePartCount);
	}

}
