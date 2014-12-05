package gui_pt.fse.listener;

import gui_pt.fse.CFS_IdentityWraper;
import gui_pt.fse.FSE_Frame;
import gui_pt.fse.FuzzySetProject;
import gui_pt.fse.FuzzySetTreePopupMenu;
import gui_pt.fse.helper.AttributeWrapper;
import gui_pt.fse.helper.ClassWrapper;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import weka.classifiers.trees.pt.FuzzySet;

public class FuzzySetTreeMouseListener implements MouseListener{
	
	FSE_Frame fse_Frame;
	
	public FuzzySetTreeMouseListener(FSE_Frame fse_Frame){
		
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

		JTree tree = this.fse_Frame.getFuzzySetTree().getTree();
		
		TreePath treePath = tree.getPathForLocation(
				mouseEvent.getX(), mouseEvent.getY() );
		
		if(mouseEvent.isPopupTrigger()
				&& treePath != null)
		{
			tree.setSelectionPath(treePath);
			
			if(((DefaultMutableTreeNode)treePath.getLastPathComponent()).getUserObject()
					instanceof AttributeWrapper)
			{
				if(((DefaultMutableTreeNode)
								treePath
								.getPathComponent(2))
								.getUserObject()
								instanceof ClassWrapper)
				{
					FuzzySetTreePopupMenu atPUM = new FuzzySetTreePopupMenu(
						this.fse_Frame, FuzzySetTreePopupMenu.OPTION_ATTRIBUTE);

					atPUM.show(mouseEvent.getComponent(),mouseEvent.getX(), mouseEvent.getY());
				}
			}
			
			else if(((DefaultMutableTreeNode)treePath.getLastPathComponent()).getUserObject()
					instanceof FuzzySetProject)
			{
				FuzzySetTreePopupMenu atPUM = new FuzzySetTreePopupMenu(
						this.fse_Frame, FuzzySetTreePopupMenu.OPTION_PROJECT);

				atPUM.show(mouseEvent.getComponent(),mouseEvent.getX(), mouseEvent.getY());
			}
			else if(((DefaultMutableTreeNode)treePath.getLastPathComponent()).getUserObject()
					instanceof FuzzySet)
			{
				FuzzySetTreePopupMenu fstp = new FuzzySetTreePopupMenu(fse_Frame
						,FuzzySetTreePopupMenu.OPTION_FUZZYSET_FOR_WORK );
				fstp.show(mouseEvent.getComponent(),mouseEvent.getX(), mouseEvent.getY());
			}
			else if(((DefaultMutableTreeNode)treePath.getLastPathComponent()).getUserObject()
					instanceof CFS_IdentityWraper)
			{
				FuzzySetTreePopupMenu fstp = new FuzzySetTreePopupMenu(fse_Frame
						,FuzzySetTreePopupMenu.OPTION_CUSTOM_SET);
				fstp.show(mouseEvent.getComponent(),mouseEvent.getX(), mouseEvent.getY());
			}
		}
		else if(mouseEvent.getButton() == MouseEvent.BUTTON1)
		{
			if(mouseEvent.getClickCount() == 2)
			{
//				TreePath treePath = tree.getPathForLocation(
//						mouseEvent.getX(), mouseEvent.getY() );
				
				if(treePath != null)
				{
					if(((DefaultMutableTreeNode)treePath.getLastPathComponent()).getUserObject()
						instanceof CFS_IdentityWraper)
					{
						DefaultMutableTreeNode attrNode = 
						((DefaultMutableTreeNode)
								((DefaultMutableTreeNode)
										treePath
										.getLastPathComponent())
										.getParent());
						TreeNode[] nodesInPath = fse_Frame.getFuzzySetTree()
													.getTreeModel().getPathToRoot(attrNode);
						
						CFS_IdentityWraper cfs_IW = (CFS_IdentityWraper)
									((DefaultMutableTreeNode)
										treePath.getLastPathComponent()).getUserObject();
						
						
						if(fse_Frame.getFuzzyEditorTapped().getComponentCount() == 0)
						{
							cfs_IW.setOpened(false);
						}
						if(!cfs_IW.isOpened())
						{
							fse_Frame.openFuzzySet(cfs_IW, nodesInPath);
						}
					}
				}
			}
		}	
	}
}
