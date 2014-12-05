package gui_pt.fse;

import gui_pt.fse.listener.FSEPActionListener;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.tree.TreeNode;





public class FuzzySetEditorPanel extends JPanel{
	
	JButton gitterButton;
	JButton hisButton;
	JPopupMenu histogrammMenu;
	JCheckBox[] hisSelectables;
	JToolBar toolBar;
	JSplitPane splitPane;
	
	private FSEDrawPanel fseDrawP;
	private CFS_PTabHeadPanel cfs_PTabHeadP;
	
	private CustomPointListPanel CPListP;
	
	private TreeNode[] projectPath;
//	private CustomFuzzySet customFS;
	private CFS_IdentityWraper cfs_Identity;
	private boolean unsafedModified;
	
	private FSEPActionListener fsepActionListener;
		
	//#################################################################
	//CONSTRUCTOR
	//#################################################################


	public FuzzySetEditorPanel(){
			
		CPListP = new CustomPointListPanel(this);
		
		this.setLayout(new BorderLayout());
				
		fsepActionListener = new FSEPActionListener(this);
		
		gitterButton = new JButton("Gitter");
		gitterButton.setActionCommand("gitter");
		gitterButton.addActionListener(fsepActionListener);
		
		hisButton = new JButton("Histogramm");
		hisButton.setActionCommand("histogramm");
		hisButton.addActionListener(fsepActionListener);
		
		histogrammMenu = new JPopupMenu();
			
		toolBar = new JToolBar();
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		toolBar.add(gitterButton);
		toolBar.add(hisButton);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(800);
		splitPane.setRightComponent(new JScrollPane(CPListP));
		
		this.add(splitPane, BorderLayout.CENTER);
		this.add(toolBar, BorderLayout.NORTH);
	}
	
	//################################################################
	//METHODS
	//################################################################
	
	private void setStatusUnsafedModified(){
		
		try {
			if(this.unsafedModified){
				this.cfs_PTabHeadP.getUnsafedLabel().setText("*");
			}
			else
			{
				this.cfs_PTabHeadP.getUnsafedLabel().setText("");
			}
		}catch(NullPointerException e){}
	}
	
	private void buildHisCheckBox(){
	
		hisSelectables = new JCheckBox[this.cfs_Identity.getHisPerClass().length];
					
		for(int i=0; i<hisSelectables.length; i++)
		{
			hisSelectables[i] = new JCheckBox(cfs_Identity.getHisPerClass()[i].getClassName());
			hisSelectables[i].setName(""+i);
			hisSelectables[i].setActionCommand("showHis");
			hisSelectables[i].addActionListener(fsepActionListener);
			this.histogrammMenu.add(hisSelectables[i]);
			this.validate();
		}
	}
	
	//################################################################
	//GET and SET 
	//################################################################

	public FSEDrawPanel getFseDrawP() {
		return fseDrawP;
	}

	public void setFseDrawP(FSEDrawPanel fseDrawP) {
		this.fseDrawP = fseDrawP;
		
		try{
			boolean[] drawHis = new boolean[this.cfs_Identity.getHisPerClass().length];
			for(int i=0; i<drawHis.length; i++)
			{
				drawHis[i] = false;
			}
			
			this.fseDrawP.setDrawHistogramm(drawHis);
		}catch(NullPointerException e){
			//TODO
		}
		
		this.splitPane.setLeftComponent(fseDrawP);
	}

	public CustomPointListPanel getCPListP() {
		return CPListP;
	}

	public void setCPListP(CustomPointListPanel cPListP) {
		CPListP = cPListP;
	}

	public CustomFuzzySet getCustomFS() {
		return cfs_Identity.getM_Cfs();
	}
	
	public void setCustomFS(CustomFuzzySet cfs) {
		cfs_Identity.setM_Cfs(cfs);
	}

	public CFS_IdentityWraper getCfs_Identity() {
		return cfs_Identity;
	}

	public void setCfs_Identity(CFS_IdentityWraper cfs_Identity) {
		this.cfs_Identity = cfs_Identity;
		
		if(cfs_Identity.getHisPerClass() != null)
		{
			this.buildHisCheckBox();
		}		
	}

	public boolean isUnsafedModified() {
		return unsafedModified;
	}

	public void setUnsafedModified(boolean unsafedModified) {
		
		if(this.unsafedModified != unsafedModified)
		{
			this.unsafedModified = unsafedModified;		
			this.setStatusUnsafedModified();
		}
	}

	public CFS_PTabHeadPanel getCfs_PTabHeadP() {
		return cfs_PTabHeadP;
	}

	public void setCfs_PTabHeadP(CFS_PTabHeadPanel cfs_PTabHeadP) {
		this.cfs_PTabHeadP = cfs_PTabHeadP;
	}

	public JPopupMenu getHistogrammMenu() {
		return histogrammMenu;
	}

	public void setHistogrammMenu(JPopupMenu histogrammMenu) {
		this.histogrammMenu = histogrammMenu;
	}

	public JButton getHisButton() {
		return hisButton;
	}

	public void setHisButton(JButton hisButton) {
		this.hisButton = hisButton;
	}

	public TreeNode[] getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(TreeNode[] projectPath) {
		this.projectPath = projectPath;
	}
}
