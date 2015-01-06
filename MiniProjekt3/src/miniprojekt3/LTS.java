package miniprojekt3;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import att.grappa.Attribute;
import att.grappa.Edge;
import att.grappa.Graph;
import att.grappa.GrappaSupport;
import att.grappa.Node;
import au.com.bytecode.opencsv.CSVReader;

public class LTS {

	Set<State> states = new HashSet<State>();
	Set<Transition> transitions = new HashSet<Transition>();
	Set<State> initialStates = new HashSet<State>();
	Set<Action> alphabet = new HashSet<Action>();
	Set<AP> atomicPropositions = new HashSet<AP>();

	public Set<State> getStates() {
		return states;
	}

	public Set<Transition> getTransitions() {
		return transitions;
	}

	public Set<State> getInitialStates() {
		return initialStates;
	}

	public Set<Action> getAlphabet() {
		return alphabet;
	}

	public Set<AP> getAtomicPropositions() {
		return atomicPropositions;
	}

	public LTS(Set<State> states, Set<Transition> transitions,
			Set<State> initialStates, Set<Action> alphabet, Set<AP> ap) {
		this.states = states;
		this.transitions = transitions;
		this.initialStates = initialStates;
		this.alphabet = alphabet;
		this.atomicPropositions= ap;
	}

	@Override
	public String toString() {
		return "States: " + states.toString() + " Transitions: "
				+ transitions.toString();
	}

	public static LTS parallelComposition(LTS lts1, LTS lts2) {

		Set<State> s1 = lts1.getStates();
		Set<State> s2 = lts2.getStates();

		Set<State> initialS1 = lts1.getInitialStates();
		Set<State> initialS2 = lts2.getInitialStates();

		Set<State> newStates = cartesianProduct(s1, s2);
		Set<State> newInitialStates = cartesianProduct(initialS1, initialS2);
		Set<AP> newAtomicPropositions = new HashSet<AP>();
		Set<AP> atomicPropositions1 = lts1.getAtomicPropositions();
		Set<AP> atomicPropositions2 = lts2.getAtomicPropositions();
		
		for (AP ap : atomicPropositions1) {
			if(!newAtomicPropositions.contains(ap)){
				newAtomicPropositions.add(ap);
			}
		}
		
		for (AP ap : atomicPropositions2) {
			if(!newAtomicPropositions.contains(ap)){
				newAtomicPropositions.add(ap);
			}
		}
		
		Set<Transition> newTransitions = new HashSet<Transition>();

		Set<Action> a1 = lts1.getAlphabet();
		Set<Action> a2 = lts2.getAlphabet();

		Set<Transition> t1 = lts1.getTransitions();
		Set<Transition> t2 = lts2.getTransitions();

		Set<Action> newAlphabet = new HashSet<Action>();
		newAlphabet.addAll(a1);
		for (Action a : a2) {
			if (!newAlphabet.contains(a)) {
				newAlphabet.add(a);
			}
		}

		Set<Action> H = new HashSet<Action>();
		for (Action a : a1) {
			for (Action b : a2) {
				if (a.equals(b)) {
					H.add(a);
				}
			}
		}

		for (Action a : newAlphabet) {
			if (H.contains(a)) {
				for (Transition u : t1) {
					if (u.getAction().equals(a)) {
						for (Transition v : t2) {
							if (v.getAction().equals(a)) {
								State from = getState(newStates, u.getBegin(),
										v.getBegin());
								State to = getState(newStates, u.getEnd(),
										v.getEnd());
								Transition t = new Transition(from, to, a);
								if (!newTransitions.contains(t)) {
									newTransitions.add(t);
								}
							}
						}
					}
				}

			} else if (a1.contains(a)) {
				for (Transition u : t1) {
					if (u.getAction().equals(a)) {
						for (Transition v : t2) {
							State from = getState(newStates, u.getBegin(),
									v.getBegin());
							State to = getState(newStates, u.getEnd(),
									v.getBegin());
							Transition t = new Transition(from, to, a);
							if (!newTransitions.contains(t)) {
								newTransitions.add(t);
							}
						}
					}
				}

			} else if (a2.contains(a)) {
				for (Transition v : t2) {
					if (v.getAction().equals(a)) {
						for (Transition u : t1) {
							State from = getState(newStates, u.getBegin(),
									v.getBegin());
							State to = getState(newStates, u.getBegin(),
									v.getEnd());
							Transition t = new Transition(from, to, a);
							if (!newTransitions.contains(t)) {
								newTransitions.add(t);
							}
						}
					}
				}

			}
		}

		LTS result = new LTS(newStates, newTransitions, newInitialStates,
				newAlphabet, newAtomicPropositions);
		return removeUnreachableStates(result);
	}

	private static State getState(Set<State> newStates, State begin,
			State begin2) {
		for (State state : newStates) {
			if (state.getName().contains(begin.getName())) {
				if (state.getName().contains(begin2.getName())) {
					return state;
				}
			}
		}
		return null;
	}

	public static Set<State> cartesianProduct(Set<State> aSet, Set<State> bSet) {
		Set<State> ret = new HashSet<State>();
		for (State a : aSet) {
			Set<AP> apsA = a.getAPs();
			for (State b : bSet) {
				Set<AP> apsB = b.getAPs();
				State s = new State(a.getName() + "/" + b.getName()); // Komma entfernt
				Set<AP> aps = new HashSet<AP>();
				aps.addAll(apsA);
				for (AP ap : apsB) {
					if(!aps.contains(ap)){
						aps.add(ap);
					}
				}
				s.setAtomicPropositions(aps);
				ret.add(s);
			}
		}
		return ret;
	}

	public static LTS read(String file) {
		CSVReader reader = null;
		LTS ret = null;

		try {
			reader = new CSVReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String[] nextLine;
		try {
			reader.readNext();
			Set<State> states = new HashSet<State>();
			Set<State> initialStates = new HashSet<State>();
			Set<Action> alphabet = new HashSet<Action>();
			Set<Transition> transitions = new HashSet<Transition>();
			Set<AP> atomicPropositions = new HashSet<AP>();
			while ((nextLine = reader.readNext()) != null) {
				String type = nextLine[0];
				switch (type) {
				case "I":
					State s = new State(nextLine[1]);
					for (int i = 2; i < nextLine.length; i++) {
						AP atomic = new AP(nextLine[i]);
						s.addAP(atomic);
						atomicPropositions.add(atomic);
					}
					states.add(s);
					initialStates.add(s);
					break;
				case "T":
					State from = new State(nextLine[1]);
					State to = new State(nextLine[2]);
					Action action = new Action(nextLine[3]);
					transitions.add(new Transition(from, to, action));
					alphabet.add(action);
					break;
				case "S":
					State state = new State(nextLine[1]);
					for (int i = 2; i < nextLine.length; i++) {
						AP atomic = new AP(nextLine[i]);
						state.addAP(atomic);
					}
					states.add(state);
					break;
				default:
					break;
				}
			}
			reader.close();
			ret = new LTS(states, transitions, initialStates, alphabet, atomicPropositions);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static void write(String file, LTS lts){
		FileWriter writer = null;

		try {
			writer = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			writer.write(System.getProperty("line.separator"));
			
			for (State s : lts.getInitialStates()) {
				String nextLine;

				nextLine = "I,"+ s.getName();
				
				for(AP ap : s.getAPs()) {
					nextLine += "," + ap.getLabel();
				}
				
				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
			}
			
			Set<State> nonInitials = lts.getStates();
			for (State s : lts.getInitialStates()) {
				nonInitials.remove(s);
			}

			for (State s : nonInitials) {
				String nextLine;

				nextLine = "S," + s.getName();
				
				for(AP ap : s.getAPs()) {
					nextLine += "," + ap.getLabel();
				}

				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
			}
			
			for (Transition t : lts.getTransitions()) {
				String nextLine = "T," + t.getBegin().getName() + "," + t.getEnd().getName() + "," + t.getAction().getAction();

				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
			}
			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static LTS parallelComposition(List<LTS> list) {
		LTS cur = list.get(0);
		for (int i = 0; i < list.size() - 1; i++) {
			cur = parallelComposition(cur, list.get(i + 1));
		}

		return removeUnreachableStates(cur);
	}

	public static LTS removeUnreachableStates(LTS lts) {
		Set<State> initialStates = lts.getInitialStates();
		Set<Transition> transitions = lts.getTransitions();

		Set<State> reachableStates = new HashSet<State>();
		
		Queue<State> queue = new LinkedList<State>();
		queue.addAll(initialStates);
		reachableStates.addAll(initialStates);
		
		while(!queue.isEmpty()){
			State currentState = queue.remove();
			
			for (Transition t : transitions) {
				State nextState = t.getEnd();

				if (currentState.equals(t.getBegin())
						&& !reachableStates.contains(nextState)) {

					queue.add(nextState);
					reachableStates.add(nextState);
				}
			}
		}
		
		for (Iterator<Transition> iterator2 = transitions
				.iterator(); iterator2.hasNext();) {
			Transition transition = (Transition) iterator2.next();
			if (!reachableStates.contains(transition.getBegin()) || !reachableStates.contains(transition.getEnd())) {
				iterator2.remove();
			}
		}
		
		return new LTS(reachableStates, transitions, initialStates, lts.getAlphabet(), lts.getAtomicPropositions());
	}
	
//	public static Graph createGraph(LTS lts, String path){
//		Graph graph = new Graph("LTS");
//
//		for (State s : lts.getStates()) {
//			Node n = new Node(graph, s.getName());
//			n.setAttribute("xlabel", s.getAPs().toString());
//			graph.addNode(n);
//			
//		}
//
//		for (Transition t : lts.getTransitions()) {
//			Edge edge = new Edge(graph, graph.findNodeByName(t.getBegin()
//					.getName()), graph.findNodeByName(t.getEnd().getName()));
//			edge.setAttribute(Attribute.LABEL_ATTR, t.getAction().getAction());
//			graph.addEdge(edge);
//		}
//
//		for (State s : lts.getInitialStates()) {
//			Node n = graph.findNodeByName(s.getName());
//			Node invis = new Node(graph);
//			Edge start = new Edge(graph, invis, n);
//			graph.addEdge(start);
//			invis.setAttribute(Attribute.STYLE_ATTR, "invis");
//			n.setAttribute(Attribute.COLOR_ATTR, Color.RED);
//			n.setAttribute(Attribute.FILLCOLOR_ATTR, Color.RED);
//			n.setAttribute(Attribute.FONTCOLOR_ATTR, Color.RED);
//		}
//
//		String[] processArgs = {
//				"./graphviz-2.38/release/bin/dot.exe",
//				"-Tpng", "-o", path};  // Output-Path
//		
//		Process formatProcess;
//		try {
//			formatProcess = Runtime.getRuntime().exec(processArgs, null,
//					null);
//			GrappaSupport.filterGraph(graph, formatProcess);
//			formatProcess.getOutputStream().close();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		
//		return graph;
//	}
	public static Graph createGraphBA(BA ba, String path){
		Graph graph = new Graph("BA");

		for (State s : ba.getStatesBA()) {
			Node n = new Node(graph, s.getName());
			n.setAttribute("xlabel", s.getAPs().toString());
			graph.addNode(n);
			
		}

		for (Transition t : ba.getTransitionsBA()) {
			Edge edge = new Edge(graph, graph.findNodeByName(t.getBegin()
					.getName()), graph.findNodeByName(t.getEnd().getName()));
			edge.setAttribute(Attribute.LABEL_ATTR, t.getAction().getAction());
			graph.addEdge(edge);
		}

		for (State s : ba.getInitialStatesBA()) {
			Node n = graph.findNodeByName(s.getName());
			Node invis = new Node(graph);
			Edge start = new Edge(graph, invis, n);
			graph.addEdge(start);
			invis.setAttribute(Attribute.STYLE_ATTR, "invis");
			n.setAttribute(Attribute.COLOR_ATTR, Color.RED);
			n.setAttribute(Attribute.FILLCOLOR_ATTR, Color.RED);
			n.setAttribute(Attribute.FONTCOLOR_ATTR, Color.RED);
		}

		String[] processArgs = {
				"./graphviz-2.38/release/bin/dot.exe",
				"-Tpng", "-o", path};  // Output-Path
		
		Process formatProcess;
		try {
			formatProcess = Runtime.getRuntime().exec(processArgs, null,
					null);
			GrappaSupport.filterGraph(graph, formatProcess);
			formatProcess.getOutputStream().close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return graph;
	}
}
