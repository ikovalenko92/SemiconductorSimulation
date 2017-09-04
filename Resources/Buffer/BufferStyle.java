package Buffer;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;

public class BufferStyle extends DefaultStyleOGL2D {

	@Override
	public VSpatial getVSpatial(Object object, VSpatial spatial) {
	
		spatial = shapeFactory.createShape(new Rectangle2D.Double(-3.5,-3.5, 7, 7));
		
		return spatial;
	}
	
	// Override the below to change the other style properties:
	public Color getColor(Object object) {
		return Color.WHITE;
	}
	
	public float getScale(Object object) {
		return 10;
	}
	
	public Color getBorderColor(Object object){
		return Color.BLACK;
	}
	
	public int getBorderSize(Object object){
		return 3;
	}

}
