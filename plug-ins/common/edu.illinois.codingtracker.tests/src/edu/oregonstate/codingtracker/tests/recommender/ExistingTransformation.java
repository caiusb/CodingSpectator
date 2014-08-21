package edu.oregonstate.codingtracker.tests.recommender;

import java.util.List;

public class ExistingTransformation implements Comparable<ExistingTransformation> {

	private long startTime;
	private long endTime;
	private ItemSet itemSet;
	private List<Long> transformationIDs;

	public ExistingTransformation(long startTime, long endTime, ItemSet itemSet, List<Long> itemOccurancesInAnInstance) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.itemSet = itemSet;
		this.transformationIDs = itemOccurancesInAnInstance;
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

	public List<Long> getTransformationIDs() {
		return transformationIDs;
	}

	public long getStartTime() {
		return startTime;
	}
}	
