package gui_pt.listener;

import gui_pt.drawHelper.DrawNorm;
import gui_pt.editPanels.EditInnerNodesPanel;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DefaultDocumentListener implements DocumentListener{
	
	EditInnerNodesPanel einP;
	DrawNorm dn;
	int normCounter;
	
	public DefaultDocumentListener(EditInnerNodesPanel einP
									, DrawNorm dn
									, int normCounter)
	{
		this.einP = einP;
		this.dn = dn;
		this.normCounter = normCounter;
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		
		dn.setCustomText(einP.getTextField_t1()[normCounter].getText());		
		einP.getDp().repaint();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {

		dn.setCustomText(einP.getTextField_t1()[normCounter].getText());
		einP.getDp().repaint();
		
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		
		dn.setCustomText(einP.getTextField_t1()[normCounter].getText());
		einP.getDp().repaint();
	}

}
