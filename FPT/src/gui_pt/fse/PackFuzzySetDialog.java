package gui_pt.fse;

import gui_pt.fse.helper.AttributeWrapper;
import gui_pt.fse.helper.ClassWrapper;
import gui_pt.fse.util.FSE_Util;
import gui_pt.fse.util.FuzzySetPack;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingEvent;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import weka.classifiers.trees.pt.FuzzySet;
import weka.core.Instances;

public class PackFuzzySetDialog extends JDialog implements ActionListener, TreeCheckingListener{
	
	JPanel mainPanel;
	JPanel leftPanel;
	JPanel rightPanel;
	JPanel cbTreePanel;
	
	JPanel controlPanel;
	JTextArea[] selectedTextArea;
	
	JPanel commandPanel;
	
	JPanel buttonPanel;
	JButton saveButton;
	JButton cancelButton;
	
	FSE_Frame fse_Frame;
	CheckboxTree cbt;
	
	public PackFuzzySetDialog(FSE_Frame fse_Frame, DefaultMutableTreeNode root){
		super(fse_Frame);
		
		this.fse_Frame = fse_Frame;
		
		cbt = new CheckboxTree(root);
		cbt.addTreeCheckingListener(this);
		
		rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		
		this.createControlPanel();
		
//		browseLabel = new JLabel("Destination ");
//		browseTextField = new JTextField(25);
//		browseButton = new JButton("browse");
//		browseButton.setActionCommand("browse");
//		browseButton.addActionListener(this);
//		
//		browsePanel = new JPanel();
//		browsePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
//		browsePanel.add(browseLabel);
//		browsePanel.add(browseTextField);
//		browsePanel.add(browseButton);
		
		saveButton = new JButton("save");
		saveButton.setActionCommand("save");
		saveButton.addActionListener(this);
		
		cancelButton = new JButton("cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(cancelButton);
		buttonPanel.add(saveButton);
		
		commandPanel = new JPanel();
		commandPanel.setLayout(new BorderLayout());
//		commandPanel.add(browsePanel, BorderLayout.NORTH);
		commandPanel.add(buttonPanel, BorderLayout.CENTER);
		
		rightPanel.add(commandPanel, BorderLayout.SOUTH);
				
		leftPanel = new JPanel();
		leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		leftPanel.setPreferredSize(new Dimension(250,550));
		leftPanel.setBackground(Color.white);
		leftPanel.add(cbt);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(new JScrollPane(leftPanel), BorderLayout.WEST);
		mainPanel.add(rightPanel, BorderLayout.CENTER);
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(mainPanel, BorderLayout.CENTER);
		
		this.setModal(true);
		this.setSize(800,600);
		this.setLocation(
				(Toolkit.getDefaultToolkit().getScreenSize().width-getSize().width)/2,
				(Toolkit.getDefaultToolkit().getScreenSize().height-getSize().height)/2);
		this.setVisible(true);
		
	}

	/**
	 * 
	 */
	private void createControlPanel(){
		
		FuzzySetProject fsp = ((FuzzySetProject)
		((DefaultMutableTreeNode)
				cbt.getModel().getRoot()).getUserObject());
		
		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
				
		selectedTextArea = new JTextArea[fsp.getClasses().length];
		for(int c=0; c<fsp.getClasses().length; c++)
		{
			selectedTextArea[c] = new JTextArea();
			
			JPanel helpPanel = new JPanel();
			helpPanel.setLayout(new BorderLayout());
			helpPanel.setBorder(new TitledBorder(fsp.getClasses()[c].getClassName()));
			helpPanel.setPreferredSize(new Dimension(150,300));
			helpPanel.add(new JScrollPane(selectedTextArea[c]), BorderLayout.CENTER);
			
			controlPanel.add(helpPanel);
		}
		
		this.rightPanel.add(new JScrollPane(controlPanel), BorderLayout.NORTH);		
	}
	
	//##############################################################################
	//ActionListener
	//##############################################################################
	
	@Override
	public void actionPerformed(ActionEvent event){
		
		if(event.getActionCommand().equals("cancel"))
		{
			this.dispose();
		}
		else if(event.getActionCommand().equals("save"))
		{
			JFileChooser jfc = new JFileChooser();
			
			int c = jfc.showSaveDialog(this);
			
			if(c == JFileChooser.APPROVE_OPTION)
			{
				Instances data = ((FuzzySetProject)
									((DefaultMutableTreeNode)
											cbt
											.getModel()
											.getRoot())
											.getUserObject())
											.getInstances();
				
				FuzzySetPack fsp = FSE_Util.extractFuzzySets(cbt
						,data);
				

				File file = jfc.getSelectedFile();
				

				try {
					FileOutputStream fos = new FileOutputStream(file);
									
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					
					oos.writeObject(fsp);
					
					oos.close();
					fos.close();
				
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
	}
	
	//##############################################################################
	//TreeCheckingListener
	//##############################################################################

	@Override
	public void valueChanged(TreeCheckingEvent arg0) {
		
		for(int i=0; i<this.selectedTextArea.length; i++)
		{
			this.selectedTextArea[i].setText("");
		}
		TreePath[] treePaths = cbt.getCheckingPaths();
		
		for(int i=0; i<treePaths.length; i++)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)treePaths[i].getLastPathComponent();
			
			if(node.getUserObject() instanceof CFS_IdentityWraper)
			{
				ClassWrapper class_W = (ClassWrapper)
													((DefaultMutableTreeNode)
														treePaths[i]
											            .getPathComponent(treePaths[i]
											            .getPathCount()-3))
											            .getUserObject();
				AttributeWrapper attr_W = (AttributeWrapper)
													((DefaultMutableTreeNode)
														treePaths[i]
											            .getPathComponent(treePaths[i]
											            .getPathCount()-2))
											            .getUserObject();
				
				String fs_name = ((CFS_IdentityWraper)(node.getUserObject())).getWorkName();
				String attr_name = attr_W.getAttribute().name();
				
				if(class_W.getClassAttribute().isNominal())
				{
					int c_index = class_W
									.getClassAttribute()
									.indexOfValue(class_W.getClassName());
					this.selectedTextArea[c_index].append(fs_name+"@"+attr_name+"\n");
				}			
			}
			else if(node.getUserObject() instanceof FuzzySet)
			{
				ClassWrapper class_W = (ClassWrapper)
										((DefaultMutableTreeNode)
											treePaths[i]
								            .getPathComponent(treePaths[i]
								            .getPathCount()-3))
								            .getUserObject();
				AttributeWrapper attr_W = (AttributeWrapper)
											((DefaultMutableTreeNode)
												treePaths[i]
									            .getPathComponent(treePaths[i]
									            .getPathCount()-2))
									            .getUserObject();

				String fs_name = ((FuzzySet)(node.getUserObject())).toString();
				String attr_name = attr_W.getAttribute().name();
				
				if(class_W.getClassAttribute().isNominal())
				{
					int c_index = class_W
									.getClassAttribute()
									.indexOfValue(class_W.getClassName());
					this.selectedTextArea[c_index].append(fs_name+"@"+attr_name+"\n");
				}
			}
		}							
	}
}
