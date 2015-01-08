package miniprojekt3;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

public class KS {

	Set<KSState> ksStates = new HashSet<KSState>();
	Set<KSTransition> ksTransitions = new HashSet<KSTransition>();
	Set<KSState> initialKSStates = new HashSet<KSState>();
	Set<AP> atomicPropositions = new HashSet<AP>();

	public Set<KSState> getKSStates() {
		return ksStates;
	}

	public Set<KSTransition> getTransitions() {
		return ksTransitions;
	}

	public Set<KSState> getInitialKSStates() {
		return initialKSStates;
	}

	public Set<AP> getAtomicPropositions() {
		return atomicPropositions;
	}

	public KS(Set<KSState> KSStates, Set<KSTransition> kSTransitions,
			Set<KSState> initialKSStates, Set<AP> ap) {
		this.ksStates = KSStates;
		this.ksTransitions = kSTransitions;
		this.initialKSStates = initialKSStates;
		this.atomicPropositions= ap;
	}

	@Override
	public String toString() {
		return "KSStates: " + ksStates.toString() + " Transitions: "
				+ ksTransitions.toString();
	}
	
	public BA transformToBA(){
		Set<BAState> newStates = new HashSet<BAState>();
		Set<BAState> newInitialStates = new HashSet<BAState>();
		Set<BAState> newAcceptingStates = new HashSet<BAState>();
		Set<BATransition> newTransitions = new HashSet<BATransition>();
		Set<Action> newAlphabet = new HashSet<Action>();
		
		BAState initial = new BAState("i", true, true);
		newInitialStates.add(initial);
		newAcceptingStates.addAll(KSState.toBAStates(this.ksStates));
		newAcceptingStates.add(initial);
		newStates.addAll(KSState.toBAStates(this.ksStates));
		newStates.add(initial);
		
		for (AP ap : this.atomicPropositions) {
			newAlphabet.add(new Action(ap.getLabel()));
		}
		
		for (KSTransition t : this.ksTransitions) {
			BAState begin = getBAState(newStates, t.getBegin().getName());
			BAState end = getBAState(newStates, t.getEnd().getName());
			Set<Action> actions = new HashSet<Action>();
			for (AP ap : t.getEnd().getAPs()) {
				actions.add(new Action(ap.getLabel()));
			}
			BATransition transition = new BATransition(begin, end, actions);
			newTransitions.add(transition);
		}
		
		for (KSState state : this.initialKSStates) {
			BAState end = getBAState(newStates, state.getName());
			Set<Action> actions = new HashSet<Action>();
			for (AP ap : state.getAPs()) {
				actions.add(new Action(ap.getLabel()));
			}
			BATransition transition = new BATransition(initial, end, actions);
			newTransitions.add(transition);
		}
		
		return new BA(newStates, newInitialStates, newAcceptingStates, newTransitions, newAlphabet);
	}


	public static BAState getBAState(Set<BAState> states, String name){
		for (BAState state : states) {
			if(state.getName().equals(name)){
				return state;
			}
		}
		return null;
	}
	
	public static KSState getKSState(Set<KSState> states, String name){
		for (KSState state : states) {
			if(state.getName().equals(name)){
				return state;
			}
		}
		return null;
	}

	public static KS read(String file) {
		CSVReader reader = null;
		KS ret = null;

		try {
			reader = new CSVReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String[] nextLine;
		try {
			reader.readNext();
			Set<KSState> states = new HashSet<KSState>();
			Set<KSState> initialKSStates = new HashSet<KSState>();
			Set<KSTransition> kSTransitions = new HashSet<KSTransition>();
			Set<AP> atomicPropositions = new HashSet<AP>();
			while ((nextLine = reader.readNext()) != null) {
				String type = nextLine[0];
				switch (type) {
				case "I":
					Set<AP> currentAPs = new HashSet<AP>();
					for (int i = 2; i < nextLine.length; i++) {
						AP atomic = new AP(nextLine[i]);
						currentAPs.add(atomic);
						atomicPropositions.add(atomic);
					}
					KSState s = new KSState(currentAPs, nextLine[1], true);
					states.add(s);
					initialKSStates.add(s);
					break;
				case "T":
					KSState from = getKSState(states,nextLine[1]);
					KSState to = getKSState(states,nextLine[2]);
					kSTransitions.add(new KSTransition(from, to));
					break;
				case "S":
					Set<AP> currentAPsS = new HashSet<AP>();
					for (int i = 2; i < nextLine.length; i++) {
						AP atomic = new AP(nextLine[i]);
						currentAPsS.add(atomic);
						atomicPropositions.add(atomic);
					}
					KSState sS = new KSState(currentAPsS, nextLine[1], true);
					states.add(sS);
					break;
				default:
					break;
				}
			}
			reader.close();
			ret = new KS(states, kSTransitions, initialKSStates, atomicPropositions);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static void write(String file, KS ks){
		FileWriter writer = null;

		try {
			writer = new FileWriter(file, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			writer.write(System.getProperty("line.separator"));
			
			for (KSState s : ks.getInitialKSStates()) {
				String nextLine;

				nextLine = "I,"+ s.getName();
				
				for(AP ap : s.getAPs()) {
					nextLine += "," + ap.getLabel();
				}
				
				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
			}
			
			Set<KSState> nonInitials = ks.getKSStates();
			for (KSState s : ks.getInitialKSStates()) {
				nonInitials.remove(s);
			}

			for (KSState s : nonInitials) {
				String nextLine;

				nextLine = "S," + s.getName();
				
				for(AP ap : s.getAPs()) {
					nextLine += "," + ap.getLabel();
				}

				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
			}
			
			for (KSTransition t : ks.getTransitions()) {
				String nextLine = "T," + t.getBegin().getName() + "," + t.getEnd().getName();

				writer.write(nextLine);
				writer.write(System.getProperty("line.separator"));
			}
			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static KS removeUnreachableKSStates(KS ks) {
		Set<KSState> initialKSStates = ks.getInitialKSStates();
		Set<KSTransition> kSTransitions = ks.getTransitions();

		Set<KSState> reachableKSStates = new HashSet<KSState>();
		
		Queue<KSState> queue = new LinkedList<KSState>();
		queue.addAll(initialKSStates);
		reachableKSStates.addAll(initialKSStates);
		
		while(!queue.isEmpty()){
			KSState currentKSState = queue.remove();
			
			for (KSTransition t : kSTransitions) {
				KSState nextKSState = t.getEnd();

				if (currentKSState.equals(t.getBegin())
						&& !reachableKSStates.contains(nextKSState)) {

					queue.add(nextKSState);
					reachableKSStates.add(nextKSState);
				}
			}
		}
		
		for (Iterator<KSTransition> iterator2 = kSTransitions
				.iterator(); iterator2.hasNext();) {
			KSTransition kSTransition = (KSTransition) iterator2.next();
			if (!reachableKSStates.contains(kSTransition.getBegin()) || !reachableKSStates.contains(kSTransition.getEnd())) {
				iterator2.remove();
			}
		}
		
		return new KS(reachableKSStates, kSTransitions, initialKSStates, ks.getAtomicPropositions());
	}
	
//	public static Graph createGraphBA(BA ba, String path){
//		Graph graph = new Graph("BA");
//
//		for (BAState s : ba.getStates()) {
//			Node n = new Node(graph, s.getName());
//			graph.addNode(n);
//			
//		}
//
//		for (BATransition t : ba.getTransitions()) {
//			Edge edge = new Edge(graph, graph.findNodeByName(t.getBegin()
//					.getName()), graph.findNodeByName(t.getEnd().getName()));
//			graph.addEdge(edge);
//		}
//
//		for (BAState s : ba.getInitialStates()) {
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
}
