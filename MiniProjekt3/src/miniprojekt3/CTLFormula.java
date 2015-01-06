package miniprojekt3;

import java.util.HashSet;
import java.util.Set;


public class CTLFormula {
	
	String formula;
	Set<State> satisfiedStates = new HashSet<State>();
	
	public CTLFormula(String f){
		this.formula = f.toLowerCase();
	}
	
	public String getString(){
		return formula;
	}
	
	public void setFormula(String f){
		this.formula = f;
	}

	public Set<State> getStates(){
		return satisfiedStates;
	}
	
	public void setStates(Set<State> s){
		this.satisfiedStates = s;
	}
}
