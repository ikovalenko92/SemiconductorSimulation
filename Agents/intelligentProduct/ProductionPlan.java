package intelligentProduct;

import java.util.ArrayList;
import java.util.Set;

import sharedInformation.PhysicalProperty;

public class ProductionPlan {

	private ProductAgent productAgent;
	private ArrayList<Set<PhysicalProperty>> setList;
	
	public ProductionPlan(ProductAgent productAgent) {
		this.productAgent = productAgent;
		
		this.setList = new ArrayList<Set<PhysicalProperty>>();
	}

	public void add(PhysicalProperty property){
		this.setList.get(this.setList.size()-1).add(property);
	}
	
	
	public void addNewStep(PhysicalProperty ){
		this.setList.add(PhysicalProperty, arg1);
	}
	
	public void addToStep(int step){
		
	}	
}
