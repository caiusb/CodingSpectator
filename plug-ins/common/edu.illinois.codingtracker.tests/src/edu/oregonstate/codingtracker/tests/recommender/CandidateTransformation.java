package edu.oregonstate.codingtracker.tests.recommender;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.illinois.codingtracker.operations.ast.UnknownTransformationDescriptor;
import edu.illinois.codingtracker.tests.analyzers.ast.transformation.Item;
import edu.illinois.codingtracker.tests.analyzers.ast.transformation.LongItem;

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
}
