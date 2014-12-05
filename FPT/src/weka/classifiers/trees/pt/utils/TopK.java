package weka.classifiers.trees.pt.utils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public class TopK<T> implements Serializable, Iterable<T> {

	private static final long serialVersionUID = 5185784532951175302L;
	
	private Vector<T> vec = null;
	private int[] hcs = null;
	private Comparator<T> comparator = null;
	private int k = 0;
	
	/** one and only constructor */
	@SuppressWarnings("unchecked")
	public TopK(int k, Comparator<T> comparator) {
		
		this.k = k;
		this.vec = new Vector<T>(k);
		this.hcs = new int[k];
		this.comparator = comparator;
		for (int i = 0 ; i <hcs.length ; i++)
			this.hcs[i] = - Integer.MAX_VALUE;
		
	}
	
	/** compares the element to existing elements and uses insertion sort or kicks it out. */
	public boolean offer(T element) {
	
		if(element == null) {
			return false;
		}
		
		// use hashmap? usually k is small...
		int hc = element.hashCode();
		for (int i = 0; i < this.k ; i++) {
			if(this.hcs[i] == hc) {
				return false;
			}
		}
		
		// compare to array elements
		for (int i = 0; i < this.k ; i++) {
			
			if(this.vec.size() <= i) {
				this.vec.add(element);
				this.hcs[i] = element.hashCode();
				return true;
			}
			
			int c = -1 * this.comparator.compare(this.vec.get(i), element);
			if(c < 0) {
				
				T tmp =null ;
				for(int j = i; j < k; j++) {
					tmp = this.vec.get(j);
					this.vec.set(j, element);
					this.hcs[j] = element.hashCode();
					element = tmp ;
					if(this.vec.size() == j+1 && this.vec.size()<k) 
					{
						this.vec.add(element);
						this.hcs[j+1] = element.hashCode();
						break ;
					}					
				}
				return true;
			}
		}
		
		return false;
		
	}
	
	/** returns the i-th element, the 0-th element is the "best" one */
	public T get(int i) {
		return this.vec.get(i);
	}

	
	/** returns a new array of current top k elements */
	public Object[] toArray() {
		return this.vec.toArray();
	}
	
	public HashSet<T> toHashSet() {
		return new HashSet<T>(this.vec);
	}
	
	public boolean contains(T element) {
		return this.vec.contains(element);
	}

	@Override
	public Iterator<T> iterator() {
		return vec.iterator();
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < this.k; i++) {
			sb.append(this.get(i)).append('\n');
		}
		return sb.toString();
	}
	
	
}
