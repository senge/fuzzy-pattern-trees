package gui_pt.fse;

import gui_pt.fse.helper.AttributeWrapper;
import gui_pt.fse.helper.ClassWrapper;
import gui_pt.guiUtil.TextHandler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import weka.classifiers.trees.pt.FuzzySet;
import weka.classifiers.trees.pt.FuzzySet.INT;
import weka.classifiers.trees.pt.FuzzySet.LO;
import weka.classifiers.trees.pt.FuzzySet.NTRI;
import weka.classifiers.trees.pt.FuzzySet.RO;
import weka.classifiers.trees.pt.FuzzySet.TRA;
import weka.classifiers.trees.pt.FuzzySet.TRI;
import weka.core.Attribute;

public class FSE_CreateDialog extends JDialog implements ActionListener, DocumentListener, ChangeListener{
	
	public static final int OPTION_CREATE_NEW = 0;
	public static final int OPTION_IMPORT_TEMPLATE = 1;
	
	JPanel mainPanel;
	JPanel northPanel;
	JPanel centerPanel;
	
	JTextPane captionPane;
	
	JPanel textFieldPanel;
	JTextField nameTextField;
	
	JPanel buttonPanel;
	JButton okButton;
	JButton cancelButton;
	
	JPanel selAttrButtonPanel;
//	JRadioButton[] selectAttrButtons;
	Attribute[] attributes;
	ButtonGroup buttonGroup_1;
	
	//Custom Attribute
	JPanel customButtonPanel;
	JSpinner spinner1;
	JSpinner spinner2;
	
	FSE_Frame fse_Frame;
	TreePath selectedPath;
	DefaultMutableTreeNode attrNode;
	DefaultMutableTreeNode proNode;
	DefaultMutableTreeNode fuzzyNode;
	DefaultMutableTreeNode classNodeTemp;
	DefaultMutableTreeNode attrNodeTemp;
	AttributeWrapper attrWrapperTemp;
	
	//###########################################################################
	//CONSTRUCTOR
	//###########################################################################
	
	public FSE_CreateDialog(FSE_Frame fse_Frame, int option){		
		super(fse_Frame);
		
		this.fse_Frame = fse_Frame;
		
		captionPane = new JTextPane();
		captionPane.setEditable(false);
		
		northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		northPanel.setBorder(new EtchedBorder());
		northPanel.add(captionPane, BorderLayout.CENTER);
			
		this.setCaptionText("Enter a fuzzyset name", 11, Color.black);
		
		okButton = new JButton("ok");
		okButton.setEnabled(false);
		
		okButton.addActionListener(this);
		
		if(option == OPTION_IMPORT_TEMPLATE)
		{
			this.searchImportantNodes();
			okButton.setActionCommand("okTemp");
		}
		else if(option == OPTION_CREATE_NEW)
		{
			selectedPath = fse_Frame.getFuzzySetTree().getTree().getSelectionPath();
			attrNode = ((DefaultMutableTreeNode)
							selectedPath.getLastPathComponent());
			proNode = (DefaultMutableTreeNode)
						((DefaultMutableTreeNode)
								((DefaultMutableTreeNode)
						selectedPath.getLastPathComponent()).getParent()).getParent();
			okButton.setActionCommand("ok");
		}
		
//		buttonGroup_1 = new ButtonGroup();
		
//		TreeModel treeModel = fse_Frame.getAttributeTree().getTreeModel();
//		int numAttr = fse_Frame.getAttributeTree().getTreeModel().getChildCount(
//						fse_Frame.getAttributeTree().getRoot());
//		selectAttrButtons = new JRadioButton[numAttr+1];
//		attributes = new Attribute[numAttr];
		
//		selAttrButtonPanel = new JPanel();
//		selAttrButtonPanel.setLayout(new GridLayout(selectAttrButtons.length,1));
//		
//		for(int i=0; i<numAttr; i++)
//		{
//			attributes[i] = ((Attribute)
//				((DefaultMutableTreeNode)treeModel.getChild(
//					fse_Frame.getAttributeTree().getRoot()
//					, i)).getUserObject());
//			selectAttrButtons[i] = new JRadioButton(attributes[i].name());
//			buttonGroup_1.add(selectAttrButtons[i]);
//			selAttrButtonPanel.add(selectAttrButtons[i]);
//		}
//		
//		selectAttrButtons[selectAttrButtons.length-1] = new JRadioButton("Custom");
//		selectAttrButtons[selectAttrButtons.length-1].setSelected(true);
//		
//		spinner1 = new JSpinner(new SpinnerNumberModel(0,Integer.MIN_VALUE,Integer.MAX_VALUE,0.001));
//		spinner1.setPreferredSize(new Dimension(60,20));
//		spinner1.addChangeListener(this);
//		spinner1.setName("spinner1");
//		
//		spinner2 = new JSpinner(new SpinnerNumberModel(1,Integer.MIN_VALUE,Integer.MAX_VALUE,0.001));
//		spinner2.setPreferredSize(new Dimension(60,20));
//		spinner2.addChangeListener(this);
//		spinner2.setName("spinner2");
//		
//		customButtonPanel = new JPanel();
//		customButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
//		customButtonPanel.add(selectAttrButtons[selectAttrButtons.length-1]);
//		customButtonPanel.add(spinner1);
//		customButtonPanel.add(spinner2);
//		
//		
//		buttonGroup_1.add(selectAttrButtons[selectAttrButtons.length-1]);
//		selAttrButtonPanel.add(customButtonPanel);
		
		nameTextField = new JTextField(40);
		nameTextField.getDocument().addDocumentListener(this);
		
		textFieldPanel = new JPanel();
		textFieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		textFieldPanel.setBorder(new TitledBorder("Select a working title"));
		textFieldPanel.add(nameTextField);
		
		cancelButton = new JButton("cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		//first buttons must be initialized
				
		centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
//		centerPanel.add(selAttrButtonPanel, BorderLayout.NORTH);
		centerPanel.add(textFieldPanel, BorderLayout.CENTER);
		centerPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		
		Container cp = this.getContentPane();
		
		cp.setLayout(new BorderLayout());
		cp.add(mainPanel, BorderLayout.CENTER);
		
//		this.setSize(500,230);
		this.pack();
		this.setLocation(
				(Toolkit.getDefaultToolkit().getScreenSize().width-getSize().width)/2,
				(Toolkit.getDefaultToolkit().getScreenSize().height-getSize().height)/2);
		this.setModal(true);
		this.setVisible(true);
	}
	
	//####################################################################################
	//METHODS
	//####################################################################################
	
	/**
	 * 
	 * @param nameInfo
	 * @param instancesInfo
	 */
	private void setCaptionText(String nameInfo, int textSize, Color textColor)
	{
		this.captionPane.setText("");
		
		SimpleAttributeSet saSet = new SimpleAttributeSet();
		saSet = (SimpleAttributeSet)TextHandler.setTextStyle(new SimpleAttributeSet(), Color.black, 17);
		try {
			TextHandler.appendText(this.captionPane,
					"Create a FuzzySet \n", saSet);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		saSet = (SimpleAttributeSet)TextHandler.setTextStyle(new SimpleAttributeSet(), textColor, textSize);
		try {
			TextHandler.appendText(this.captionPane,
					"\n" + nameInfo
					, saSet);
			
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void searchImportantNodes(){
		
		//first fetch Tree Information (Nodes needed, pathes etc.)
		this.selectedPath = fse_Frame.getFuzzySetTree().getTree().getSelectionPath();
		this.fuzzyNode = ((DefaultMutableTreeNode)
				selectedPath.getLastPathComponent());
		
		this.proNode = (DefaultMutableTreeNode)selectedPath.getPathComponent(1);
		
		this.classNodeTemp = (DefaultMutableTreeNode)selectedPath.getPathComponent(3);
		ClassWrapper classWrapperTemp = (ClassWrapper)classNodeTemp.getUserObject();
		
		this.attrNodeTemp = (DefaultMutableTreeNode)selectedPath.getPathComponent(4);
		this.attrWrapperTemp = (AttributeWrapper)attrNodeTemp.getUserObject();
		
		//search new parent node
		for(int c = 0; c < proNode.getChildCount(); c++)
		{
			DefaultMutableTreeNode classNode = (DefaultMutableTreeNode)proNode.getChildAt(c);
			if(classNode.getUserObject()
					instanceof ClassWrapper)
			{
				if(((ClassWrapper)classNode.getUserObject()).getClassName()
						.equals(classWrapperTemp.getClassName()))
				{
					for(int a = 0; a < classNode.getChildCount(); a++)
					{
						attrNode = (DefaultMutableTreeNode)classNode.getChildAt(a);
						if(attrNode.getUserObject()
								instanceof AttributeWrapper)
						{
							if(((AttributeWrapper)attrNode.getUserObject()).getAttribute().name()
									.equals(attrWrapperTemp.getAttribute().name()))
							{		
								//parent node found.
								break;
							}
						}
					}
					break;
				}
			}
		}
	}
	
	//############################################################################
	//ActionListener
	//############################################################################

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getActionCommand().equals("ok"))
		{	
			Attribute attr = null;
			double min = 0;
			double max = 1;
			
			AttributeWrapper attrW = ((AttributeWrapper)((DefaultMutableTreeNode)this.selectedPath.getLastPathComponent())
					.getUserObject());
			
			if( attrW.getAttribute() != null)
			{
				attr = attrW.getAttribute();
				min = attrW.getMin();
				max = attrW.getMax();
			}
//			else
//			{						
//				if(selectAttrButtons[selectAttrButtons.length-1].isSelected())
//				{
//					attr = new Attribute("Custom_Attribute");
//					min = (Double)this.spinner1.getValue();
//					max = (Double)this.spinner2.getValue();
//				}
//				else
//				{
//					for(int i=0; i<attributes.length-1; i++)
//					{
//						if(selectAttrButtons[i].isSelected())
//						{
//							attr = attributes[i];
//							
//							min = ((DoubleNode)
//									((DefaultMutableTreeNode)
//											((DefaultMutableTreeNode)
//								fse_Frame
//								.getAttributeTree()
//									.getTreeModel()
//										.getChild(fse_Frame.getAttributeTree().getRoot(), i))
//										.getChildAt(0)).getUserObject()).getValue();
//							
//							max = ((DoubleNode)
//									((DefaultMutableTreeNode)
//											((DefaultMutableTreeNode)
//								fse_Frame
//								.getAttributeTree()
//									.getTreeModel()
//										.getChild(fse_Frame.getAttributeTree().getRoot(), i))
//										.getChildAt(1)).getUserObject()).getValue();
//						}
//					}			
//				}
//			}
//				
			FuzzySetProject fs_Pro = ((FuzzySetProject)
										proNode.getUserObject());
			
			CustomFuzzySet customFS = new CustomFuzzySet(min, max);
						                                          
			CFS_IdentityWraper cfs_IW = new CFS_IdentityWraper(customFS
							,attr);
			
			cfs_IW.setWorkName(this.nameTextField.getText());
			
			
//			fs_Pro.addCFS(cfs_IW);
			
//			//build Histogramms
//			if(!attr.name().equals("Custom_Attribute"))
//			{
//				Histogramm[] hisPerClass = new Histogramm[fse_Frame.getWorkInstances().numClasses()];
//				
//				for(int i=0; i<hisPerClass.length; i++)
//				{
//					hisPerClass[i] = new Histogramm(100);
//					hisPerClass[i].fill(fse_Frame.getWorkInstances()
//							, attr.index()
//							, i);
//				}
			if(fs_Pro.getHistogramms() != null)
			{	
				cfs_IW.setHisPerClass(fs_Pro.getHistogramms()[attr.index()]);
			}
//			}
			
			//open FuzzySet Tab
			fse_Frame.openFuzzySet(cfs_IW, fse_Frame.getFuzzySetTree().getTreeModel().getPathToRoot(attrNode));
			//store FuzzySet
			fse_Frame.storeFocusedCFS();
							
			//Add to Tree
			DefaultMutableTreeNode fsNode = new DefaultMutableTreeNode(cfs_IW.getWorkName());
			fsNode.setUserObject(cfs_IW);
			
			fse_Frame.getFuzzySetTree().addChildNode(fsNode, attrNode);
			
			this.dispose();
		}
		else if(arg0.getActionCommand().equals("okTemp"))
		{
			
			//extract FuzzySet and Wrap
			FuzzySet fs = (FuzzySet)fuzzyNode.getUserObject();
			
			//new CFS
			
			double min = attrWrapperTemp.getMin();
			double max = attrWrapperTemp.getMax();
			CustomFuzzySet cfs = new CustomFuzzySet(min, max);
			
			if(fs instanceof RO)
			{
				CustomPoint cp1 = new CustomPoint(((RO)fs).getA(),0);
				cp1.setID(0);
				
				CustomPoint cp2 = new CustomPoint(((RO)fs).getB(),1);
				cp2.setID(1);
				
				CustomPoint cp3 = new CustomPoint(max, 1);
				cp3.setID(2);
				
				cfs.getCpTreeSet().add(cp1);
				cfs.getCpTreeSet().add(cp2);
				cfs.getCpTreeSet().add(cp3);
			}
			else if(fs instanceof LO)
			{
				CustomPoint cp1 = new CustomPoint(min, 1);
				cp1.setID(0);
				
				CustomPoint cp2 = new CustomPoint(((LO)fs).getA(),1);
				cp2.setID(1);
				
				CustomPoint cp3 = new CustomPoint(((LO)fs).getB(),0);
				cp3.setID(2);
				
				
				cfs.getCpTreeSet().add(cp1);
				cfs.getCpTreeSet().add(cp2);
				cfs.getCpTreeSet().add(cp3);
			}
			else if(fs instanceof TRI)
			{
				CustomPoint cp1 = new CustomPoint(((TRI)fs).getA(), 0);
				cp1.setID(0);
				
				CustomPoint cp2 = new CustomPoint(((TRI)fs).getB(),1);
				cp2.setID(1);
				
				CustomPoint cp3 = new CustomPoint(((TRI)fs).getC(),0);
				cp3.setID(2);
				
				
				cfs.getCpTreeSet().add(cp1);
				cfs.getCpTreeSet().add(cp2);
				cfs.getCpTreeSet().add(cp3);
			}
			else if(fs instanceof NTRI)
			{
				CustomPoint cp1 = new CustomPoint(min, 1);
				cp1.setID(0);
				
				CustomPoint cp2 = new CustomPoint(((NTRI)fs).getTRI().getA(), 1);
				cp2.setID(1);
				
				CustomPoint cp3 = new CustomPoint(((NTRI)fs).getTRI().getB(),0);
				cp3.setID(2);
				
				CustomPoint cp4 = new CustomPoint(((NTRI)fs).getTRI().getC(),1);
				cp4.setID(3);
				
				CustomPoint cp5 = new CustomPoint(max, 1);
				cp5.setID(4);
								
				cfs.getCpTreeSet().add(cp1);
				cfs.getCpTreeSet().add(cp2);
				cfs.getCpTreeSet().add(cp3);
				cfs.getCpTreeSet().add(cp4);
				cfs.getCpTreeSet().add(cp5);
			}
			else if(fs instanceof TRA)
			{			
				CustomPoint cp1 = new CustomPoint(((TRA)fs).getA(), 0);
				cp1.setID(0);
				
				CustomPoint cp2 = new CustomPoint(((TRA)fs).getB(),1);
				cp2.setID(1);
				
				CustomPoint cp3 = new CustomPoint(((TRA)fs).getC(),1);
				cp3.setID(2);
				
				CustomPoint cp4 = new CustomPoint(((TRA)fs).getD(),0);
				cp4.setID(3);
								
				cfs.getCpTreeSet().add(cp1);
				cfs.getCpTreeSet().add(cp2);
				cfs.getCpTreeSet().add(cp3);
				cfs.getCpTreeSet().add(cp4);
			}
			if(fs instanceof INT)
			{
				CustomPoint cp1 = new CustomPoint(((INT)fs).getA(),1);
				cp1.setID(0);
				
				CustomPoint cp2 = new CustomPoint(((INT)fs).getB(),1);
				cp2.setID(1);
				
				
				cfs.getCpTreeSet().add(cp1);
				cfs.getCpTreeSet().add(cp2);
			}
			
			
			CFS_IdentityWraper cfs_IW = new CFS_IdentityWraper(cfs, attrWrapperTemp.getAttribute());
			cfs_IW.setWorkName(this.nameTextField.getText());
			FuzzySetProject fs_Pro = (FuzzySetProject)proNode.getUserObject();
			
			if(fs_Pro.getHistogramms() != null)
			{
				cfs_IW.setHisPerClass(fs_Pro.getHistogramms()[attrWrapperTemp.getAttribute().index()]);
			}	
			//open FuzzySet Tab
			fse_Frame.openFuzzySet(cfs_IW, fse_Frame.getFuzzySetTree().getTreeModel().getPathToRoot(attrNode));
			//store FuzzySet
			fse_Frame.storeFocusedCFS();
			//Add to Tree
			DefaultMutableTreeNode fsNode = new DefaultMutableTreeNode(cfs_IW.getWorkName());
			fsNode.setUserObject(cfs_IW);
			
			fse_Frame.getFuzzySetTree().addChildNode(fsNode, attrNode);
			
			this.dispose();
		}
		else if(arg0.getActionCommand().equals("cancel"))
		{
			this.dispose();
		}
	}
	
	//###################################################################################
	//DocumentListener
	//###################################################################################

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {

//		TreeModel treeModel = fse_Frame.getFuzzySetTree().getTreeModel();
//		
//		boolean isFalse = false;
//		
//		for(int i=0; i<treeModel.getChildCount(
//				this.attrNode); i++)
//		{
//			if((this.nameTextField.getText()+".fso").equals(
//					treeModel.getChild(
//							this.attrNode, i).toString()))
//			{
//				this.okButton.setEnabled(false);
//				this.setCaptionText("A FuzzySet with this name already exists", 14, Color.red);
//				isFalse = true;
//			}
//		}
//		
//		if(!isFalse)
//		{
			this.okButton.setEnabled(true);
//			this.setCaptionText("Enter a fuzzyset name", 11, Color.black);
//		}
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		
		insertUpdate(arg0);
		
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public JTextField getNameTextField() {
		return nameTextField;
	}

	public void setNameTextField(JTextField nameTextField) {
		this.nameTextField = nameTextField;
	}

}
