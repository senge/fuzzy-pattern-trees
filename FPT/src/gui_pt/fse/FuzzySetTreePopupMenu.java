package gui_pt.fse;

import gui_pt.fse.helper.AttributeWrapper;
import gui_pt.fse.helper.ClassWrapper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class FuzzySetTreePopupMenu extends JPopupMenu implements ActionListener{

	private FSE_Frame fse_Frame;
	private int option;
	
	public final static int OPTION_ATTRIBUTE = 0;
	public final static int OPTION_PROJECT = 1;
	public final static int OPTION_FUZZYSET_FOR_WORK = 2;
	public final static int OPTION_CUSTOM_SET = 3;
	
	public FuzzySetTreePopupMenu(FSE_Frame fse_Frame, int option)
	{
		this.fse_Frame = fse_Frame;
		this.option = option;
		
		if(option == OPTION_ATTRIBUTE)
		{
			JMenuItem item1 = new JMenuItem("create new Fuzzyset");
			item1.setActionCommand("createFS");
			item1.addActionListener(this);
			
			this.add(item1);	
		}
		else if(option == OPTION_CUSTOM_SET)
		{
			JMenuItem item1 = new JMenuItem("delete Fuzzyset");
			item1.setActionCommand("deleteFS");
			item1.addActionListener(this);
			
			this.add(item1);
		}
//		else if(option == OPTION_TEMPLATES)
//		{
//			JMenuItem item2 = new JMenuItem("add Template");
//			item2.setActionCommand("addTemplate");
//			item2.addActionListener(this);	
//				
//			this.add(item2);
//		}
		else if(option == OPTION_FUZZYSET_FOR_WORK)
		{
			JMenuItem item = new JMenuItem("add to workset");
			item.setActionCommand("toworkset");
			item.addActionListener(this);
			this.add(item);
		}
		else if(option == OPTION_PROJECT)
		{
			JMenuItem packFSitem = new JMenuItem("build fuzzyset pack");
			packFSitem.setActionCommand("packFS");
			packFSitem.addActionListener(this);
			this.add(packFSitem);
		}

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getActionCommand().equals("createFS"))
		{
			FSE_CreateDialog fse_CD = new FSE_CreateDialog(this.fse_Frame
					, FSE_CreateDialog.OPTION_CREATE_NEW);
		}
//		else if(arg0.getActionCommand().equals("addTemplate"))
//		{
//			TreePath selectedPath = fse_Frame.getFuzzySetTree().getTree().getSelectionPath();
//			DefaultMutableTreeNode proNode = ((DefaultMutableTreeNode)
//					selectedPath.getLastPathComponent());
//			
//			TemplatePackCreator tpc = new TemplatePackCreator();
//			
//			tpc.createTemplatePack(fse_Frame.getWorkInstances());
//			fse_Frame.getFuzzySetTree().addChildNode(
//					tpc.createTemplatePack(fse_Frame.getWorkInstances())
//					, proNode);
//		}
		else if(arg0.getActionCommand().equals("toworkset"))
		{
			FSE_CreateDialog fse_CD = new FSE_CreateDialog(this.fse_Frame
					, FSE_CreateDialog.OPTION_IMPORT_TEMPLATE);
		}
		else if(arg0.getActionCommand().equals("packFS"))
		{
			TreePath selectedPath = fse_Frame.getFuzzySetTree().getTree().getSelectionPath();
			DefaultMutableTreeNode root = (DefaultMutableTreeNode)selectedPath.getLastPathComponent();
			PackFuzzySetDialog pfsd = new PackFuzzySetDialog(this.fse_Frame, root);
		}
		else if(arg0.getActionCommand().equals("deleteFS"))
		{
			TreePath selectedPath = fse_Frame.getFuzzySetTree().getTree().getSelectionPath();
			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)
											selectedPath.getLastPathComponent();
			
			CFS_IdentityWraper cfs_IW = (CFS_IdentityWraper)
										node.getUserObject();
			
			//first close Tab if open
			if(fse_Frame.getTtwsMap().containsKey(cfs_IW.getKey()))
			{
				((CFS_PTabHeadPanel)fse_Frame.getTtwsMap().get(cfs_IW.getKey()).getControler()).getCloseButton().doClick();			
			}
			fse_Frame.getTtwsMap().remove(cfs_IW.getKey());
			
			//second delete File 
			File file = new File(
					fse_Frame.getStartW()
					.getStartSettings()
					.getWorkspacePath()+"/workspaceEditor/"
					+((FuzzySetProject)((DefaultMutableTreeNode)selectedPath.getPathComponent(1))
							.getUserObject()).getM_ProjectName()
					+"/"
					+((ClassWrapper)((DefaultMutableTreeNode)selectedPath.getPathComponent(2)).getUserObject())
							.getClassName()
					+"/"
					+((AttributeWrapper)((DefaultMutableTreeNode)selectedPath.getPathComponent(3))
							.getUserObject()).getAttribute().name()
					+"/"
					+cfs_IW.getWorkName()
					+".fso");
			
			file.delete();
			
			//third remove set from tree
			fse_Frame.getFuzzySetTree().getTreeModel().removeNodeFromParent(node);
		}
	}
}
