package gui_pt.fse.util;

import gui_pt.accessLayer.FuzzySetCreator.FuzzySetCreator;
import gui_pt.fse.helper.AttributeWrapper;
import gui_pt.fse.helper.ClassWrapper;
import gui_pt.pt.Calculations;

import javax.swing.tree.DefaultMutableTreeNode;

import weka.core.Instances;

public class TemplatePackCreator {
	
	public DefaultMutableTreeNode createTemplatePack(FuzzySetCreator fsc, Instances data){
		
		
		fsc.initFuzzySets();
		
		DefaultMutableTreeNode templatePackNode = new DefaultMutableTreeNode("Templatepack");
		
		int numAttr = data.numAttributes();
		
		//wrap Attributes
		AttributeWrapper[] attributes = new AttributeWrapper[numAttr];
		for(int a=0; a<numAttr; a++)
		{
			double min;
			double max;
			
			if(data.attribute(a).isNumeric())
			{
				min = Calculations.calcMin(data, a);
				max = Calculations.calcMax(data, a);
			}
			else
			{
				min = 0;
				max = data.attribute(a).numValues()-1;
			}
			attributes[a] = new AttributeWrapper(data.attribute(a), min, max);
		}
		
		//wrap Classes
		ClassWrapper[] classes;
		if(data.classAttribute().isNominal())
		{
			classes = new ClassWrapper[data.numClasses()];
			for(int c = 0; c< data.numClasses(); c++)
			{
				classes[c] = new ClassWrapper();
				classes[c].setClassAttribute(data.classAttribute());
				classes[c].setClassName(data.classAttribute().value(c));
			}
		}
		else
		{
			classes = new ClassWrapper[1];
			classes[0] = new ClassWrapper();
			classes[0].setClassName("Regression");
		}
		
		for(int c=0; c<data.numClasses(); c++)
		{
			DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(classes[c]);
			
			for(int a=0; a<fsc.getFuzzySets()[c].length; a++)
			{
				DefaultMutableTreeNode attrNode = new DefaultMutableTreeNode(attributes[a]);
							
				for(int k=0; k<fsc.getFuzzySets()[c][a].length; k++)
				{
					DefaultMutableTreeNode fuzzysetNode = new DefaultMutableTreeNode(fsc.getFuzzySets()[c][a][k].toString());
					fuzzysetNode.setUserObject(fsc.getFuzzySets()[c][a][k]);
					
					attrNode.add(fuzzysetNode);
				}				
				classNode.add(attrNode);
			}
			templatePackNode.add(classNode);
		}		
		return templatePackNode;
	}

}
