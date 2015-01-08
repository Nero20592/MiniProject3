package miniprojekt3;

public class KSTransition {

	KSState begin;
	KSState end;

	public KSState getBegin() {
		return begin;
	}

	public KSState getEnd() {
		return end;
	}

	public KSTransition(KSState begin, KSState end) {
		super();
		this.begin = begin;
		this.end = end;
	}

	@Override
	public String toString() {
		return "(" + begin.toString() + ", " + end.toString() + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (this.getBegin().getName()
				.equals(((KSTransition) o).getBegin().getName())) {
			if (this.getEnd().getName()
					.equals(((KSTransition) o).getEnd().getName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 1;
	}

}
