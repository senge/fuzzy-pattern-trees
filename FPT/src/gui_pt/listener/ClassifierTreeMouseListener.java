package gui_pt.listener;

import gui_pt.gui.ClassifierTreePopupMenu;
import gui_pt.gui.MainWindow;
import gui_pt.guiUtil.UserObjectWrapper;
import gui_pt.plugin.PTVisualisation;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class ClassifierTreeMouseListener implements MouseListener{
	
	MainWindow mainW;
	
	public ClassifierTreeMouseListener(MainWindow mainW){
		
		this.mainW = mainW;
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
		
		JTree tree = this.mainW.getcTree().getTree();
		
		TreePath treePath = tree.getPathForLocation(
				mouseEvent.getX(), mouseEvent.getY() );

		if(mouseEvent.getButton() == MouseEvent.BUTTON1
				&& treePath != null)
		{
			if(mouseEvent.getClickCount() == 2
					&& treePath.getPathCount() == 4)
			{
				DefaultMutableTreeNode ptvNode = (DefaultMutableTreeNode)
													treePath.getLastPathComponent();
				UserObjectWrapper uow = (UserObjectWrapper)
											ptvNode.getUserObject();
				if(!uow.isOpen())
				{
					mainW.openVisualisation((PTVisualisation)uow.obj
							, ptvNode);
				}
			}
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent) {

		JTree tree = this.mainW.getcTree().getTree();
		
		TreePath treePath = tree.getPathForLocation(
				mouseEvent.getX(), mouseEvent.getY() );
		
		tree.setSelectionPath(treePath);
		
		if(mouseEvent.isPopupTrigger()
				&& treePath != null)
		{
			if(treePath.getPathCount() == 3)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)treePath.getLastPathComponent();			
				UserObjectWrapper uop = ((UserObjectWrapper)node.getUserObject());
				
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode)treePath.getPathComponent(1);
				
				String mode_S = (String)parent.getUserObject();
				
				int mode = 0;
				if(mode_S.equals("Online")) mode = 1;
				
				ClassifierTreePopupMenu ctpm = new ClassifierTreePopupMenu(mainW, uop, mode, node, mouseEvent);
				ctpm.show(mouseEvent.getComponent(),mouseEvent.getX(), mouseEvent.getY());
			}		
		}
		
	}

}
