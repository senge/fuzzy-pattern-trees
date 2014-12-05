package gui_pt.gui;

import gui_pt.accessLayer.loader.PTLoader;
import gui_pt.accessLayer.util.AccessPT;
import gui_pt.io.InstancesLoader;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import weka.core.Instances;

public class DefaultPTCreatorDialog extends JDialog implements ActionListener{
	
	JPanel mainPanel;
	JPanel centerPanel;
	JPanel southPanel;
	JLabel label1;
	
	JPanel browsePanel;
	JTextField browseTextField;
	JButton browseButton;
	
	JPanel optionPanel;
	JLabel oLabel;
	JTextField oTextField;
	
	JPanel algoPanel;
	JLabel aLabel;
	JComboBox<String> aComboBox;
	
	JButton okButton;
	JButton cancelButton;
	
	
	private MainWindow mainW;
	
	public DefaultPTCreatorDialog(MainWindow mainW)
	{
		super(mainW);
		
		this.mainW = mainW;
		
		label1 = new JLabel("Instances: ");
		
		browseTextField = new JTextField(30);
		
		browseButton = new JButton("Browse");
		browseButton.setActionCommand("browse");
		browseButton.addActionListener(this);
		
		browsePanel = new JPanel();
		browsePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		browsePanel.add(label1);
		browsePanel.add(browseTextField);
		browsePanel.add(browseButton);
		
		oLabel = new JLabel("Options:     ");
		
		oTextField = new JTextField(30);
		oTextField.setText("-C  5 -E 0.0025");
		
		optionPanel = new JPanel();
		optionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		optionPanel.add(oLabel);
		optionPanel.add(oTextField);
		
		aLabel = new JLabel("Algorithm:");
		
		String[] algos = {"PTBU","PTTD"};				
		aComboBox = new JComboBox<String>(algos);

		algoPanel = new JPanel();
		algoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		algoPanel.add(aLabel);
		algoPanel.add(aComboBox);
		
		centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(browsePanel, BorderLayout.NORTH);
		centerPanel.add(optionPanel, BorderLayout.CENTER);
		centerPanel.add(algoPanel, BorderLayout.SOUTH);
		
		okButton = new JButton("Ok");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		
		southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		southPanel.add(cancelButton);
		southPanel.add(okButton);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		
		cp.add(mainPanel, BorderLayout.CENTER);
				
		this.setSize(600,200);
		
		this.setLocation(
				(Toolkit.getDefaultToolkit().getScreenSize().width-getSize().width)/2,
				(Toolkit.getDefaultToolkit().getScreenSize().height-getSize().height)/2);
		this.setVisible(true);
	}
	
	//#######################################################################################
	//ActionListener
	//#######################################################################################

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(arg0.getActionCommand().equals("browse"))
		{
			JFileChooser jfc = new JFileChooser();
			jfc.setFileFilter(new FileNameExtensionFilter(null, "arff"));
			
			int i = jfc.showOpenDialog(mainW);
			
			if(i == JFileChooser.APPROVE_OPTION)
			{
				this.browseTextField.setText(jfc.getSelectedFile().toString());
			}
		}
		if(arg0.getActionCommand().equals("ok"))
		{
			File file = new File(this.browseTextField.getText());
			
			Instances data= InstancesLoader.loadInstances(file);
			
			AccessPT accPT = null;
			
			String[] options = this.oTextField.getText().split(" ");
			
			PTLoader ptl = new PTLoader();
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));			
			accPT = ptl.create(data, null, options, aComboBox.getSelectedIndex());
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			this.mainW.addAccPT(accPT, false, null);
						
			this.mainW.validate();
			
			this.dispose();
		}
		if(arg0.getActionCommand().equals("cancel"))
		{
			this.dispose();
		}
	}
}
