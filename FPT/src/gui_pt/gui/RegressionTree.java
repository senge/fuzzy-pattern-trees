package gui_pt.gui;

import gui_pt.accessLayer.util.AccessPT;
import gui_pt.guiUtil.UserObjectWrapper;
import gui_pt.plugin.PTVisualisation;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

public class RegressionTree implements TreeSelectionListener{
		
	private DefaultMutableTreeNode root;
	private DefaultMutableTreeNode offline_Node;
	private DefaultMutableTreeNode online_Node;
	private DefaultTreeModel treeModel;
	private JTree tree;
	
	public RegressionTree(){
		
		root = new DefaultMutableTreeNode("Regression");
		offline_Node = new DefaultMutableTreeNode("Offline");
		online_Node = new DefaultMutableTreeNode("Online");
		
		root.add(offline_Node);
		root.add(online_Node);

		treeModel = new DefaultTreeModel(root);
		tree = new JTree(treeModel);
		
		tree.addTreeSelectionListener(this);
		tree.setRootVisible(true);
	}
	
	public DefaultMutableTreeNode addRegression(AccessPT accPT, boolean isStream, String name) {
		
		DefaultMutableTreeNode node = new DefaultMutableTreeNode();
		
		if(name != null)
		{
			node.setUserObject(new UserObjectWrapper(accPT,name));
		}
		else
		{
			node.setUserObject(new UserObjectWrapper(accPT
					,"Regression_"+(offline_Node.getChildCount()+online_Node.getChildCount())));
		}
		
		if(isStream)
		{
			addChildNode(node, this.online_Node);
		}
		else 
		{
			addChildNode(node, this.offline_Node);
		}
		
		return node;
	}
	
	public DefaultMutableTreeNode addPTV(PTVisualisation ptv, DefaultMutableTreeNode parent){
		
		DefaultMutableTreeNode node = new DefaultMutableTreeNode();
		UserObjectWrapper uow = new UserObjectWrapper(ptv, ptv.getMarking()+"_"+parent.getChildCount());
		node.setUserObject(uow);
		
		addChildNode(node, parent);
		
		return node;
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
	
	//###############################################################################
	//Inner Classes
	//###############################################################################
	
	class RegressionTreeRenderer  extends JLabel implements TreeCellRenderer {

		public RegressionTreeRenderer() {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
		}


		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expended, boolean leaf, int row, boolean hasFocus) {
						
			
			return null;
		}
	}

}


