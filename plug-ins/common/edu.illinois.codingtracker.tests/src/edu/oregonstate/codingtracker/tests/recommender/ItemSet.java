package edu.oregonstate.codingtracker.tests.recommender;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import edu.illinois.codingtracker.tests.analyzers.ast.transformation.Item;

public class ItemSet implements Iterable<Item> {

	private Set<Item> items = null;
	private int size;
	private int frequency;
	
	private List<Tuple<Long, Long>> occurances;

	public ItemSet() {
		items = new TreeSet<Item>();
	}

	public ItemSet(Set<Item> items, int size, int frequency) {
		this.items = items;
		this.size = size;
		this.frequency = frequency;
	}

	public void addItem(Item item) {
		items.add(item);
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	public void setOccurances(List<Tuple<Long, Long>> occurances) {
		this.occurances = occurances;
	}

	public Iterator<Item> iterator() {
		return items.iterator();
	}

	public boolean contains(Item item) {
		return items.contains(item);
	}

	public int size() {
		return size;
	}

	public int frequency() {
		return frequency;
	}
	
	public List<Tuple<Long, Long>> getOccurances() {
		return occurances;
	}
	
	@Override
	public int hashCode() {
		return items.hashCode();
	}

	@Override
	public String toString() {
		return items.toString();
	}
}
