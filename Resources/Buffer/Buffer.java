package Buffer;

import java.awt.Point;
import java.util.ArrayList;

import Part.Part;
import repast.simphony.space.grid.Grid;

public class Buffer {

	private Point[] enterPoints;
	private Point storagePoint;
	
	private ArrayList<String> partList;
	
	private Grid<Object> grid;
	private String name;
	
	/**
	 * @param enterPoint1
	 * @param enterPoint2
	 * @param storagePoint
	 * @param grid
	 */
	public Buffer(String name, Point[] enterPoints, Point storagePoint, Grid<Object> grid) {
		this.name = name;
		this.enterPoints = enterPoints;
		this.storagePoint = storagePoint;
		
		this.grid = grid;
		
		this.partList = new ArrayList<String>();
	}
	
	@Override
	public String toString(){
		return this.name;
	}
		
	//================================================================================
    // System Output
    //================================================================================
	
	/**
	 * @return An array of all points to enter/exit the buffer
	 */
	public Point[] getEnterPoints() {
		return enterPoints;
	}

	/**
	 * @return The single storage point
	 */
	public Point getStoragePoint() {
		return storagePoint;
	}
	
	public ArrayList<String> getPartList(){
		return this.partList;
	}
	
	public Part getPartatStorage() {
		
		for (Object object : this.grid.getObjectsAt(storagePoint.x, storagePoint.y)){
			if (object.getClass().getName().contains("Part")){
				return (Part) object;
			}
		}
		
		return null;
	}
	

	//================================================================================
    // System Input
    //================================================================================
	/** Move the part from the enterPoint to a storage location
	 * @param partName
	 * @param enterPoint
	 * @return if the operation was performed
	 */
	public boolean moveToStorage(String partName, Point enterPoint){
		// Check all of the objects and move it to storage
		for (Object object : this.grid.getObjectsAt(enterPoint.x, enterPoint.y)){
			//Move the object if it is the desired part
			if (object.getClass().getName().contains("Part") && ((Part) object).toString().equals(partName)){
				grid.moveTo(object, this.storagePoint.x, this.storagePoint.y);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @param partName
	 * @param exitPoint
	 * @return if the proccess was completed
	 */
	public boolean moveFromStorage(String partName, Point enterPoint){
		// Check all of the objects and move it out of storage
		for (Object object : this.grid.getObjectsAt(this.storagePoint.x, this.storagePoint.y)){
			//Move the object if it is the desired part
			if (object.getClass().getName().contains("Part") && ((Part) object).toString().equals(partName)){
				grid.moveTo(object, enterPoint.x, enterPoint.y);
				return true;
			}
		}
		
		return false;
	}
	
}