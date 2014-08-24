package edu.oregonstate.codingtracker.tests.recommender;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.illinois.codingtracker.operations.ast.UnknownTransformationDescriptor;
import edu.illinois.codingtracker.tests.analyzers.ast.transformation.Item;
import edu.illinois.codingtracker.tests.analyzers.ast.transformation.LongItem;

public class CandidateTransformation implements Comparable<CandidateTransformation>{
	
	public static final int MAX_FOREIGN_ITEMS = 5;
	
	private ItemSet itemSet;
	private Set<Item> discoveredItems;
	private int age;
	private Item lastInvalidNodeSeen = null;

	public CandidateTransformation(ItemSet itemSet, Item firstItem) {
		this.itemSet = itemSet;
		discoveredItems = new TreeSet<Item>();
		discoveredItems.add(firstItem);
		age = 0;
	}

	public boolean continuesCandidate(Item item) {
		if (discoveredItems.contains(item))
			return true;
		if (itemSet.contains(item))
			return true;
		if (age < MAX_FOREIGN_ITEMS) {
			if (!item.equals(lastInvalidNodeSeen))
				age++;
			return true;
		}

		return false;
	}

	public float getCompleteness() {
		return ((float) discoveredItems.size()) / itemSet.size();
	}
	
	public float getRanking() {
		return getCompleteness() * (float) itemSet.frequency();
//		return getCompleteness();
	}

	public void addItem(Item item) {
		if (!discoveredItems.contains(item))
			discoveredItems.add(item);
	}

	@Override
	public String toString() {
		return getRanking() + " " + discoveredItems + "/" + itemSet; 
	}
	
	public String getTransformationInHumanTerms(Map<Long, UnknownTransformationDescriptor> transformationKinds) {
		String result = "[";
		for (Item item : itemSet) {
			UnknownTransformationDescriptor transformationDescriptor = transformationKinds.get(((LongItem)item).getValue());
			result += "(";
			result += transformationDescriptor.getOperationKind();
			result += ":";
			result += transformationDescriptor.getAffectedNodeType();
			result += ")";
			result += ";";
		}
		result = result.substring(0, result.length() - 1);
		result += "]";
		return result;
		
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CandidateTransformation))
			return false;
		
		return ((CandidateTransformation)o).itemSet.equals(itemSet);
	}
	
	@Override
	public int hashCode() {
		return itemSet.hashCode();
	}

	@Override
	public int compareTo(CandidateTransformation o) {
		float ranking1 = getRanking();
		float ranking2 = o.getRanking();
		if (ranking1 < ranking2)
			return -1;
		if (ranking1 == ranking2)
			return 0;
		return 1;
	}

	public ItemSet getItemSet() {
		return itemSet;
	}
}
