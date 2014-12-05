package gui_pt.gui;

import gui_pt.accessLayer.loader.PTLoader;
import gui_pt.accessLayer.util.AccessPT;
import gui_pt.fse.CFS_IdentityWraper;
import gui_pt.fse.FuzzySetProject;
import gui_pt.fse.helper.AttributeWrapper;
import gui_pt.fse.helper.ClassWrapper;
import gui_pt.fse.util.FSE_Util;
import gui_pt.fse.util.FuzzySetPack;
import gui_pt.io.InstancesLoader;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.DefaultTreeCheckingModel;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingEvent;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import weka.classifiers.trees.pt.FuzzySet;
import weka.core.Attribute;
import weka.core.Instances;

public class PTCreatorDialog extends JDialog implements TreeCheckingListener, ActionListener, DocumentListener{
	
	JPanel mainPanel; //CardLayout
	
	JPanel cardPanel_1; //BorderLayout
	
		JPanel centerPanel_1;
		JPanel radioButtonPanel; //GridLayout(2,1)
		JRadioButton radioButton1;
		JRadioButton radioButton2;
		ButtonGroup buttonGroup1;			
		JPanel browsePanel; //FlowLayout
		JTextField browseTextField;
		JButton browseButton;
		
		JPanel southPanel_1;
		JPanel nextCancelPanel; //FlowLayout
		JButton nextButton;
		JButton cancelButton_1;
				
	JPanel cardPanel_2;
		
		JPanel northPanel; //BorderLayout
		JTextPane captionPane;
		
		JPanel centerPanel; //BorderLayout
		JPanel fuzzySelectionPanel; //BorderLayout
		JSplitPane splitPane1;
		JPanel treePanel; //FlowLayout
		JTabbedPane treeTabbed;
		ArrayList<CheckboxTree> cbtInactive = new ArrayList<CheckboxTree>();
		ArrayList<CheckboxTree> cbtFieldActive = new ArrayList<CheckboxTree>();
		JTabbedPane tabbedPane;
		JPanel infoPanel;
		JPanel requirementPanel;
		JLabel[] classLabel;
		JLabel[] iconLabel;
		
		JPanel southPanel; //Gridlayout(2,1)
		JPanel namePanel; //FlowLayout.LEFT
		JLabel nameLabel;
		JTextField nameTextField;
		JPanel oPanel;
		JLabel oLabel;
		JTextField oTextField;
		JPanel aPanel;
		JLabel aLabel;
		JComboBox<String> aComboBox;
		JPanel okCancelPanel; //FlowLayout.LEFT
		JButton okButton;
		JButton cancelButton;
		
	private MainWindow mainW;
	private Instances data;
	private DefaultMutableTreeNode[] proNodes;
	
	private boolean[] classesFullFilled;
	private boolean isCorrectFile;
	private int checkedIndex = -1;
	private int lastUpdatedIndex = -1;
	
	public PTCreatorDialog(MainWindow mainW){
		super(mainW);
		
		this.mainW = mainW;
		
		radioButton1 = new JRadioButton("Use fuzzyset-project instances");
		radioButton1.setActionCommand("radio1");
		radioButton1.addActionListener(this);
		
		radioButton2 = new JRadioButton("Load new Instances");
		radioButton2.setActionCommand("radio2");
		radioButton2.addActionListener(this);
		
		buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(radioButton1);
		buttonGroup1.add(radioButton2);
		
		radioButtonPanel = new JPanel();
		radioButtonPanel.setLayout(new GridLayout(2,1));
		radioButtonPanel.add(radioButton1);
		radioButtonPanel.add(radioButton2);
		
		browseTextField = new JTextField(30);
		browseTextField.getDocument().addDocumentListener(this);
		
		browseButton = new JButton("Browse");
		browseButton.setActionCommand("browse");
		browseButton.addActionListener(this);
		
		browsePanel = new JPanel();
		browsePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		browsePanel.setBorder(new EtchedBorder());
		browsePanel.add(browseTextField);
		browsePanel.add(browseButton);
		
		this.enableBrowsePanel(false);
		
		centerPanel_1 = new JPanel();
		centerPanel_1.setLayout(new BorderLayout());
		centerPanel_1.add(radioButtonPanel, BorderLayout.CENTER);
		centerPanel_1.add(browsePanel, BorderLayout.SOUTH);
		
		cancelButton_1 = new JButton("Cancel");
		cancelButton_1.setActionCommand("cancel");
		cancelButton_1.addActionListener(this);
		
		nextButton = new JButton("Next");
		nextButton.setActionCommand("next");
		nextButton.addActionListener(this);
		nextButton.setEnabled(false);
		
		nextCancelPanel = new JPanel();
		nextCancelPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		nextCancelPanel.add(cancelButton_1);
		nextCancelPanel.add(nextButton);
		
		southPanel_1 = new JPanel();
		southPanel_1.setLayout(new BorderLayout());
		southPanel_1.add(nextCancelPanel, BorderLayout.CENTER);
		
		cardPanel_1 = new JPanel();
		cardPanel_1.setLayout(new BorderLayout());
		cardPanel_1.add(centerPanel_1, BorderLayout.CENTER);
		cardPanel_1.add(southPanel_1, BorderLayout.SOUTH);
		
		proNodes = new DefaultMutableTreeNode[this.mainW.getStartW().getProjectTrees().size()];
		
		int i=0;
		for(DefaultMutableTreeNode dmtNode: this.mainW.getStartW().getProjectTrees())
		{
			proNodes[i] = dmtNode;
			i++;
		}
		
//		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Fuzzyset-Packs");
		
		treeTabbed = new JTabbedPane();
			
		treePanel = new JPanel();
		treePanel.setLayout(new BorderLayout());
		treePanel.setBackground(Color.white);
		treePanel.add(treeTabbed, BorderLayout.CENTER);		
				
//		cbtFieldActive = new CheckboxTree[proNodes.length];
//		for(int i=0; i<proNodes.length; i++)
//		{
//			cbtFieldActive[i] = new CheckboxTree(proNodes[i]);
//			cbtFieldActive[i].addTreeCheckingListener(this);
//			treePanel.add(cbtFieldActive[i]);
//		}
		
		infoPanel = new JPanel();
		
		//requirementPanel ---------------------------------------
				
		requirementPanel = new JPanel();
		
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.addTab("requirement", requirementPanel);
		tabbedPane.addTab("info", infoPanel);
		
				
		splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane1.setDividerLocation(200);
		splitPane1.setLeftComponent(new JScrollPane(treePanel));
		splitPane1.setRightComponent(new JScrollPane(tabbedPane));
		
		fuzzySelectionPanel = new JPanel();
		fuzzySelectionPanel.setLayout(new BorderLayout());
		fuzzySelectionPanel.setPreferredSize(new Dimension(800,400));
		fuzzySelectionPanel.add(splitPane1, BorderLayout.CENTER);
					
		centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());	
		centerPanel.add(fuzzySelectionPanel, BorderLayout.CENTER);
			
		nameLabel = new JLabel("Name: ");
		
		nameTextField = new JTextField(30);
		
		oLabel = new JLabel("Options: ");
		
		oTextField = new JTextField(30);
		oTextField.setText("-C  5 -E 0.0025");
		
		aLabel = new JLabel("Algortihm");
		
		String[] algos = {"PTBU","PTTD"};	
		aComboBox = new JComboBox<String>(algos);
		
		aPanel = new JPanel();
		aPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		aPanel.add(aLabel);
		aPanel.add(aComboBox);
		
		oPanel = new JPanel();
		oPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		oPanel.add(oLabel);
		oPanel.add(oTextField);
		
		namePanel = new JPanel();
		namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		namePanel.add(nameLabel);
		namePanel.add(nameTextField);
		
		okButton = new JButton("Ok");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
//		okButton.setEnabled(false);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		
		okCancelPanel = new JPanel();
		okCancelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		okCancelPanel.add(okButton);
		okCancelPanel.add(cancelButton);
		
		southPanel = new JPanel();
		southPanel.setLayout(new GridLayout(4,1));
		southPanel.add(namePanel);
		southPanel.add(oPanel);
		southPanel.add(aPanel);
		southPanel.add(okCancelPanel);
		
		cardPanel_2 = new JPanel();
		cardPanel_2.setLayout(new BorderLayout());
		cardPanel_2.add(centerPanel, BorderLayout.CENTER);
		cardPanel_2.add(southPanel, BorderLayout.SOUTH);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new CardLayout());
		mainPanel.add(cardPanel_1, ""+1);
		mainPanel.add(cardPanel_2, ""+2);
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		
		cp.add(mainPanel, BorderLayout.CENTER);
		
		//for fast working
		this.radioButton1.doClick();
		this.nextButton.doClick();
		
		this.setSize(600,600);
		
		this.setLocation(
				(Toolkit.getDefaultToolkit().getScreenSize().width-getSize().width)/2,
				(Toolkit.getDefaultToolkit().getScreenSize().height-getSize().height)/2);
		this.setVisible(true);
	}
	
	//####################################################################################
	//Methods
	//####################################################################################
	
	/**
	 * 
	 */
	private void enableBrowsePanel(boolean enable){
		
		this.browseButton.setEnabled(enable);
		this.browseTextField.setEnabled(enable);
	}
	
	/**
	 * 
	 * @param enable
	 */
//	private void enableInstanceLessFuzzyPacks(boolean enable)
//	{
//		for(int i=0; i<this.cbtFieldActive.size(); i++)
//		{
//			if(((FuzzySetProject)
//					((DefaultMutableTreeNode)
//							cbtFieldActive.get(i).getModel().getRoot()).getUserObject()).getInstances() == null)
//			{
//				cbtFieldActive.get(i).setEnabled(enable);
//			}
//		}
//	}
	
	private void buildCheckTreeTabs(boolean option)
	{
		JPanel tabPanel_1 = new JPanel();
		tabPanel_1.setBackground(Color.white);
		JPanel tabPanel_2 = new JPanel();
		tabPanel_2.setBackground(Color.white);
		
		treeTabbed.removeAll();
		
		for(int i=0; i<proNodes.length; i++)
		{
			CheckboxTree cbt = new CheckboxTree(proNodes[i]);
			if(option)
			{					
				final Attribute classAttr = ((FuzzySetProject)
						((DefaultMutableTreeNode)
								cbt.getModel()
					               .getRoot())
					               .getUserObject())
					               .getClasses()[0]
					               .getClassAttribute();
				
				if(classAttr != null
						&& classAttr.equals(data.classAttribute()))
				{
					cbt.addTreeCheckingListener(this);
					cbtFieldActive.add(cbt);						
				}
				else
				{
					cbt.setEnabled(false);
					cbtInactive.add(cbt);
				}					
			}
			else
			{
				if(((FuzzySetProject)
						((DefaultMutableTreeNode)
								cbt.getModel()
								.getRoot())
								.getUserObject())
								.getInstances() != null)
				{
					cbt.addTreeCheckingListener(this);
					cbtFieldActive.add(cbt);						
				}
				else
				{
					cbt.setEnabled(false);
					cbtInactive.add(cbt);
				}
			}				
		}
		
		tabPanel_1.setLayout(new GridLayout(1,cbtFieldActive.size()));
		tabPanel_2.setLayout(new GridLayout(1,cbtInactive.size()));
		
		for(int i=0; i< cbtFieldActive.size(); i++)
		{
			tabPanel_1.add(cbtFieldActive.get(i));
		}
		for(int i=0; i< cbtInactive.size(); i++)
		{
			tabPanel_2.add(cbtInactive.get(i));
		}
		
		treeTabbed.addTab("active", tabPanel_1);
		treeTabbed.addTab("inactive", tabPanel_2);
		
	}
	
	/**
	 * 
	 */
	private void buildClassAttributCheckMonitor()
	{
		if(data != null)
		{
			classLabel = new JLabel[data.numClasses()];
			iconLabel = new JLabel[data.numClasses()];
			classesFullFilled = new boolean[data.numClasses()];
			
			JPanel[] helpPanel = new JPanel[data.numClasses()];
			
			JPanel classLabelPanel = new JPanel();
			classLabelPanel.setLayout(new GridLayout(data.numClasses(), 1));
			
			for(int c=0; c < classLabel.length; c++)
			{
				classLabel[c] = new JLabel(data.classAttribute().value(c));
				iconLabel[c] = new JLabel(new ImageIcon("res/icons/redCross.png"));
				
				classesFullFilled[c] = false;
				
				helpPanel[c] = new JPanel();
				helpPanel[c].setLayout(new FlowLayout(FlowLayout.LEFT));
				helpPanel[c].add(classLabel[c]);
				helpPanel[c].add(iconLabel[c]);
				
				classLabelPanel.add(helpPanel[c]);
			}		
			
			this.requirementPanel.removeAll();
			this.requirementPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			this.requirementPanel.add(classLabelPanel);
			this.requirementPanel.validate();
			this.requirementPanel.repaint();

		}
		else
		{
			this.requirementPanel.removeAll();
			this.requirementPanel.validate();
			this.requirementPanel.repaint();

		}
	}
	
	private void updateClassCheckMonitor()
	{
		if(checkedIndex == lastUpdatedIndex)
		{
			this.buildClassAttributCheckMonitor();
		}
		
		lastUpdatedIndex = checkedIndex;
		
		//first set all unfullfilled
		for(int i=0; i<classesFullFilled.length; i++)
		{
			classesFullFilled[i] = false;
		}		
		if(data != null)
		{	
			TreePath[] paths = cbtFieldActive.get(this.checkedIndex).getCheckingPaths();
			
			for(int p = 0; p < paths.length; p++)
			{
				if(((DefaultMutableTreeNode)paths[p].getLastPathComponent()).getUserObject()
						instanceof CFS_IdentityWraper
						||((DefaultMutableTreeNode)paths[p].getLastPathComponent()).getUserObject()
						instanceof FuzzySet)
				{
					if(((DefaultMutableTreeNode)paths[p].getPathComponent(1)).getUserObject()
						instanceof ClassWrapper)
					{
						final ClassWrapper class_W = (ClassWrapper)
							((DefaultMutableTreeNode)
									paths[p].getPathComponent(1)).getUserObject();					
						for(int c=0; c < this.data.numClasses(); c++)
						{
							if(data.classAttribute().isNumeric()
									&& !data.attribute(data.numAttributes()-1).name()
									.equals(((AttributeWrapper)
											((DefaultMutableTreeNode)
													paths[p]
													      .getPathComponent(2))
													      .getUserObject())
													      .getAttribute().name()))
							{
								classesFullFilled[c] = true;
							}
							else if(data.classAttribute().value(c).equals(class_W.getClassName()))
							{
								classesFullFilled[c] = true;
								iconLabel[c].setIcon(new ImageIcon("res/icons/greenCheck.png"));
							}
						}
					}
					else if(paths[p].getPathCount() >= 3)
					{
						final ClassWrapper class_W = (ClassWrapper)
						((DefaultMutableTreeNode)
								paths[p].getPathComponent(2)).getUserObject();
						
						for(int c=0; c < this.data.numClasses(); c++)
						{
							if(data.classAttribute().isNumeric()
									&& !data.attribute(data.numAttributes()-1).name()
									.equals(((AttributeWrapper)
											((DefaultMutableTreeNode)
													paths[p]
													      .getPathComponent(3))
													      .getUserObject())
													      .getAttribute().name()))
							{
								classesFullFilled[c] = true;
							}
							else if(data.classAttribute().value(c).equals(class_W.getClassName()))
							{
								classesFullFilled[c] = true;
								iconLabel[c].setIcon(new ImageIcon("res/icons/greenCheck.png"));
							}
						}
					}
				}
			}
		}
		
		boolean isOk = true;
		for(int i=0; i< iconLabel.length; i++)
		{
			if(classesFullFilled[i])
			{
				iconLabel[i].setIcon(new ImageIcon("res/icons/greenCheck.png"));
			}
			else 
			{
				iconLabel[i].setIcon(new ImageIcon("res/icons/redCross.png"));
				isOk = false;
			}
		}
		if(isOk)
		{
			this.okButton.setEnabled(true);
		}
		else
		{
			this.okButton.setEnabled(false);
		}
	}
	
//	/**
//	 * 
//	 * @param cbt
//	 * @param data
//	 * @return
//	 */
//	private FuzzySet[][][] extractFuzzySets(CheckboxTree cbt, Instances data){
//		
//		FuzzySet[][][] fuzzySet = new FuzzySet[data.numClasses()][data.numAttributes()][];
//		int[][] fuzzySetCounter = new int[data.numClasses()][data.numAttributes()];
//		
//		LinkedList<FuzzySetWrapper> ll_fs_W = new LinkedList<FuzzySetWrapper>();
//		
//		TreePath[] checkedPaths = cbt.getCheckingPaths();
//		
//		//extract root
////		DefaultMutableTreeNode root = (DefaultMutableTreeNode)
////											cbt.getModel().getRoot();
//			
//		for(int i=0; i<checkedPaths.length; i++)
//		{
//			if(((DefaultMutableTreeNode)checkedPaths[i].getLastPathComponent()).getUserObject()
//					instanceof CFS_IdentityWraper)
//			{
////				CPLFS cplFuzzySet = new CPLFS();
//
//				//confert cp to double[2]
//				ArrayList<double[]> pointSet = new ArrayList<double[]>();
//				
//				TreeSet<CustomPoint> cpSet = new TreeSet<CustomPoint>();
//				cpSet = ((CFS_IdentityWraper)
//						((DefaultMutableTreeNode)
//						checkedPaths[i]
//						.getLastPathComponent())
//						.getUserObject())
//						.getM_Cfs()
//						.getCpTreeSet();
//				
//				for(CustomPoint cp: cpSet)
//				{
//					double[] point = new double[2];
//					point[0] = cp.getX();
//					point[1] = cp.getY();
//					pointSet.add(point);
//				}
//				
//				CPLFS cplFuzzySet = new FuzzySet.CPLFS(pointSet);
//				
//				ll_fs_W.add(
//					this.wrapFuzzySet(cplFuzzySet, checkedPaths[i], data, fuzzySetCounter));
//			}	
//			else if(((DefaultMutableTreeNode)checkedPaths[i].getLastPathComponent()).getUserObject()
//							instanceof FuzzySet)
//			{
//				FuzzySet fs = (FuzzySet)
//							((DefaultMutableTreeNode)checkedPaths[i]
//							     .getLastPathComponent()).getUserObject();			
//				ll_fs_W.add(
//						this.wrapFuzzySet(fs, checkedPaths[i], data, fuzzySetCounter));
//			}
//		}
//		
//		for(int c=0; c < fuzzySet.length; c++)
//		{
//			for(int a=0; a < fuzzySet[c].length; a++)
//			{
//				fuzzySet[c][a] = new FuzzySet[fuzzySetCounter[c][a]];
//			}
//		}	
//		for(FuzzySetWrapper fs_W: ll_fs_W)
//		{
//			fuzzySet[fs_W.classIndex][fs_W.attrIndex][fs_W.fuzzySetIndex] = fs_W.fuzzySet;
//		}
//		
//		return fuzzySet;
//	}
//	/**
//	 * 
//	 * @param fuzzySet
//	 * @param treePath
//	 * @param data
//	 * @param fsCounter
//	 * @return
//	 */
//	private FuzzySetWrapper wrapFuzzySet(FuzzySet fuzzySet, TreePath treePath, Instances data, int[][] fsCounter){
//		
//		int pathCount = treePath.getPathCount();
//		Object[] paths = treePath.getPath();
//		
//		//extract Attribute
//		AttributeWrapper attrW = (AttributeWrapper)
//					((DefaultMutableTreeNode)
//							paths[pathCount-2]).getUserObject();
//		Attribute attr = attrW.getAttribute();
//		//get attrIndex
//		int attrIndex = -1;
//		for(int j=0; j<data.numAttributes(); j++)
//		{
//			if(data.attribute(j).equals(attr))
//			{
//				attrIndex = j;
//				break;
//			}
//		}
//		
//		//extract Class
//		ClassWrapper classW = (ClassWrapper)
//					((DefaultMutableTreeNode)
//							paths[pathCount-3]).getUserObject();
//		//get classIndex
//		int classIndex = -1;
//		
//		if(data.classAttribute().isNominal())
//		{
//			for(int j=0; j< data.numClasses(); j++)
//			{
//				if(data.classAttribute().value(j).equals(classW.getClassName()))
//				{
//					classIndex = j;
//				}
//			}
//		}
//		else
//		{
//			classIndex = 0;
//		}
//
//		//count fuzzySet
//		
//		if(classIndex >= 0 && attrIndex >= 0)
//		{
//			fsCounter[classIndex][attrIndex]++;
//			FuzzySetWrapper cplfs_W = new FuzzySetWrapper(fuzzySet
//					, attrIndex
//					, classIndex
//					, fsCounter[classIndex][attrIndex]-1);
//			return cplfs_W;
//		}
//		return null;		
//	}
//	
//	private class FuzzySetWrapper {
//		
//		FuzzySet fuzzySet;
//		int attrIndex;
//		int classIndex;
//		int fuzzySetIndex;
//		
//		public FuzzySetWrapper(FuzzySet fuzzySet, int attrIndex, int classIndex, int fuzzySetIndex){
//			
//			this.fuzzySet = fuzzySet;
//			this.attrIndex = attrIndex;
//			this.classIndex = classIndex;
//			this.fuzzySetIndex = fuzzySetIndex;
//		}		
//	};
	
	//####################################################################################
	//TreeCheckingListener
	//####################################################################################

	@Override
	public void valueChanged(TreeCheckingEvent arg0) {

		if(arg0.isCheckedPath())
		{
			//get data			
			if(this.radioButton1.isSelected())
			{
				Object[] path = arg0.getPath().getPath();
				DefaultMutableTreeNode root = (DefaultMutableTreeNode)path[0];
				FuzzySetProject fs_Pro = (FuzzySetProject)root.getUserObject();
				
				this.data = fs_Pro.getInstances();
				buildClassAttributCheckMonitor();
			}
			
			//disable all other fuzzypacks
			for(int i=0; i<this.cbtFieldActive.size(); i++)
			{
				if(cbtFieldActive.get(i).getCheckingPaths().length == 0)
				{
					cbtFieldActive.get(i).setEnabled(false);
				}
				else
				{
					checkedIndex = i;
				}
			}		
		}
		else if(((DefaultTreeCheckingModel)arg0.getSource()).getCheckingPaths().length == 0)
		{
			for(int i=0; i<this.cbtFieldActive.size(); i++)
			{
					cbtFieldActive.get(i).setEnabled(true);
			}
			
			if(radioButton1.isSelected())
			{
				this.data = null;
			}			
		}
		
		updateClassCheckMonitor();
	}
	
	//#####################################################################################
	//ActionListener
	//#####################################################################################

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getActionCommand().equals("radio1"))
		{
			this.enableBrowsePanel(false);
			this.nextButton.setEnabled(true);
		}
		else if(arg0.getActionCommand().equals("radio2"))
		{
			this.enableBrowsePanel(true);
			if(this.isCorrectFile)
			{
				this.nextButton.setEnabled(true);
			}
			else
			{
				this.nextButton.setEnabled(false);
			}
			
		}
		else if(arg0.getActionCommand().equals("browse"))
		{
			JFileChooser jfc = new JFileChooser();
			jfc.setFileFilter(new FileNameExtensionFilter(null, "arff"));
			
			int i = jfc.showOpenDialog(mainW);
			
			if(i == JFileChooser.APPROVE_OPTION)
			{
				this.browseTextField.setText(jfc.getSelectedFile().toString());
			}
		}
		else if(arg0.getActionCommand().equals("next"))
		{
			if(this.radioButton2.isSelected())
			{
				this.data = InstancesLoader.loadInstances(this.browseTextField.getText());
				buildClassAttributCheckMonitor();
				this.buildCheckTreeTabs(true);
			}
			else
			{
				this.buildCheckTreeTabs(false);
			}
			
			((CardLayout)this.mainPanel.getLayout()).next(this.mainPanel);		
		}
		else if(arg0.getActionCommand().equals("ok"))
		{
			FuzzySetPack fsp = FSE_Util.extractFuzzySets(cbtFieldActive.get(checkedIndex), data);
			
			AccessPT accPT = null;
			String name =  this.nameTextField.getText();
			
			String[] options = this.oTextField.getText().split(" ");
			
			PTLoader ptl = new PTLoader();
			
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));			
			accPT = ptl.create(data, fsp, options, aComboBox.getSelectedIndex());

			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			this.mainW.addAccPT(accPT, false, name);
			
			this.mainW.validate();
			
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

		//check file to load

		File file = new File(this.browseTextField.getText());
		
		String[] fileEnding = this.browseTextField.getText().split("\\.");
		
		if(fileEnding.length > 0
				&& fileEnding[fileEnding.length-1].equals("arff")
				&& file.isFile())
		{
			isCorrectFile = true;
		}
		else
		{
			isCorrectFile = false;
		}
		
		//check Project Name
				
		if(isCorrectFile && this.radioButton2.isSelected())
		{
			this.nextButton.setEnabled(true);			
		}
		else
		{
			this.nextButton.setEnabled(false);
		}		
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {

		insertUpdate(arg0);		
	}
}
