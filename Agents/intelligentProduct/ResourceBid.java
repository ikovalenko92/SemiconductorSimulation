/*package intelligentProduct;

import java.util.ArrayList;

import resourceAgent.ResourceAgent;
import sharedInformation.CapabilitiesEdge;

public class ResourceBid {
	private ArrayList<ResourceAgent> resourceList;
	private int bidTime;
	private ArrayList<CapabilitiesEdge> edgeList;

	public ResourceBid(ArrayList<ResourceAgent> resourceList, int bidTime, ArrayList<CapabilitiesEdge> edgeList){
		this.resourceList = resourceList;
		this.bidTime = bidTime;
		this.edgeList = edgeList;
	}

	*//**
	 * @return the resourceAgent
	 *//*
	public ArrayList<ResourceAgent> getResourceList() {
		return resourceList;
	}

	*//**
	 * @return the bidTime
	 *//*
	public int getBidTime() {
		return bidTime;
	}
	
	public ArrayList<CapabilitiesEdge> getEdgeList(){
		return this.edgeList;
	}

	 (non-Javadoc)
	 * @see java.lang.Object#toString()
	 
	@Override
	public String toString() {
		String printString = "";
		for (ResourceAgent resource : resourceList){
			printString = printString + resource.toString() + " ";
		}
		return "" + resourceList + " Bid:" + bidTime;
	}	
}
*/