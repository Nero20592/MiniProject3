package miniprojekt3;

import java.awt.Color;
import java.io.IOException;
import java.util.HashSet;
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
