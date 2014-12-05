package gui_pt.listener;

import gui_pt.drawHelper.DrawTreeBuilder;
import gui_pt.editPanels.EditInnerNodesPanel;
import gui_pt.gui.DrawPanel;
import gui_pt.visualisation.DefaultPTV;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

public class DefaultPTVToolBarActionListener implements ActionListener{
	
	private DefaultPTV dPTV;
	private DrawPanel showingDP = null;
	
	//################################################################################################
	// Constructor
	//################################################################################################
	
	public DefaultPTVToolBarActionListener(DefaultPTV dPTV)
	{
		this.dPTV = dPTV;
	}
	
	//################################################################################################
	// ActionListener
	//################################################################################################

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(dPTV.getCardPanel() != null)
		{
			if(arg0.getActionCommand().equals("zIN"))
			{
			
				showingDP.zoomIN();
				showingDP.repaint();
			}
			else if(arg0.getActionCommand().equals("zOUT"))
			{
				showingDP.zoomOut();
				showingDP.repaint();
			}

			else if(arg0.getActionCommand().equals("antialiasing"))
			{
				if(dPTV.getToggleAntialiasing().isSelected())
				{
					showingDP.setAlaising(true);
				}
				else
				{
					showingDP.setAlaising(false);
				}
				
				showingDP.repaint();
			}
			else if(arg0.getActionCommand().equals("rect"))
			{
				showingDP.setEdgeRect(((JToggleButton)arg0.getSource()).isSelected());
				showingDP.repaint();
			}
			else if(arg0.getActionCommand().equals("nodeMide"))
			{
				showingDP.setNodeMidEdge(((JToggleButton)arg0.getSource()).isSelected());
				DrawTreeBuilder dtb = new DrawTreeBuilder();
				dtb.setNodeLocations(showingDP.getDrawTree(), showingDP);
				showingDP.repaint();
			}
			else if(arg0.getActionCommand().equals("edit"))
			{
				JDialog jd = new JDialog();
				EditInnerNodesPanel einP = new EditInnerNodesPanel(dPTV, showingDP);
				jd.add(new JScrollPane(einP));
				jd.setModal(true);
				jd.setSize(new Dimension(600,200));
				jd.setLocation(300,200);
				jd.setVisible(true);
			}
			if(arg0.getActionCommand().equals("next"))
			{
				dPTV.setCardIndex((dPTV.getCardIndex()+1)%dPTV.getCardPanel().getComponentCount());
				((CardLayout)dPTV.getCardPanel().getLayout()).next(dPTV.getCardPanel());
				
				showingDP = (DrawPanel)dPTV.getCardPanel().getComponent(dPTV.getCardIndex());
				dPTV.updateToolBar(showingDP.getSettings());
			}
			else if(arg0.getActionCommand().equals("prev"))
			{
				if(dPTV.getCardIndex() > 0)
				{
					dPTV.setCardIndex(dPTV.getCardIndex()-1);
				}
				else
				{
					dPTV.setCardIndex(dPTV.getCardPanel().getComponentCount()-1);
				}
				((CardLayout)dPTV.getCardPanel().getLayout()).previous(dPTV.getCardPanel());
				
				showingDP = (DrawPanel)dPTV.getCardPanel().getComponent(dPTV.getCardIndex());
				dPTV.updateToolBar(showingDP.getSettings());
			}
			else if(arg0.getActionCommand().equals("showTreeX"))
			{
				JButton button = (JButton)arg0.getSource();
				JPanel panel = (JPanel)button.getParent();
				
				int index = panel.getComponentZOrder(button);
				
				dPTV.setWatchedAccRootPack(dPTV.getSvLink().getTreeHistory().get(index));
				
				int watchedIndex = index + 1;
				if(dPTV.getSvLink().getUpdateCount() > dPTV.getSvLink().getMaxHistorySize())
				{
					watchedIndex = dPTV.getSvLink().getUpdateCount()
							- dPTV.getSvLink().getMaxHistorySize()
							+ index
							+ 1;
				}
				dPTV.setWatchedIndex(watchedIndex);
				dPTV.getCurrLabel().setText("<html><font color = 'FF0000'>"+watchedIndex+"</font></html>");
				dPTV.update(dPTV.getWatchedAccRootPack());
			}
			else if(arg0.getActionCommand().equals("live"))
			{
				dPTV.setWatchedAccRootPack(null);
				dPTV.setWatchedIndex(dPTV.getSvLink().getUpdateCount());
				dPTV.getCurrLabel().setText("<html><font color = 'FF0000'>live</font></html>");
				dPTV.update(dPTV.getSvLink().getTreeHistory().get(
						dPTV.getSvLink().getTreeHistory().size()-1));
			}
			else if(arg0.getActionCommand().equals("reset"))
			{
				showingDP.getDrawTree().getRoot().resetTransformation();
				showingDP.calcLayerNew();
			}
			else if(arg0.getActionCommand().equals("toLatex"))
			{
				JPopupMenu popupM = new JPopupMenu();
				
				for(int i=0; i < dPTV.getTcp().getDp_A().length; i++)
				{
					JCheckBox checkBox = new JCheckBox(dPTV.getTcp().getClass_Names()[i]);
					popupM.add(checkBox);
				}
				
				JButton button = new JButton("Ok");
				button.setActionCommand("toLatexOk");
				button.addActionListener(this);
				popupM.add(button);
				
				popupM.show(dPTV
						,dPTV.getToLatex().getX()
						,dPTV.getToLatex().getY()+25);

			}
			else if(arg0.getActionCommand().equals("toLatexOk"))
			{	
				JPopupMenu popupM = (JPopupMenu)((JButton)arg0.getSource()).getParent();
				
				JFileChooser jfc = new JFileChooser();
				
				int save = jfc.showSaveDialog(this.dPTV);
				
				if(save == 0)
				{
					File file = jfc.getSelectedFile();
					
					try {

						for(int i=0; i<dPTV.getTcp().getDp_A().length; i++)
						{
							if(((JCheckBox)popupM.getComponent(i)).isSelected())
							{
								File iFile = new File(file.toString()+"_"+i+".tex");
								FileWriter fw = new FileWriter(iFile);
								BufferedWriter bw = new BufferedWriter(fw);
								bw.write(this.dPTV.getTcp().getDp_A()[i].toLatex(1.0));
								bw.close();
								fw.close();
							}							
						}						
											
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}		
	}
	
	//####################################################################################################
	// GET and SET
	//####################################################################################################

	public DrawPanel getShowingDP() {
		return showingDP;
	}

	public void setShowingDP(DrawPanel showingDP) {
		this.showingDP = showingDP;
	}
}
