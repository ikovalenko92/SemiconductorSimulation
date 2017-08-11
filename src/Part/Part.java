package Part;

import java.awt.Color;
import java.awt.geom.GeneralPath;

import Sensors.RFIDTag;


public class Part {

	private GeneralPath shape;
	private float size;
	private Color color;
	private int WindingRule;
	private RFIDTag tag;
	
	/** Creates a new physicalComponent_Part
	 * Generic physicalComponent_Part = black rectangle of size 4x4 pixels
	 */
	public Part(RFIDTag tag){
		this.shape = new GeneralPath();
		shape.moveTo(-1.5,-1);
		shape.lineTo(1.5,-1);
		shape.lineTo(1.5,1);
		shape.lineTo(-1.5,1);
		shape.lineTo(-1.5,-1);
		
		this.WindingRule = 0;
		this.size = 4;
		this.color = Color.BLACK;
		
		shape.setWindingRule(0);
		
		this.tag = tag;
	}
		
	public RFIDTag getRFIDTag(){
		return this.tag;
	}
	
	public void setRFIDTag(RFIDTag tag){
		this.tag = tag;
	}
	
	public GeneralPath getShape() {
		return this.shape;
	}
	
	public void setShape(GeneralPath shape) {
		this.shape = shape;
	}
	
	public void changeShape(GeneralPath shape) {
		this.shape.append(shape, false);
	}

	public float getSize() {
		return this.size;
	}
	
	public void setSize(float newScale) {
		this.size = newScale;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getWindingRule() {
		return WindingRule;
	}

	public void setWindingRule(int windingRule) {
		WindingRule = windingRule;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Part [tag=" + tag.getID() + "]";
	}
}