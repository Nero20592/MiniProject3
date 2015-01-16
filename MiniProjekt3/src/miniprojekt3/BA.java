package miniprojekt3;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import att.grappa.Attribute;
import att.grappa.Edge;
import att.grappa.Graph;
import att.grappa.GrappaSupport;
import att.grappa.Node;

public class BA {

	Set<BAState> bAStates = new HashSet<BAState>();
	Set<BAState> initialStates = new HashSet<BAState>();
	Set<BAState> acceptingStates = new HashSet<BAState>();
	Set<BATransition> transitions = new HashSet<BATransition>();
	Set<Action> alphabet = new HashSet<Action>();

	// for the search of SCC
	Set<BATransition> possibleTransitions = new HashSet<BATransition>();
	HashMap<BAState, Integer> indexTable = new HashMap<>();
	HashMap<BAState, Integer> lowlinkTable = new HashMap<>();
	int index = 0;
	Stack<BAState> stack = new Stack<BAState>();

	public BA(Set<BAState> bAStates, Set<BAState> initialStates, Set<BAState> acceptingStates, Set<BATransition> transitions, Set<Action> alphabet) {
		super();
		this.bAStates = bAStates;
		this.initialStates = initialStates;
		this.acceptingStates = acceptingStates;
		this.transitions = transitions;
		this.alphabet = alphabet;
	}

	public Set<BAState> getStates() {
		return bAStates;
	}

	public Set<BAState> getInitialStates() {
		return initialStates;
	}

	public Set<BAState> getAcceptingStates() {
		return acceptingStates;
	}

	public Set<BATransition> getTransitions() {
		return transitions;
	}

	public Set<Action> getAlphabet() {
		return alphabet;
	}

	public BA constructProduct(BA other) {

		Set<Action> newAlphabet = new HashSet<Action>();
		newAlphabet.addAll(this.getAlphabet());

		List<Set<BAState>> cartesianProducts = cartesianProducts(this.getStates(), other.getStates());
		Set<BAState> newStates = new HashSet<BAState>();
		newStates.addAll(cartesianProducts.get(0));
		Set<BAState> newInitialStates = new HashSet<BAState>();
		newInitialStates.addAll(cartesianProducts.get(1));
		Set<BAState> newAcceptingStates = new HashSet<BAState>();
		newAcceptingStates.addAll(cartesianProducts.get(2));

		Set<BATransition> newTransitions = new HashSet<BATransition>();

		for (BATransition t1 : this.getTransitions()) {
			for (BATransition t2 : other.getTransitions()) {
				for (Action a1 : t1.getAction()) {
					for (Action a2 : t2.getAction()) {
						if (a1.equals(a2)) {
							for (int i = 1; i < 3; i++) {
								BAState source = null;
								BAState target = null;
								if (i == 1) {
									source = getBAState(newStates, t1.getSourceState().getLabel() + "," + t2.getSourceState().getLabel() + ",1");
									if (!t1.getSourceState().isFinal()) {
										target = getBAState(newStates, t1.getTargetState().getLabel() + "," + t2.getTargetState().getLabel() + ",1");
									} else {
										target = getBAState(newStates, t1.getTargetState().getLabel() + "," + t2.getTargetState().getLabel() + ",2");
									}
								} else if(i == 2){
									source = getBAState(newStates, t1.getSourceState().getLabel() + "," + t2.getSourceState().getLabel() + ",2");
									if (!t2.getSourceState().isFinal()) {
										target = getBAState(newStates, t1.getTargetState().getLabel() + "," + t2.getTargetState().getLabel() + ",2");
									} else {
										target = getBAState(newStates, t1.getTargetState().getLabel() + "," + t2.getTargetState().getLabel() + ",1");
									}
								}
								Set<Action> actions = new HashSet<Action>();
								actions.add(a1);
								BATransition transition = new BATransition(source, target, actions);
								newTransitions.add(transition);
							}
						}
					}
				}
			}
		}

		BA ret = new BA(newStates, newInitialStates, newAcceptingStates, newTransitions, newAlphabet);
		return ret.removeUnreachableKSStates();
	}

	public BA removeUnreachableKSStates() {
		Set<BAState> initialStates = this.getInitialStates();
		Set<BATransition> transitions = this.getTransitions();

		Set<BAState> reachableStates = new HashSet<BAState>();
		Set<BAState> accepting = new HashSet<BAState>();

		Queue<BAState> queue = new LinkedList<BAState>();
		queue.addAll(initialStates);
		reachableStates.addAll(initialStates);
		for (BAState baState : initialStates) {
			if (baState.isFinal()) {
				accepting.add(baState);
			}
		}

		while (!queue.isEmpty()) {
			BAState currentState = queue.remove();

			for (BATransition t : transitions) {
				BAState nextState = t.getTargetState();

				if (currentState.equals(t.getSourceState()) && !reachableStates.contains(nextState)) {

					queue.add(nextState);
					reachableStates.add(nextState);
					if (nextState.isFinal()) {
						accepting.add(nextState);
					}
				}
			}
		}

		for (Iterator<BATransition> iterator2 = transitions.iterator(); iterator2.hasNext();) {
			BATransition transition = (BATransition) iterator2.next();
			if (!reachableStates.contains(transition.getSourceState()) || !reachableStates.contains(transition.getTargetState())) {
				iterator2.remove();
			}
		}

		return new BA(reachableStates, initialStates, accepting, transitions, this.alphabet);
	}

	public boolean isAcceptedLanguageEmpty() {

		Set<HashSet<BAState>> SCC = new HashSet<HashSet<BAState>>();
		possibleTransitions = new HashSet<BATransition>();

		for (BATransition t : transitions) {
			if (bAStates.contains(t.getSourceState()) && bAStates.contains(t.getTargetState())) {
				possibleTransitions.add(t);
			}
		}

		indexTable = new HashMap<>();
		lowlinkTable = new HashMap<>();
		index = 0;

		stack = new Stack<BAState>();

		for (BAState s : bAStates) {
			if (!indexTable.containsKey(s)) {
				HashSet<BAState> temp = strongConnect(s);

				SCC.add(temp);
			}
		}

		boolean containsAccepting = false;

		for (HashSet<BAState> set : SCC) {
			for (BAState s : acceptingStates) {
				if (set.contains(s)) {
					containsAccepting = true;
				}
			}

			if (containsAccepting) {
				// Breitensuche nach initial

				Set<BAState> reachableStates = new HashSet<BAState>();

				Queue<BAState> queue = new LinkedList<BAState>();
				queue.addAll(set);
				reachableStates.addAll(set);

				while (!queue.isEmpty()) {
					BAState currentState = queue.remove();

					for (BATransition t : transitions) {
						BAState nextState = t.getSourceState();

						if (currentState.equals(t.getTargetState()) && !reachableStates.contains(nextState)) {

							if (initialStates.contains(nextState)) {
								return false;
							}

							queue.add(nextState);
							reachableStates.add(nextState);
						}
					}
				}
			}

			containsAccepting = false;
		}

		return true;
	}

	private HashSet<BAState> strongConnect(BAState s) {
		HashSet<BAState> SCC = new HashSet<BAState>();

		indexTable.put(s, index);
		lowlinkTable.put(s, index);
		index++;
		stack.push(s);

		for (BATransition t : possibleTransitions) {
			if (s.equals(t.getSourceState())) {
				BAState nextState = t.getTargetState();

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

			for (BATransition t : possibleTransitions) {
				if (temp.contains(t.getSourceState()) && temp.contains(t.getTargetState())) {
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

	public static BAState getBAState(Set<BAState> states, String name) {
		for (BAState state : states) {
			if (state.getLabel().equals(name)) {
				return state;
			}
		}
		return null;
	}

	public static List<Set<BAState>> cartesianProducts(Set<BAState> aSet, Set<BAState> bSet) {
		Set<BAState> states = new HashSet<BAState>();
		Set<BAState> initialStates = new HashSet<BAState>();
		Set<BAState> acceptingStates = new HashSet<BAState>();
		List<Set<BAState>> ret = new ArrayList<Set<BAState>>();
		for (BAState a : aSet) {
			for (BAState b : bSet) {
				for (int i = 1; i < 3; i++) {
					BAState current = null;
					if(i == 1){
						if(a.isInitial() && b.isInitial()){
							current = new BAState(a.getLabel() + "," + b.getLabel() + "," + i, true, false);
							initialStates.add(current);
						} else {
							current = new BAState(a.getLabel() + "," + b.getLabel() + "," + i, false, false);
						}
					} else if (i == 2){
						if(b.isFinal()){
							current = new BAState(a.getLabel() + "," + b.getLabel() + "," + i, false, true);
							acceptingStates.add(current);
						} else {
							current = new BAState(a.getLabel() + "," + b.getLabel() + "," + i, false, false);
						}
					}
					states.add(current);
					
				}
			}
		}
		ret.add(states);
		ret.add(initialStates);
		ret.add(acceptingStates);

		return ret;
	}

	public Graph createGraph(String path) {
		Graph graph = new Graph("BA");

		for (BAState s : this.getStates()) {
			Node n = new Node(graph, s.getLabel());
			graph.addNode(n);

		}

		for (BATransition t : this.getTransitions()) {
			Edge edge = new Edge(graph, graph.findNodeByName(t.getSourceState().getLabel()), graph.findNodeByName(t.getTargetState().getLabel()));
			edge.setAttribute(Attribute.LABEL_ATTR, "" + t.getAction());
			graph.addEdge(edge);
		}

		for (BAState s : this.getInitialStates()) {
			Node n = graph.findNodeByName(s.getLabel());
			Node invis = new Node(graph);
			Edge start = new Edge(graph, invis, n);
			graph.addEdge(start);
			invis.setAttribute(Attribute.STYLE_ATTR, "invis");
			n.setAttribute(Attribute.COLOR_ATTR, Color.RED);
			n.setAttribute(Attribute.FILLCOLOR_ATTR, Color.RED);
			n.setAttribute(Attribute.FONTCOLOR_ATTR, Color.RED);
		}

		for (BAState s : this.getAcceptingStates()) {
			Node n = graph.findNodeByName(s.getLabel());
			n.setAttribute(Attribute.SHAPE_ATTR, Node.DOUBLECIRCLE_SHAPE);
		}

		String[] processArgs = { "./graphviz-2.38/release/bin/dot.exe", "-Tpng", "-o", path }; // Output-Path

		Process formatProcess;
		try {
			formatProcess = Runtime.getRuntime().exec(processArgs, null, null);
			GrappaSupport.filterGraph(graph, formatProcess);
			formatProcess.getOutputStream().close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return graph;
	}

	@Override
	public String toString() {
		return this.bAStates.toString() + "Trans: " + this.transitions.toString();
	}

	public static void main(String... args) {
		BAState r1 = new BAState("r1", true, true);
		BAState r2 = new BAState("r2", false, false);
		Action a = new Action("a");
		Action b = new Action("b");
		Set<Action> aSet = new HashSet<Action>();
		aSet.add(a);
		Set<Action> bSet = new HashSet<Action>();
		bSet.add(b);
		Set<Action> alphabet = new HashSet<Action>();
		alphabet.add(a);
		alphabet.add(b);
		BATransition r1r2 = new BATransition(r1, r2, bSet);
		BATransition r2r1 = new BATransition(r2, r1, aSet);
		BATransition r1r1 = new BATransition(r1, r1, aSet);
		BATransition r2r2 = new BATransition(r2, r2, bSet);
		Set<BAState> states = new HashSet<BAState>();
		Set<BATransition> transitions = new HashSet<BATransition>();
		states.add(r1);
		states.add(r2);
		transitions.add(r1r2);
		transitions.add(r2r1);
		transitions.add(r1r1);
		transitions.add(r2r2);
		Set<BAState> initialStates = new HashSet<BAState>();
		initialStates.add(r1);
		Set<BAState> acceptingStates = new HashSet<BAState>();
		acceptingStates.add(r1);
		BA ba1 = new BA(states, initialStates, acceptingStates, transitions, alphabet);

		BAState q1 = new BAState("q1", true, true);
		BAState q2 = new BAState("q2", false, false);
		BATransition q1q2 = new BATransition(q1, q2, aSet);
		BATransition q2q1 = new BATransition(q2, q1, bSet);
		BATransition q1q1 = new BATransition(q1, q1, bSet);
		BATransition q2q2 = new BATransition(q2, q2, aSet);
		Set<BAState> states2 = new HashSet<BAState>();
		Set<BATransition> transitions2 = new HashSet<BATransition>();
		states2.add(q1);
		states2.add(q2);
		transitions2.add(q1q2);
		transitions2.add(q2q1);
		transitions2.add(q1q1);
		transitions2.add(q2q2);
		Set<BAState> initialStates2 = new HashSet<BAState>();
		initialStates2.add(q1);
		Set<BAState> acceptingStates2 = new HashSet<BAState>();
		acceptingStates2.add(q1);
		BA ba2 = new BA(states2, initialStates2, acceptingStates2, transitions2, alphabet);

		ba1.createGraph("./ba1.png");
		System.out.println(ba1);
		ba2.createGraph("./ba2.png");

		BA product = ba1.constructProduct(ba2);
		System.out.println(product.isAcceptedLanguageEmpty());
		product.createGraph("./product.png");

//		KS ks1 = KS.read("C:/Users/Raphael/Documents/KS1.csv");
//		KS ks2 = KS.read("C:/Users/Raphael/Documents/KS2.csv");
//		BA ba3 = ks1.transformToBA();
//		BA ba4 = ks2.transformToBA();
//		BA product2 = ba3.constructProduct(ba4);
//		System.out.println(ba3.isAcceptedLanguageEmpty());
//		ba3.createGraph("./ba1.png");
	}

}
