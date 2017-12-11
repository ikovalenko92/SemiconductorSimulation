package Buffer;

import java.awt.Point;
import java.util.ArrayList;

public class BufferLLC {
	
	private String name;	
	private Buffer buffer;

	/**
	 * @param name
	 * @param buffer
	 */
	public BufferLLC(Buffer buffer){
		this.name = name;
		this.buffer = buffer;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ""+this.buffer;
	}
	
	//================================================================================
    // Input from HLC
    //================================================================================
	
	/**
	 * @param partName
	 * @param enterPoint
	 * @return
	 */
	public boolean moveToStorage(String partName, Point enterPoint){
			
		//Check to see if the proposed point is one of the enter points
		boolean flag = false;
		for (Point point : buffer.getEnterPoints()){
			if (enterPoint.equals(point)){
				flag = true;
			}
		}
		
		if (!flag){
			return false;
		}		
		return buffer.moveToStorage(partName, enterPoint);

	}
	
	/**
	 * @param partName
	 * @param enterPoint
	 * @return
	 */
	public boolean moveFromStorage(String partName, Point enterPoint){
			
		//Check to see if the proposed point is one of the enter points
		boolean flag = false;
		for (Point point : buffer.getEnterPoints()){
			if (enterPoint.equals(point)){
				flag = true;
			}
		}
		
		if (!flag){
			return false;
		}
		
		return buffer.moveFromStorage(partName, enterPoint);
	}
	//================================================================================
    // Output to HLC
    //================================================================================
	
	public Point[] getEnterPoints(){
		return this.buffer.getEnterPoints();
	}

	public Point getStoragePoint() {
		return this.buffer.getStoragePoint();
	}
	
	public ArrayList<String> getPartList(){
		return this.buffer.getPartList();
	}
	
	/**
	 * @param partName (string)
	 * @return
	 */
	public boolean hasPart(String partName){
		return this.buffer.getPartList().contains(partName);
	}

	public Buffer getBuffer() {
		return this.buffer;
	}	
}
