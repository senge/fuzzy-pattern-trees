package gui_pt.fse;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class CPointPanel extends JPanel implements ActionListener, ChangeListener, Comparable{
	
	FuzzySetEditorPanel fseP;
	CustomPointListPanel cplP;
	
	JPanel helpP;
	JPanel helpP1;
	JPanel helpP2;
	JPanel helpP3;
	JPanel helpP3_1;
	
	JSpinner spinner1;
	JSpinner spinner2;
	
	JLabel cpID;
	CustomPoint cp;
	
	double min;
	double max;
	
	public CPointPanel(CustomPoint cp, FuzzySetEditorPanel fseP, CustomPointListPanel cplP){
		
		this.fseP = fseP;
		this.cplP = cplP;
		this.cp = cp;
		
		this.min = fseP.getCustomFS().getMin();
		this.max = fseP.getCustomFS().getMax();
		
		this.setLayout(new BorderLayout());
		this.setBorder(new EtchedBorder());
		
		cpID = new JLabel();
		JLabel labelX = new JLabel("x: ");
		JLabel labelY = new JLabel("y: ");
		spinner1 = new JSpinner(new SpinnerNumberModel(cp.getX(),min,max,0.001));
		spinner1.setPreferredSize(new Dimension(60,20));
		spinner1.addChangeListener(this);
		spinner1.setName("spinner1");
		
		spinner2 = new JSpinner(new SpinnerNumberModel(cp.getY(),0,1,0.001));
		spinner2.setPreferredSize(new Dimension(60,20));
		spinner2.addChangeListener(this);
		spinner2.setName("spinner2");

		JButton delete = new JButton("x");
		delete.setPreferredSize(new Dimension(15,15));
		delete.setActionCommand("delete");
		delete.addActionListener(this);
		
		helpP1 = new JPanel();
		helpP1.setLayout(new FlowLayout(FlowLayout.LEFT));
		helpP1.add(labelX);
		helpP1.add(spinner1);
		
		helpP2 = new JPanel();
		helpP2.setLayout(new FlowLayout(FlowLayout.LEFT));
		helpP2.add(labelY);
		helpP2.add(spinner2);
		
		helpP = new JPanel();
		helpP.setLayout(new GridLayout(2,1));
		helpP.add(helpP1);
		helpP.add(helpP2);
		
		helpP3_1 = new JPanel();
		helpP3_1.setLayout(new FlowLayout(FlowLayout.LEFT));
		helpP3_1.add(delete);
		
		helpP3 = new JPanel();
		helpP3.setLayout(new BorderLayout());
		helpP3.add(cpID, BorderLayout.NORTH);
		helpP3.add(helpP3_1, BorderLayout.CENTER);
	
		this.add(helpP, BorderLayout.CENTER);
		this.add(helpP3, BorderLayout.WEST);
		
		this.setPreferredSize(new Dimension(150,70));
	}
	
	public void updateSpinner(){
		
		this.spinner1.setValue(cp.getX());
		this.spinner2.setValue(cp.getY());
		
	}
	
	//############################################################################
	//ActionListener
	//############################################################################
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		//changes should be stored
		fseP.setUnsafedModified(true);

		if(arg0.getActionCommand().equals("delete"))
		{
			if(this.cp.equals(this.fseP.getFseDrawP().getSelectedPoint()))
			{
				this.fseP.getFseDrawP().setSelectedPoint(null);
			}

			this.fseP.getCustomFS().getCpTreeSet().remove(this.cp);
			this.fseP.getFseDrawP().repaint();
			this.cplP.buildPanelList();
		}
		
	}
	//##################################################################################
	//ChangeListener
	//##################################################################################
	
	@Override
	public void stateChanged(ChangeEvent arg0) {
		
		//changes should be stored
		fseP.setUnsafedModified(true);

		if(((JSpinner)arg0.getSource()).getName().equals("spinner1"))
		{
			
			double value = ((Double)this.spinner1.getValue());
			
			if(this.fseP.getCustomFS().getCpTreeSet().higher(cp) != null
					&& this.fseP.getCustomFS().getCpTreeSet().lower(cp) != null)
			{
				if(value < this.fseP.getCustomFS().getCpTreeSet().higher(cp).getX()
					&& value > this.fseP.getCustomFS().getCpTreeSet().lower(cp).getX())
				{
					this.cp.setX(value);
					this.fseP.getFseDrawP().repaint();
				}
				else
				{
					this.spinner1.setValue(this.cp.getX());
				}
			}
			else if(this.fseP.getCustomFS().getCpTreeSet().higher(cp) != null)
			{
				if(value < this.fseP.getCustomFS().getCpTreeSet().higher(cp).getX()
					&& value >= min)
				{
					this.cp.setX(value);
					this.fseP.getFseDrawP().repaint();
				}
				else
				{
					this.spinner1.setValue(this.cp.getX());
				}
			}
			else if(this.fseP.getCustomFS().getCpTreeSet().lower(cp) != null)
			{
				if(value <= max
					&& value > this.fseP.getCustomFS().getCpTreeSet().lower(cp).getX())
				{
					this.cp.setX(value);
					this.fseP.getFseDrawP().repaint();
				}
				else
				{
					this.spinner1.setValue(this.cp.getX());
				}
			}
			else
			{
				if(value <= max
						&& value >= min)
					{
						this.cp.setX(value);
						this.fseP.getFseDrawP().repaint();
					}
					else
					{
						this.spinner1.setValue(this.cp.getX());
					}
			}

		}
		else if(((JSpinner)arg0.getSource()).getName().equals("spinner2"))
		{
				this.cp.setY(((Double)this.spinner2.getValue()));
				this.fseP.getFseDrawP().repaint();
		}
		
	}
	
	//##############################################################################
	//Comparable
	//##############################################################################
	@Override
	public int compareTo(Object arg0) {

		return this.getCp().compareTo(((CPointPanel)arg0).getCp());
	}
	
	//##############################################################################
	//GET and SET
	//##############################################################################

	public JLabel getCpID() {
		return cpID;
	}

	public void setCpID(JLabel cpID) {
		this.cpID = cpID;
	}

	public CustomPoint getCp() {
		return cp;
	}

	public void setCp(CustomPoint cp) {
		this.cp = cp;
	}


}
