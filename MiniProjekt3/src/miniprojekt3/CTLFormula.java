package miniprojekt3;

import java.util.HashSet;
import java.util.Set;


public class CTLFormula {
	
	String formula;
	Set<BAState> satisfiedStates = new HashSet<BAState>();
	
	public CTLFormula(String f){
		this.formula = f.toLowerCase();
	}
	
	public String getString(){
		return formula;
	}
	
	public void setFormula(String f){
		this.formula = f;
	}

	public Set<BAState> getStates(){
		return satisfiedStates;
	}
	
	public void setStates(Set<BAState> s){
		this.satisfiedStates = s;
	}
}
