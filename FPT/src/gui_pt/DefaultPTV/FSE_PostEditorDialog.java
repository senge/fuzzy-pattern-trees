package gui_pt.DefaultPTV;

import gui_pt.drawObjects.DrawNode;
import gui_pt.fse.CFS_IdentityWraper;
import gui_pt.fse.CustomFuzzySet;
import gui_pt.fse.CustomPoint;
import gui_pt.fse.FSEDrawPanel;
import gui_pt.fse.FuzzySetEditorPanel;
import gui_pt.fse.Histogramm;
import gui_pt.fse.util.FSE_Util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import weka.classifiers.trees.pt.FuzzySet.CPLFS;

public class FSE_PostEditorDialog extends JDialog implements ActionListener{
	
	JPanel mainPanel;
	
	JPanel leftPanel;
	JPanel fsSelectPanel;
	
	JPanel buttonPanel;
	JButton assumeButton;
	JButton newButton;
	JLabel 	trennLabel;
	JButton okButton;
	JButton cancelButton;
	
	private DrawNode dn;
	private FuzzySetEditorPanel fsePanel;
	private FsSelectDrawPanel selectedPanel = null;
	private int idCount = 0;
	
	//##########################################################################################################
	// Constructor
	//##########################################################################################################
	
	public FSE_PostEditorDialog(JFrame owner, DrawNode dn){
		super(owner);
		
		this.dn = dn;
		
		CustomFuzzySet cfs = buildCFS(dn.getPointSelection().get(dn.getSelectedPoints()));
						
		CFS_IdentityWraper cfs_Identity = new CFS_IdentityWraper(cfs, dn.getAccNode().getAttribute());
		
		try
		{
			cfs_Identity.setHisPerClass(Histogramm.getHistogrammsFromData(dn.getAccNode().getAccPT().getData())[dn.getAttributeIndex()]);
		}
		catch(NullPointerException e)
		{
			//Do Nothing
		}
								
		fsePanel = new FuzzySetEditorPanel();
		fsePanel.setCfs_Identity(cfs_Identity);
		fsePanel.getCPListP().buildPanelList();
		
		FSEDrawPanel fseDrawP = new FSEDrawPanel(fsePanel);
		fseDrawP.setMin(cfs_Identity.getM_Cfs().getMin());
		fseDrawP.setMax(cfs_Identity.getM_Cfs().getMax());
		
		fsePanel.setFseDrawP(fseDrawP);
		
		assumeButton = new JButton("Assume");
		assumeButton.setActionCommand("assume");
		assumeButton.addActionListener(this);
		
		newButton = new JButton("New FuzzySet");
		newButton.setActionCommand("new");
		newButton.addActionListener(this);
		
		trennLabel = new JLabel("||");
		
		okButton = new JButton("Ok");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(assumeButton);
		buttonPanel.add(newButton);
		buttonPanel.add(trennLabel);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		fsSelectPanel = new JPanel();
		
		buildFsSelectDP();
		
		leftPanel = new JPanel();
		leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		leftPanel.add(fsSelectPanel);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(fsePanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		mainPanel.add(new JScrollPane(leftPanel), BorderLayout.WEST);
				
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(mainPanel, BorderLayout.CENTER);
		
	}
	
	//##########################################################################################################
	// Methods
	//##########################################################################################################
	
	public CustomFuzzySet buildCFS(ArrayList<double[]> points){
		
		CustomFuzzySet cfs = new CustomFuzzySet(dn.getAttMin(), dn.getAttMax());

		for(int i=0; i<points.size(); i++)
		{
			CustomPoint cp = new CustomPoint(
					points.get(i)[0]
					,points.get(i)[1]);
			cp.setID(i);
			cfs.getCpTreeSet().add(cp);
		}
		
		return cfs;
	}
	
	public void buildFsSelectDP(){
		
		fsSelectPanel.removeAll();		
		fsSelectPanel.setLayout(new BoxLayout(fsSelectPanel, BoxLayout.PAGE_AXIS));
		
		for(int i=0; i<dn.getPointSelection().size(); i++)
		{			
			addFsSDP(dn.convertToDrawPoints(dn.getPointSelection().get(i), dn.getFak()));
		}	
	}
	
	public void addFsSDP(ArrayList<double[]> points){
		
		FsSelectDrawPanel fsSDP = new FsSelectDrawPanel(points, this);
		if(idCount == dn.getSelectedPoints()){
			fsSDP.setBackground(Color.red);
			this.selectedPanel = fsSDP;
		}
		fsSDP.setId(idCount++);
		
		fsSDP.setPreferredSize(new Dimension(40,20));
		fsSelectPanel.add(fsSDP);
	}
	
	public ArrayList<double[]> convertCfsToPoints(CustomFuzzySet cfs){
		
		ArrayList<double[]> points = new ArrayList<double[]>();
		
		for(CustomPoint cp: cfs.getCpTreeSet())
		{
			double[] point = {cp.getX(), cp.getY()};
			points.add(point);
		}
		
		return points;
	}
	
	//##########################################################################################################
	// ActionListener
	//##########################################################################################################

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(arg0.getActionCommand().equals("new"))
		{
			dn.getPointSelection().add(convertCfsToPoints(fsePanel.getCustomFS()));
			addFsSDP(dn.convertToDrawPoints(
							dn.getPointSelection().get(dn.getPointSelection().size()-1), dn.getFak()));
			this.validate();
		}
		else if(arg0.getActionCommand().equals("assume"))
		{
			dn.getPointSelection().set(this.selectedPanel.getId(),convertCfsToPoints(fsePanel.getCustomFS()));
			this.selectedPanel.setPoints(dn.convertToDrawPoints(
					dn.getPointSelection().get(this.selectedPanel.getId()), dn.getFak()));
			this.repaint();
			this.validate();
		}
		else if(arg0.getActionCommand().equals("ok"))
		{
			dn.setPoints(selectedPanel.getPoints());
			dn.setSelectedPoints(selectedPanel.getId());
			
			CPLFS fs = FSE_Util.CustomFSToCPLFS(this.fsePanel.getCustomFS());
			
			dn.getAccNode().setFuzzySet(fs);
		}
	}

	public FsSelectDrawPanel getSelectedPanel() {
		return selectedPanel;
	}

	public void setSelectedPanel(FsSelectDrawPanel selectedPanel) {
		this.selectedPanel = selectedPanel;
	}

	public FuzzySetEditorPanel getFsePanel() {
		return fsePanel;
	}

	public void setFsePanel(FuzzySetEditorPanel fsePanel) {
		this.fsePanel = fsePanel;
	}

	public DrawNode getDn() {
		return dn;
	}

	public void setDn(DrawNode dn) {
		this.dn = dn;
	}
}
