package gui_pt.gui;

import gui_pt.accessLayer.util.AccessNode;
import gui_pt.accessLayer.util.AccessPT;
import gui_pt.guiUtil.TabToWindowSwitch;
import gui_pt.guiUtil.UserObjectWrapper;
import gui_pt.listener.ClassifierTreeMouseListener;
import gui_pt.listener.FileMenuActionListener;
import gui_pt.plugin.PTVisualisation;
import gui_pt.stream.StreamView;
import gui_pt.visualisation.DefaultPTV;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.tree.DefaultMutableTreeNode;

public class MainWindow extends JFrame{
	
	private JMenuBar 	menu;	
	private JMenu 		fileMenu;
	private JMenu 		newMenu;
	private JMenuItem 	newPatternTree;
	private JMenuItem 	newDefaultPT;
	private JMenuItem	newStream;
	private JMenu	 	load;
	private JMenuItem	loadPT;
	private JMenuItem 	loadPTV;
	private JMenuItem 	printItem;
	
	private JSplitPane	mainSplitPane;
	private JPanel		rightMainPanel;
	private JTabbedPane ptvTabbedPane;
	
	private JPanel		mainTreePanel;
	private JTabbedPane explorerTabbedPane;
	private JPanel		classifierPanel;
	private JPanel		regressionPanel;
	
	Container cp;
	
	//SelectionTrees
	private ClassifierTree cTree = new ClassifierTree();
	private RegressionTree rTree = new RegressionTree();
	
	private StartWindow startW;
	
	//+++++++++++++Singleton stuff+++++++++++++++++++++++++++++++++++++++++++
	private static MainWindow instance = null;
	
	public static MainWindow getInstance(StartWindow startW){
		
		if(instance == null)
		{
			instance = new MainWindow(startW);
			return instance;
		}
		
		return instance;
	}
	
	public static boolean isInitialized()
	{
		if(instance == null) return false;
		
		return true;
	}
	
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	private MainWindow(StartWindow startW)
	{		
		this.startW = startW;
		
		MouseListener mouseListener = new ClassifierTreeMouseListener(this);
		cTree.getTree().addMouseListener(mouseListener);
		
		// MenuBar start ::::::::::::::::::::::::::::::::::::::::::::::::::
		FileMenuActionListener fmaListener = new FileMenuActionListener(this);
		
		loadPT = new JMenuItem("Load PatternTree");
		loadPT.setActionCommand("loadPT");
		loadPT.addActionListener(fmaListener);
		
		loadPTV = new JMenuItem("Load PatternTree-Visualisation");
		loadPTV.setActionCommand("loadPTV");
		loadPTV.addActionListener(fmaListener);
		
		load = new JMenu("Load");
		load.add(loadPT);
		load.add(loadPTV);
				
		printItem = new JMenuItem("print");
		printItem.setEnabled(false);
		printItem.setActionCommand("print");
		printItem.addActionListener(fmaListener);
				
		newPatternTree = new JMenuItem("Custom Pattern-Tree");
		newPatternTree.setActionCommand("createPT");
		newPatternTree.addActionListener(fmaListener);
		
		newDefaultPT = new JMenuItem("Default Pattern-Tree");
		newDefaultPT.setActionCommand("createDefaultPT");
		newDefaultPT.addActionListener(fmaListener);
		
		newStream = new JMenuItem("Pattern-Tree-Stream");
		newStream.setActionCommand("openPTStream");
		newStream.addActionListener(fmaListener);
		
		newMenu = new JMenu("new...");
		newMenu.add(newPatternTree);
		newMenu.add(newDefaultPT);
		newMenu.add(newStream);
		
		fileMenu = new JMenu("File");
		fileMenu.setName("file");
		fileMenu.addMenuListener(fmaListener);
		fileMenu.add(newMenu);
		fileMenu.addSeparator();
		fileMenu.add(load);
		fileMenu.add(printItem);
				
		menu = new JMenuBar();
		
		menu.add(fileMenu);
		
		this.setJMenuBar(menu);
		// MenuBar end ::::::::::::::::::::::::::::::::::::::::::::::::::::
		
		
		// ToolBar start ::::::::::::::::::::::::::::::::::::::::::::::::::
//		connect_Button = new RolloverButton("connect");
//		connect_Button.setActionCommand("connect");
//		connect_Button.addActionListener(l);
//		
//		mainToolBar = new JToolBar();
//		mainToolBar.setRollover(true);
//		mainToolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
//		mainToolBar.add(connect_Button);
		
		
		// ToolBar end ::::::::::::::::::::::::::::::::::::::::::::::::::::
		
		
		cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		
		classifierPanel = new JPanel();
		classifierPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		classifierPanel.setBackground(Color.white);
		classifierPanel.add(cTree.getTree());
		
		regressionPanel = new JPanel();
		regressionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		regressionPanel.setBackground(Color.white);
		regressionPanel.add(rTree.getTree());
		
		explorerTabbedPane = new JTabbedPane();
		explorerTabbedPane.addTab("Classifier", new JScrollPane(classifierPanel));
		explorerTabbedPane.addTab("Regression", new JScrollPane(regressionPanel));
		
		mainTreePanel = new JPanel();
		mainTreePanel.setLayout(new BorderLayout());
		mainTreePanel.add(explorerTabbedPane, BorderLayout.CENTER);
		
		//RightMainPanel
		ptvTabbedPane = new JTabbedPane();
		
		rightMainPanel =new JPanel();
		rightMainPanel.setLayout(new BorderLayout());
		rightMainPanel.add(ptvTabbedPane, BorderLayout.CENTER);
		
		mainSplitPane = new JSplitPane();
		mainSplitPane.setLeftComponent(mainTreePanel);
		mainSplitPane.setRightComponent(rightMainPanel);
		mainSplitPane.setDividerLocation(200);
		
//		cp.add(mainToolBar, BorderLayout.NORTH);
		cp.add(mainSplitPane, BorderLayout.CENTER);
		
//		this.addWindowListener(new WindowClosingAdapter());
		
		this.setSize(800,800);
		this.setLocation(
				(Toolkit.getDefaultToolkit().getScreenSize().width-getSize().width)/2,
				(Toolkit.getDefaultToolkit().getScreenSize().height-getSize().height)/2);		
		this.setVisible(true);
		
		
	}
	
	//#####################################################################################
	//METHODS
	//#####################################################################################
	
	public void addAccPT(AccessPT accPT, boolean isStream, String name) {
		
		if(accPT.getAccessTrees()[0].getTt() == AccessNode.TreeType.CLASSIFICATION)
		{
			DefaultMutableTreeNode node = cTree.addClassifier(accPT, name);
			
			PTVisualisation ptv = new DefaultPTV(this);
			ptv.buildVisualisation(accPT, null);
			DefaultMutableTreeNode ptvNode = addVisualisation(ptv, node, 0);
			openVisualisation(ptv, ptvNode);
		}
		else
		{
			DefaultMutableTreeNode node = rTree.addRegression(accPT,isStream, name);
			
			PTVisualisation ptv = new DefaultPTV(this);
			ptv.buildVisualisation(accPT, null);
			DefaultMutableTreeNode ptvNode = addVisualisation(ptv, node, 1);
			openVisualisation(ptv, ptvNode);
		}
	}
	
	public void addStreamView(StreamView sv, String name) {
		
		DefaultMutableTreeNode node = cTree.addClassifierStream(sv, name);
		
		DefaultPTV ptv = new DefaultPTV(this);
		sv.getStreamAssists().add(ptv);
		ptv.linkStreamView(sv);
		ptv.buildVisualisation(null, null);

		DefaultMutableTreeNode ptvNode = addVisualisation(ptv, node, 0);
		openVisualisation(ptv, ptvNode);
	}
	
	public DefaultMutableTreeNode addVisualisation(PTVisualisation ptv, DefaultMutableTreeNode node, int tree){
		
		DefaultMutableTreeNode retNode = null;
		switch(tree){
		
			case 0:
				retNode = cTree.addPTV(ptv, node);
				break;
			case 1: 
				retNode = rTree.addPTV(ptv, node);
				break;
		}
		return retNode;
	}
	
	public void openVisualisation(PTVisualisation ptv, DefaultMutableTreeNode ptvNode){
		
		((UserObjectWrapper)ptvNode.getUserObject()).setOpen(true);
		//tab to window switcher
		TabToWindowSwitch ttws = new TabToWindowSwitch(ptvTabbedPane, ptv.getPanel());
		
		PTVTabHeadPanel ptvTabHeadPanel = new PTVTabHeadPanel(ttws
											, ((UserObjectWrapper)ptvNode.getUserObject()).toString()
											, ptvNode);
		
		//To make drawPanel visible
		ptvTabbedPane.add(ptv.getPanel());
		ptvTabbedPane.setSelectedIndex(ptvTabbedPane.getTabCount()-1);
		ptvTabbedPane.setTabComponentAt(ptvTabbedPane.indexOfComponent(ptv.getPanel()), ptvTabHeadPanel);
		
	}
	
	//#####################################################################################
	//GET and SET
	//#####################################################################################

	public StartWindow getStartW() {
		return startW;
	}

	public void setStartW(StartWindow startW) {
		this.startW = startW;
	}

	public ClassifierTree getcTree() {
		return cTree;
	}

	public void setcTree(ClassifierTree cTree) {
		this.cTree = cTree;
	}

	public RegressionTree getrTree() {
		return rTree;
	}

	public void setrTree(RegressionTree rTree) {
		this.rTree = rTree;
	}


}
	
	