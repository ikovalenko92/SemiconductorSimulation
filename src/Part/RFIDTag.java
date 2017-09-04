package Part;

import java.util.ArrayList;

/**
 * @author ikoval
 * Build the RFID tag by creating setters/getters for various information
 */
public class RFIDTag {

	private char Type = 'Z';
	
	public RFIDTag(char Type){
		this.Type = Type;
	}
	
	public int getID(){
		return hashCode();
	}
	
	public char getType(){
		return this.Type;
	}
	
	public void setType(char Type){
		this.Type = Type;
	}

	@Override
	public String toString() {
		return "Type = [" + Type + "]";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Type;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RFIDTag)) {
			return false;
		}
		RFIDTag other = (RFIDTag) obj;
		if (Type != other.Type) {
			return false;
		}
		return true;
	}
	
}