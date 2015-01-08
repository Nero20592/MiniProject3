package miniprojekt3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;




public class LTLChecker {
	
	Set<BAState> initialStates = new HashSet<BAState>();
	Set<BAState> bAStates = new HashSet<BAState>();
	Set<KSTransition> kSTransitions = new HashSet<KSTransition>();
	
	// for the search of SCC
		Set<KSTransition> possibleTransitions = new HashSet<KSTransition>();
		HashMap<BAState, Integer> indexTable = new HashMap<>();
		HashMap<BAState, Integer> lowlinkTable = new HashMap<>();
		int index = 0;
		Stack<BAState> stack = new Stack<BAState>();
	
	public LTLChecker(KS ks){
		this.initialStates = ks.getInitialStates();
		this.bAStates = ks.getStates();
		this.kSTransitions = ks.getTransitions();
		
	}
	private Set<BAState> getSCC(Set<BAState> possibleStates) {

		Set<BAState> SCC = new HashSet<BAState>();
		possibleTransitions = new HashSet<KSTransition>();

		for (KSTransition t : kSTransitions) {
			if (possibleStates.contains(t.getBegin())
					&& possibleStates.contains(t.getEnd())) {
				possibleTransitions.add(t);
			}
		}

		indexTable = new HashMap<>();
		lowlinkTable = new HashMap<>();
		index = 0;

		stack = new Stack<BAState>();

		for (BAState s : possibleStates) {
			if (!indexTable.containsKey(s)) {
				Set<BAState> temp = strongConnect(s);

				SCC.addAll(temp);
			}
		}

		return SCC;
	}

	private Set<BAState> strongConnect(BAState s) {
		Set<BAState> SCC = new HashSet<BAState>();

		indexTable.put(s, index);
		lowlinkTable.put(s, index);
		index++;
		stack.push(s);

		for (KSTransition t : possibleTransitions) {
			if (s.equals(t.getBegin())) {
				BAState nextState = t.getEnd();

				if (!indexTable.containsKey(nextState)) {
					Set<BAState> temp = strongConnect(nextState);

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
			BAState SccState;
			Set<BAState> temp = new HashSet<BAState>();

			do {
				SccState = stack.pop();
				temp.add(SccState);
			} while (!s.equals(SccState));

			boolean hasTransition = false;

			for (KSTransition t : possibleTransitions) {
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

		return new HashSet<BAState>();
	}
}
