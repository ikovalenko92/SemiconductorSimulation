package Sensors;

import java.awt.Point;

import repast.simphony.space.grid.Grid;

public class Presence{

	private Grid<Object> grid;
	private double direction;
	private Point location;

	/**
	 * @param location
	 * @param direction
	 * @param grid
	 */
	public Presence(Point location, double direction, Grid<Object> grid) {
		this.grid = grid;
		this.direction = direction;
		this.location = location;
	}
	
	//================================================================================
    // System Input
    //================================================================================
	
	
	/**
	 * @return If there is something in front of this sensor
	 */
	public Boolean query() {
		int newX = (int) (location.getX() + Math.cos(direction));
		int newY = (int) (location.getY() + Math.sin(direction));
		
		if (grid.getObjectAt(newX, newY) == null){
			return false;
		}	
		else{
			return true;
		}
	}
	
	/**
	 * @return If there is something in front of this sensor
	 */
	public Boolean queryExcluding(String exclude) {
		int newX = (int) (location.getX() + Math.cos(direction));
		int newY = (int) (location.getY() + Math.sin(direction));
		
		if (newX == 15){};
		
		if (grid.getObjectAt(newX, newY) == null){
			return false;
		}	
		else{
			for (Object object : grid.getObjectsAt(newX,newY)){
				if (!object.getClass().toString().contains(exclude)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	//================================================================================
    // Output regarding location
    //================================================================================
	
	public Point getLocationMonitored(){
		int newX = (int) (location.getX() + Math.cos(direction));
		int newY = (int) (location.getY() + Math.sin(direction));
		return new Point(newX, newY);
	}
}
