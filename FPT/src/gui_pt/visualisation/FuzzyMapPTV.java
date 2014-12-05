package gui_pt.visualisation;

import gui_pt.accessLayer.util.AccessNode;
import gui_pt.accessLayer.util.AccessPT;
import gui_pt.fuzzyMap.FmDrawPanel;
import gui_pt.fuzzyMap.FmMPActionListener;
import gui_pt.fuzzyMap.FmMPChangeListener;
import gui_pt.guiUtil.RolloverToggleButton;
import gui_pt.io.PTVstorePack;
import gui_pt.plugin.PTVisualisation;
import gui_pt.plugin.StreamAssist;
import gui_pt.pt.Calculations;
import gui_pt.stream.StreamView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class FuzzyMapPTV extends JPanel implements PTVisualisation, StreamAssist{
	
	JToolBar toolBar;
	
	JButton classColorButton;
	JPopupMenu classColorPopup;
	JCheckBox[] classSelectionBox;
	JButton[] chooseColorButton;
	
	JButton colorModeButton;
	JPopupMenu colorModePopup;
	
	RolloverToggleButton drawDataButton;
	
	JSplitPane mainSplitPane;
	JSplitPane leftSplitPaneV;
	
	JPanel leftBottomPanel;
	JLabel[] sliderLabel;
	
	JPanel leftTopPanel;
	
	JPanel rightPanel;
	
	JLabel testLabel;
	
	JComboBox comboBox_1;
	JComboBox comboBox_2;
	
	FmDrawPanel fmDP;
	
	// streamBar --------------------------------------
	private JToolBar streamBar;
	private JPanel streamHistoryPanel;
	private JButton liveButton;
	private JLabel currLabel;
	
	// streamBar ende ---------------------------------
	
	private StreamView svLink;
	private AccessPT watchedAccPT = null;
	private int watchedIndex = 0;
	
	private Instance protoInstance;
	private AccessNode[] accRootPack;
	private JFrame owner;
	
	private int[] selectedAttr = {0, 0};
	private double[] attrMin;
	private double[] attrMax;
//	private int[] attr = null;
	private Instances data;
	private Instance[][] instance_A;
	private int resolution = 50;
	private Color[] classColor;
	private Color[] defaultColor = {Color.red, Color.green, Color.blue, Color.white, Color.black};
	private boolean[] selectedClasses;
	private FmMPActionListener fmMPAL = new FmMPActionListener(this);
	private FmMPChangeListener fmMPCL = new FmMPChangeListener(this);
	//for connectability
	private ArrayList<PTVisualisation> connected = new ArrayList<PTVisualisation>();
	private ArrayList<JSlider> sliderList = new ArrayList<JSlider>();
	
	//############################################################################################
	//Constructor
	//############################################################################################
	
	public FuzzyMapPTV(){
		
		leftBottomPanel = new JPanel();
		leftTopPanel = new JPanel();
		leftTopPanel.setLayout(new BorderLayout());
		leftTopPanel.add(new JLabel("NORTH"), BorderLayout.NORTH);
		
		leftSplitPaneV = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		leftSplitPaneV.setBottomComponent(leftBottomPanel);
		leftSplitPaneV.setTopComponent(leftTopPanel);
				
		rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
			
		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainSplitPane.setLeftComponent(leftSplitPaneV);
		mainSplitPane.setRightComponent(rightPanel);
		
		//ToolBar+++++++++++++++++
		
		classColorPopup = new JPopupMenu();
		classColorButton = new JButton("classes");
		classColorButton.setActionCommand("classes");
		classColorButton.addActionListener(fmMPAL);
		
		colorModePopup = new JPopupMenu();
		colorModeButton = new JButton("colorMode");
		colorModeButton.setActionCommand("colorMode");
		colorModeButton.addActionListener(fmMPAL);
		
		JRadioButton radioFuzzy = new JRadioButton("Fuzzy");
		radioFuzzy.setName("0");
		radioFuzzy.setActionCommand("changeColorMode");
		radioFuzzy.addActionListener(fmMPAL);
		
		JRadioButton radioMerge = new JRadioButton("Merge");
		radioMerge.setName("1");
		radioMerge.setActionCommand("changeColorMode");
		radioMerge.addActionListener(fmMPAL);
		radioMerge.setSelected(true);
		
		JRadioButton radioCrisp = new JRadioButton("Crisp");
		radioCrisp.setName("2");
		radioCrisp.setActionCommand("changeColorMode");
		radioCrisp.addActionListener(fmMPAL);
		
		ButtonGroup group1 = new ButtonGroup();
		group1.add(radioFuzzy);
		group1.add(radioMerge);
		group1.add(radioCrisp);
		
		JPanel panel1 = new JPanel();
		panel1.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel1.add(radioFuzzy);
		panel1.add(radioMerge);
		panel1.add(radioCrisp);
		
		colorModePopup.add(panel1);
		
		drawDataButton = new RolloverToggleButton("Draw Data");
		drawDataButton.setActionCommand("drawData");
		drawDataButton.addActionListener(fmMPAL);
		drawDataButton.setSelected(true);
		
		JSpinner resolutionSpinner = new JSpinner(new SpinnerNumberModel(50,1,100,1));
		resolutionSpinner.setName("resolution");
		resolutionSpinner.addChangeListener(fmMPCL);
		
		toolBar = new JToolBar();
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		toolBar.add(classColorButton);
		toolBar.add(colorModeButton);
		toolBar.add(drawDataButton);
		toolBar.add(resolutionSpinner);
		
		// streamBar start :::::::::::::::::::::::::::::::::::::::::
		
		currLabel = new JLabel("<html><font color = 'FF0000'>live</font></html>");
		
		liveButton = new JButton("live");
		liveButton.setActionCommand("live");
		liveButton.addActionListener(fmMPAL);
		
		streamHistoryPanel = new JPanel();
		streamHistoryPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		streamBar = new JToolBar();
		streamBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		streamBar.add(currLabel);
		streamBar.add(new JScrollPane(streamHistoryPanel));
		streamBar.add(liveButton);
				
		// streamBar end :::::::::::::::::::::::::::::::::::::::::::
				
		this.setLayout(new BorderLayout());
		
		this.add(toolBar, BorderLayout.NORTH);
		this.add(mainSplitPane, BorderLayout.CENTER);			
	}
	
	public void buildFmPanel(Instances data){
				
		this.data = data;
		
		fmDP = new FmDrawPanel(this);
		fmDP.setLayout(new BorderLayout());
		rightPanel.add(fmDP, BorderLayout.CENTER);
		
		this.classColor = new Color[data.numClasses()];
		this.classSelectionBox = new JCheckBox[data.numClasses()];
		this.chooseColorButton = new JButton[data.numClasses()];
		this.selectedClasses = new boolean[data.numClasses()];
		
		for(int i=0; i<classColor.length; i++)
		{
			if(i >= defaultColor.length)
			{
				classColor[i] = Color.white;
			}
			else
			{
				classColor[i] = this.defaultColor[i];
			}
			
			//create checkboxes
			classSelectionBox[i] = new JCheckBox(data.classAttribute().value(i));
			classSelectionBox[i].setName(""+i);
			classSelectionBox[i].setSelected(true);
			classSelectionBox[i].setActionCommand("classSelection");
			classSelectionBox[i].addActionListener(fmMPAL);
			
			chooseColorButton[i] = new JButton();
			chooseColorButton[i].setName(""+i);
			chooseColorButton[i].setBackground(classColor[i]);
			chooseColorButton[i].setPreferredSize(new Dimension(10,10));
			chooseColorButton[i].setActionCommand("changeColor");
			chooseColorButton[i].addActionListener(fmMPAL);
			
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.LEFT));
			panel.add(chooseColorButton[i]);
			panel.add(classSelectionBox[i]);
			
			classColorPopup.add(panel);
			
			this.selectedClasses[i] = true;
			
		}
		
		//util stuff
		attrMin = Calculations.calcMin(data);
		attrMax = Calculations.calcMax(data);
							
		//Vorarbeiten
		if(this.protoInstance == null)
		{
			this.protoInstance = (new DenseInstance(data.get(0)));
		}
		
		//build Attribute radioButton-Matrix
		buildComboBoxPair();
		//build slider
		buildSlider();
		//init Instance Array
		initInstanceArray();
		
	}
	
	private void buildComboBoxPair(){
		
		int numAttr = data.numAttributes()-1;
				
		JPanel rbmPanel = new JPanel();
		rbmPanel.setLayout(new GridLayout(4, 1));
		
		JLabel labelx = new JLabel("x-Axis");
		JLabel labely = new JLabel("y-Axis");
		
				
		//Fill top line with attribute names
		Attribute[] attrName_A = new Attribute[numAttr];
		for(int i=0; i<numAttr; i++)
		{
			attrName_A[i] = this.data.attribute(i);
		}
		
		ComboBoxRenderer renderer_1 = new ComboBoxRenderer();
		ComboBoxRenderer renderer_2 = new ComboBoxRenderer();
		
		comboBox_1 = new JComboBox(attrName_A);
		comboBox_1.setRenderer(renderer_1);
		comboBox_1.setActionCommand("combo");
		comboBox_1.addActionListener(fmMPAL);
		
		comboBox_2 = new JComboBox(attrName_A);
		comboBox_2.setRenderer(renderer_2);
		comboBox_2.setActionCommand("combo");
		comboBox_2.addActionListener(fmMPAL);
	
		rbmPanel.add(labelx);
		rbmPanel.add(comboBox_1);
		rbmPanel.add(labely);
		rbmPanel.add(comboBox_2);
		
		this.leftTopPanel.removeAll();
		this.leftTopPanel.add(new JScrollPane(rbmPanel), BorderLayout.CENTER);
		this.validate();
	}
	
	/**
	 * 
	 */
	public void buildSlider(){
		
		this.sliderList.clear();
		int numSelecAttr = 2;
		
		if(selectedAttr[0] == selectedAttr[1]) numSelecAttr = 1;
		
		
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new GridLayout(1,data.numAttributes()-numSelecAttr));
		
		sliderLabel = new JLabel[data.numAttributes()-numSelecAttr];
		
		this.leftBottomPanel.removeAll();
		this.leftBottomPanel.setLayout(new BorderLayout());
		this.leftBottomPanel.add(new JScrollPane(sliderPanel), BorderLayout.CENTER);
		
		this.initInstanceArray();
		int labelCount = 0;
		for(int i=0; i<data.numAttributes()-1; i++)
		{
					
			if(i != selectedAttr[0]
					&& i != selectedAttr[1])
			{
				sliderLabel[labelCount] = new JLabel(protoInstance.value(i)+"");
				
				JSlider attrSlider = null;
				
				if(data.attribute(i).isNumeric())
				{
					attrSlider = new JSlider(JSlider.VERTICAL
							, (int)(attrMin[i]*1000)
							, (int)(attrMax[i]*1000)
							,(int)(protoInstance.value(i)*1000));
				}
				else
				{
					attrSlider = new JSlider(JSlider.VERTICAL
							, (int)(attrMin[i])
							, (int)(attrMax[i])
							,(int)(protoInstance.value(i)));
				}
				
				this.sliderList.add(attrSlider);
				attrSlider.setName(""+i+"X"+(labelCount));
				attrSlider.addChangeListener(new ChangeListener(){

					@Override
					public void stateChanged(ChangeEvent arg0) {
						
						JSlider source = (JSlider)arg0.getSource();
						
						String[] nameAndCount = source.getName().split("X");
						
						double toSetValue = 0;

						if(data.attribute(Integer.parseInt(nameAndCount[0])).isNumeric())
						{
							toSetValue = source.getValue()/1000d;
						}
						else
						{
							toSetValue = source.getValue();
						}
						
						protoInstance.setValue(Integer.parseInt(nameAndCount[0])
								,toSetValue);
						
						FuzzyMapPTV.this.getSliderLabel()[Integer.parseInt(nameAndCount[1])]
						                                  .setText((toSetValue) + "");
						
						FuzzyMapPTV.this.fmDP.repaint();
						FuzzyMapPTV.this.updateInstanceArray();
						
						notifyConnections(Integer.parseInt(nameAndCount[0]));
					}				
				});
					
				JPanel helpPanel = new JPanel();
				helpPanel.setLayout(new BorderLayout());
				helpPanel.setBorder(new TitledBorder(data.attribute(i).name()));
				helpPanel.setPreferredSize(new Dimension(40,400));
				helpPanel.add(attrSlider, BorderLayout.CENTER);
				helpPanel.add(sliderLabel[labelCount++], BorderLayout.NORTH);
				
				sliderPanel.add(helpPanel);
			}
		}
		this.validate();
	}
	
	/**
	 * 
	 */
	public void initInstanceArray(){
		
		instance_A = new Instance[resolution][resolution];
		
		for(int i=0; i< resolution; i++)
		{
			for(int j=0; j<resolution; j++)
			{
				instance_A[i][j] = new DenseInstance(protoInstance);
				
				double difMinMax0 = (attrMax[selectedAttr[0]]-attrMin[selectedAttr[0]])/resolution;
				double difMinMax1 = (attrMax[selectedAttr[1]]-attrMin[selectedAttr[1]])/resolution;
				
				if(data.attribute(selectedAttr[0]).isNumeric())
				{
					instance_A[i][j].setValue(selectedAttr[0]
				                               , attrMin[selectedAttr[0]]
				                                         +i*difMinMax0+difMinMax0/2d);
				}
				else
				{
					double value = (int)(i*(attrMax[selectedAttr[0]]+1)/resolution);
					instance_A[i][j].setValue(selectedAttr[0]
					                            , value);
				}
				
				if(data.attribute(selectedAttr[1]).isNumeric())
				{
					instance_A[i][j].setValue(selectedAttr[1]
				                               , attrMin[selectedAttr[1]]
				                                         +j*difMinMax1+difMinMax1/2d);
				}
				else
				{
					double value = (int)(j*(attrMax[selectedAttr[1]]+1)/resolution);
					instance_A[i][j].setValue(selectedAttr[1]
					                            , value);
				}				
			}
		}
		updateInstanceArray();
	}
	
	public void adaptProtoInstance(int i, int j){
		
		double value1 = instance_A[i][j].value(selectedAttr[0]);
		double value2 = instance_A[i][j].value(selectedAttr[1]);
		
		protoInstance.setValue(selectedAttr[0], value1);
		protoInstance.setValue(selectedAttr[1], value2);
		
		//one notification per changed attribute necessary
		notifyConnections(selectedAttr[0]);
		notifyConnections(selectedAttr[1]);
	}
	
	/**
	 * 
	 */
	private void updateInstanceArray(){
		
		for(int i=0; i< resolution; i++)
		{
			for(int j=0; j<resolution; j++)
			{
				for(int k=0; k<data.numAttributes(); k++)
				{
					if( k!=selectedAttr[0]
                          && k != selectedAttr[1])
					{
						instance_A[i][j].setValue(k,
								this.protoInstance.value(k));
					}
				}
			}
		}
	}
	
	//########################################################################################
	// PTVisualisation
	//########################################################################################
	
	@Override
	public void buildVisualisation(AccessPT accPT, PTVstorePack ptvST) {

		if(accPT != null && accPT.getAccessTrees() != null)
		{
			Instances data = accPT.getData();
			
			this.accRootPack = accPT.getAccessTrees();
			this.protoInstance = new DenseInstance(data.get(0));
			
			this.buildFmPanel(data);
		}		
	}

	@Override
	public JPanel getPanel() {
		
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
		
		for(int i=0; i<this.sliderList.size(); i++)
		{
			String[] idAndCount = sliderList.get(i).getName().split("X");
						
			if(data.attribute(i).isNumeric())
			{
				sliderList.get(i).setValue(
					(int)(protoInstance.value(Integer.parseInt(idAndCount[0]))*1000));
			}
			else
			{
				sliderList.get(i).setValue(
						(int)protoInstance.value(Integer.parseInt(idAndCount[0])));
			}
		}
		
		this.updateInstanceArray();
		this.fmDP.repaint();
	}

	@Override
	public Instance getProtoInstance() {
		
		return protoInstance;
	}

	@Override
	public void setProtoInstance(Instance protoInstance) {
		
		this.protoInstance = protoInstance;
	}

	@Override
	public String getMarking() {
		// TODO Auto-generated method stub
		return "FuzzyMap";
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
	
	public void update(AccessPT accPT){
		
		this.setAccRootPack(accPT.getAccessTrees());
				
		this.rightPanel.removeAll();
		fmDP = new FmDrawPanel(this);
		fmDP.setLayout(new BorderLayout());
		rightPanel.add(fmDP, BorderLayout.CENTER);
		rightPanel.validate();
		fmDP.repaint();
	}
	
	@Override
	public void updatePTV() {

		JButton button = new JButton(""+svLink.getUpdateCount());
		button.setActionCommand("showTreeX");
		button.addActionListener(fmMPAL);

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
	//		while(!this.svLink.synRepaintUpdate(true))
	//		{
	//			// do nothing;
	//System.out.println("lock");
	//		}
				
	//		this.watchedIndex = svLink.getUpdateCount();
				
			if(this.accRootPack != null)
			{
				update(accPT);
			}
			else
			{
				//build storePack
				PTVstorePack ptvSP = new PTVstorePack(accPT);
					
				String name = ""; //TODO
					
				this.buildVisualisation(accPT, ptvSP);
					
				this.validate();
			}
			//unlock
	//		this.svLink.synRepaintUpdate(false);
		}
	}
	
	@Override
	public void linkStreamView(StreamView sv) {

		this.svLink = sv;
		this.add(streamBar, BorderLayout.SOUTH);
	}
	
	//###########################################################################################
	//GET and SET
	//###########################################################################################

	public JPopupMenu getClassColorPopup() {
		return classColorPopup;
	}

	public void setClassColorPopup(JPopupMenu classColorPopup) {
		this.classColorPopup = classColorPopup;
	}

	public JCheckBox[] getClassSelectionBox() {
		return classSelectionBox;
	}

	public void setClassSelectionBox(JCheckBox[] classSelectionBox) {
		this.classSelectionBox = classSelectionBox;
	}

	public int[] getSelectedAttr() {
		return selectedAttr;
	}

	public void setSelectedAttr(int[] selectedAttr) {
		this.selectedAttr = selectedAttr;
	}

	public int getResolution() {
		return resolution;
	}

	public void setResolution(int resolution) {
		this.resolution = resolution;
	}

	public Instance[][] getInstance_A() {
		return instance_A;
	}

	public void setInstance_A(Instance[][] instance_A) {
		this.instance_A = instance_A;
	}

	public Color[] getClassColor() {
		return classColor;
	}

	public void setClassColor(Color[] classColor) {
		this.classColor = classColor;
	}

	public FmDrawPanel getFmDP() {
		return fmDP;
	}

	public void setFmDP(FmDrawPanel fmDP) {
		this.fmDP = fmDP;
	}

	public Instances getData() {
		return data;
	}

	public void setData(Instances data) {
		this.data = data;
	}

	public boolean[] getSelectedClasses() {
		return selectedClasses;
	}

	public void setSelectedClasses(boolean[] selectedClasses) {
		this.selectedClasses = selectedClasses;
	}

	public JButton getClassColorButton() {
		return classColorButton;
	}

	public void setClassColorButton(JButton classColorButton) {
		this.classColorButton = classColorButton;
	}

	public double[] getAttrMin() {
		return attrMin;
	}

	public void setAttrMin(double[] attrMin) {
		this.attrMin = attrMin;
	}

	public double[] getAttrMax() {
		return attrMax;
	}

	public void setAttrMax(double[] attrMax) {
		this.attrMax = attrMax;
	}

	public JLabel[] getSliderLabel() {
		return sliderLabel;
	}

	public void setSliderLabel(JLabel[] sliderLabel) {
		this.sliderLabel = sliderLabel;
	}

	public JPopupMenu getColorModePopup() {
		return colorModePopup;
	}

	public void setColorModePopup(JPopupMenu colorModePopup) {
		this.colorModePopup = colorModePopup;
	}

	public JButton getColorModeButton() {
		return colorModeButton;
	}

	public void setColorModeButton(JButton colorModeButton) {
		this.colorModeButton = colorModeButton;
	}
	
	public JComboBox getComboBox_1() {
		return comboBox_1;
	}

	public void setComboBox_1(JComboBox comboBox_1) {
		this.comboBox_1 = comboBox_1;
	}

	public JComboBox getComboBox_2() {
		return comboBox_2;
	}

	public void setComboBox_2(JComboBox comboBox_2) {
		this.comboBox_2 = comboBox_2;
	}

	class ComboBoxRenderer  extends JLabel implements ListCellRenderer {

		public ComboBoxRenderer() {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object arg1,
				int arg2, boolean isSelected, boolean arg4) {


			if (isSelected) {
	            setBackground(list.getSelectionBackground());
	            setForeground(list.getSelectionForeground());
	        } else {
	            setBackground(list.getBackground());
	            setForeground(list.getForeground());
	        }

			setText(((Attribute)arg1).name());
			
			return this;
		}
	}

	public StreamView getSvLink() {
		return svLink;
	}

	public void setSvLink(StreamView svLink) {
		this.svLink = svLink;
	}

	public AccessNode[] getAccRootPack() {
		return accRootPack;
	}

	public void setAccRootPack(AccessNode[] accRootPack) {
		this.accRootPack = accRootPack;
	}

	public AccessPT getWatchedAccPT() {
		return watchedAccPT;
	}

	public void setWatchedAccPT(AccessPT watchedAccPT) {
		this.watchedAccPT = watchedAccPT;
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
}
