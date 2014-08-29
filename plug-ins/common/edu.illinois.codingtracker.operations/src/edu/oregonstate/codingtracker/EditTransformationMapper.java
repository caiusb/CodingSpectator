package edu.oregonstate.codingtracker;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.codingtracker.operations.textchanges.TextChangeOperation;

public class EditTransformationMapper {
	
	private static EditTransformationMapper instance = null;
	
	private List<UpdatableTextChangeOperation> unmachedOperations;
	
	private EditTransformationMapper() {
		unmachedOperations = new ArrayList<UpdatableTextChangeOperation>();
	}

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
