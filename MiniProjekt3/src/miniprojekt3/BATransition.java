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

	public BAState getBegin() {
		return begin;
	}

	public BAState getEnd() {
		return end;
	}

	@Override
	public String toString() {
		return "(" + begin.toString() + ", " + action.toString() + ", " + end.toString() + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (this.getBegin().getName().equals(((BATransition) o).getBegin().getName())) {
			if (this.getEnd().getName().equals(((BATransition) o).getEnd().getName())) {
				if(this.getEnd().equals(((BATransition) o).getAction())){
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
