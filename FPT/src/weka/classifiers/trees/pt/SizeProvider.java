package weka.classifiers.trees.pt;

/**
 * @author Robin Senge [senge@mathematik.uni-marburg.de]
 */
public interface SizeProvider {

	/** returns a measure of size reaasonable for this specific model class */
	public double size();
	
}
