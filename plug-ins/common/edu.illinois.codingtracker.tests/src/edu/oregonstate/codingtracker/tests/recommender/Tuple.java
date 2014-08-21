package edu.oregonstate.codingtracker.tests.recommender;

public class Tuple<X,Y> {

	private final X first;
	private final Y second;

	public Tuple(X first, Y second) {
		this.first = first;
		this.second = second;
	}
	
	public X getFirst() {
		return first;
	}
	
	public Y getSecond() {
		return second;
	}
	
	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Tuple))
			return false;
		Tuple<X,Y> other = (Tuple<X,Y>) obj;
		return (other.getFirst().equals(this.getFirst())) && (other.getSecond().equals(this.getSecond()));
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		return 31 ^ first.hashCode() ^ second.hashCode();
	}

}
