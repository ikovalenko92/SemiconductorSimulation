package Machine;

import java.awt.Color;
import java.awt.geom.GeneralPath;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;

public class MachineStyle extends DefaultStyleOGL2D {
	@Override
	public VSpatial getVSpatial(Object object, VSpatial spatial) {
		GeneralPath shape = new GeneralPath(0);
		shape.moveTo(-2,-1);
		shape.lineTo(2,-1);
		shape.lineTo(2,1);
		shape.lineTo(-2,1);
		shape.lineTo(-2,-1);
		shape.closePath();
		
		GeneralPath inside = new GeneralPath(0);
		inside.moveTo(-1,-0.5);
		inside.lineTo(-1,0.5);
		inside.lineTo(1,0.5);
		inside.lineTo(1,-0.5);
		inside.lineTo(-1,-0.5);
		inside.closePath();
		
		shape.append(inside, false);
		
		spatial = shapeFactory.createShape(shape);
		
		return spatial;
	}

	// Override the below to change the other style properties:
	public Color getColor(Object object) {
		return Color.GRAY;
	}
	
	public float getScale(Object object) {
		return 14;
	}
	
	public float getRotation(Object object) {
		return ((Machine) object).getRotation();
	}
}
