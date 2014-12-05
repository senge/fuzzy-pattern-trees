package gui_pt.fse;
import gui_pt.accessLayer.FuzzySetCreator.FuzzySetCreator;
import gui_pt.fse.helper.AttributeWrapper;
import gui_pt.fse.helper.ClassWrapper;
import gui_pt.fse.util.TemplatePackCreator;
import gui_pt.guiUtil.TextHandler;
import gui_pt.io.FS_ProjectWriter;
import gui_pt.io.InstancesLoader;
import gui_pt.pt.Calculations;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import weka.core.Instances;



public class NewProjectDialog extends JDialog implements ActionListener, DocumentListener{
		
	JPanel selectProTypePanel;
	JRadioButton radioProType1;
	JRadioButton radioProType2;
	ButtonGroup buttonGroupProType;
	
	JPanel loadAndProPanel;
	
	JPanel loadInstancesPanel;
	JLabel loadInstLabel;
	JTextField loadInstTextField;
	JButton browseInstButton;
	
	JPanel storeInstancesPanel;
	JCheckBox storeInstCheckBox;
	
	JPanel projectNamePanel;
	JLabel projectNameLabel;
	JTextField projectNameTextField;
	
	JPanel buttonPanel;
	JButton okButton;
	JButton cancelButton;
	
	JPanel captionPanel;
	JTextPane captionPane;
	
	private FSE_Frame fse_Frame;
	
	private boolean nameInvalide = false;
	
	public NewProjectDialog(FSE_Frame fse_Frame){
		super(fse_Frame);
		
		this.fse_Frame = fse_Frame;
		
		captionPane = new JTextPane();
		captionPane.setPreferredSize(new Dimension(400,100));
		captionPane.setEditable(false);
		
		TextHandler[] textHField = new TextHandler[1];
		textHField[0] = new TextHandler("Select a project type\n", Color.black,11);
		this.setCaptionText(textHField);
		
		captionPanel = new JPanel();
		captionPanel.setLayout(new BorderLayout());
		captionPanel.setBorder(new EtchedBorder());
		captionPanel.add(captionPane, BorderLayout.CENTER);
		
		radioProType1 = new JRadioButton("Empty project");
		radioProType1.setActionCommand("emptypro");
		radioProType1.addActionListener(this);
		radioProType1.setEnabled(false); //TODO ersteinmal nicht zugelassen
		radioProType2 = new JRadioButton("Instance based project");
		radioProType2.setActionCommand("instancebasedpro");
		radioProType2.addActionListener(this);
		
		radioProType2.setSelected(true);	//sticked
		
		buttonGroupProType = new ButtonGroup();
		buttonGroupProType.add(radioProType1);
		buttonGroupProType.add(radioProType2);
		
		selectProTypePanel = new JPanel();
		selectProTypePanel.setLayout(new GridLayout(2,1));
		selectProTypePanel.setBorder(new EtchedBorder());
		selectProTypePanel.add(radioProType1);
		selectProTypePanel.add(radioProType2);
		
		loadInstLabel = new JLabel("Instances: ");
		loadInstTextField = new JTextField(37);
		loadInstTextField.getDocument().addDocumentListener(this);
		
		browseInstButton = new JButton("Browse");
		browseInstButton.setActionCommand("browse");
		browseInstButton.addActionListener(this);
		
		loadInstancesPanel = new JPanel();
		loadInstancesPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		loadInstancesPanel.add(loadInstLabel);
		loadInstancesPanel.add(loadInstTextField);
		loadInstancesPanel.add(browseInstButton);
		
		storeInstCheckBox = new JCheckBox("Store instances");
		storeInstCheckBox.setSelected(true);
		
		storeInstancesPanel = new JPanel();
		storeInstancesPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		storeInstancesPanel.add(storeInstCheckBox);
				
		projectNameLabel = new JLabel("Preject-Name: ");
		projectNameTextField = new JTextField(37);
		projectNameTextField.getDocument().addDocumentListener(this);

		projectNameTextField.addActionListener(this);
		
		projectNamePanel = new JPanel();
		projectNamePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		projectNamePanel.setBorder(new EtchedBorder());
		projectNamePanel.add(projectNameLabel);
		projectNamePanel.add(projectNameTextField);
				
		okButton = new JButton("Ok");	
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBorder(new EtchedBorder());
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		loadAndProPanel = new JPanel();
		loadAndProPanel.setLayout(new GridLayout(4,1));
		loadAndProPanel.add(loadInstancesPanel);
		loadAndProPanel.add(storeInstancesPanel);
		loadAndProPanel.add(projectNamePanel);
		loadAndProPanel.add(buttonPanel);
		
		this.enableLoadInstPanel(false);
		this.enableNamePanel(false);	
		okButton.setEnabled(false);

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(captionPanel, BorderLayout.NORTH);
		cp.add(selectProTypePanel, BorderLayout.CENTER);
		cp.add(loadAndProPanel, BorderLayout.SOUTH);
		
		radioProType2.doClick(); //TODO remove in future
		
		this.pack();
		this.setLocation(
				(Toolkit.getDefaultToolkit().getScreenSize().width-getSize().width)/2,
				(Toolkit.getDefaultToolkit().getScreenSize().height-getSize().height)/2);
		this.setVisible(true);
	}
	
	//####################################################################################
	//METHODS
	//####################################################################################
	
	/**
	 * 
	 * @param nameInfo
	 * @param instancesInfo
	 */
	private void setCaptionText(TextHandler[] textHField)
	{
		this.captionPane.setText("");
		
		SimpleAttributeSet saSet = new SimpleAttributeSet();
		saSet = (SimpleAttributeSet)TextHandler.setTextStyle(new SimpleAttributeSet(), Color.black, 17);
		try {
			TextHandler.appendText(this.captionPane,
					"Create a FuzzySet Project \n", saSet);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i=0; i<textHField.length; i++)
		{
			saSet = (SimpleAttributeSet)TextHandler.setTextStyle(new SimpleAttributeSet()
			, textHField[i].getColor()
			, textHField[i].getFontSize());
			try {
				TextHandler.appendText(this.captionPane,
						"\n" + textHField[i].getText()
						, saSet);
				
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	private void enableLoadInstPanel(boolean enabled)
	{
		this.loadInstLabel.setEnabled(enabled);
		this.loadInstTextField.setEnabled(enabled);
		this.browseInstButton.setEnabled(enabled);
		this.storeInstCheckBox.setEnabled(enabled);
	}
	
	private void enableNamePanel(boolean enabled){
		
		this.projectNameLabel.setEnabled(enabled);
		this.projectNameTextField.setEnabled(enabled);
	}
	
	//#########################################################################
	//ActionListener
	//#########################################################################

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getActionCommand().equals("emptypro"))
		{
			this.loadInstTextField.setText("");
			this.enableLoadInstPanel(false);
			this.enableNamePanel(true);
			
			
			TextHandler[] textHField = new TextHandler[1];
			if(!this.nameInvalide)
			{
				textHField[0] = new TextHandler("Enter a project name", Color.black, 11);
				this.okButton.setEnabled(true);
			}
			else
			{
				textHField[0] = new TextHandler("A project with this name already exists"
						, Color.red
						, 14);
			}
			
			this.setCaptionText(textHField);
		}
		else if(arg0.getActionCommand().equals("instancebasedpro"))
		{
			this.enableLoadInstPanel(true);
			this.enableNamePanel(true);
			this.okButton.setEnabled(false);
			
			TextHandler[] textHField = new TextHandler[2];
			textHField[0] = new TextHandler("Choose a set of Instances", Color.black, 11);
	
			if(!this.nameInvalide)
			{
				textHField[1] = new TextHandler("Enter a project name", Color.black, 11);
			}
			else
			{
				textHField[1] = new TextHandler("A project with this name already exists"
						, Color.red
						, 14);
			}
			this.setCaptionText(textHField);
		}
		else if(arg0.getActionCommand().equals("browse"))
		{
			JFileChooser jfc = new JFileChooser();
			jfc.setFileFilter(new javax.swing.filechooser.FileFilter(){

				@Override
				public boolean accept(File arg0) {
					
					if(arg0.isDirectory())
					{
						return true;
					}
					
					String[] fileEnding = arg0.getName().split("\\.");
					
					if(fileEnding[fileEnding.length-1].equals("arff"))
					{
						return true;
					}		
					return false;
				}

				@Override
				public String getDescription() {
					// TODO Auto-generated method stub
					return null;
				}			
			});
			
			int i = jfc.showOpenDialog(fse_Frame);
			
			if(i == JFileChooser.APPROVE_OPTION)
			{
				this.loadInstTextField.setText(jfc.getSelectedFile().toString());
			}
		}
		else if(arg0.getActionCommand().equals("ok"))
		{
			//load Instances
			Instances data = null;
			if(this.radioProType2.isSelected())
			{
				File file = new File(this.loadInstTextField.getText());
				
				data= InstancesLoader.loadInstances(file);
			}
			int numAttr = data.numAttributes();
//			if(data.classAttribute() != null)
//			{
//				numAttr--;
//			}
			
			//Calculate attributes max and min
			double min[] = new double[numAttr];
			double max[] = new double[numAttr];
			
			FuzzySetProject fs_Pro = new FuzzySetProject();
			fs_Pro.setM_ProjectName(this.projectNameTextField.getText());
			
			//Store Attributes
			AttributeWrapper[] attributes = new AttributeWrapper[numAttr];
			for(int a=0; a<numAttr; a++)
			{
				if(data.attribute(a).isNumeric())
				{
					min[a] = Calculations.calcMin(data, a);
					max[a] = Calculations.calcMax(data, a);
				}
				else
				{
					min[a] = 0;
					max[a] = data.attribute(a).numValues()-1;
				}

				attributes[a] = new AttributeWrapper(data.attribute(a), min[a], max[a]);
			}
			fs_Pro.setAttributes(attributes);
			
			//Store Classes
			ClassWrapper[] classes = null;
			if(data.classAttribute().isNominal())
			{
				classes = new ClassWrapper[data.numClasses()];
				
				for(int c = 0; c< data.numClasses(); c++)
				{					
					classes[c] = new ClassWrapper();
					classes[c].setClassAttribute(data.classAttribute());
					classes[c].setClassName(data.classAttribute().value(c));
				}
			}
			else
			{
				classes = new ClassWrapper[1];
				classes[0] = new ClassWrapper();
				classes[0].setClassName("Regression");
			}
			fs_Pro.setClasses(classes);
			
			//Instances should be stored
			if(this.storeInstCheckBox.isSelected())
			{
				fs_Pro.setInstances(data);
			}
			
			//Build Histogramms
			if(data.classAttribute().isNominal())
			{
				fs_Pro.setHistogramms(Histogramm.getHistogrammsFromData(data));
			}
									
			//Build Pro-Tree
			DefaultMutableTreeNode proNode = new DefaultMutableTreeNode(fs_Pro.getM_ProjectName());
			proNode.setUserObject(fs_Pro);
			
			if(data != null)
			{				
				for(int c=0; c<data.numClasses(); c++)
				{
					DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(classes[c]);
					
					for(int a=0; a<numAttr; a++)
					{
						DefaultMutableTreeNode attrNode = new DefaultMutableTreeNode(attributes[a]);
						
						classNode.add(attrNode);
					}
					proNode.add(classNode);
				}
			}
			//Add to StartWindow
			this.fse_Frame.getStartW().getProjectTrees().add(proNode);
			
			//Add to Tree
			this.fse_Frame.getFuzzySetTree().addChildNode(proNode
					, this.fse_Frame.getFuzzySetTree().getRoot());
			
			//Create Templates
			TemplatePackCreator tpc = new TemplatePackCreator();
			
			FuzzySetCreator fsc = new FuzzySetCreator(data);
			fs_Pro.setFsc(fsc);
			DefaultMutableTreeNode templateTree = tpc.createTemplatePack(fsc, data);
			
			//add Templates to project-Tree
			fse_Frame.getFuzzySetTree().addChildNode(templateTree
					, proNode);
			
			//add Templates to project
			fs_Pro.setTemplateTree(templateTree);
			
			//Save Project
			File file = new File(
					fse_Frame.getStartW()
					.getStartSettings().getWorkspacePath()
					.toString()+"/workspaceEditor");
			
			FS_ProjectWriter fs_ProWriter = new FS_ProjectWriter(file, fs_Pro.getM_ProjectName());
			try {
				if(data == null)
				{
					fs_ProWriter.write(fs_Pro);
				}
				else
				{
					for(int i=0; i< fs_Pro.getClasses().length; i++)
					{
						System.out.println(fs_Pro.getClasses()[i].getClassName());
					}
					
					fs_ProWriter.write(fs_Pro, data);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			this.dispose();
		}
		else if(arg0.getActionCommand().equals("cancel"))
		{
			this.dispose();
		}
	}	
	
	//###################################################################################
	//DocumentListener
	//###################################################################################

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		
		TextHandler[] textHField = new TextHandler[2];
		textHField[0] = new TextHandler("Choose a set of Instances", Color.black, 11);
		textHField[1] = new TextHandler("Enter a project name", Color.black, 11);

		//check file to load
		boolean isCorrectFile = false;
		if(this.loadInstTextField.isEnabled())
		{
			String[] fileEnding = this.loadInstTextField.getText().split("\\.");
			if(fileEnding[fileEnding.length-1].equals("arff"))
			{
				isCorrectFile = true;
				textHField[0].setText("");
			}
		}
		else
		{
			isCorrectFile = true;
			textHField[0].setText("");
		}
		
		//check Project Name
		TreeModel treeModel = fse_Frame.getFuzzySetTree().getTreeModel();
		
		nameInvalide = false;
		
		for(int i=0; i<treeModel.getChildCount(
				fse_Frame.getFuzzySetTree().getRoot()); i++)
		{
			if(this.projectNameTextField.getText().equals(
					treeModel.getChild(treeModel.getRoot(), i).toString()))
			{
				textHField[1].setText("A project with this name already exists");
				textHField[1].setFontSize(14);
				textHField[1].setColor(Color.red);
				nameInvalide = true;
			}
		}
				
		if(!nameInvalide && isCorrectFile)
		{
			this.okButton.setEnabled(true);			
		}
		else
		{
			this.okButton.setEnabled(false);
		}
		this.setCaptionText(textHField);
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		
		insertUpdate(arg0);
		
	}
}
