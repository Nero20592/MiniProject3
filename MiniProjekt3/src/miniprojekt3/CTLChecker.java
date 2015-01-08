package miniprojekt3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class CTLChecker {

	Set<BAState> initialStates = new HashSet<BAState>();
	Set<BAState> bAStates = new HashSet<BAState>();
	Set<KSTransition> kSTransitions = new HashSet<KSTransition>();

	// for the search of SCC
	Set<KSTransition> possibleTransitions = new HashSet<KSTransition>();
	HashMap<BAState, Integer> indexTable = new HashMap<>();
	HashMap<BAState, Integer> lowlinkTable = new HashMap<>();
	int index = 0;
	Stack<BAState> stack = new Stack<BAState>();

	public CTLChecker(KS ks) {
		this.initialStates = ks.getInitialStates();
		this.bAStates = ks.getStates();
		this.kSTransitions = ks.getTransitions();
	}

	public boolean checkFormula(CTLFormula f) {
		f.setStates(bAStates);

		Set<BAState> satisfiedStates = checkFormulaRek(f);

		if (satisfiedStates.containsAll(initialStates)) {
			return true;
		}

		return false;
	}

	private Set<BAState> checkFormulaRek(CTLFormula f) {

		Set<BAState> satisfiedStates = new HashSet<BAState>();

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
			satisfiedStates = bAStates;

		} else if (str.startsWith("false")) {
			satisfiedStates.clear();

		} else {
			for (BAState s : bAStates) {
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

	private Set<BAState> checkNOT(CTLFormula f) {

		Set<BAState> formulaStates = checkFormulaRek(f);

		// initilisierung mit allen möglichen states
		Set<BAState> satisfiedStates = bAStates;

		for (BAState s : formulaStates) {
			satisfiedStates.remove(s);
		}

		return satisfiedStates;
	}

	private Set<BAState> checkAND(CTLFormula f1, CTLFormula f2) {

		Set<BAState> formulaStates1 = checkFormulaRek(f1);
		Set<BAState> formulaStates2 = checkFormulaRek(f2);

		Set<BAState> satisfiedStates = new HashSet<BAState>();

		for (BAState s : formulaStates1) {
			if (formulaStates2.contains(s)) {
				satisfiedStates.add(s);
			}
		}

		return satisfiedStates;
	}

	private Set<BAState> checkOR(CTLFormula f1, CTLFormula f2) {

		Set<BAState> formulaStates1 = checkFormulaRek(f1);
		Set<BAState> formulaStates2 = checkFormulaRek(f2);

		Set<BAState> satisfiedStates = formulaStates1;
		satisfiedStates.addAll(formulaStates2);

		return satisfiedStates;
	}

	private Set<BAState> checkEX(CTLFormula f) {

		/*
		 * EX φ (next)
		 * 
		 * 1. find all states that satisfy φ 2. mark all predecessors to
		 * satisfy EX φ
		 */

		Set<BAState> formulaStates = checkFormulaRek(f);

		Set<BAState> satisfiedStates = new HashSet<BAState>();

		for (KSTransition t : kSTransitions) {
			if (formulaStates.contains(t.getEnd())) {
				satisfiedStates.add(t.getBegin());
			}
		}

		return satisfiedStates;
	}

	private Set<BAState> checkEG(CTLFormula f) {

		/*
		 * EG φ (always)
		 * 
		 * 1. Be S' ⊆ S all states in which φ holds 2. calculate all
		 * maximally strongly connected components (SCC) of S' that have at
		 * least one transition 3. Mark all states in S' from which such an SCC
		 * is reachable to satisfy EG φ
		 */

		Set<BAState> formulaStates = checkFormulaRek(f);

		Set<BAState> SCC = getSCC(formulaStates);

		Set<BAState> satisfiedStates = SCC;

		Queue<BAState> queue = new LinkedList<BAState>();
		queue.addAll(SCC);

		while (!queue.isEmpty()) {
			BAState currentState = queue.remove();

			for (KSTransition tra : kSTransitions) {
				BAState nextState = tra.getBegin();

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

	private Set<BAState> checkEU(CTLFormula f1, CTLFormula f2) {

		/*
		 * E[φ U ψ] (until)
		 * 
		 * 1. find all states that satisfy ψ 2. from these states, navigate all
		 * transitions backwards as long as φ holds 3. Mark these states to
		 * satisfy EU[φ U ψ]
		 */

		Set<BAState> formulaStates1 = checkFormulaRek(f1);
		Set<BAState> formulaStates2 = checkFormulaRek(f2);

		Set<BAState> satisfiedStates = new HashSet<BAState>();

		Queue<BAState> queue = new LinkedList<BAState>();
		queue.addAll(formulaStates2);

		while (!queue.isEmpty()) {
			BAState currentState = queue.remove();
			satisfiedStates.add(currentState);

			for (KSTransition tra : kSTransitions) {
				BAState nextState = tra.getBegin();

				if (currentState.equals(tra.getEnd())
						&& !satisfiedStates.contains(nextState)
						&& formulaStates1.contains(nextState)) {

					queue.add(nextState);
				}
			}
		}

		return satisfiedStates;
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
