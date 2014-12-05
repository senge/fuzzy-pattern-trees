package gui_pt.gui;

import gui_pt.guiUtil.UserObjectWrapper;
import gui_pt.plugin.PTVisualisation;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import weka.core.DenseInstance;
import weka.core.Instance;

public class ConnectionDialog extends JDialog implements ActionListener{
	
	private JPanel mainPanel;
	private JPanel rightPanel;
	private JPanel leftPanel;
	private JPanel rightCheckBoxPanel;
	private JPanel leftCheckBoxPanel;
	
	private JButton connectButton;
	private JButton disconnectButton;
	
	private ArrayList<JCheckBox> connList = new ArrayList<JCheckBox>();
	private ArrayList<PTVisualisation> connPTVList = new ArrayList<PTVisualisation>();
	private ArrayList<JCheckBox> disList = new ArrayList<JCheckBox>();
	private ArrayList<PTVisualisation> disPTVList = new ArrayList<PTVisualisation>();
	
	private int classCount = 0;
	
	public ConnectionDialog(MainWindow mainW, DefaultMutableTreeNode parent){
		super(mainW);
		
		rightCheckBoxPanel = new JPanel();
		rightCheckBoxPanel.setLayout(new BoxLayout(rightCheckBoxPanel, BoxLayout.PAGE_AXIS));
		
		leftCheckBoxPanel = new JPanel();
		leftCheckBoxPanel.setLayout(new BoxLayout(leftCheckBoxPanel, BoxLayout.PAGE_AXIS));
		
		connectButton = new JButton("Connect");
		connectButton.setActionCommand("connect");
		connectButton.addActionListener(this);
		
		disconnectButton = new JButton("Disconnect");
		disconnectButton.setActionCommand("disconnect");
		disconnectButton.addActionListener(this);
		
		rightPanel = new JPanel();
		rightPanel.setBorder(new TitledBorder("Connected"));
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(new JScrollPane(rightCheckBoxPanel), BorderLayout.CENTER);
		rightPanel.add(disconnectButton, BorderLayout.SOUTH);
		
		leftPanel = new JPanel();
		leftPanel.setBorder(new TitledBorder("Disconnected"));
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(new JScrollPane(leftCheckBoxPanel), BorderLayout.CENTER);
		leftPanel.add(connectButton, BorderLayout.SOUTH);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(rightPanel, BorderLayout.EAST);
		mainPanel.add(leftPanel, BorderLayout.WEST);
		
		this.buildCheckBoxes(parent);
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(mainPanel, BorderLayout.CENTER);
		
		this.setModal(true);
		this.setSize(300, 400);
		this.setLocation(
				(Toolkit.getDefaultToolkit().getScreenSize().width-getSize().width)/2,
				(Toolkit.getDefaultToolkit().getScreenSize().height-getSize().height)/2);	
		this.setVisible(true);
		
	}
	
	public void addConnBox(String name, PTVisualisation ptv){
		
		JCheckBox cb = new JCheckBox(name);
		connList.add(cb);
		connPTVList.add(ptv);
		leftCheckBoxPanel.add(cb);
		this.validate();
	}
	
	public void addDisBox(String name, PTVisualisation ptv, int classID){
		
		JCheckBox cb = new JCheckBox(name+"("+classID+")");
		disList.add(cb);
		disPTVList.add(ptv);
		rightCheckBoxPanel.add(cb);
		this.validate();
	}
	
	public void buildCheckBoxes(DefaultMutableTreeNode parent){
		
		ArrayList<UserObjectWrapper> connClasses = new ArrayList<UserObjectWrapper>();
		
		for(int i=0; i< parent.getChildCount(); i++)
		{
			UserObjectWrapper childUOW = 
				((UserObjectWrapper)
				((DefaultMutableTreeNode)parent.getChildAt(i)).getUserObject());
			
			PTVisualisation ptv = (PTVisualisation)childUOW.obj;
			
			if(ptv.getConnection().size() > 0)
			{
				boolean classFound = false;
				for(int j = 0; j<connClasses.size(); j++)
				{
					if(ptv.getConnection().contains((PTVisualisation)connClasses.get(j).obj))
					{
						this.addDisBox(childUOW.toString(), ptv, j);
						classFound = true;
						break;
					}
				}
				if(!classFound)
				{
					connClasses.add(childUOW);
					this.addDisBox(childUOW.toString(), ptv, connClasses.size()-1);
				}					
			}
			else
			{
				this.addConnBox(childUOW.toString(), ptv);
			}				
		}
		this.classCount = connClasses.size();
		this.validate();
	}
	
	private void updateDialog(){
		
		rightCheckBoxPanel.removeAll();
		leftCheckBoxPanel.removeAll();
		
		rightCheckBoxPanel.setLayout(new BoxLayout(rightCheckBoxPanel, BoxLayout.PAGE_AXIS));
		leftCheckBoxPanel.setLayout(new BoxLayout(leftCheckBoxPanel, BoxLayout.PAGE_AXIS));
		
		for(JCheckBox cb: connList)
		{
			cb.setText(cb.getText().split("\\(")[0]);
			leftCheckBoxPanel.add(cb);
		}
		for(JCheckBox cb: disList)
		{
			rightCheckBoxPanel.add(cb);
		}
		this.validate();
		this.repaint();
	}
	
	//###################################################################################################
	// ActionListener
	//###################################################################################################

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getActionCommand().equals("connect"))
		{
			Instance protoInstance = null;
			boolean firstFound = false;
			ArrayList<PTVisualisation> helpList = new ArrayList<PTVisualisation>();
			ArrayList<JCheckBox> cbDeleteList = new ArrayList<JCheckBox>();
			
			for(int i=0; i<connList.size(); i++)
			{
				if(connList.get(i).isSelected())
				{
					if(firstFound)
					{
						connPTVList.get(i).setProtoInstance(protoInstance);
					}
					else
					{
						protoInstance = connPTVList.get(i).getProtoInstance();
						firstFound = true;
					}
					connList.get(i).setSelected(false);
					helpList.add(connPTVList.get(i));
					cbDeleteList.add(connList.get(i));
				}
			}			
			for(int i=0; i<helpList.size(); i++)
			{
				for(int j=0; j<helpList.size(); j++)
				{
					if(j != i)
					{
						helpList.get(i).registerConnection(helpList.get(j));
					}
				}
			}
			//update Dialog
			if(helpList.size()>1)
			{
				//update classInfo
				for(JCheckBox cb: cbDeleteList)
				{
					cb.setText(cb.getText()+"("+this.classCount+")");
				}
				connList.removeAll(cbDeleteList);
				connPTVList.removeAll(helpList);
				disList.addAll(cbDeleteList);
				disPTVList.addAll(helpList);
					
				this.classCount++;
				updateDialog();
			}		
		}		
		else if(arg0.getActionCommand().equals("disconnect"))
		{		
			ArrayList<PTVisualisation> helpList = new ArrayList<PTVisualisation>();
			ArrayList<JCheckBox> cbDeleteList = new ArrayList<JCheckBox>();
			for(int i=0; i<disList.size(); i++)
			{
				if(disList.get(i).isSelected())
				{
					disPTVList.get(i).setProtoInstance(new DenseInstance(
							disPTVList.get(i).getProtoInstance()));
					helpList.add(disPTVList.get(i));
					cbDeleteList.add(disList.get(i));
					//disconnect
					for(PTVisualisation ptv: disPTVList.get(i).getConnection())
					{
						ptv.removeConnection(disPTVList.get(i));
					}
					disPTVList.get(i).getConnection().clear();
				}
			}
			disList.removeAll(cbDeleteList);
			disPTVList.removeAll(helpList);
			connList.addAll(cbDeleteList);
			connPTVList.addAll(helpList);
			
			//for later use
			helpList.clear();
			cbDeleteList.clear();
			
			int[] classCount_A = new int[classCount+1];
			for(int i=0; i<classCount_A.length; i++)
			{
				classCount_A[i] = 0;
			}
			//update classInformation
			for(int i=0; i<disList.size(); i++)
			{
				String classNumber_S = disList.get(i).getText().split("\\(")[1];
				classNumber_S = classNumber_S.replace(")", "");
				int  classNumber = Integer.parseInt(classNumber_S);
				
				classCount_A[classNumber]++;
			}
			//count Classes
			this.classCount = 0;
			for(int i=0; i<classCount_A.length; i++)
			{
				if(classCount_A[i]>1)
				{
					classCount++;
				}
				else if(classCount_A[i] == 1)
				{
					for(int j=0; j<disList.size(); j++)
					{
						String classNumber_S = disList.get(j).getText().split("\\(")[1];
						classNumber_S = classNumber_S.replace(")", "");
						int  classNumber = Integer.parseInt(classNumber_S);
						
						if(i == classNumber)
						{
							disPTVList.get(j).setProtoInstance(new DenseInstance(
							disPTVList.get(j).getProtoInstance()));
							helpList.add(disPTVList.get(j));
							cbDeleteList.add(disList.get(j));
							
							//disconnect
							for(PTVisualisation ptv: disPTVList.get(j).getConnection())
							{
								ptv.removeConnection(disPTVList.get(j));
							}
							disPTVList.get(j).getConnection().clear();
						}
					}
				}
			}
			
			disList.removeAll(cbDeleteList);
			disPTVList.removeAll(helpList);
			connList.addAll(cbDeleteList);
			connPTVList.addAll(helpList);
			
			updateDialog();
		}
	}
}
