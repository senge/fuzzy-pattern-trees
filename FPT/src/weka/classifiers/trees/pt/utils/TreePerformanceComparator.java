/**
 * 
 */
package weka.classifiers.trees.pt.utils;

import java.io.Serializable;
import java.util.Comparator;

import weka.classifiers.trees.pt.nodes.AbstractNode;

/**
 * @author Robin Senge [mailto:senge@mathematik.uni-marburg.de]
 *
 */
public class TreePerformanceComparator implements Comparator<AbstractNode>, Serializable {
	
	private static final long serialVersionUID = 7503379167197943052L;

	public int compare(AbstractNode node1, AbstractNode node2) {
		if (node1.equals(node2)) {
			return 0;
		}
		return Double.compare(node1.error, node2.error);
	}
}
