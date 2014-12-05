package gui_pt.gui;

import gui_pt.accessLayer.util.AccessNode;
import gui_pt.drawHelper.DrawTree;
import gui_pt.drawHelper.TreeClassPack;
import gui_pt.listener.Plotter2DMouseAndMotionListener;
import gui_pt.plotter2D.Plotter2D;
import gui_pt.pt.Calculations;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import weka.core.DenseInstance;
import weka.core.Instance;

public class AnalysePanel extends JPanel{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 7340482290193091781L;
	JPanel mainPanel;
	JPanel northPanel;
	
	JPanel flowRapper1;
	
	JPanel classOutputPanel;
	JPanel outputPanel;
	JLabel outputLabel;
	JLabel outputL;
	
	JPanel[] subPanel;
	JPanel[] subPanelDis;
	
	Plotter2D[] plotter2D;	
	
	private TreeClassPack tcp;
	private AccessNode accRoot;
	private DrawTree dT;
	private int[] attr;
	private int[] disabledAttr;
	private int resolution = 200;
	
	ArrayList<JSlider> attrSliderList = new ArrayList<JSlider>();
	
	//################################################################################################
	// CONSTRUCTOR
	//################################################################################################
	
	/**
	 * Default Constructor
	 */
	public AnalysePanel(){}
	
	/**
	 * 
	 * @param root
	 */
	public AnalysePanel(AccessNode accRoot, DrawTree dT)
	{	
		this.buildAnalyseFrame(accRoot);
	}
	
	//################################################################################################
	// METHODES
	//################################################################################################
	
	/**
	 * @param root
	 */
	public void buildAnalyseFrame(AccessNode accRoot)
	{
		this.accRoot = accRoot;
		
		//Vorarbeiten
		if(this.tcp.getProtoInstance() == null)
		{
			this.tcp.setProtoInstance(new DenseInstance(tcp.getAccessPT().getData().get(0)));
		}
		
		double[] attrMin = Calculations.calcMin(tcp.getAccessPT().getData());
		double[] attrMax = Calculations.calcMax(tcp.getAccessPT().getData());

		int[][] exAttr = Calculations.extractAttr(accRoot);
		attr = exAttr[0];
		disabledAttr = exAttr[1];

		subPanel = new JPanel[attr.length];
		plotter2D = new Plotter2D[attr.length];
		
		mainPanel = new JPanel();
//		mainPanel.setLayout(new GridLayout(attr.length+disabledAttr.length,1));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		
		flowRapper1 = new JPanel();
		flowRapper1.setLayout(new FlowLayout(FlowLayout.LEFT));
		flowRapper1.add(mainPanel);
		
		for(int i=0; i<attr.length; i++)
		{
			plotter2D[i] = new Plotter2D();
			plotter2D[i].setxMin(attrMin[attr[i]]);
			plotter2D[i].setxMax(attrMax[attr[i]]);
			plotter2D[i].setxRange(Math.abs(attrMax[attr[i]]-attrMin[attr[i]]));
			
			double[] x = new double[resolution];
			for(int j=0; j<x.length; j++)
			{
				x[j] = attrMin[attr[i]]+plotter2D[i].getxRange()/(double)(resolution-1)*j;
			}
			
			double[] values = new double[resolution];
			Instance helpInstance = new DenseInstance(this.tcp.getProtoInstance());
			Calculations.p2DValues(accRoot, helpInstance, values, x, attr[i]);
			plotter2D[i].setValues(values);
			plotter2D[i].setVX(x);
			plotter2D[i].setCurrentX(this.tcp.getProtoInstance().value(attr[i]));
			plotter2D[i].setCurrentY(accRoot.fire(this.tcp.getProtoInstance()));
			plotter2D[i].setAttrName(tcp.getAccessPT().getData().attribute(attr[i]).name());
			plotter2D[i].setAttrIndex(tcp.getAccessPT().getData().attribute(attr[i]).index());
				
			Plotter2DMouseAndMotionListener p2DMaML = new Plotter2DMouseAndMotionListener(this, plotter2D[i]);
			plotter2D[i].addMouseListener(p2DMaML);
			plotter2D[i].addMouseMotionListener(p2DMaML);
			
			helpInstance = new DenseInstance(this.tcp.getProtoInstance());
			Calculations.p2DValues(accRoot
					, helpInstance
					, plotter2D[i].getValues()
					, plotter2D[i].getVX()
					, attr[i]);
			double fire = accRoot.fire(this.tcp.getProtoInstance());
			plotter2D[i].setCurrentY(fire);
			
			//for difference quotient
				//Plus
			helpInstance = new DenseInstance(this.tcp.getProtoInstance());
			
			if((plotter2D[i].getCurrentX()
					+ plotter2D[i].getxDelta()) > plotter2D[i].getxMax() )
			{
				helpInstance.setValue(attr[i], plotter2D[i].getxMax());				
				plotter2D[i].setCurrXDeltaP(plotter2D[i].getxMax());
			}
			else
			{
				helpInstance.setValue(attr[i], plotter2D[i].getCurrentX()
						+ plotter2D[i].getxDelta());				
				plotter2D[i].setCurrXDeltaP(plotter2D[i].getCurrentX()
						+plotter2D[i].getxDelta());
			}

			fire = accRoot.fire(helpInstance);
			plotter2D[i].setCurrYDeltaP(fire);
			
				//Minus 
			helpInstance = new DenseInstance(this.tcp.getProtoInstance());
			if((plotter2D[i].getCurrentX()
					- plotter2D[i].getxDelta()) < plotter2D[i].getxMin())
			{
				helpInstance.setValue(attr[i], plotter2D[i].getxMin());				
				plotter2D[i].setCurrXDeltaM(plotter2D[i].getxMin());
			}
			else
			{
				helpInstance.setValue(attr[i], plotter2D[i].getCurrentX()
						- plotter2D[i].getxDelta());				
				plotter2D[i].setCurrXDeltaM(plotter2D[i].getCurrentX()
						-plotter2D[i].getxDelta());
			}
			fire = accRoot.fire(helpInstance);
			plotter2D[i].setCurrYDeltaM(fire);
						
			subPanel[i] = new JPanel();
			subPanel[i].setLayout(new BorderLayout());
			subPanel[i].setPreferredSize(new Dimension(500,140));
			subPanel[i].setBorder(new LineBorder(Color.black));
			subPanel[i].add(plotter2D[i], BorderLayout.CENTER);
			
			mainPanel.add(subPanel[i]);
		}
		
		// Handle Disabled Attributes;
		subPanelDis = new JPanel[this.disabledAttr.length];
		attrSliderList.clear();
		for(int i=0; i<this.disabledAttr.length; i++)
		{
			//JLabel attrNameLabel = new JLabel(accRoot.getData().attribute(disabledAttr[i]).name());
			//TODO make slider for Nominal
			JSlider attrSlider = new JSlider(JSlider.HORIZONTAL
					, (int)(attrMin[disabledAttr[i]]*1000)
					, (int)(attrMax[disabledAttr[i]]*1000)
					,(int)(tcp.getProtoInstance().value(disabledAttr[i])*1000));
			attrSliderList.add(attrSlider);
			attrSlider.setName(""+i);
			
			TitledBorder tBorder = new TitledBorder(tcp.getAccessPT().getData().attribute(disabledAttr[i]).name()
									+ " " + tcp.getProtoInstance().value(disabledAttr[i]));
			
			attrSlider.addChangeListener(new ChangeListener(){

				@Override
				public void stateChanged(ChangeEvent arg0) {
					
					JSlider source = (JSlider)arg0.getSource();
					
					tcp.getProtoInstance().setValue(disabledAttr[new Integer(source.getName())]
							,source.getValue()/1000d);
					
					TitledBorder tb = (TitledBorder)subPanelDis[Integer.parseInt(source.getName())].getBorder();
					tb.setTitle(AnalysePanel.this.tcp.getAccessPT().getData().attribute(disabledAttr[new Integer(source.getName())]).name()
									+ " "
									+ tcp.getProtoInstance().value(disabledAttr[new Integer(source.getName())]));

					subPanelDis[Integer.parseInt(source.getName())].repaint();
					
					for(int j=0; j< attr.length; j++)
					{
						plotter2D[j].repaint();
						classOutputPanel.repaint();
					}
					tcp.getdPTV().notifyConnections(disabledAttr[new Integer(source.getName())]);
				}				
			});
		
			subPanelDis[i] = new JPanel();
			subPanelDis[i].setLayout(new BorderLayout());
			subPanelDis[i].setBorder(tBorder);
			subPanelDis[i].setPreferredSize(new Dimension(400,40));
			subPanelDis[i].add(attrSlider, BorderLayout.NORTH);
			
			mainPanel.add(subPanelDis[i]);
		}
		
		// Output whole Tree per Class
		classOutputPanel = new DrawSiblingPanel(this.tcp);
		classOutputPanel.setPreferredSize(new Dimension(this.getWidth(), 100));
				
		// Output current Node
		outputLabel = new JLabel(""+(int)(accRoot.fire(this.tcp.getProtoInstance())*1000)/1000d);
		outputL = new JLabel("Output: ");
		
		outputPanel = new JPanel();
		outputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		outputPanel.add(outputL);
		outputPanel.add(outputLabel);
		
		// northPanel
		northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		northPanel.add(classOutputPanel, BorderLayout.CENTER);
		northPanel.add(outputPanel, BorderLayout.SOUTH);

		this.removeAll();
		this.setLayout(new BorderLayout());
		this.add(northPanel, BorderLayout.NORTH);
		this.add(new JScrollPane(flowRapper1), BorderLayout.CENTER);
	
		this.validate();
	}
	
	public void updatePlotter(){
	
		for(int i=0; i<plotter2D.length; i++)
		{
System.out.println("updagte");
			plotter2D[i].getCurrentX();
			plotter2D[i].repaint();
		}		
	}
	
	
	
	//GET and SET #########################################################################

//	public Instance getProtoInstance() {
//		return protoInstance;
//	}
//
//	public void setProtoInstance(Instance protoInstance) {
//		this.protoInstance = protoInstance;
//	}

//	public JSlider[] getAttrValueSlider() {
//		return attrValueSlider;
//	}
//
//	public void setAttrValueSlider(JSlider[] attrValueSlider) {
//		this.attrValueSlider = attrValueSlider;
//	}

	public Plotter2D[] getPlotter2D() {
		return plotter2D;
	}

	public void setPlotter2D(Plotter2D[] plotter2d) {
		plotter2D = plotter2d;
	}

	public int[] getAttr() {
		return attr;
	}

	public void setAttr(int[] attr) {
		this.attr = attr;
	}

	public JLabel getOutputLabel() {
		return outputLabel;
	}

	public void setOutputLabel(JLabel outputLabel) {
		this.outputLabel = outputLabel;
	}

	public JPanel getClassOutputPanel() {
		return classOutputPanel;
	}

	public void setClassOutputPanel(JPanel classOutputPanel) {
		this.classOutputPanel = classOutputPanel;
	}

	public TreeClassPack getTcp() {
		return tcp;
	}

	public void setTcp(TreeClassPack tcp) {
		this.tcp = tcp;
	}

	public AccessNode getAccRoot() {
		return accRoot;
	}

	public void setAccRoot(AccessNode accRoot) {
		this.accRoot = accRoot;
	}

	public ArrayList<JSlider> getAttrSliderList() {
		return attrSliderList;
	}

	public void setAttrSliderList(ArrayList<JSlider> attrSliderList) {
		this.attrSliderList = attrSliderList;
	}

	public int[] getDisabledAttr() {
		return disabledAttr;
	}

	public void setDisabledAttr(int[] disabledAttr) {
		this.disabledAttr = disabledAttr;
	}

}