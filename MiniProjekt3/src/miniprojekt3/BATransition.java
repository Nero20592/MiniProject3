package miniprojekt3;

import java.util.HashSet;
import java.util.Set;

public class BATransition {

	BAState begin;
	BAState end;
	Set<Action> actions = new HashSet<Action>();

	public BATransition(BAState begin, BAState end, Set<Action> actions) {
		super();
		this.begin = begin;
		this.end = end;
		this.actions = actions;
	}

	public Set<Action> getActions() {
		return actions;
	}

	public BAState getBegin() {
		return begin;
	}

	public BAState getEnd() {
		return end;
	}

	@Override
	public String toString() {
		return "(" + begin.toString() + ", " + actions.toString() + ", "
				+ end.toString() + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (this.getBegin().getName()
				.equals(((BATransition) o).getBegin().getName())) {
			if (this.getEnd().getName()
					.equals(((BATransition) o).getEnd().getName())) {
				Set<Action> otherActions = ((BATransition) o).getActions();
				boolean equals = true;
				for (Action actionA : otherActions) {
					for (Action actionB : this.actions) {
						if(actionA.getAction() != actionB.getAction()){
							equals = false;
						}
					}
				}
				if(equals){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 1;
	}

}
