package Machine;

import java.awt.Color;
import java.awt.geom.GeneralPath;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;

public class MachineStyle extends DefaultStyleOGL2D {
	@Override
	public VSpatial getVSpatial(Object object, VSpatial spatial) {
		
		GeneralPath shape = new GeneralPath(0);
		shape.moveTo(-3,-5);
		shape.lineTo(3,-5);
		shape.lineTo(3,-1);
		shape.lineTo(-3,-1);
		shape.lineTo(-3,-5);
		shape.closePath();
		
		GeneralPath inside = new GeneralPath(0);
		inside.moveTo(-1.5,-4);
		inside.lineTo(1.5,-4);
		inside.lineTo(1.5,-2);
		inside.lineTo(-1.5,-2);
		inside.lineTo(-1.5,-4);
		inside.closePath();
		
		GeneralPath storage = new GeneralPath(0);
		shape.moveTo(-2,-1.2);
		shape.lineTo(2,-1.2);
		shape.lineTo(2,1);
		shape.lineTo(-2,1);
		shape.lineTo(-2,-1.2);
		shape.closePath();
		
		GeneralPath storageInside = new GeneralPath(0);
		inside.moveTo(-1.5,-0.5);
		inside.lineTo(-1.5,0.5);
		inside.lineTo(1.5,0.5);
		inside.lineTo(1.5,-0.5);
		inside.lineTo(-1.5,-0.5);
		inside.closePath();
		
		shape.append(inside, false);
		shape.append(storage, false);
		shape.append(storageInside, false);
		
		spatial = shapeFactory.createShape(shape);
		
		return spatial;
	}

	// Override the below to change the other style properties:
	public Color getColor(Object object) {
		return Color.GRAY;
	}
	
	public float getScale(Object object) {
		return 10;
	}
	
	public float getRotation(Object object) {
		return ((Machine) object).getRotation();
	}
}
