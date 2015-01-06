package miniprojekt3;

import java.util.HashSet;
import java.util.Set;



public class State {
	
	String name;
	Set<AP> atomicPropositions = new HashSet<AP>();
	

	public State(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void addAP(AP ap){
		atomicPropositions.add(ap);
	}
	
	public boolean contains(AP ap){
		return atomicPropositions.contains(ap);
	}
	
	public Set<AP> getAPs(){
		return atomicPropositions;
	}
	
	
	
	public void setAtomicPropositions(Set<AP> atomicPropositions) {
		this.atomicPropositions = atomicPropositions;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		return this.name.equals(((State)arg0).getName());
	}
	
	@Override
	public String toString() {
		return this.name + " APs: " + atomicPropositions.toString();
	}

}
