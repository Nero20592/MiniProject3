package miniprojekt3;

public class BAState {
	
	String name;
	boolean initial = false;
	boolean accepting = false;

	public BAState(String name, boolean initial, boolean accepting) {
		super();
		this.name = name;
		this.initial = initial;
		this.accepting = accepting;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isInitial() {
		return initial;
	}

	public boolean isAccepting() {
		return accepting;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	
	@Override
	public boolean equals(Object arg0) {
		return this.name.equals(((BAState)arg0).getName());
	}
	
	@Override
	public String toString() {
		return this.name;
	}

}
