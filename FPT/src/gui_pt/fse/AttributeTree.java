package gui_pt.fse;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class AttributeTree implements TreeSelectionListener{
	
	FSE_Frame fse_Frame;

	private DefaultMutableTreeNode root;
	private DefaultTreeModel treeModel;
	private JTree tree;
	
	public AttributeTree(){
		
		root = new DefaultMutableTreeNode("Attribute");

		treeModel = new DefaultTreeModel(root);
		tree = new JTree(treeModel);
		
		tree.addTreeSelectionListener(this);
		tree.setRootVisible(true);
	}
	
	public void addChildNode(DefaultMutableTreeNode Node, DefaultMutableTreeNode parent){
			
		this.treeModel.insertNodeInto(Node, parent, parent.getChildCount());		
	}
	
	public void clearTree(){
		
		this.root.removeAllChildren();
		this.treeModel.reload();
		
	}
	
	//#####################################################################################
	// SelectionListener Methods
	//#####################################################################################
	
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {

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
