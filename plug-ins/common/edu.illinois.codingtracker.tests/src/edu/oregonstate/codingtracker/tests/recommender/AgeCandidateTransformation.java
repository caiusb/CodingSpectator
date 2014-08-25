package edu.oregonstate.codingtracker.tests.recommender;

import edu.illinois.codingtracker.tests.analyzers.ast.transformation.Item;

public class AgeCandidateTransformation extends CandidateTransformation {
	
	public static final int DEFAULT_MAX_TIME = 5 * 60 * 1000; // 5 minutes = 5 * 60 seconds = 5 * 60 * 1000 milliseconds.
	private final long startTime;
	private final int maxTime;
	
	public AgeCandidateTransformation(ItemSet itemSet, Item firstItem, long startTime, int maxTime) {
		super(itemSet, firstItem);
		this.startTime = startTime;
		this.maxTime = maxTime;
	}
	
	public AgeCandidateTransformation(ItemSet itemSet, Item firstItem, long startTime) {
		this(itemSet, firstItem, startTime, DEFAULT_MAX_TIME);
	}

	@Override
	public boolean continuesCandidate(ItemOccurance itemOccurance) {
		if (super.continuesCandidate(itemOccurance))
			return true;
		
		if (maxTime == 0)
			return false;
		
		long time = itemOccurance.getTime();
		
		return (time - startTime) <= maxTime;
	}

}
