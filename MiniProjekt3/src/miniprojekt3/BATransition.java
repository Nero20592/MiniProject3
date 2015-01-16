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

	public Set<Action> getAction() {
		return actions;
	}

	public BAState getSourceState() {
		return begin;
	}

	public BAState getTargetState() {
		return end;
	}

	@Override
	public String toString() {
		return "(" + begin.toString() + ", " + actions.toString() + ", " + end.toString() + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (this.getSourceState().getLabel().equals(((BATransition) o).getSourceState().getLabel())) {
			if (this.getTargetState().getLabel().equals(((BATransition) o).getTargetState().getLabel())) {
				if(this.getAction().equals(((BATransition) o).getAction())){
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
