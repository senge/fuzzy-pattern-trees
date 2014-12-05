package gui_pt.visualisation;

import gui_pt.accessLayer.util.AccessPT;
import gui_pt.drawHelper.TreeClassPack;
import gui_pt.gui.AnalysePanel;
import gui_pt.gui.DrawPanel;
import gui_pt.guiUtil.RolloverButton;
import gui_pt.guiUtil.RolloverToggleButton;
import gui_pt.io.PTVstorePack;
import gui_pt.io.Settings;
import gui_pt.listener.DefaultPTVToolBarActionListener;
import gui_pt.plotter2D.Plotter2D;
import gui_pt.plugin.PTVisualisation;
import gui_pt.plugin.StreamAssist;
import gui_pt.pt.Calculations;
import gui_pt.stream.StreamView;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import weka.core.DenseInstance;
import weka.core.Instance;

public class DefaultPTV extends JPanel implements PTVisualisation, StreamAssist{
	
	// toolBarPane ------------------------------------
	private JToolBar 				toolBar;
	private RolloverButton 			nextButton;
	private RolloverButton 			prevButton;
	private RolloverButton	 		zoomIn;
	private RolloverButton 			zoomOut;
	private RolloverButton			toLatex;
	private RolloverToggleButton 	toggleAntialiasing;
	private RolloverToggleButton 	toggleRect;
	private RolloverToggleButton 	toggleMidNode;
	private RolloverButton 			editNodeButton;
	private RolloverButton			resetTransformationButton;

	// toolBarPane ende -------------------------------
	
	// streamBar --------------------------------------
	private JToolBar streamBar;
	private JPanel streamHistoryPanel;
	private JButton liveButton;
	private JLabel currLabel;
	
	// streamBar ende ---------------------------------
	
	private DefaultPTVToolBarActionListener aL_1;
	private JFrame owner;
	private JPanel cardPanel;
	private int cardIndex = 0;
	
	private TreeClassPack tcp = null;
	private PTVstorePack ptvSP = null;
	
	private StreamView svLink;
	private AccessPT watchedAccPT= null;
	private int watchedIndex = 0;
	//for connectability
	private ArrayList<PTVisualisation> connected = new ArrayList<PTVisualisation>();
	
	// Default Constructor
	
	public DefaultPTV(JFrame owner){
		
		this.owner = owner;
		
		aL_1 = new DefaultPTVToolBarActionListener(this);
		
		// toolBar start :::::::::::::::::::::::::::::::::::::::::::		
		nextButton = new RolloverButton(new ImageIcon("res/icons/cardArrowRight.png"));
		nextButton.setPreferredSize(new Dimension(15,15));
		nextButton.setActionCommand("next");
		nextButton.addActionListener(aL_1);
		
		prevButton = new RolloverButton(new ImageIcon("res/icons/cardArrowLeft.png"));
		prevButton.setPreferredSize(new Dimension(15,15));
		prevButton.setActionCommand("prev");
		prevButton.addActionListener(aL_1);
				
		zoomIn = new RolloverButton(new ImageIcon("res/icons/collapsed.gif"));
		zoomOut = new RolloverButton(new ImageIcon("res/icons/expanded.gif"));
		zoomIn.setPreferredSize(new Dimension(20,20));
		zoomOut.setPreferredSize(new Dimension(20,20));
		zoomIn.setActionCommand("zIN");
		zoomOut.setActionCommand("zOUT");
		
		zoomIn.addActionListener(aL_1);
		zoomOut.addActionListener(aL_1);
		
		resetTransformationButton = new RolloverButton(new ImageIcon("res/icons/square.png"));
		resetTransformationButton.setActionCommand("reset");
		resetTransformationButton.addActionListener(aL_1);
		
		toggleAntialiasing = new RolloverToggleButton(new ImageIcon("res/icons/antialiasing.png"));
		toggleAntialiasing.setSelected(true);
		toggleAntialiasing.setPreferredSize(new Dimension(20,20));
		toggleAntialiasing.setActionCommand("antialiasing");
		toggleAntialiasing.addActionListener(aL_1);
		
		toggleRect = new RolloverToggleButton(new ImageIcon("res/icons/rect.png"));
		toggleRect.setActionCommand("rect");
		toggleRect.setPreferredSize(new Dimension(20,20));
		toggleRect.addActionListener(aL_1);
		
		toggleMidNode = new RolloverToggleButton(new ImageIcon("res/icons/nodeMid.png"));
		toggleMidNode.setPreferredSize(new Dimension(20,20));
		toggleMidNode.setSelected(true);
		toggleMidNode.setActionCommand("nodeMide");
		toggleMidNode.addActionListener(aL_1);
		
		editNodeButton = new RolloverButton("edit");
		editNodeButton.addActionListener(aL_1);
		
		toLatex = new RolloverButton("toLatex");
		toLatex.setActionCommand("toLatex");
		toLatex.addActionListener(aL_1);
					
		toolBar = new JToolBar();
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		toolBar.add(prevButton);
		toolBar.add(nextButton);
		toolBar.addSeparator();
		toolBar.add(zoomIn);
		toolBar.add(zoomOut);
		toolBar.add(resetTransformationButton);
		toolBar.add(toggleAntialiasing);
		toolBar.add(toggleRect);
		toolBar.add(toggleMidNode);
		toolBar.add(editNodeButton);
		toolBar.add(toLatex);
		
		// toolBar end :::::::::::::::::::::::::::::::::::::::::::::
		
		// streamBar start :::::::::::::::::::::::::::::::::::::::::
		
		currLabel = new JLabel("<html><font color = 'FF0000'>live</font></html>");
		
		liveButton = new JButton("live");
		liveButton.setActionCommand("live");
		liveButton.addActionListener(aL_1);
		
		streamHistoryPanel = new JPanel();
		streamHistoryPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JPanel helpPanel = new JPanel();
		helpPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		helpPanel.add(currLabel);
		helpPanel.add(liveButton);
		helpPanel.add(streamHistoryPanel);
		
		streamBar = new JToolBar();
		streamBar.setLayout(new BorderLayout());

		streamBar.add(new JScrollPane(helpPanel), BorderLayout.CENTER);

				
		// streamBar end :::::::::::::::::::::::::::::::::::::::::::

		this.setLayout(new BorderLayout());
		this.setBorder(new EtchedBorder());
		this.add(toolBar, BorderLayout.NORTH);
		
				
	}
	//########################################################################################
	// Methods
	//########################################################################################
	
	public void updateToolBar(Settings settings){
		
		toggleAntialiasing.setSelected(settings.alaising);
		toggleRect.setSelected(settings.edgeRect);
		toggleMidNode.setSelected(settings.nodeMidEdge);
		
	}
	
	//########################################################################################
	// PTVisualisation
	//########################################################################################
	
	@Override
	public void buildVisualisation(AccessPT accPT, PTVstorePack ptvSP) {
					
		if(accPT != null)
		{
		
			if(ptvSP == null)
			{
				this.ptvSP = new PTVstorePack(accPT);
			}
			else
			{
				this.ptvSP = ptvSP;
			}
			
			cardPanel = new JPanel();
			cardPanel.setLayout(new CardLayout());
			
			tcp = new TreeClassPack();
			
			tcp.setdPTV(this);
			tcp.setAp(new AnalysePanel());
			tcp.setAccessPT(accPT);
			
			JTabbedPane toolTabbed = new JTabbedPane();
			toolTabbed.add(tcp.getAp());
			
			JSplitPane TreeClassPanelSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			TreeClassPanelSplitPane.setLeftComponent(cardPanel);
			TreeClassPanelSplitPane.setRightComponent(toolTabbed);
			TreeClassPanelSplitPane.setDividerLocation(800);
			
			
			this.add(TreeClassPanelSplitPane, BorderLayout.CENTER);
				
			tcp.setDp_A(new DrawPanel[accPT.getAccessTrees().length]);
			String[] class_Names_Array = new String[accPT.getAccessTrees().length];
			
			for(int i=0; i<accPT.getAccessTrees().length; i++)
			{
				tcp.getDp_A()[i] = new DrawPanel(this.ptvSP.getSettings()[i], this.ptvSP.getDrawTreePack()[i], i, tcp);
	//			tcp.getDp_A()[i].setName(name + accRootPack[i].getClass_Name());
				class_Names_Array[i] = accPT.getAccessTrees()[i].getClass_Name();
				tcp.getDp_A()[i].setLayout(new BorderLayout());			
				
				tcp.getDp_A()[i].ID = i;
				tcp.getDp_A()[i].setdPTV(this);
				cardPanel.add(tcp.getDp_A()[i], ""+i);
			}
			//set FocusedDrawPanel
			if(accPT.getAccessTrees().length>0)
			{
				tcp.getAp().buildAnalyseFrame(accPT.getAccessTrees()[0]);
				tcp.setClass_Names(class_Names_Array);
				aL_1.setShowingDP(tcp.getDp_A()[0]);
			}	
		}
	}

	@Override
	public JPanel getPanel() {
		// TODO Auto-generated method stub
		return this;
	}
	
	@Override
	public boolean isConnectable() {
		
		return true;
	}
	
	@Override
	public ArrayList<PTVisualisation> getConnection() {

		return this.connected;
	}

	@Override
	public void registerConnection(PTVisualisation ptv) {
		
		this.connected.add(ptv);
	}
	
	@Override
	public void removeConnection(PTVisualisation ptv){
		
		this.connected.remove(ptv);
	}
	public void notifyConnections(int attrIndex){

		for(PTVisualisation ptv: connected)
		{
			ptv.updateConnection(attrIndex);
		}
	}

	@Override
	public void updateConnection(int attrIndex) {

		AnalysePanel af = tcp.getAp();
		Plotter2D plotter = null;
				
		for(int i=0; i<af.getPlotter2D().length; i++)
		{
			if(af.getPlotter2D()[i].getAttrIndex() == attrIndex)
			{
				plotter = af.getPlotter2D()[i];
				break;
			}
		}
		
		if(plotter != null)
		{
			plotter.setCurrentX(tcp.getProtoInstance().value(attrIndex));
			
			Instance inc = new DenseInstance(af.getTcp().getProtoInstance());
//			
//			for(int i=0; i<af.getPlotter2D().length; i++)
//			{
////				inc.setValue(af.getAttr()[i], af.getPlotter2D()[i].getCurrentX());
//				af.getTcp().getProtoInstance().setValue(af.getAttr()[i], af.getPlotter2D()[i].getCurrentX());
//			}
			double output;
			for(int i=0; i<af.getPlotter2D().length; i++)
			{
				Instance helpInstance = new DenseInstance(inc);
				Calculations.p2DValues(af.getAccRoot()
						, helpInstance
						, af.getPlotter2D()[i].getValues()
						, af.getPlotter2D()[i].getVX()
						, af.getAttr()[i]);
				double fire = af.getAccRoot().fire(inc);
				output = fire;
				af.getPlotter2D()[i].setCurrentY(fire);
				
				//for difference quotient
					//Plus
				helpInstance = new DenseInstance(inc);
				
				if((af.getPlotter2D()[i].getCurrentX()
						+ af.getPlotter2D()[i].getxDelta()) > af.getPlotter2D()[i].getxMax() )
				{
					helpInstance.setValue(af.getAttr()[i], af.getPlotter2D()[i].getxMax());				
					af.getPlotter2D()[i].setCurrXDeltaP(af.getPlotter2D()[i].getxMax());
				}
				else
				{
					helpInstance.setValue(af.getAttr()[i], af.getPlotter2D()[i].getCurrentX()
							+ af.getPlotter2D()[i].getxDelta());				
					af.getPlotter2D()[i].setCurrXDeltaP(af.getPlotter2D()[i].getCurrentX()
							+af.getPlotter2D()[i].getxDelta());
				}

				fire = af.getAccRoot().fire(helpInstance);
				af.getPlotter2D()[i].setCurrYDeltaP(fire);
				
					//Minus 
				helpInstance = new DenseInstance(inc);
				if((af.getPlotter2D()[i].getCurrentX()
						- af.getPlotter2D()[i].getxDelta()) < af.getPlotter2D()[i].getxMin())
				{
					helpInstance.setValue(af.getAttr()[i], af.getPlotter2D()[i].getxMin());				
					af.getPlotter2D()[i].setCurrXDeltaM(af.getPlotter2D()[i].getxMin());
				}
				else
				{
					helpInstance.setValue(af.getAttr()[i], af.getPlotter2D()[i].getCurrentX()
							- af.getPlotter2D()[i].getxDelta());				
					af.getPlotter2D()[i].setCurrXDeltaM(af.getPlotter2D()[i].getCurrentX()
							-af.getPlotter2D()[i].getxDelta());
				}
				fire = af.getAccRoot().fire(helpInstance);
				af.getPlotter2D()[i].setCurrYDeltaM(fire);
				
				af.getPlotter2D()[i].repaint();
				af.getClassOutputPanel().repaint();
				
				//output
//				af.getTcp().setProtoInstance(new DenseInstance(inc));
				af.getOutputLabel().setText(""+(int)(output*1000)/1000d);
			}
		}
		else
		{
			for(int i=0; i < af.getAttrSliderList().size(); i++)
			{
				if(af.getDisabledAttr()[Integer.parseInt(af.getAttrSliderList().get(i).getName())]
						== attrIndex)
				{
					af.getAttrSliderList().get(i).setValue((int)(tcp.getProtoInstance().value(attrIndex)*1000));					                     				}
			}
		}
	}

	@Override
	public Instance getProtoInstance() {
		// TODO Auto-generated method stub
		return tcp.getProtoInstance();
	}

	@Override
	public void setProtoInstance(Instance instance) {
		// TODO Auto-generated method stub
		tcp.setProtoInstance(instance);
	}

	@Override
	public String getMarking() {
		
		return "DefaultPTV";
	}
	
	public void update(AccessPT accPT){
		
		tcp.setAccessPT(accPT);
		
				
		for(int i=0; i<tcp.getDp_A().length; i++){
			
			tcp.getDp_A()[i].setAccRoot(accPT.getAccessTrees()[i]);
			tcp.getDp_A()[i].updateDrawTree();
			tcp.getDp_A()[i].setSelectedNodeID(0);
			if(tcp.getDp_A()[i].isShowing())
			{
				tcp.getAp().buildAnalyseFrame(accPT.getAccessTrees()[i]);
			}					
		}		
	}
	
	@Override
	public void setOwner(JFrame owner) {
		
		this.owner = owner;
	}

	@Override
	public JFrame getOwner() {
		
		return owner;
	}
	
	//###########################################################################################
	// StreamAssist
	//###########################################################################################
	
	@Override
	public void updatePTV() {

		JButton button = new JButton(""+svLink.getUpdateCount());
		button.setActionCommand("showTreeX");
		button.addActionListener(this.aL_1);

		this.streamHistoryPanel.add(button);
		
		if(this.streamHistoryPanel.getComponentCount() > svLink.getMaxHistorySize())
		{
			this.streamHistoryPanel.remove(0);
		}

		this.validate();
		
		if(this.watchedAccPT == null)
		{
			AccessPT accPT = svLink.getTreeHistory().get(
				svLink.getTreeHistory().size()-1);
		
			//lock repaint
			while(!this.svLink.synRepaintUpdate(true))
			{
				// do nothing;
System.out.println("lock - DefaultPTV 472");
			}
			
			this.watchedIndex = svLink.getUpdateCount();
			
			if(tcp != null)
			{
				update(accPT);
			}
			else
			{
				//build storePack
				PTVstorePack ptvSP = new PTVstorePack(accPT);
				
				String name = ""; //TODO
				
				tcp = new TreeClassPack();
				this.buildVisualisation(accPT, ptvSP);
				
				for(int i=0; i<tcp.getDp_A().length; i++)
				{
					tcp.getDp_A()[i].setSv(svLink);
					tcp.getDp_A()[i].setStreamMode(true);
					tcp.getDp_A()[i].setdPTV(this);
					tcp.getDp_A()[i].repaint();
				}
				
				this.validate();
			}
			//unlock
			this.svLink.synRepaintUpdate(false);
		}		
	}

	@Override
	public void linkStreamView(StreamView sv) {
		
		this.svLink = sv;
		this.add(streamBar, BorderLayout.SOUTH);
	}
	
	//###########################################################################################
	// GET and SET
	//###########################################################################################

	public JPanel getCardPanel() {
		return cardPanel;
	}

	public void setCardPanel(JPanel cardPanel) {
		this.cardPanel = cardPanel;
	}

	public RolloverToggleButton getToggleAntialiasing() {
		return toggleAntialiasing;
	}

	public void setToggleAntialiasing(RolloverToggleButton toggleAntialiasing) {
		this.toggleAntialiasing = toggleAntialiasing;
	}

	public int getCardIndex() {
		return cardIndex;
	}

	public void setCardIndex(int cardIndex) {
		this.cardIndex = cardIndex;
	}

	public AccessPT getWatchedAccRootPack() {
		return watchedAccPT;
	}

	public void setWatchedAccRootPack(AccessPT watchedAccPT) {
		this.watchedAccPT = watchedAccPT;
	}

	public StreamView getSvLink() {
		return svLink;
	}

	public void setSvLink(StreamView svLink) {
		this.svLink = svLink;
	}

	public int getWatchedIndex() {
		return watchedIndex;
	}

	public void setWatchedIndex(int watchedIndex) {
		this.watchedIndex = watchedIndex;
	}

	public JLabel getCurrLabel() {
		return currLabel;
	}

	public void setCurrLabel(JLabel currLabel) {
		this.currLabel = currLabel;
	}

	public TreeClassPack getTcp() {
		return tcp;
	}

	public void setTcp(TreeClassPack tcp) {
		this.tcp = tcp;
	}

	public RolloverButton getToLatex() {
		return toLatex;
	}

	public void setToLatex(RolloverButton toLatex) {
		this.toLatex = toLatex;
	}





}
