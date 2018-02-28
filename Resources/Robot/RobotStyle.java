package Robot;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;

public class RobotStyle extends DefaultStyleOGL2D {

	@Override
	public VSpatial getVSpatial(Object object, VSpatial spatial) {
		
		Robot robot = ((Robot) object);
		
		//Exit Human
		if(robot.toString().contains("uman")){
			GeneralPath shape = new GeneralPath(0);
			shape.moveTo(0,0);
			shape.lineTo(-1,1);
			shape.lineTo(0,2);
			shape.lineTo(1,1);
			shape.lineTo(0,0);
			shape.closePath();
			
			GeneralPath body = new GeneralPath();
			body.moveTo(0,0);
			body.lineTo(0,-2);
			shape.append(body,false);
			
			GeneralPath leg1 = new GeneralPath();
			leg1.moveTo(0,-2);
			leg1.lineTo(-1,-3);
			shape.append(leg1,false);
			
			GeneralPath leg2 = new GeneralPath();
			leg2.moveTo(0,-2);
			leg2.lineTo(1,-3);
			shape.append(leg2,false);
			
			GeneralPath arms = new GeneralPath();
			arms.moveTo(-1.5,0);
			arms.lineTo(1.5,0);
			shape.append(arms,false);
			
			spatial = shapeFactory.createShape(shape);
		}
		//Regular Robot
		else{
			Point endpoint = robot.getEnd();
			spatial = shapeFactory.createShape(new Ellipse2D.Double(endpoint.x-1.5,endpoint.y-1.5,3,3));
		}
		
		return spatial;
	}

	// Override the below to change the other style properties:
	public Color getColor(Object object) {
		if(((Robot) object).toString().contains("uman")){
			return Color.BLACK;
		}
		
		return Color.ORANGE;
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
