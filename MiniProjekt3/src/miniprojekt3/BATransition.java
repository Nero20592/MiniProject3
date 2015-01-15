package miniprojekt3;


public class BATransition {

	BAState begin;
	BAState end;
	Action action;

	public BATransition(BAState begin, BAState end, Action action) {
		super();
		this.begin = begin;
		this.end = end;
		this.action = action;
	}

	public Action getAction() {
		return action;
	}

	public BAState getSourceState() {
		return begin;
	}

	public BAState getTargetState() {
		return end;
	}

	@Override
	public String toString() {
		return "(" + begin.toString() + ", " + action.toString() + ", " + end.toString() + ")";
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
