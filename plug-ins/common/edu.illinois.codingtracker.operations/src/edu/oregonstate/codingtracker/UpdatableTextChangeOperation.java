package edu.oregonstate.codingtracker;

import edu.illinois.codingtracker.operations.textchanges.TextChangeOperation;

public class UpdatableTextChangeOperation {

	private TextChangeOperation operation;

	private int actualOffset;
	private int actualLength;
	
	public UpdatableTextChangeOperation(TextChangeOperation operation) {
		this.operation = operation;
		this.actualLength = operation.getLength();
		this.actualOffset = operation.getOffset();
	}
}
