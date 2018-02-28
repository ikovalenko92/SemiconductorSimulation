package initializingAgents;

import java.util.ArrayList;
import java.util.HashSet;
import sharedInformation.PhysicalProperty;

public class ProductionPlan {

	private ArrayList<HashSet<PhysicalProperty>> setList;
	
	public ProductionPlan() {
		
		this.setList = new ArrayList<HashSet<PhysicalProperty>>();
		HashSet<PhysicalProperty> initialSet = new HashSet<PhysicalProperty>();
		this.setList.add(initialSet);
	}

	@Override
	public String toString() {
		String output = "";
		for (HashSet<PhysicalProperty> set:this.setList){
			output = output + "{";
			String[] outputString = (String[]) set.toArray(new String[set.size()]);
			for (String s:outputString){
				output = output + s + ",";
			}
			output = output + "},";
		}
		
		return output;
	}


	public void add(PhysicalProperty property){
		this.setList.get(this.setList.size()-1).add(property);
	}
	
	public void addNewSet(PhysicalProperty property){
		HashSet<PhysicalProperty> newSet = new HashSet<PhysicalProperty>();
		newSet.add(property);
		this.setList.add(newSet);
	}
	
	public void addNewSet(HashSet<PhysicalProperty> propertySet){
		this.setList.add(propertySet);
	}
	
	public HashSet<PhysicalProperty> getLastSet(){
		return this.setList.get(this.setList.size()-1);
	}
}
