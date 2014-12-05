package gui_pt.fse.listener;

import gui_pt.fse.AttributeTreePopUpMenu;
import gui_pt.fse.FSE_Frame;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

public class AttributeTreeMouseListener implements MouseListener{
	
	FSE_Frame fse_Frame;
	
	public AttributeTreeMouseListener(FSE_Frame fse_Frame){
		
		this.fse_Frame = fse_Frame;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent) {

		JTree tree = this.fse_Frame.getAttributeTree().getTree();
		
		if(mouseEvent.isPopupTrigger())
		{
			TreePath treePath = tree.getPathForLocation(
					mouseEvent.getX(), mouseEvent.getY() );

			tree.setSelectionPath(treePath);
			
			if(treePath.getPathCount() == 2)
			{
				AttributeTreePopUpMenu atPUM = new AttributeTreePopUpMenu(
						this.fse_Frame);

				atPUM.show(mouseEvent.getComponent(),mouseEvent.getX(), mouseEvent.getY());
			}		
		}
		
	}

}
