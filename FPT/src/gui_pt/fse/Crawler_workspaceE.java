package gui_pt.fse;

import gui_pt.fse.helper.AttributeWrapper;
import gui_pt.fse.helper.ClassWrapper;
import gui_pt.gui.StartWindow;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.tree.DefaultMutableTreeNode;

public class Crawler_workspaceE implements FileFilter{
	
	StartWindow startW;
	final static int FILTER_DIR = 0;
	final static int FILTER_FSO = 1;
	int filterOption = 0;
	
	public Crawler_workspaceE(StartWindow startW){
		
		this.startW = startW;
	}
		
	public DefaultMutableTreeNode[] crawl(){
				
		File file = new File(startW.getStartSettings().getWorkspacePath()+"/workspaceEditor");
			
		this.setFilterOption(FILTER_DIR);
		File[] projects = file.listFiles(this);
		
		DefaultMutableTreeNode[] projectTrees = new DefaultMutableTreeNode[projects.length];
		
		for(int i=0; i<projects.length; i++)
		{			
			File projectFile = new File(projects[i].toString()+"/project.fspr");
			
			DefaultMutableTreeNode proNode = null;
			FuzzySetProject fs_Pro = null;
			try {
				FileInputStream fis = new FileInputStream(projectFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				
				fs_Pro = (FuzzySetProject)ois.readObject();
				
				proNode = new DefaultMutableTreeNode(projects[i]);
				proNode.setUserObject(fs_Pro);
				
				ois.close();
				fis.close();
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
			this.setFilterOption(FILTER_DIR);
			File[] classDirs = projects[i].listFiles(this);
			
			for(int c=0; c<classDirs.length; c++)
			{
				//addNode
				//add Node
				ClassWrapper classW = null;
				for(int j = 0; j<fs_Pro.getClasses().length; j++)
				{
					if(fs_Pro.getClasses()[j].getClassName().equals(classDirs[c].getName()))
					{
						classW = fs_Pro.getClasses()[j];
						break;
					}
				}
				DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(classW);
					
				this.setFilterOption(FILTER_DIR);
				File[] attributeDirs = classDirs[c].listFiles(this);
				for(int a=0; a<attributeDirs.length; a++)
				{
					//add Node
					AttributeWrapper attrW = null;
					for(int j = 0; j<fs_Pro.getAttributes().length; j++)
					{
						if(fs_Pro.getAttributes()[j].getAttribute().name().equals(attributeDirs[a].getName()))
						{
							attrW = fs_Pro.getAttributes()[j];
							break;
						}
					}
					DefaultMutableTreeNode attrNode = new DefaultMutableTreeNode(attrW);
					
					this.setFilterOption(FILTER_FSO);
					File[] fuzzyFiles = attributeDirs[a].listFiles(this);
					for(int j=0; j<fuzzyFiles.length; j++)
					{
						try {
							FileInputStream fis = new FileInputStream(fuzzyFiles[j]);
							ObjectInputStream ois = new ObjectInputStream(fis);
							
							CFS_IdentityWraper cfs_IW = (CFS_IdentityWraper)ois.readObject();
							cfs_IW.setKey(new Integer(-1));
							
							ois.close();
							fis.close();
							
							DefaultMutableTreeNode fuzzySetNode = new DefaultMutableTreeNode(fuzzyFiles[j].getName());
							fuzzySetNode.setUserObject(cfs_IW);
							
							attrNode.add(fuzzySetNode);
							
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}						
					classNode.add(attrNode);
				}					
				proNode.add(classNode);
			}
			//add Templates
			try{
				proNode.add(fs_Pro.getTemplateTree());
			}
			catch(IllegalArgumentException e)
			{
				//TODO
			}
			
			projectTrees[i] = proNode;
		}
		return projectTrees;
	}
	
	//###################################################################################
	//FileFilter
	//###################################################################################

	@Override
	public boolean accept(File arg0) {

		if(this.getFilterOption() == FILTER_DIR)
		{
			if(arg0.isDirectory())
			{
				return true;
			}
			return false;
		}
		else if(this.getFilterOption() == FILTER_FSO)
		{
			String[] fileEnding = arg0.getName().split("\\.");
		
			if(fileEnding[fileEnding.length-1].equals("fso"))
			{
				return true;
			}		
			return false;
		}
		return false;
	}

	public int getFilterOption() {
		return filterOption;
	}

	public void setFilterOption(int filterOption) {
		this.filterOption = filterOption;
	}

}
