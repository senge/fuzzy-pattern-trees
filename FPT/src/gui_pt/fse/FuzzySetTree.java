package gui_pt.fse;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

public class FuzzySetTree implements TreeSelectionListener{
	
	FSE_Frame fse_Frame;

	private DefaultMutableTreeNode root;
	private DefaultTreeModel treeModel;
	private JTree tree;
	
	public FuzzySetTree(){
		
		root = new DefaultMutableTreeNode("FuzzySet");

		root.add(new DefaultMutableTreeNode("empty"));
		treeModel = new DefaultTreeModel(root);
		tree = new JTree(treeModel);
		
		tree.addTreeSelectionListener(this);
		tree.setRootVisible(true);
	}
	
	public void addChildNode(DefaultMutableTreeNode Node, DefaultMutableTreeNode parent){
		
		if(this.treeModel.getChildCount(parent)==1)
		{
			if(parent.getChildAt(0).toString().equals("empty"))
			{			
				tree.clearSelection();
				this.treeModel.removeNodeFromParent((MutableTreeNode)parent.getChildAt(0));
			}			
		}	
		this.treeModel.insertNodeInto(Node, parent, parent.getChildCount());
		this.tree.expandRow(2);
	}
		
	public void clearTree(){
		
		this.root.removeAllChildren();
		this.root.add(new DefaultMutableTreeNode("empty"));
		this.treeModel.reload();
		
	}
	
	//#####################################################################################
	// SelectionListener Methods
	//#####################################################################################
	
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {

		//TODO
//		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
//        		tree.getLastSelectedPathComponent();
//		
//		
//		try{
//			if(node.getUserObject() instanceof Attribute)
//			{
//				System.out.println(((Attribute)node.getUserObject()).toString());
//				System.out.println(tree.getSelectionPath());
//			}
//		}catch(NullPointerException e) {
//			//TODO
//		}		
	}
	
	//#####################################################################################
	//GET and SET
	//#####################################################################################

	public DefaultMutableTreeNode getRoot() {
		return root;
	}

	public void setRoot(DefaultMutableTreeNode root) {
		this.root = root;
	}

	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(DefaultTreeModel treeModel) {
		this.treeModel = treeModel;
	}

	public JTree getTree() {
		return tree;
	}

	public void setTree(JTree tree) {
		this.tree = tree;
	}

}
