package gui_pt.DefaultPTV;

import gui_pt.accessLayer.util.AccessNode;
import gui_pt.drawObjects.DrawNode;
import gui_pt.gui.DrawPanel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import weka.classifiers.trees.pt.utils.FuzzyUtils.AGGREGATORS;

public class PostAGGRSelectDialog extends JDialog implements ActionListener, ListSelectionListener{
	
	JPanel mainPanel;
	JPanel buttonPanel;
	JButton okButton;
	JList<AGGREGATORS> aggr_List;
	DefaultListModel<AGGREGATORS> listModel;
	
	JTable paraTable;
	
	private DrawNode dn;
	private DrawPanel dp;
	
	//##########################################################################################################
	// Constructor
	//##########################################################################################################
	
	public PostAGGRSelectDialog(JFrame owner, DrawNode dn, DrawPanel dp)
	{
		super(owner);
		
		this.dn = dn;
		this.dp = dp;
		
		AGGREGATORS[] aggr_A = dn.getAccNode().getAccPT().getAggregators();
		
		
		
		listModel = new DefaultListModel<AGGREGATORS>();
		
		aggr_List = new JList<AGGREGATORS>(listModel);
		
		for(int i=0; i<aggr_A.length; i++)
		{
			listModel.addElement(aggr_A[i]);
		}
		
		paraTable = new JTable(0,0);
		
		aggr_List.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		aggr_List.addListSelectionListener(this);
		aggr_List.setLayoutOrientation(JList.VERTICAL);
		aggr_List.setVisibleRowCount(-1);
		
		okButton = new JButton("ok");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(okButton);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(new JScrollPane(aggr_List), BorderLayout.CENTER);
		mainPanel.add(paraTable, BorderLayout.EAST);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
				
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(mainPanel, BorderLayout.CENTER);
	}

	//##########################################################################################################
	// ActionListener
	//##########################################################################################################

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		if(arg0.getActionCommand().equals(okButton.getActionCommand()))
		{
			double[] paras = new double[paraTable.getRowCount()];
			for(int i=0; i<paraTable.getRowCount(); i++)
			{
				paras[i] = Double.parseDouble((String)paraTable.getValueAt(i, 0));
			}
			
			dn.setAggr(aggr_List.getSelectedValue().toString());
			
			if(dn.getAccNode().getAccPT().getAggregators()[aggr_List.getSelectedIndex()].isTNorm())
			{
				dn.getAccNode().setAggrType(AccessNode.AggrType.TNORM);
			}
			else if(dn.getAccNode().getAccPT().getAggregators()[aggr_List.getSelectedIndex()].isTCONorm())
			{
				dn.getAccNode().setAggrType(AccessNode.AggrType.TCONORM);
			}
			else if(dn.getAccNode().getAccPT().getAggregators()[aggr_List.getSelectedIndex()].isAverage())
			{
				dn.getAccNode().setAggrType(AccessNode.AggrType.AVERAGE);
			}
			dn.getAccNode().setAggr(aggr_List.getSelectedIndex(), paras);
			dp.getTcp().getAp().updatePlotter();
		}	
	}
	//##########################################################################################################
	// ListSelectionListener
	//##########################################################################################################

	@Override
	public void valueChanged(ListSelectionEvent arg0) 
	{
		mainPanel.remove(paraTable);
		int numParas = aggr_List.getSelectedValue().numParameters();
		paraTable = new JTable(numParas,1);
		for(int i=0; i<numParas; i++)
		{
			paraTable.setValueAt((1.0/numParas)+"", i, 0);
		}		
		mainPanel.add(paraTable, BorderLayout.EAST);
		this.revalidate();
	}

}
