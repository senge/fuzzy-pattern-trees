/**
 * 
 */
package weka.classifiers.trees.pt.utils;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 *
 */
public class SequentialTests {
	
	
	public static enum TestResult {
		
		/** Reject the null-hypothesis. Accepting the alternative hypothesis. */
		Reject,
		
		/** Accept the null-hypothesis. Rejecting the alternative hypothesis. */
		Accept,
		
		/** No conclusion possible. */
		None
		
	}


}

