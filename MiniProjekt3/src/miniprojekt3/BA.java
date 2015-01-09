package miniprojekt3;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	public BA(Set<BAState> bAStates, Set<BAState> initialStates,
			Set<BAState> acceptingStates, Set<BATransition> transitions,
			Set<Action> alphabet) {
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

		List<Set<BAState>> cartesianProducts = cartesianProducts(
				this.getStates(), other.getStates());
		Set<BAState> newStates = new HashSet<BAState>();
		newStates.addAll(cartesianProducts.get(0));
		Set<BAState> newInitialStates = new HashSet<BAState>();
		newInitialStates.addAll(cartesianProducts.get(1));
		Set<BAState> newAcceptingStates = new HashSet<BAState>();
		newAcceptingStates.addAll(cartesianProducts.get(2));

		Set<BATransition> newTransitions = new HashSet<BATransition>();
		for (BATransition t1 : this.getTransitions()) {
			for (BATransition t2 : other.getTransitions()) {
				for (int i = 0; i < 3; i++) {
					BAState begin = getBAState(newStates, t1.getBegin()
							.getName() + ",");
					BAState end = getBAState(newStates, "");
				}
			}
		}
		for (BAState s1 : newStates) {
			for (BATransition t1 : this.getTransitions()) {
				for (BATransition t2 : other.getTransitions()) {
					String[] s1Name = s1.getName().split(",");
					if (t1.getActions().equals(t2.getActions())) {
						if (s1Name[0].equals(t1.getBegin().getName())) {
							int y = -1;
							if (Integer.parseInt(s1Name[2]) == 0) {
								for (BAState ba : this.getAcceptingStates()) {
									Set<BAState> currentState = getBAState(
											newStates, ba.getName(), null, "1");
									for (BAState baState : currentState) {
										BATransition currentTransition = new BATransition(
												s1, baState, t1.getActions());
									}
								}
							} else if (Integer.parseInt(s1Name[2]) == 1) {
								for (BAState ba : other.getAcceptingStates()) {
									Set<BAState> currentState = getBAState(
											newStates, null, ba.getName(), "2");
									for (BAState baState : currentState) {
										BATransition currentTransition = new BATransition(
												s1, baState, t1.getActions());
									}
								}
							} else if (Integer.parseInt(s1Name[2]) == 2) {
									Set<BAState> currentState = getBAState(
											newStates, null, null, "0");
									for (BAState baState : currentState) {
										BATransition currentTransition = new BATransition(
												s1, baState, t1.getActions());
								}
							} else {
								Set<BAState> currentState = getBAState(
										newStates, null, null, s1Name[2]);
								for (BAState baState : currentState) {
									BATransition currentTransition = new BATransition(
											s1, baState, t1.getActions());
								}
							}
						}
					}
				}
			}
		}

		BA ret = new BA(newStates, newInitialStates, newAcceptingStates,
				newTransitions, newAlphabet);
		return null;
	}

	public static Set<BAState> getBAState(Set<BAState> states, String first,
			String second, String third) {
		Set<BAState> ret = new HashSet<BAState>();
		if (first == null) {
			for (BAState baState : states) {
				String[] name = baState.getName().split(",");
				if (second.equals(name[1]) && third.equals(name[2])) {
					ret.add(baState);
				}
			}
		} else if (second == null) {
			for (BAState baState : states) {
				String[] name = baState.getName().split(",");
				if (first.equals(name[0]) && third.equals(name[2])) {
					ret.add(baState);
				}
			}
		}
		return ret;
	}

	public static BAState getBAState(Set<BAState> states, String name) {
		for (BAState state : states) {
			if (state.getName().equals(name)) {
				return state;
			}
		}
		return null;
	}

	public static List<Set<BAState>> cartesianProducts(Set<BAState> aSet,
			Set<BAState> bSet) {
		Set<BAState> states = new HashSet<BAState>();
		Set<BAState> initialStates = new HashSet<BAState>();
		Set<BAState> acceptingStates = new HashSet<BAState>();
		List<Set<BAState>> ret = new ArrayList<Set<BAState>>();
		for (BAState a : aSet) {
			for (BAState b : bSet) {
				states.add(new BAState(a.getName() + "," + b.getName() + ","
						+ 1, false, false));
			}
		}
		for (BAState a : aSet) {
			for (BAState b : bSet) {
				BAState currentState = new BAState(a.getName() + ","
						+ b.getName() + "," + 2, false, true);
				acceptingStates.add(currentState);
				states.add(currentState);
			}
		}
		for (BAState a : aSet) {
			for (BAState b : bSet) {
				BAState currentState;
				if (a.isInitial() && b.isInitial()) {
					currentState = new BAState(a.getName() + "," + b.getName()
							+ "," + 0, true, false);
					initialStates.add(currentState);
				} else {
					currentState = new BAState(a.getName() + "," + b.getName()
							+ "," + 0, false, false);
				}
				states.add(currentState);
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
			Node n = new Node(graph, s.getName());
			graph.addNode(n);

		}

		for (BATransition t : this.getTransitions()) {
			Edge edge = new Edge(graph, graph.findNodeByName(t.getBegin()
					.getName()), graph.findNodeByName(t.getEnd().getName()));
			edge.setAttribute(Attribute.LABEL_ATTR, "" + t.getActions());
			graph.addEdge(edge);
		}

		for (BAState s : this.getInitialStates()) {
			Node n = graph.findNodeByName(s.getName());
			Node invis = new Node(graph);
			Edge start = new Edge(graph, invis, n);
			graph.addEdge(start);
			invis.setAttribute(Attribute.STYLE_ATTR, "invis");
			n.setAttribute(Attribute.COLOR_ATTR, Color.RED);
			n.setAttribute(Attribute.FILLCOLOR_ATTR, Color.RED);
			n.setAttribute(Attribute.FONTCOLOR_ATTR, Color.RED);
		}

		for (BAState s : this.getAcceptingStates()) {
			Node n = graph.findNodeByName(s.getName());
			n.setAttribute(Attribute.SHAPE_ATTR, Node.DOUBLECIRCLE_SHAPE);
		}

		String[] processArgs = { "./graphviz-2.38/release/bin/dot.exe",
				"-Tpng", "-o", path }; // Output-Path

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

}
