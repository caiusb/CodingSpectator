package edu.oregonstate.codingtracker;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.codingtracker.operations.textchanges.TextChangeOperation;
import edu.oregonstate.codingtracker.helpers.Tuple;

/**
 * I am a wrapper around a TextChangeOperation. This allows
 * me to change my offset, so I always point to the right
 * place in the source code file.
 * 
 */
public class UpdatableTextChangeOperation {

	private TextChangeOperation operation;
	
	private List<Tuple<Integer,Integer>> fragments = new ArrayList<Tuple<Integer,Integer>>();

	public UpdatableTextChangeOperation(TextChangeOperation operation) {
		this.operation = operation;
		fragments.add(new Tuple<Integer, Integer>(operation.getOffset(), operation.getNewText().length()));
	}

	protected UpdatableTextChangeOperation(int offset, int length) {
		fragments.add(new Tuple<Integer, Integer>(offset, length));
	}

	public void updateInRegardTo(TextChangeOperation textChangeOperation) {
		
		List<Tuple<Integer,Integer>> newFragments = new ArrayList<Tuple<Integer,Integer>>();
		
		for (Tuple<Integer, Integer> fragment : fragments) {
			if (isAfter(textChangeOperation, fragment)) {
				newFragments.add(fragment);
				continue;
			}
			
			if (isBefore(textChangeOperation, fragment)) {
				newFragments.add(new Tuple<Integer,Integer>(fragment.getFirst() + textChangeOperation.getNewText().length(), fragment.getSecond()));
				continue;
			}
			
			Tuple<Integer, Integer> fragment1 = new Tuple<Integer, Integer>(fragment.getFirst(), textChangeOperation
					.getNewText().length() + 1);
			Tuple<Integer, Integer> fragment2 = new Tuple<Integer, Integer>(textChangeOperation.getOffset()
					+ textChangeOperation.getNewText().length() + 1, textChangeOperation.getOffset()
					+ textChangeOperation.getNewText().length() + fragment.getSecond());
			
			newFragments.add(fragment1);
			newFragments.add(fragment2);
		}
		
		
        fragments = newFragments;
	}

	private boolean isBefore(TextChangeOperation textChangeOperation, Tuple<Integer, Integer> fragment) {
		return textChangeOperation.getOffset() < fragment.getFirst();
	}

	private boolean isAfter(TextChangeOperation textChangeOperation, Tuple<Integer, Integer> fragment) {
		return textChangeOperation.getOffset() > (fragment.getFirst() + fragment.getSecond());
	}

	public boolean contains(int matchingOffset, int matchingLength) {
		for (Tuple<Integer,Integer> fragment : fragments) {
			Integer actualOffset = fragment.getFirst();
			Integer actualLength = fragment.getSecond();
			int matchingEndingIntex = matchingOffset + matchingLength;
			int transformationEndingIndex = actualOffset + actualLength;
			if ((matchingOffset >= actualOffset) && (matchingOffset <= transformationEndingIndex)
					|| (matchingEndingIntex >= actualOffset) && (matchingEndingIntex <= transformationEndingIndex)
					|| (matchingOffset <= actualOffset) && (matchingEndingIntex >= transformationEndingIndex))
				return true;
		}
		return false;
	}
	
	public TextChangeOperation getOperation() {
		return operation;
	}
}
