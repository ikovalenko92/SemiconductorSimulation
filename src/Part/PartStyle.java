package Part;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D.Float;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;

public class PartStyle extends DefaultStyleOGL2D {
	
	@Override
	public VSpatial getVSpatial(Object object, VSpatial spatial) {
		GeneralPath gp= ((Part) object).getShape();
		
		char partType = ((Part) object).getRFIDTag().getType();
		GeneralPath points = new GeneralPath();
		
		// Case statements to identify the type of part it is
		// Each block creates a vertical line. Part A - 1 line, Part B - 2 lines, etc.
		if (partType == 'A'){
			GeneralPath point = new GeneralPath();
			point.moveTo(0,-1.2);
			point.lineTo(0,-2.2);
			points.append(point,false);
		}
		
		else if (partType == 'B'){
			GeneralPath point = new GeneralPath();
			point.moveTo(-0.2,-1.2);
			point.lineTo(-0.2,-2.2);
			points.append(point,false);
			
			point.reset();
			point.moveTo(0.2,-1.2);
			point.lineTo(0.2,-2.2);
			points.append(point,false);
		}
		
		/*else if (partType == 'C'){
			GeneralPath point = new GeneralPath();
			point.moveTo(-0.3,-1.2);
			point.lineTo(-0.3,-2.2);
			points.append(point,false);
			
			point.reset();
			point.moveTo(0,-1.2);
			point.lineTo(0,-2.2);
			points.append(point,false);
			
			point.reset();
			point.moveTo(0.3,-1.2);
			point.lineTo(0.3,-2.2);
			points.append(point,false);
		}
		
		else if (partType == 'D'){
			GeneralPath point = new GeneralPath();
			point.moveTo(-0.6,-1.2);
			point.lineTo(-0.6,-2.2);
			points.append(point,false);
			
			point.reset();
			point.moveTo(-0.2,-1.2);
			point.lineTo(-0.2,-2.2);
			points.append(point,false);
						
			point.reset();
			point.moveTo(0.2,-1.2);
			point.lineTo(0.2,-2.2);
			points.append(point,false);
			
			point.reset();
			point.moveTo(0.6,-1.2);
			point.lineTo(0.6,-2.2);
			points.append(point,false);
		}
		
		else if (partType == 'E'){
			GeneralPath point = new GeneralPath();
			point.moveTo(-0.6,-1.2);
			point.lineTo(-0.6,-2.2);
			points.append(point,false);
			
			point.reset();
			point.moveTo(-0.3,-1.2);
			point.lineTo(-0.3,-2.2);
			points.append(point,false);
			
			point.reset();
			point.moveTo(0,-1.2);
			point.lineTo(0,-2.2);
			points.append(point,false);
			
			point.reset();
			point.moveTo(0.3,-1.2);
			point.lineTo(0.3,-2.2);
			points.append(point,false);
			
			point.reset();
			point.moveTo(0.6,-1.2);
			point.lineTo(0.6,-2.2);
			points.append(point,false);
		}
		
		else if (partType == 'F'){
			GeneralPath point = new GeneralPath();
			point.moveTo(-1,-1.2);
			point.lineTo(-1,-2.2);
			points.append(point,false);
			
			point.reset();
			point.moveTo(-0.6,-1.2);
			point.lineTo(-0.6,-2.2);
			points.append(point,false);
			
			point.reset();
			point.moveTo(-0.2,-1.2);
			point.lineTo(-0.2,-2.2);
			points.append(point,false);
						
			point.reset();
			point.moveTo(0.2,-1.2);
			point.lineTo(0.2,-2.2);
			points.append(point,false);
			
			point.reset();
			point.moveTo(0.6,-1.2);
			point.lineTo(0.6,-2.2);
			points.append(point,false);
			
			point.reset();
			point.moveTo(1,-1.2);
			point.lineTo(1,-2.2);
			points.append(point,false);
		}
		*/
		gp.append(points, false);
		
		spatial = shapeFactory.createShape(gp);
		
		return spatial;
	}

	// Override the below to change the other style properties:
	public Color getColor(Object object) {
		return ((Part) object).getColor();
	}
	
	public float getScale(Object object) {
		return ((Part) object).getSize();
	}
	
	public int getBorderSize(Object object){
		return 0;
	}
}
