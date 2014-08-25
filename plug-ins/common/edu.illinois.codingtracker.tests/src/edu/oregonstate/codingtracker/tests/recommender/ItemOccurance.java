package edu.oregonstate.codingtracker.tests.recommender;

import edu.illinois.codingtracker.tests.analyzers.ast.transformation.Item;

public class ItemOccurance {
	
	private Item item;
	private long time;
	
	public ItemOccurance(Item item, long time) {
		this.item = item;
		this.time = time;
	}
	
	public Item getItem() {
		return item;
	}
	
	public long getTime() {
		return time;
	}
}
