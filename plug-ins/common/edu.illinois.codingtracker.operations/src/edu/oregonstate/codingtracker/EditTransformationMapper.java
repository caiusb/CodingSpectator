package edu.oregonstate.codingtracker;

import java.util.List;

import edu.illinois.codingtracker.operations.textchanges.TextChangeOperation;

public class EditTransformationMapper {
	
	private static EditTransformationMapper instance = null;
	
	private List<UpdatableTextChangeOperation> unmachedOperations; 

	public static EditTransformationMapper getInstance() {
		if (instance == null)
			instance = new EditTransformationMapper();
		return instance;
	}

	public void processTextChange(TextChangeOperation textChangeOperation) {
		for (UpdatableTextChangeOperation operation : unmachedOperations) {
			operation.updateInRegardTo(textChangeOperation);
		}
	}

}
