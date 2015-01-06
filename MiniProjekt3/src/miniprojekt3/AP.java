package miniprojekt3;
public class AP {
	
	String label;
	
	public AP(String l){
		this.label = l;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String l) {
		this.label = l;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return label;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		return this.label.equals(((AP)arg0).getLabel());
	}
}
