package sharedInformation;

import java.awt.Point;

public interface SystemVertex {
	
	public Point getLocation();
	public Object getObject();
	public String getProcessCompleted();
	
	//These need to be overwritten:
	//Change to "Instance of" not "Object"
	//Change to getters instead of .parameter
	public boolean equals(Object vertex);
	public int hashCode();
}