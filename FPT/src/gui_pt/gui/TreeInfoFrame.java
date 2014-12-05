package gui_pt.gui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import weka.classifiers.trees.pt.nodes.AbstractNode;
import weka.classifiers.trees.pt.nodes.InternalNode;
import weka.classifiers.trees.pt.nodes.LeafNode;

//import weka.classifiers.trees.PTTD.AbstractNode;


public class TreeInfoFrame extends JFrame{
	
	JTextArea outputArea;
		
	public TreeInfoFrame(AbstractNode root)
	{
		outputArea = new JTextArea(5,5);
		
		//create outputText
		StringBuffer sb = new StringBuffer();
		sb.append(root.toString());
		sb.append("\n");
		if(root instanceof InternalNode)
		{		
			sb.append("Tree-Size: ");
			sb.append(((InternalNode)root).getNumAllChilds());
			sb.append("\n");
			sb.append("Number Inner-Nodes: ");
			sb.append(((InternalNode)root).getNumAllChilds()-root.getNumLeafs());
		}		
		else
		{		
			sb.append("Tree-Size: ");
			sb.append(((LeafNode)root).getNumAllChilds());
			sb.append("\n");
			sb.append("Number Inner-Nodes: ");
			sb.append(((LeafNode)root).getNumAllChilds()-root.getNumLeafs());
		}
		sb.append("\n");	
		sb.append("Number Leafs: ");
		sb.append(root.getNumLeafs());
		sb.append("\n");
		
		outputArea.setText(sb.toString());
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(new JScrollPane(outputArea));
		
		this.setSize(700,200);
		this.setVisible(true);
	}
	
	//GET and SET #######################################################################

	public JTextArea getOutputArea() {
		return outputArea;
	}

	public void setOutputArea(JTextArea outputArea) {
		this.outputArea = outputArea;
	}

}
