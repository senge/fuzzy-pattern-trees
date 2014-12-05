package gui_pt.listener;

import gui_pt.drawHelper.DrawNorm;
import gui_pt.drawObjects.DrawDetail;
import gui_pt.editPanels.EditInnerNodesPanel;
import gui_pt.editPanels.FontDialog;
import gui_pt.gui.DrawPanel;
import gui_pt.visualisation.DefaultPTV;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class EditInnerNodePanelActionListener implements ActionListener{
	
	DefaultPTV dPTV;
	DrawPanel dp;
	int normCounter;
	DrawNorm dn;
	
	
	EditInnerNodesPanel einP;
	
	
	public EditInnerNodePanelActionListener(DefaultPTV dPTV, EditInnerNodesPanel einP, DrawNorm dn
								
								, int normCounter)
	{
		this.dPTV = dPTV;
		this.dp = einP.getDp();
		this.dn = dn;
		this.normCounter = normCounter;
		this.einP = einP;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(this.dp != null)
		{			
			if(arg0.getActionCommand().equals("normColor"))
			{
				Color color = JColorChooser
								.showDialog(this.dp, "test", Color.black);
				dn.setNormColor(color);
				this.dp.repaint();
				
				((JButton)arg0.getSource()).setBackground(color);
			}
			else if(arg0.getActionCommand().equals("normFont"))
			{
				FontDialog fd = new FontDialog(this.einP, dn);
			}
			else if(arg0.getActionCommand().equals("normSymbol"))
			{
				dn.setNormRep(DrawNorm.SYMBOL);
				this.dp.repaint();
			}
			else if(arg0.getActionCommand().equals("normString"))
			{
				dn.setNormRep(DrawNorm.STRING);
				this.dp.repaint();
			}
			else if(arg0.getActionCommand().equals("normCustom"))
			{
				dn.setNormRep(DrawNorm.CUSTOM);
	
				dn.setCustomText(einP.getTextField_t1()[normCounter].getText());
				this.dp.repaint();
			}
			else if(arg0.getActionCommand().equals("tbrowse"))
			{
				FileNameExtensionFilter fileNEF 
					= new FileNameExtensionFilter("Picture", "gif","jpg", "png");
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.setFileFilter(fileNEF);
				int i = jfc.showOpenDialog(this.einP);
				
				if(i == JFileChooser.APPROVE_OPTION)
				{
					File file = jfc.getSelectedFile();
					this.einP.gettImage()[normCounter] = this.einP.getToolkit().getImage(file.toString());
					this.einP.getImagePanel()[normCounter].repaint();
					dn.setNormImage(this.dp.getToolkit().getImage(file.toString()));
				}				
			}
			else if(arg0.getActionCommand().equals("dynamic"))
			{
				this.dp.calcMaxStringLength();
				this.dp.resizeINodes((int)(this.dp.getMaxLength()*1));
				this.dp.repaint();
			}
			else if(arg0.getActionCommand().equals("default"))
			{
				this.dp.resizeINodes(30);
				this.dp.repaint();
			}
			else if(arg0.getActionCommand().equals("custom"))
			{
				this.dp.resizeINodes((Integer)einP.getCustomSizeSpinner().getValue());
				this.dp.repaint();
			}
			else if(arg0.getActionCommand().equals("details"))
			{
				this.dp.setShowDetails(true);
				this.einP.enableAllDetailChecks(false);
			}
			else if(arg0.getActionCommand().equals("custom-details"))
			{
				this.dp.setShowDetails(false);
				this.einP.enableAllDetailChecks(true);
				this.einP.selectCustomDetailChecks(this.dp.getDetailsToShow());
			}
			else if(arg0.getActionCommand().equals("attrCheck"))
			{
				if(((JCheckBox)arg0.getSource()).isSelected())
				{
					this.dp.getDetailsToShow().add(DrawDetail.ATTRIBUTE_NAME);
				}
				else
				{
					this.dp.getDetailsToShow().remove(DrawDetail.ATTRIBUTE_NAME);
				}
			}
			else if(arg0.getActionCommand().equals("fuzzySetCheck"))
			{
				if(((JCheckBox)arg0.getSource()).isSelected())
				{
					this.dp.getDetailsToShow().add(DrawDetail.FUZZYSET);
				}
				else
				{
					this.dp.getDetailsToShow().remove(DrawDetail.FUZZYSET);
				}
			}
			else if(arg0.getActionCommand().equals("performanceCheck"))
			{
				if(((JCheckBox)arg0.getSource()).isSelected())
				{
					this.dp.getDetailsToShow().add(DrawDetail.PERFORMANCE);
				}
				else
				{
					this.dp.getDetailsToShow().remove(DrawDetail.PERFORMANCE);
				}
			}
			else if(arg0.getActionCommand().equals("outputCheck"))
			{
				if(((JCheckBox)arg0.getSource()).isSelected())
				{
					this.dp.getDetailsToShow().add(DrawDetail.OUTPUT);
				}
				else
				{
					this.dp.getDetailsToShow().remove(DrawDetail.OUTPUT);
				}
			}
		}

		
		
	}

}
