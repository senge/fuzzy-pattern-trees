package gui_pt.gui;

import gui_pt.accessLayer.stream.AccessStream;
import gui_pt.stream.DoTaskFromGui;
import gui_pt.stream.RunAsStreamAble;
import gui_pt.stream.StreamView;
import gui_pt.stub.PTStub;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import moa.classifiers.trees.ePTTD;

public class OpenStreamDialog extends JDialog implements ActionListener, ListSelectionListener{
	
	JTextArea command_textArea;
	JList<String> command_List;
	DefaultListModel<String> listModel;
	
	JButton saveCommand_Button;
	JTextField saveCommand_textField;
	JButton deleteCommand_Button;
	
	JButton startButton;
	
	JPanel centerPanel;
	JPanel centerNorthPanel;
	JPanel southPanel;
	JPanel southPanelW;
	JPanel southPanelO;
	
	private RunAsStreamAble[] obj = {new PTStub()};
	private MainWindow mainW;
	
	//####################################################################################
	//Constructor
	//####################################################################################
	
	public OpenStreamDialog(MainWindow mainW){
		super(mainW);
		
		this.mainW = mainW;
			
		command_textArea = new JTextArea();
		command_textArea.setPreferredSize(new Dimension(700,120));
		command_textArea.setLineWrap(true);
		command_textArea.setBorder(new TitledBorder("Command"));
		
		String[] keys = mainW.getStartW().getStartSettings().getCommandKeys();
		
		listModel = new DefaultListModel<String>();
		
		command_List = new JList<String>(listModel);
		
		if(keys != null)
		{
			for(int i=0; i<keys.length; i++)
			{
				listModel.addElement(keys[i]);
			}
		}
		
		saveCommand_Button = new JButton("save command");
		saveCommand_Button.setActionCommand("saveCommand");
		saveCommand_Button.addActionListener(this);
		
		deleteCommand_Button = new JButton("delete");
		deleteCommand_Button.setActionCommand("deleteCommand");
		deleteCommand_Button.addActionListener(this);
		
		saveCommand_textField = new JTextField(40);
		
		command_List.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		command_List.addListSelectionListener(this);
		command_List.setLayoutOrientation(JList.VERTICAL);
		command_List.setVisibleRowCount(-1);
		
		centerNorthPanel = new JPanel();
		centerNorthPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		centerNorthPanel.add(saveCommand_Button);
		centerNorthPanel.add(saveCommand_textField);
		
		centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(new JScrollPane(command_List), BorderLayout.CENTER);
		centerPanel.add(centerNorthPanel, BorderLayout.NORTH);
		
		//default text.
		command_textArea.setText(mainW.getStartW().getStartSettings().getDefaultCommand());
		
		command_textArea.setText("EvaluatePeriodicHeldOutTest -e BasicRegressionPerformanceEvaluator -l " +
		"(LearnModel -l " +
		"(moa.classifiers.trees.ePTTD -C 5 -E 0.04 -U) " +
		"-s (generators.HyperplaneGeneratorReg -m CubicDistance -i 1 -a 4) -m 1000) " +
		"-s (ConceptDriftStream -s (generators.HyperplaneGeneratorReg -i 1 -a 4 -m CubicDistance) " +
		"-d (generators.HyperplaneGeneratorReg -i 13 -a 4 -m CubicDistance) -p 25000 -w 10000) " +
		"-n 200 -i 50000 -f 1000 -d C:\\SVN-4CubicDistanceHyperplane.csv" );
		
		startButton = new JButton("start");
		startButton.setActionCommand("start");
		startButton.addActionListener(this);
		
		southPanelW = new JPanel();
		southPanelW.setLayout(new FlowLayout(FlowLayout.LEFT));
		southPanelW.add(deleteCommand_Button);
		
		southPanelO = new JPanel();
		southPanelO.setLayout(new FlowLayout(FlowLayout.RIGHT));
		southPanelO.add(startButton);
		
		southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		southPanel.add(southPanelW, BorderLayout.WEST);
		southPanel.add(southPanelO, BorderLayout.EAST);
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		
		cp.add(command_textArea, BorderLayout.NORTH);
		cp.add(centerPanel, BorderLayout.CENTER);
		cp.add(southPanel, BorderLayout.SOUTH);
		
		this.setSize(700,400);
		this.setModal(true);
		this.setLocation(
				(Toolkit.getDefaultToolkit().getScreenSize().width-getSize().width)/2,
				(Toolkit.getDefaultToolkit().getScreenSize().height-getSize().height)/2);
		this.setVisible(true);		
	}
	
	//###################################################################################
	//ActionListener
	//###################################################################################

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getActionCommand().equals("start"))
		{						
			AccessStream as = new AccessStream();
			
			//create new StreamView
			
			StreamView sv = new StreamView();
			mainW.addStreamView(sv, null);
			
//			as.addObserver(sv);
			ePTTD.setObserver(sv);
						
//		    JDialog jDialog = new JDialog(mainW);
//		    
//			JTextArea out_textArea;
//			out_textArea = new JTextArea();
//			DefaultCaret caret = (DefaultCaret)out_textArea.getCaret();
//			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
//			out_textArea.setLineWrap(true);
//		    
//		    Container cp = jDialog.getContentPane();
//		    cp.setLayout(new BorderLayout());
//		    cp.add(new JScrollPane(out_textArea), BorderLayout.CENTER);
//		    
//		    jDialog.setModal(false);
//		    jDialog.setSize(600,200);
//		    jDialog.setLocation(
//					(Toolkit.getDefaultToolkit().getScreenSize().width-getSize().width)/2,
//					(Toolkit.getDefaultToolkit().getScreenSize().height-getSize().height)/2);
//		    jDialog.setVisible(true);
//
//		    PrintStream originalStream = System.out;
//		    PrintStream printStream = new PrintStream(new TextAreaOutputStream(out_textArea));
//		    System.setOut(printStream);
//		    System.setErr(printStream);
		    
		    SwingWorker<Object, Void> sw = new SwingWorker<Object, Void>(){

				@Override
				protected Void doInBackground() throws Exception {
					
					DoTaskFromGui.doTask(command_textArea.getText().split(" "));
					return null;
				}		    	
		    };
		    sw.execute();
		    			
//			System.setOut(originalStream);
//			System.setErr(originalStream);
			    
		    this.dispose();
			
		}
		else if(arg0.getActionCommand().equals(saveCommand_Button.getActionCommand()))
		{
			String text = saveCommand_textField.getText();
		
			if(text.equals("")) text = "Command_1";

			int count = 0;
			String helpString = text;
			for(int i=0; i< listModel.size(); i++)
			{
				if(listModel.getElementAt(i).equals(helpString))
				{
					count++;

					helpString = text + "(" + count + ")";
					i=0;
				}
			}			
			listModel.addElement(helpString);
			mainW.getStartW().getStartSettings().addCommand(helpString, command_textArea.getText());
		}
		else if(arg0.getActionCommand().equals(deleteCommand_Button.getActionCommand()))
		{
			String key = command_List.getSelectedValue();
			int index = command_List.getSelectedIndex();
			
			if(key != null)
			{
				mainW.getStartW().getStartSettings().deleteCommand(key);
				listModel.removeElement(key);
				
				if(listModel.size() > index)
				{
					command_List.setSelectedIndex(index);
				}
				else
				{
					command_List.setSelectedIndex(index-1);
				}
			}
		}
	}
	
	//###################################################################################
	// ListSelectionListener
	//###################################################################################

	@Override
	public void valueChanged(ListSelectionEvent arg0) 
	{
		if(command_List.getSelectedIndex() != -1)
		{
			String key = command_List.getSelectedValue();
			
			command_textArea.setText(mainW.getStartW().getStartSettings().getCommand(key));
		}
		
	}
}
