package edu.oregonstate.codingtracker.tests.recommender;

public class ExistingTransformation implements Comparable<ExistingTransformation> {

	private long startTime;
	private long endTime;
	private ItemSet itemSet;

	public ExistingTransformation(long startTime, long endTime, ItemSet itemSet) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.itemSet = itemSet;
	}
	
	public boolean containsTimestamp(long timestamp) {
		return (startTime < timestamp) && (endTime > timestamp);
	}

	/**
	 * I only compare based on star time and end times. The earlier I start, the
	 * "smaller" I am. For the same start time, the earlier I finish, the
	 * "smaller" I am.
	 */
	@Override
	public int compareTo(ExistingTransformation o) {
		if (o.startTime < startTime)
			return -1;
		else if (o.startTime > startTime)
			return 1;
		else if (o.endTime < endTime)
			return -1;
		else if (o.endTime > endTime)
			return 1;
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ExistingTransformation))
			return false;
		
		ExistingTransformation other = (ExistingTransformation) obj;
		
		return (other.startTime == startTime) && (other.endTime == other.endTime) && (other.itemSet == itemSet);
	}
	
	@Override
	public String toString() {
		return "(" + startTime + "," + endTime + "): " + itemSet;
	}
}	
