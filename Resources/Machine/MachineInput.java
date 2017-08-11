package Machine;

import java.awt.geom.GeneralPath;


public class MachineInput {
	private GeneralPath shape;

	//Create a GeneralPath input
	public MachineInput(GeneralPath shape){
		this.shape = shape;
	}
	
	public GeneralPath getShape() {
		return this.shape;
	}
}