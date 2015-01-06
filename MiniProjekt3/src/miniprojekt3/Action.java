package miniprojekt3;
public class Action {
	
	String action;

	public Action(String action) {
		super();
		this.action = action;
	}
	
	@Override
	public String toString() {
		return this.action;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this.action.equals(((Action) o).getAction())){
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return 1;
	}

}
