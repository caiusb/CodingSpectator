package edu.oregonstate.codingtracker.tests.recommender;

import edu.illinois.codingtracker.tests.analyzers.ast.transformation.Item;

public class AgeCandidateTransformation extends CandidateTransformation {
	
	public AgeCandidateTransformation(ItemSet itemSet, Item firstItem) {
		super(itemSet, firstItem);
	}

	public static final int DEFAULT_MAX_TIME = 5 * 60 * 1000; // 5 minutes = 5 * 60 seconds = 5 * 60 * 1000 milliseconds. 
	
	@Override
	public boolean continuesCandidate(ItemOccurance itemOccurance) {
		if(super.continuesCandidate(itemOccurance))
			return true;
		
		return false;
	}

}
