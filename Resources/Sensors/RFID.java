package Sensors;

import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import repast.simphony.space.grid.Grid;

public class RFID {
	
	private Grid<Object> grid;
	private double direction;
	private Point location;

	public RFID (Point location, double direction, Grid<Object> grid) {
		this.grid = grid;
		this.direction = direction;
		this.location = location;
	}

	public ArrayList<RFIDTag> query() {
		int newX = (int) (location.getX() + Math.cos(direction));
		int newY = (int) (location.getY() + Math.sin(direction));
		
		Iterable<Object> objectsRFID = grid.getObjectsAt(newX, newY);
		
		ArrayList<RFIDTag> rfidHere = new ArrayList<RFIDTag>();
		
		for(Object object : objectsRFID){
				//Return the status object
				Method method;
				try {
					method = object.getClass().getMethod("getRFIDTag");
					rfidHere.add((RFIDTag) method.invoke(object));
				}
				catch (NoSuchMethodException | SecurityException | IllegalAccessException |
						IllegalArgumentException| InvocationTargetException e) {
					System.out.println("No RFID at [" + location.getX() +", " + location.getY() + "]");
				}
		}
		return rfidHere;
	}
	
	public void write(Object object, RFIDTag status) {
		int newX = (int) (location.getX() + Math.cos(direction));
		int newY = (int) (location.getY() + Math.sin(direction));

		//Return the status object
		Method method;
		try {
			method = object.getClass().getMethod("setRFIDTag");
			method.invoke(object,status);
		}
		catch (NoSuchMethodException | SecurityException | IllegalAccessException |
				IllegalArgumentException| InvocationTargetException e) {
			System.out.println("No RFID at [" + location.getX() +", " + location.getY() + "]");
		}
		
	}
}