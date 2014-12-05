/**
 * 
 * ePTTD: Evolving Fuzzy Pattern Trees
 *
 * this class is used as an indicator for the type of 
 * the loss function to be used by the eFPTTD
 * @author Ammar Shaker [mailto:Shaker@mathematik.uni-marburg.de]
 * @version 2.0
 * 
 */

package weka.classifiers.trees.pt.utilsEv;

public enum LossFunction  {
	AbsoluteLoss,
	SquaredLoss,
	ZeroOneLoss
}
