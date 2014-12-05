package gui_pt.fse;

import gui_pt.fse.helper.AttributeWrapper;
import gui_pt.fse.helper.ClassWrapper;
import gui_pt.fse.listener.AttributeTreeMouseListener;
import gui_pt.fse.listener.FSE_MenuBar_ActionListener;
import gui_pt.fse.listener.FuzzySetTreeMouseListener;
import gui_pt.fse.listener.fse_KeyEventDispatcher;
import gui_pt.gui.StartWindow;
import gui_pt.guiUtil.TabHeadPanel;
import gui_pt.guiUtil.TabToWindowSwitch;
import gui_pt.io.CFS_Writer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import weka.core.Instances;

public class FSE_Frame extends JFrame{
	
	private StartWindow startW;
	
	JMenuBar menuBar;
	JMenu fileMenu;
	JMenu loadMenu;
	JMenu newMenu;
	JMenuItem loadInstances;
	JMenuItem newProject;
	
	JSplitPane centerSplit;
	
	JTabbedPane leftTabbed;
	JTabbedPane fuzzyEditorTapped;
	
	//Attribute-Tree
	JPanel attributePanel;
	JPanel attributeInfoPanel;
	JLabel minLabel;
	JLabel maxLabel;
	JLabel typeLabel;
	JButton createButton;	
	JPanel attributeListPanel;
	AttributeTree attributeTree;
	
	//FuzzySEt-Tree
	JPanel fuzzySetTreePanel;
	FuzzySetTree fuzzySetTree;	
	
//	FuzzySetEditorPanel fse_panel;
	
	private Instances workInstances;
//	private String instancesName;
	private String projectName;
	
	private TreeMap<Integer, TabToWindowSwitch> ttwsMap = new TreeMap<Integer, TabToWindowSwitch>();
	private int keyCounter = 0;
	
	//+++++++++++++Singelton stuff+++++++++++++++++++++++++++++++++++++++++++
	private static FSE_Frame instance = null;
	
	public static FSE_Frame getInstance(StartWindow startW){
		
		if(instance == null)
		{
			instance = new FSE_Frame(startW);
			return instance;
		}
		
		return instance;
	}
	
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	
	//#####################################################################################
	//CONSTRUCTOR
	//#####################################################################################
	
	private FSE_Frame(StartWindow startW){
		super("Fuzzyset-Editor");
				
		this.startW = startW;
		// MenuBar------------------------------------
		FSE_MenuBar_ActionListener fse_MB_AL = new FSE_MenuBar_ActionListener(this);
		
		loadInstances = new JMenuItem("Instances");
		loadInstances.setActionCommand("loadInstances");
		loadInstances.addActionListener(fse_MB_AL);
		
		loadMenu = new JMenu("Load..");
		loadMenu.add(loadInstances);
		
		newProject = new JMenuItem("New Project");
		newProject.setActionCommand("newproject");
		newProject.addActionListener(fse_MB_AL);		
		
		newMenu = new JMenu("New");
		newMenu.add(newProject);
		
		fileMenu = new JMenu("file");
//		fileMenu.add(loadMenu); TODO not yet needed
		fileMenu.add(newMenu);
		
		menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		
		this.setJMenuBar(menuBar);
		// MenuBar ende ------------------------------
		
		//Right SplitPane
		fuzzyEditorTapped = new JTabbedPane();
		
		//Left SplitPane
		minLabel = new JLabel("min: ");
		maxLabel = new JLabel("max: ");
		typeLabel= new JLabel("type: ");
		createButton = new JButton("create F-Set");
		
		attributeInfoPanel = new JPanel();
		attributeInfoPanel.setLayout(new GridLayout(2,2));
		attributeInfoPanel.add(minLabel);
		attributeInfoPanel.add(maxLabel);
		attributeInfoPanel.add(typeLabel);
		attributeInfoPanel.add(createButton);
		
		attributeTree = new AttributeTree();
		attributeTree.getTree().addMouseListener(new AttributeTreeMouseListener(this));
		
		attributeListPanel = new JPanel();
		attributeListPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		attributeListPanel.setBackground(Color.white);
		attributeListPanel.add(attributeTree.getTree());
		
		attributePanel = new JPanel();
		attributePanel.setLayout(new BorderLayout());
//		attributePanel.add(attributeInfoPanel, BorderLayout.NORTH);
		attributePanel.add(attributeListPanel, BorderLayout.CENTER);
		
		//FuzzySet Tree
		fuzzySetTree = new FuzzySetTree();
		fuzzySetTree.getTree().addMouseListener(new FuzzySetTreeMouseListener(this));
		
		fuzzySetTreePanel = new JPanel();
		fuzzySetTreePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		fuzzySetTreePanel.setBackground(Color.white);
		fuzzySetTreePanel.add(fuzzySetTree.getTree());
		
		//WorkspaceE Crawler		
		for(DefaultMutableTreeNode dmtNode: this.getStartW().getProjectTrees())
		{
			this.getFuzzySetTree().addChildNode(
					dmtNode
				, this.getFuzzySetTree().getRoot());
		}
		
		this.traverProTreeAndOpen();
		
		leftTabbed = new JTabbedPane();
		leftTabbed.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		leftTabbed.addTab("FuzzySets", fuzzySetTreePanel);
//		leftTabbed.addTab("Attributes", attributePanel);	//TODO maybe later, but now not needed
		
		
//		fse_panel = new FuzzySetEditorPanel();
		
		centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		centerSplit.setDividerLocation(200);
//		centerSplit.setRightComponent(fse_panel);
		centerSplit.setRightComponent(fuzzyEditorTapped);
		centerSplit.setLeftComponent(new JScrollPane(leftTabbed));
		
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new fse_KeyEventDispatcher(this));
		
		Container cp = this.getContentPane();		
		cp.setLayout(new BorderLayout());
		
		cp.add(centerSplit, BorderLayout.CENTER);
		
//		this.addWindowListener(new WindowClosingAdapter());
		
		this.setSize(800,600);
		this.setLocation(
				(Toolkit.getDefaultToolkit().getScreenSize().width-getSize().width)/2,
				(Toolkit.getDefaultToolkit().getScreenSize().height-getSize().height)/2);
		this.setVisible(true);
	}
	
	//#################################################################################
	//METHODS
	//#################################################################################
	
	private void traverProTreeAndOpen(){
		
		for(int p=0; p<this.getFuzzySetTree().getRoot().getChildCount(); p++)
		{
			TreeNode projectNode = this.getFuzzySetTree().getRoot().getChildAt(p);
			int classCount = projectNode.getChildCount();
			for(int c=0; c<classCount; c++)
			{
				DefaultMutableTreeNode classNode = (DefaultMutableTreeNode)projectNode.getChildAt(c);
				
				if(classNode.getUserObject() instanceof ClassWrapper)
				{
					int attrCount = classNode.getChildCount();
					for(int a=0; a<attrCount; a++)
					{
						DefaultMutableTreeNode attrNode = (DefaultMutableTreeNode)classNode.getChildAt(a);
						int fuzzySetCount = attrNode.getChildCount();
						for(int f=0; f<fuzzySetCount; f++)
						{
							CFS_IdentityWraper cfs_IW = (CFS_IdentityWraper)
										((DefaultMutableTreeNode)
												((DefaultMutableTreeNode)
														attrNode).getChildAt(f))
														.getUserObject();
							if(cfs_IW.isOpened())
							{
								TreeNode[] nodesInPath = this.getFuzzySetTree().getTreeModel().getPathToRoot(attrNode);
								this.openFuzzySet(cfs_IW, nodesInPath);
							}
						}
					}
				}
			}
		}
		
	}
	/**
	 * 
	 * @param cfs_IW
	 * @param nodesInPath
	 */
	public void openFuzzySet(CFS_IdentityWraper cfs_IW, TreeNode[] nodesInPath){
		
		cfs_IW.setOpened(true);
		
		FuzzySetEditorPanel fse_Panel = new FuzzySetEditorPanel();
				
		fse_Panel.setCfs_Identity(cfs_IW);
		fse_Panel.getCPListP().buildPanelList();
		fse_Panel.setProjectPath(nodesInPath);
						
		FSEDrawPanel fseDrawP = new FSEDrawPanel(fse_Panel);
		fseDrawP.setMin(cfs_IW.getM_Cfs().getMin());
		fseDrawP.setMax(cfs_IW.getM_Cfs().getMax());
		
		fse_Panel.setFseDrawP(fseDrawP);
												
		//tab to window switcher
		TabToWindowSwitch ttws = new TabToWindowSwitch(this.fuzzyEditorTapped, fse_Panel);
		Integer key = new Integer(keyCounter++);
		ttwsMap.put(key, ttws);
		cfs_IW.setKey(key);
		
		//Tab Name And Buttons to flip through cards
		TabHeadPanel tabPanel = new CFS_PTabHeadPanel(ttws
				, cfs_IW.getWorkName()
				, this);
		fse_Panel.setCfs_PTabHeadP((CFS_PTabHeadPanel)tabPanel);
				
		this.getFuzzyEditorTapped().add(fse_Panel);
		this.getFuzzyEditorTapped().setTabComponentAt(
				this.fuzzyEditorTapped.indexOfComponent(fse_Panel), tabPanel);
		this.getFuzzyEditorTapped().setSelectedComponent(fse_Panel);
		this.validate();		
		
		//store open
		fse_Panel.setUnsafedModified(true);
		this.storeFocusedCFS();
	}
	/**
	 * 
	 */
	public void storeFocusedCFS(){
		
		int index = this.fuzzyEditorTapped.getSelectedIndex();
		
		storeCFS(index);
	}
	
	/**
	 * 
	 * @param index
	 */
	public void storeCFS(int index){
		
		FuzzySetEditorPanel fse_P = (FuzzySetEditorPanel)
										this.fuzzyEditorTapped.getComponentAt(index);
		
		if(fse_P.isUnsafedModified())
		{
			TreeNode[] dmtn = fse_P.getProjectPath();
			
			File dir = new File(
					this.getStartW()
					.getStartSettings()
					.getWorkspacePath()+"/workspaceEditor/"
					+((FuzzySetProject)((DefaultMutableTreeNode)dmtn[1])
							.getUserObject()).getM_ProjectName()
					+"/"
					+((ClassWrapper)((DefaultMutableTreeNode)dmtn[2]).getUserObject())
							.getClassName()
					+"/"
					+((AttributeWrapper)((DefaultMutableTreeNode)dmtn[3])
							.getUserObject()).getAttribute().name());		
			CFS_Writer cfs_Writer = new CFS_Writer(dir, fse_P.getCfs_Identity().getWorkName());
			try {
				cfs_Writer.write(fse_P.getCfs_Identity());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			fse_P.setUnsafedModified(false);
		}
	}
	
	//#################################################################################
	//GET and SET
	//#################################################################################

	public Instances getWorkInstances() {
		return workInstances;
	}

	public void setWorkInstances(Instances workInstances) {
		this.workInstances = workInstances;
			
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for(int i=0; i<workInstances.numAttributes(); i++)
		{
			DefaultMutableTreeNode attributeNode = new DefaultMutableTreeNode(
					workInstances.attribute(i).name());
			attributeNode.setUserObject(workInstances.attribute(i));
			this.attributeTree.addChildNode(
					attributeNode, this.attributeTree.getRoot());
			
			min = Double.MAX_VALUE;
			max = Double.MIN_VALUE;

			for(int j=0; j<workInstances.numInstances(); j++)
			{
				if(min >= workInstances.instance(j).value(i))
				{
					min = workInstances.instance(j).value(i);
				}
				
				if(max <= workInstances.instance(j).value(i))
				{
					max = workInstances.instance(j).value(i);
				}
			}
			
			DefaultMutableTreeNode minNode = new DefaultMutableTreeNode("min");
			minNode.setUserObject(new DoubleNode(min,"min"));
			this.attributeTree.addChildNode(minNode, attributeNode);
			
			DefaultMutableTreeNode maxNode = new DefaultMutableTreeNode("max");
			maxNode.setUserObject(new DoubleNode(max,"max"));
			this.attributeTree.addChildNode(maxNode, attributeNode);
		}
	}



	public AttributeTree getAttributeTree() {
		return attributeTree;
	}



	public void setAttributeTree(AttributeTree attributeTree) {
		this.attributeTree = attributeTree;
	}



	public JTabbedPane getFuzzyEditorTapped() {
		return fuzzyEditorTapped;
	}



	public void setFuzzyEditorTapped(JTabbedPane fuzzyEditorTapped) {
		this.fuzzyEditorTapped = fuzzyEditorTapped;
	}

//	public String getInstancesName() {
//		return instancesName;
//	}
//
//	public void setInstancesName(String instancesName) {
//		this.instancesName = instancesName;
//	}

	public StartWindow getStartW() {
		return startW;
	}

	public void setStartW(StartWindow startW) {
		this.startW = startW;
	}

	public FuzzySetTree getFuzzySetTree() {
		return fuzzySetTree;
	}

	public void setFuzzySetTree(FuzzySetTree fuzzySetTree) {
		this.fuzzySetTree = fuzzySetTree;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public TreeMap<Integer, TabToWindowSwitch> getTtwsMap() {
		return ttwsMap;
	}

	public void setTtwsMap(TreeMap<Integer, TabToWindowSwitch> ttwsMap) {
		this.ttwsMap = ttwsMap;
	}
}
