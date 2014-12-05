package weka.classifiers.trees.pt.utils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Robin Senge [mailto:senge@informatik.uni-marburg.de]
 *
 */
public class SortedArray<T> implements Serializable {

	
	/** for serialization */
	private static final long serialVersionUID = -758154571377966471L;

	/** container */
	private T[] array = null;

	/** size of container */
	private int capacity;
	
	/** number of added items. */
	private int count;
	
	/** is this array locked? */
	private boolean locked = false;

	/** compares nodes */
	private Comparator<T> comparator = null;
	
	/** Constructor. */
	public SortedArray(int capacity, Comparator<T> comparator) {
		this.array = (T[])new Object[capacity];
		this.capacity = capacity;
		this.comparator = comparator;
	}

	/** Returns the size of this list. */
	public int length() {
		return this.capacity;
	}
	
	/** Returns the number of added items. */
	public int count() {
		return count;
	}

	/** 
	 * Adds a new element and the list remains sorted. 
	 * Returns true if, the item has been included. 
	 */
	public boolean add(T item) {

		if(locked) {
			throw new RuntimeException("SortedArray has already been locked by setNull()!");
		}
		
		if(item != null) count++;
		
		// insertion sort
		int i;
		for (i = capacity - 1; i >= 0; i--) {			
			if (this.array[i] != null) {
				int c = comparator.compare(this.array[i], item);
				if (c < 0) { 
					break;
				}
			}
		}
		int index = i + 1;
		if (index >= capacity) {
			return false;
		} else {
			for (int j = index; j < capacity; j++) {
				T tmp = this.array[j];
				this.array[j] = item;
				if (tmp == null)
					break;
				item = tmp;
			}
			return true;
		}

	}

	/** Adds a whole bunch of items. */
	public void addAll(Object[] items) {
		if(locked) {
			throw new RuntimeException("SortedArray has already been locked by setNull()!");
		}
		for (int i = items.length - 1; i >= 0 ; i--) {
			this.add((T)items[i]);
		}
	}

	/** Returns an element. */
	public T get(int index) {
		return this.array[index];
	}

	/** Returns the first (best) element. */
	public T first() {
		return this.array[0];
	}
	
	/** Returns the last (worst) element. */
	public T last() {
		return this.array[capacity-1];
	}
	
	/** Returns a flat clone of the inner array. */
	public Object[] toArray() {
		return this.array.clone();
	}
	
	/** Returns a flat clone of the inner array. */
	public List<T> toList() {
		LinkedList<T> list = new LinkedList<T>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		SortedArray<T> clone = new SortedArray<T>(this.capacity, this.comparator);
		clone.array = this.array.clone();
		return clone;
	}

	public void setNull(int k) {
		this.array[k] = null;	
		this.locked = true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			sb.append(i == 0 ? "[" : "")
				.append(i+1)
				.append(":")
				.append(array[i].toString())
				.append(i < array.length -1 ? "|" : "]");
		}
		return sb.toString();
		
	}
	
	public boolean contains(T item) {
		
		for (int i = 0; i < array.length; i++) {
			if(array[i] == item) return true;
		}
		return false;
	}
	
	
	
	
}
