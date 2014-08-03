package edu.oregonstate.codingtracker.tests.recommender;

import java.util.Set;
import java.util.TreeSet;

import edu.illinois.codingtracker.tests.analyzers.ast.transformation.Item;

public class CandidateTransformation {

	private Set<Item> itemSet;
	private Set<Item> discoveredItems;

	public CandidateTransformation(Set<Item> itemSet, Item firstItem) {
		this.itemSet = itemSet;
		discoveredItems = new TreeSet<Item>();
		discoveredItems.add(firstItem);
	}

	public boolean continuesCandidate(Item item) {
		if (discoveredItems.contains(item))
			return true;
		if (itemSet.contains(item))
			return true;

		return false;
	}

	public float getRanking() {
		return ((float) discoveredItems.size()) / itemSet.size();
	}

	public void addItem(Item item) {
		if (!discoveredItems.contains(item))
			discoveredItems.add(item);
	}

}
