package edu.oregonstate.codingtracker;

import edu.illinois.codingtracker.operations.textchanges.TextChangeOperation;

public class UpdatableTextChangeOperation {

	private TextChangeOperation operation;

	private int actualOffset;
	private int actualLength;
	
	public UpdatableTextChangeOperation(TextChangeOperation operation) {
		this.operation = operation;
		this.actualLength = operation.getNewText().length();
		this.actualOffset = operation.getOffset();
	}

	public void updateInRegardTo(TextChangeOperation textChangeOperation) {
		if (isAfter(textChangeOperation))
			return;
		
		if (isBefore(textChangeOperation)) {
			actualOffset += textChangeOperation.getLength();
			return;
		}
		
		// what to do if the thing actually happens in the middle? Can this be the case?
		// I will ignore this case for now. I hope it does not bite me in the ass!
	}

	private boolean isBefore(TextChangeOperation textChangeOperation) {
		return textChangeOperation.getOffset() < actualOffset;
	}

	private boolean isAfter(TextChangeOperation textChangeOperation) {
		return textChangeOperation.getOffset() > (actualOffset + actualLength);
	}

	public boolean contains(int offset, int length) {
		return (((offset > (actualOffset)) && (offset < (actualOffset + actualLength))) 
				|| (((offset + length) > actualLength) && ((offset + length) < (actualOffset + actualLength))));
	}
	
	public TextChangeOperation getOperation() {
		return operation;
	}
	
	@Override
	public String toString() {
		return operation + "\nActualOffset: " + actualOffset + "\nActtualLength: " + actualLength;
	}
}
