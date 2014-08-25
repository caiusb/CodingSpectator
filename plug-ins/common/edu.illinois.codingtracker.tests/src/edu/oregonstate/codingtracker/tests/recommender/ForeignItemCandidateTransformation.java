package edu.oregonstate.codingtracker.tests.recommender;

import edu.illinois.codingtracker.tests.analyzers.ast.transformation.Item;

public class ForeignItemCandidateTransformation extends CandidateTransformation {
	
	public static final int DEFAULT_MAX_FOREIGN_ITEMS = 5;
	
	private int foreignItems;
	private Item lastInvalidNodeSeen = null;
	private int maxForeignItems;
	
	public ForeignItemCandidateTransformation(ItemSet itemSet, Item firstItem, int maxForeignItems) {
		super(itemSet, firstItem);
		this.maxForeignItems = maxForeignItems;
	}

	public ForeignItemCandidateTransformation(ItemSet itemSet, Item firstItem) {
		this(itemSet, firstItem, DEFAULT_MAX_FOREIGN_ITEMS);
	}

	@Override 
	public boolean continuesCandidate(ItemOccurance itemOccurance) {
		if (super.continuesCandidate(itemOccurance) == true)
			return true;
		
		Item item = itemOccurance.getItem();
		
		if (foreignItems < maxForeignItems) {
			if (!item.equals(lastInvalidNodeSeen))
				foreignItems++;
			return true;
		}

		return false;
	}
}
