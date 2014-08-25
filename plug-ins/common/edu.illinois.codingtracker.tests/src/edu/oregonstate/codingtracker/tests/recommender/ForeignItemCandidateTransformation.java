package edu.oregonstate.codingtracker.tests.recommender;

import java.util.TreeSet;

import edu.illinois.codingtracker.tests.analyzers.ast.transformation.Item;

public class ForeignItemCandidateTransformation extends CandidateTransformation {
	
	public static final int DEFAULT_MAX_FOREIGN_ITEMS = 5;
	public static final int DEFAULT_MAX_TIME = 5 * 60 * 1000; // 5 minutes = 5 * 60 seconds = 5 * 60 * 1000 milliseconds. 
	
	private int foreignItems;
	private Item lastInvalidNodeSeen = null;
	private int maxForeignItems;
	private final int maxAgeMinutes;
	
	public ForeignItemCandidateTransformation(ItemSet itemSet, Item firstItem, int maxForeignItems, int maxAgeMinutes) {
		this.itemSet = itemSet;
		discoveredItems = new TreeSet<Item>();
		discoveredItems.add(firstItem);
		foreignItems = 0;
		this.maxForeignItems = maxForeignItems;
		this.maxAgeMinutes = maxAgeMinutes * 60 * 1000; // so I get the millisecond distance
		
	}
	
	public ForeignItemCandidateTransformation(ItemSet itemSet, Item firstItem, int maxForeignItems) {
		this(itemSet, firstItem, maxForeignItems, DEFAULT_MAX_TIME);
	}

	public ForeignItemCandidateTransformation(ItemSet itemSet, Item firstItem) {
		this(itemSet, firstItem, DEFAULT_MAX_FOREIGN_ITEMS);
	}

	@Override 
	public boolean continuesCandidate(Item item) {
		if (super.continuesCandidate(item) == true)
			return true;
		
		if (foreignItems < maxForeignItems) {
			if (!item.equals(lastInvalidNodeSeen))
				foreignItems++;
			return true;
		}

		return false;
	}
}
