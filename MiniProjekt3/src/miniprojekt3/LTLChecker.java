package miniprojekt3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;




public class LTLChecker {
	
	Set<State> initialStates = new HashSet<State>();
	Set<State> states = new HashSet<State>();
	Set<Transition> transitions = new HashSet<Transition>();
	
	// for the search of SCC
		Set<Transition> possibleTransitions = new HashSet<Transition>();
		HashMap<State, Integer> indexTable = new HashMap<>();
		HashMap<State, Integer> lowlinkTable = new HashMap<>();
		int index = 0;
		Stack<State> stack = new Stack<State>();
	
	public LTLChecker(LTS lts){
		this.initialStates = lts.getInitialStates();
		this.states = lts.getStates();
		this.transitions = lts.getTransitions();
		
	}
	private Set<State> getSCC(Set<State> possibleStates) {

		Set<State> SCC = new HashSet<State>();
		possibleTransitions = new HashSet<Transition>();

		for (Transition t : transitions) {
			if (possibleStates.contains(t.getBegin())
					&& possibleStates.contains(t.getEnd())) {
				possibleTransitions.add(t);
			}
		}

		indexTable = new HashMap<>();
		lowlinkTable = new HashMap<>();
		index = 0;

		stack = new Stack<State>();

		for (State s : possibleStates) {
			if (!indexTable.containsKey(s)) {
				Set<State> temp = strongConnect(s);

				SCC.addAll(temp);
			}
		}

		return SCC;
	}

	private Set<State> strongConnect(State s) {
		Set<State> SCC = new HashSet<State>();

		indexTable.put(s, index);
		lowlinkTable.put(s, index);
		index++;
		stack.push(s);

		for (Transition t : possibleTransitions) {
			if (s.equals(t.getBegin())) {
				State nextState = t.getEnd();

				if (!indexTable.containsKey(nextState)) {
					Set<State> temp = strongConnect(nextState);

					SCC.addAll(temp);

					int i = lowlinkTable.get(s);
					int j = lowlinkTable.get(nextState);

					lowlinkTable.put(s, Math.min(i, j));
				} else if (stack.contains(nextState)) {
					int i = lowlinkTable.get(s);
					int j = indexTable.get(nextState);

					lowlinkTable.put(s, Math.min(i, j));
				}
			}
		}

		if (lowlinkTable.get(s) == indexTable.get(s)) {
			State SccState;
			Set<State> temp = new HashSet<State>();

			do {
				SccState = stack.pop();
				temp.add(SccState);
			} while (!s.equals(SccState));

			boolean hasTransition = false;

			for (Transition t : possibleTransitions) {
				if (temp.contains(t.getBegin()) && temp.contains(t.getEnd())) {
					hasTransition = true;
					break;
				}
			}

			if (hasTransition) {
				SCC.addAll(temp);
			}

			return SCC;
		}

		return new HashSet<State>();
	}
}
