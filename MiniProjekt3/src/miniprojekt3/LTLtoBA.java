package miniprojekt3;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import rwth.i2.ltl2ba4j.model.IState;
import rwth.i2.ltl2ba4j.model.ITransition;

public class LTLtoBA {

	
	 Set<BAState> initialStates = new HashSet<BAState>();
	 Set<BAState> bAStates = new HashSet<BAState>();
	 Set<BAState> acceptingStates = new HashSet<BAState>();
	 Set<BATransition> transitions = new HashSet<BATransition>();
	 Set<Action> alphabet = new HashSet<Action>();
	
	 String[] tempInitialStates;
	 String[] tempBAStates;
	 String[] tempAcceptingStates;
	 String[] tempTransitions;
	 String[] tempAlphabet;

	public LTLtoBA(){
		
	}
	public BA transformLTLtoBA(String value){
		String temp = value.substring(24, value.length()-2); 
		tempInitialStates = temp.split("shape=box");
		tempBAStates = value.split("s");
		tempAcceptingStates = value.split("style=dotted");
		tempTransitions = value.split("label=");
		for(String s : tempInitialStates){
			System.out.println("INITIAL: " + s);
		}
		for(String a : tempBAStates){
			System.out.println("STATES: " + a);
		}
		for(String b : tempAcceptingStates){
			System.out.println("ACCEPT: " + b);
		}
		for(String c : tempTransitions){
			System.out.println("TRANS: " + c);
		}
		
		
		//return new BA(BAstates, initialStates, acceptingStates, transitions, alphabet);
		return null;
		
		
	}
	
	public BA transformLTLtoBA(Collection<ITransition> automaton){
		   Set<IState> SourceState = new HashSet<IState>();
		   Set<IState> TargetState = new HashSet<IState>();
	        for(ITransition t: automaton) {
	            IState ISourceState = t.getSourceState();
	            if(!SourceState.contains(ISourceState)) {
	                SourceState.add(ISourceState);
	            }
	            IState ITargetState = t.getTargetState();
	            if(!TargetState.contains(ITargetState)) {
	                TargetState.add(ISourceState);
	            }
	        }
	            
	            for(IState state: SourceState) {
	              //   boolean isInitial = false, isFinal = false;
	                if(state.isInitial()) {
	                    initialStates.add((BAState) state);
	                }
	                if(state.isFinal()) {
	                    acceptingStates.add((BAState) state);
	                }
	            }
	            
//	            for(ITransition transition: automaton) {
//	                if(SourceState.equals(transition)){
//	                	transitions.add((BATransition) transition);
//	                	alphabet.add((Action) transition.getLabels());
//	                }
//	                
//	            }
	            
		return new BA(bAStates, initialStates, acceptingStates, transitions, alphabet);
		
	}
	
	
	
	
	
}
