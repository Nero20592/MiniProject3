package miniprojekt3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class CTLChecker {

	Set<State> initialStates = new HashSet<State>();
	Set<State> states = new HashSet<State>();
	Set<Transition> transitions = new HashSet<Transition>();

	// for the search of SCC
	Set<Transition> possibleTransitions = new HashSet<Transition>();
	HashMap<State, Integer> indexTable = new HashMap<>();
	HashMap<State, Integer> lowlinkTable = new HashMap<>();
	int index = 0;
	Stack<State> stack = new Stack<State>();

	public CTLChecker(LTS lts) {
		this.initialStates = lts.getInitialStates();
		this.states = lts.getStates();
		this.transitions = lts.getTransitions();
	}

	public boolean checkFormula(CTLFormula f) {
		f.setStates(states);

		Set<State> satisfiedStates = checkFormulaRek(f);

		if (satisfiedStates.containsAll(initialStates)) {
			return true;
		}

		return false;
	}

	private Set<State> checkFormulaRek(CTLFormula f) {

		Set<State> satisfiedStates = new HashSet<State>();

		String str = f.getString().trim();

		if (str.startsWith("not ") || str.startsWith("not(")) {
			str = str.substring(3);
			f.setFormula(str);

			satisfiedStates = checkNOT(f);

		} else if (str.startsWith("(")) {
			CTLFormula f1 = f;
			CTLFormula f2 = new CTLFormula(str);
			f2.setStates(f.getStates());

			int and = nextUnexcludedAnd(str);
			int or = nextUnexcludedOr(str);

			if (and < or) {
				f1.setFormula(str.substring(1, and));
				f2.setFormula(str.substring(and + 5, str.length() - 1));

				satisfiedStates = checkAND(f1, f2);

			} else if (or < and) {
				f1.setFormula(str.substring(1, or));
				f2.setFormula(str.substring(or + 4, str.length() - 1));

				satisfiedStates = checkOR(f1, f2);
			} else {
				f.setFormula(str.substring(1, str.length() - 2));
				satisfiedStates = checkFormulaRek(f);
			}
		} else if (str.startsWith("ex ") || str.startsWith("ex(")) {
			str = str.substring(2);
			f.setFormula(str);

			satisfiedStates = checkEX(f);

		} else if (str.startsWith("ax ") || str.startsWith("ax(")) {
			str = "NOT EX NOT " + str.substring(2);
			f.setFormula(str);

			satisfiedStates = checkFormulaRek(f);

		} else if (str.startsWith("eg ") || str.startsWith("eg(")) {
			str = str.substring(2);
			f.setFormula(str);

			satisfiedStates = checkEG(f);

		} else if (str.startsWith("ag ") || str.startsWith("ag(")) {
			str = "NOT EF NOT " + str.substring(2);
			f.setFormula(str);

			satisfiedStates = checkFormulaRek(f);

		} else if (str.startsWith("ef ") || str.startsWith("ef(")) {
			CTLFormula f1 = f;
			CTLFormula f2 = new CTLFormula(str);
			f2.setStates(f.getStates());

			f1.setFormula("true");
			f2.setFormula(str.substring(2));

			satisfiedStates = checkEU(f1, f2);

		} else if (str.startsWith("af ") || str.startsWith("af(")) {
			str = "NOT EG NOT " + str.substring(2);
			f.setFormula(str);

			satisfiedStates = checkFormulaRek(f);

		} else if (str.startsWith("e[")) {
			CTLFormula f1 = f;
			CTLFormula f2 = new CTLFormula(str);
			f2.setStates(f.getStates());

			int nextU = nextUnexcludedU(str);

			f1.setFormula(str.substring(2, nextU));
			f2.setFormula(str.substring(nextU + 3, str.length() - 1));

			satisfiedStates = checkEU(f1, f2);

		} else if (str.startsWith("true")) {
			satisfiedStates = states;

		} else if (str.startsWith("false")) {
			satisfiedStates.clear();

		} else {
			for (State s : states) {
				for (AP ap : s.getAPs()) {
					String label = ap.getLabel().toLowerCase();

					if (label.equals(str)) {
						satisfiedStates.add(s);
					}
				}
			}
		}

		return satisfiedStates;
	}

	private int nextUnexcludedAnd(String str) {
		if (!str.contains("and"))
			return str.length();

		int brackets = 0;

		for (int charIndex = 1; charIndex + 5 < str.length(); charIndex++) {
			char c = str.charAt(charIndex);

			if (c == '(') {
				brackets++;
			} else if (c == ')') {
				brackets--;
			} else if (brackets == 0
					&& str.substring(charIndex, charIndex + 5).equals(" and ")) {
				return charIndex;
			}
		}

		return str.length();
	}

	private int nextUnexcludedOr(String str) {
		if (!str.contains("or"))
			return str.length();

		int brackets = 0;

		for (int charIndex = 1; charIndex + 4 < str.length(); charIndex++) {
			char c = str.charAt(charIndex);

			if (c == '(') {
				brackets++;
			} else if (c == ')') {
				brackets--;
			} else if (brackets == 0
					&& str.substring(charIndex, charIndex + 4).equals(" or ")) {
				return charIndex;
			}
		}

		return str.length();
	}

	private int nextUnexcludedU(String str) {
		int brackets = 0;

		for (int charIndex = 2; charIndex + 3 < str.length(); charIndex++) {
			char c = str.charAt(charIndex);

			if (c == '[') {
				brackets++;
			} else if (c == ']') {
				brackets--;
			} else if (brackets == 0
					&& str.substring(charIndex, charIndex + 3).equals(" u ")) {
				return charIndex;
			}
		}

		return str.length();
	}

	private Set<State> checkNOT(CTLFormula f) {

		Set<State> formulaStates = checkFormulaRek(f);

		// initilisierung mit allen möglichen states
		Set<State> satisfiedStates = states;

		for (State s : formulaStates) {
			satisfiedStates.remove(s);
		}

		return satisfiedStates;
	}

	private Set<State> checkAND(CTLFormula f1, CTLFormula f2) {

		Set<State> formulaStates1 = checkFormulaRek(f1);
		Set<State> formulaStates2 = checkFormulaRek(f2);

		Set<State> satisfiedStates = new HashSet<State>();

		for (State s : formulaStates1) {
			if (formulaStates2.contains(s)) {
				satisfiedStates.add(s);
			}
		}

		return satisfiedStates;
	}

	private Set<State> checkOR(CTLFormula f1, CTLFormula f2) {

		Set<State> formulaStates1 = checkFormulaRek(f1);
		Set<State> formulaStates2 = checkFormulaRek(f2);

		Set<State> satisfiedStates = formulaStates1;
		satisfiedStates.addAll(formulaStates2);

		return satisfiedStates;
	}

	private Set<State> checkEX(CTLFormula f) {

		/*
		 * EX φ (next)
		 * 
		 * 1. find all states that satisfy φ 2. mark all predecessors to
		 * satisfy EX φ
		 */

		Set<State> formulaStates = checkFormulaRek(f);

		Set<State> satisfiedStates = new HashSet<State>();

		for (Transition t : transitions) {
			if (formulaStates.contains(t.getEnd())) {
				satisfiedStates.add(t.getBegin());
			}
		}

		return satisfiedStates;
	}

	private Set<State> checkEG(CTLFormula f) {

		/*
		 * EG φ (always)
		 * 
		 * 1. Be S' ⊆ S all states in which φ holds 2. calculate all
		 * maximally strongly connected components (SCC) of S' that have at
		 * least one transition 3. Mark all states in S' from which such an SCC
		 * is reachable to satisfy EG φ
		 */

		Set<State> formulaStates = checkFormulaRek(f);

		Set<State> SCC = getSCC(formulaStates);

		Set<State> satisfiedStates = SCC;

		Queue<State> queue = new LinkedList<State>();
		queue.addAll(SCC);

		while (!queue.isEmpty()) {
			State currentState = queue.remove();

			for (Transition tra : transitions) {
				State nextState = tra.getBegin();

				if (currentState.equals(tra.getEnd())
						&& !satisfiedStates.contains(nextState)
						&& formulaStates.contains(nextState)) {

					queue.add(nextState);
					satisfiedStates.add(nextState);
				}
			}
		}

		return satisfiedStates;
	}

	private Set<State> checkEU(CTLFormula f1, CTLFormula f2) {

		/*
		 * E[φ U ψ] (until)
		 * 
		 * 1. find all states that satisfy ψ 2. from these states, navigate all
		 * transitions backwards as long as φ holds 3. Mark these states to
		 * satisfy EU[φ U ψ]
		 */

		Set<State> formulaStates1 = checkFormulaRek(f1);
		Set<State> formulaStates2 = checkFormulaRek(f2);

		Set<State> satisfiedStates = new HashSet<State>();

		Queue<State> queue = new LinkedList<State>();
		queue.addAll(formulaStates2);

		while (!queue.isEmpty()) {
			State currentState = queue.remove();
			satisfiedStates.add(currentState);

			for (Transition tra : transitions) {
				State nextState = tra.getBegin();

				if (currentState.equals(tra.getEnd())
						&& !satisfiedStates.contains(nextState)
						&& formulaStates1.contains(nextState)) {

					queue.add(nextState);
				}
			}
		}

		return satisfiedStates;
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
