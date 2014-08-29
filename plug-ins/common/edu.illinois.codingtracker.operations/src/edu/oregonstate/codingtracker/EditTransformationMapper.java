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
		ArrayList<UpdatableTextChangeOperation> copyOfUnmachedOperations = makeCopyOfUnmachedOperations();
		for (UpdatableTextChangeOperation operation : copyOfUnmachedOperations) {
			operation.updateInRegardTo(textChangeOperation);
		}
		unmachedOperations.add(new UpdatableTextChangeOperation(textChangeOperation));
	}
	}

	private ArrayList<UpdatableTextChangeOperation> makeCopyOfUnmachedOperations() {
		ArrayList<UpdatableTextChangeOperation> copyOfUnmachedOperations = new ArrayList<UpdatableTextChangeOperation>();
		copyOfUnmachedOperations.addAll(unmachedOperations);
		return copyOfUnmachedOperations;
	}
	
	public int getNumberOFUnmachedTrasformations() {
		return unmachedOperations.size();
	}
}
