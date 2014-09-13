package edu.oregonstate.codingtracker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.BadLocationException;
import org.junit.Before;
import org.junit.Test;

import edu.illinois.codingtracker.operations.textchanges.TextChangeOperation;

public class UpdatableTextChangeOperationTest {
	
	private UpdatableTextChangeOperation operation;
	
	@Before
	public void setUp() {
		operation = new UpdatableTextChangeOperation(1,5);
	}
	
	@Test
	public void testStartBeforeOffsetAndEndAfterLength() {
		assertTrue(operation.contains(0, 2));
	}
	
	@Test
	public void testStartAfterOffsetAndEndAfterLength() {
		assertTrue(operation.contains(5, 7));
	}
	
	@Test
	public void testStartAfterOffsetAndEndBeforeLength() {
		assertTrue(operation.contains(3,4));
	}
	
	@Test
	public void testContainsWholeOperation() {
		assertTrue(operation.contains(-1,8));
	}
	
	@Test
	public void testHappensBefore() {
		assertFalse(operation.contains(-2,-1));
	}
	
	@Test
	public void testHappensAfter() {
		assertFalse(operation.contains(9,10));
	}

}
