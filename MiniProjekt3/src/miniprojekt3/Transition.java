package miniprojekt3;

public class Transition {

	State begin;
	State end;
	Action action;

	public Action getAction() {
		return action;
	}

	public State getBegin() {
		return begin;
	}

	public State getEnd() {
		return end;
	}
	public Transition(State begin, State end) {
		super();
		this.begin = begin;
		this.end = end;
	}
	public Transition(State begin, State end, Action action) {
		super();
		this.begin = begin;
		this.end = end;
		this.action = action;
	}

	@Override
	public String toString() {
		return "(" + begin.toString() + ", " + action.toString() + ", "
				+ end.toString() + ")";
	}
	
	@Override
	public boolean equals(Object o) {
		if(this.getBegin().getName().equals(((Transition) o).getBegin().getName())){
			if(this.getEnd().getName().equals(((Transition) o).getEnd().getName())){
				if(this.action.getAction().equals(((Transition) o).getAction().getAction())){
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
