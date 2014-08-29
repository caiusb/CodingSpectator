package edu.oregonstate.codingtracker.tests.recommender;

import java.util.List;

import edu.illinois.codingtracker.operations.ast.ASTOperationDescriptor.OperationKind;

public class ExistingTransformation implements Comparable<ExistingTransformation> {

	private long startTime;
	private long endTime;
	private ItemSet itemSet;
	private List<Long> transformationIDs;
	private Tuple<Tuple<String,OperationKind>,Long> middleOperation;
	private int number;
	
	public ExistingTransformation(long startTime, long endTime, ItemSet itemSet, List<Long> itemOccurancesInAnInstance, Tuple<Tuple<String,OperationKind>,Long> middleOperation, int number) {
		this(startTime, endTime, itemSet, itemOccurancesInAnInstance, middleOperation);
		this.number = number;
	}

	public ExistingTransformation(long startTime, long endTime, ItemSet itemSet, List<Long> itemOccurancesInAnInstance, Tuple<Tuple<String,OperationKind>,Long> middleOperation) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.itemSet = itemSet;
		this.transformationIDs = itemOccurancesInAnInstance;
		this.middleOperation = middleOperation;
	}
	
	public boolean containsTimestamp(long timestamp) {
		return (startTime < timestamp) && (endTime > timestamp);
	}
	
	public boolean containsMiddleItem(Tuple<Tuple<String, OperationKind>,Long> item) {
		return item.equals(middleOperation);
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
		return number + ": (" + startTime + "," + endTime + "): " + itemSet;
	}

	public List<Long> getTransformationIDs() {
		return transformationIDs;
	}

	public long getStartTime() {
		return startTime;
	}

	public ItemSet getItemSet() {
		return itemSet;
	}
	
	private int getNumber() {
		return number;
	}
}	
