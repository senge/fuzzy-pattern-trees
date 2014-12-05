package gui_pt.gui;

import gui_pt.fse.Crawler_workspaceE;
import gui_pt.fse.FSE_Frame;
import gui_pt.guiUtil.WorkspaceLuncher;
import gui_pt.io.StartSettings;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

public class StartWindow extends JFrame implements ActionListener, WindowListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5805421447611201500L;
	JPanel mainPanel;
	JPanel buttonPanel;
	JPanel vButtonPanel;
	JPanel eButtonPanel;
	JButton viewerButton;
	JButton editorButton;
	
	StartSettings startSettings;
	
	MainWindow mainW = null;
	FSE_Frame fse_Frame = null;
	LinkedList<DefaultMutableTreeNode> projectTrees = new LinkedList<DefaultMutableTreeNode>();
	
	//###########################################################################
	//CONSTRUCTOR
	//###########################################################################
	
	public StartWindow(){
		
		//------------- Reading StartSettings--------------------------------------
		this.startSettings = new StartSettings();
		try {
			FileInputStream fis = new FileInputStream(new File("./PTV/startsettings.obj"));
			ObjectInputStream ois = new ObjectInputStream(fis);
			this.startSettings = (StartSettings)ois.readObject();
				ois.close();
				fis.close();
		} catch (FileNotFoundException e) {
			File file = new File("./PTV");
			file.mkdirs();
			try {
				startSettings.writeToFile(new File(file.toString()+"/startsettings.obj"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//-------------------------------------------------------------------------
		//--- WorkspaceLuncher-----------------------------------------------------
		
		if(startSettings.isShowWorkspaceLuncher())
		{
			WorkspaceLuncher wl = new WorkspaceLuncher(this);
		}	
		//-------------------------------------------------------------------------
		
		viewerButton = new JButton("Viewer");
		viewerButton.setActionCommand("viewer");
		viewerButton.addActionListener(this);
		
		vButtonPanel = new JPanel();
		vButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		vButtonPanel.add(viewerButton);
		
		editorButton = new JButton("Editor");
		editorButton.setActionCommand("editor");
		editorButton.addActionListener(this);
		
		eButtonPanel = new JPanel();
		eButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		eButtonPanel.add(editorButton);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(vButtonPanel);
		buttonPanel.add(eButtonPanel);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		
		cp.add(mainPanel, BorderLayout.CENTER);
		
		//get projects TODO fit too new fpt project
		Crawler_workspaceE cwE = new Crawler_workspaceE(this);
		DefaultMutableTreeNode[] dmtField = cwE.crawl();
		for(int i=0; i< dmtField.length; i++)
		{
			projectTrees.add(dmtField[i]); 
		}
		
		this.addWindowListener(this);
		this.setSize(500,400);
		this.setLocation(
				(Toolkit.getDefaultToolkit().getScreenSize().width-getSize().width)/2,
				(Toolkit.getDefaultToolkit().getScreenSize().height-getSize().height)/2);
		this.setVisible(true);
	}

	//###########################################################################
	//ActionListener
	//###########################################################################
	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getActionCommand().equals("viewer"))
		{
			mainW = MainWindow.getInstance(this);
			mainW.setVisible(true);
		}
		else if(arg0.getActionCommand().equals("editor"))
		{
			fse_Frame = FSE_Frame.getInstance(this);
			fse_Frame.setVisible(true);
		}
		
	}
	
	//##########################################################################################
	//WindowListener
	//##########################################################################################

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent winE) {
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(new File("./PTV/startsettings.obj"));
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this.getStartSettings());
			oos.close();
			fos.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		System.exit(0);
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	//##########################################################################################
	//Get and Set
	//##########################################################################################

	public StartSettings getStartSettings() {
		return startSettings;
	}

	public void setStartSettings(StartSettings startSettings) {
		this.startSettings = startSettings;
	}

	public LinkedList<DefaultMutableTreeNode> getProjectTrees() {
		return projectTrees;
	}

	public void setProjectTrees(LinkedList<DefaultMutableTreeNode> projectTrees) {
		this.projectTrees = projectTrees;
	}
	

}
