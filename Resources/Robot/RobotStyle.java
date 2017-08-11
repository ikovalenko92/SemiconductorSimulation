package Robot;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;

public class RobotStyle extends DefaultStyleOGL2D {

	@Override
	public VSpatial getVSpatial(Object object, VSpatial spatial) {
		ArrayList<Point> gp= ((Robot) object).getPath();
		
		GeneralPath totalPath = new GeneralPath();
		
		for (int ind = 0; ind < gp.size()-1;ind++){
			Rectangle rect= new Rectangle(gp.get(ind));
			rect.add(gp.get(ind+1));
			rect.grow(1, 1);
			GeneralPath boundingPath = new GeneralPath(rect);
			
			totalPath.append(boundingPath,false);
		}	
		
		spatial = shapeFactory.createShape(totalPath);
		
		return spatial;
	}

	// Override the below to change the other style properties:
	public Color getColor(Object object) {
		return Color.ORANGE;
	}
	
	public float getScale(Object object) {
		return 10;
	}
	
	public Color getBorderColor(Object object){
		return Color.BLACK;
	}

	public int getBorderSize(Object object){
		return 0;
	}
}
