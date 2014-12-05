/**
 * 
 */
package weka.classifiers.trees.pt;

/**
 * @author senge
 *
 */
public enum Fuzzyfication {

	/**
	 * Low from min to max.
	 */
	LOW,
	
	/**
	 * High from min to max.
	 */
	HIGH,
		
	/**
	 * Low and high from min to max.
	 */
	LOW_HIGH,

	/**
	 * Low from min to half, high 
	 * from half to max, mid from min 
	 * over half to max.
	 */
	LOW_MID_HIGH,

	/**
	 * Low from min to opt, high 
	 * from opt to max, opt from min 
	 * over opt to max.
	 */
	LOW_OPT_HIGH,

	/**
	 * Low and high from min to max, 
	 * opt from min over opt to max.
	 */
	LOW_HIGH_OPT
}
