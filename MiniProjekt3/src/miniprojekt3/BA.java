package miniprojekt3;

import java.util.HashSet;
import java.util.Set;

public class BA {
	
	static BA ba;
	// Kripke-Structure 
		static Set<State> initialStatesLTS = new HashSet<State>();
		static Set<State> statesLTS = new HashSet<State>();
		static Set<Transition> transitionsLTS = new HashSet<Transition>();
		static Set<AP> atomicPrepositionsLTS = new HashSet<AP>();
		
		//Büchi-Automata
		static Set<State> initialStatesBA = new HashSet<State>();
		static Set<State> statesBA = new HashSet<State>();
		static Set<State> acceptingStatesBA = new HashSet<State>();
		static Set<Transition> transitionsBA = new HashSet<Transition>();
		static Set<AP> atomicPrepositionsBA = new HashSet<AP>();
		static Set<AP> alphabetBA = new HashSet<AP>();
		

	public Set<State> getStatesBA() {
		return statesBA;
	}

	public Set<Transition> getTransitionsBA() {
		return transitionsBA;
	}

	public Set<State> getInitialStatesBA() {
		return initialStatesBA;
	}

	public Set<AP> getAlphabetBA() {
		return alphabetBA;
	}

	public Set<State> getAcceptingStatesBA() {
		return acceptingStatesBA;
	}

	public BA(Set<State> states, Set<AP> alphabet, Set<Transition> transitions,
			Set<State> initialStates, Set<State> as) {
		BA.statesBA = states;
		BA.transitionsBA = transitions;
		BA.initialStatesBA = initialStates;
		BA.alphabetBA = alphabet;
		BA.acceptingStatesBA = as;
	}
	
	public BA() {
		// TODO Auto-generated constructor stub
	}

	public static BA transformToBA(LTS lts){
		State a = new State("A");
		initialStatesBA.add(a);
		initialStatesLTS = lts.getInitialStates();
		statesBA = lts.getStates();
		alphabetBA = lts.getAtomicPropositions();
		acceptingStatesBA = lts.getStates();
		transitionsBA = lts.getTransitions();
		
		//for each state in initstate füge Transition von a nach initialStateLTS hinzu
		for(State s : initialStatesLTS){
		Transition t = new Transition(a, s);
		transitionsBA.add(t);

		}
		
		return ba = new BA(statesBA, alphabetBA, transitionsBA, initialStatesBA, acceptingStatesBA);
		}
}
