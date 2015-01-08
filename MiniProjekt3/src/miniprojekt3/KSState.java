package miniprojekt3;

import java.util.HashSet;
import java.util.Set;

public class KSState{
	
	Set<AP> atomicPropositions = new HashSet<AP>();
	String name;
	boolean initial = false;

	public KSState(Set<AP> atomicPropositions, String name, boolean initial) {
		super();
		this.atomicPropositions = atomicPropositions;
		this.name = name;
		this.initial = initial;
	}

	public Set<AP> getAtomicPropositions() {
		return atomicPropositions;
	}

	public String getName() {
		return name;
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
	
	@Override
	public int hashCode() {
		return 1;
	}
	
	@Override
	public boolean equals(Object arg0) {
		return this.name.equals(((KSState)arg0).getName());
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public BAState toBAState(){
		return new BAState(this.name, false, true);
	}
	
	public static Set<BAState> toBAStates(Set<KSState> states){
		Set<BAState> ret = new HashSet<BAState>();
		for (KSState state : states) {
			ret.add(state.toBAState());
		}
		return ret;
	}
	
}
