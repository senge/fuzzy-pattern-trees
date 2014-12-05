package gui_pt.guiUtil;

import gui_pt.gui.StartWindow;
import gui_pt.listener.WindowClosingAdapter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

 public class WorkspaceLuncher extends JDialog implements ActionListener{
	
	StartWindow startWindow;
	
	JPanel panelMain;
	JPanel panelNorth;
	JPanel panelCenter;
	JPanel panelSouth;
	JPanel panelSouthWest;
	JPanel panelSouthEast;
	JTextPane textPane1;
	JLabel label1;
	JLabel label2;
	JTextField textField1;
	JButton buttonBrowse;
	JButton buttonOK;
	JButton buttonCancel;
	JCheckBox checkBox1;
	
	boolean closeOwner = false;
	
	public WorkspaceLuncher(StartWindow startWindow){
		
		super(startWindow, "Workspace Luncher", true);
		
		this.startWindow = startWindow;
		
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		
		panelMain = new JPanel();
		panelNorth = new JPanel();
		panelCenter = new JPanel();
		panelSouth = new JPanel();
		panelSouthWest = new JPanel();
		panelSouthEast = new JPanel();
		textPane1 = new JTextPane();
		label1 = new JLabel("Workspace");
		label2 = new JLabel("use this as default and don`t ask again");
		textField1 = new JTextField(50);
		buttonBrowse = new JButton("Browse");
		buttonOK = new JButton("OK");
		buttonCancel = new JButton("Cancel");
		checkBox1 = new JCheckBox();
		textPane1.setEditable(false);
		panelMain.setLayout(new BorderLayout());
		panelNorth.setLayout(new BorderLayout());
		panelCenter.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelSouth.setLayout(new GridLayout(1,2));
		panelSouthWest.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelSouthEast.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelNorth.setBorder(new EtchedBorder());
		textField1.setText(this.startWindow.getStartSettings().getWorkspacePath());
		buttonBrowse.addActionListener(this);
		buttonOK.addActionListener(this);
		buttonCancel.addActionListener(this);
		checkBox1.addActionListener(this);
		checkBox1.setActionCommand("check");
		panelNorth.add(textPane1);
		panelCenter.add(label1);
		panelCenter.add(textField1);
		panelCenter.add(buttonBrowse);
		panelSouthWest.add(checkBox1);
		panelSouthWest.add(label2);
		panelSouthEast.add(buttonOK);
		panelSouthEast.add(buttonCancel);
		panelSouth.add(panelSouthWest);
		panelSouth.add(panelSouthEast);
		panelMain.add(panelNorth, BorderLayout.NORTH);
		panelMain.add(panelCenter, BorderLayout.CENTER);
		panelMain.add(panelSouth, BorderLayout.SOUTH);
		
		cp.add(panelMain, BorderLayout.CENTER);
		
		SimpleAttributeSet saSet = new SimpleAttributeSet();
		saSet = (SimpleAttributeSet)TextHandler.setTextStyle(new SimpleAttributeSet(), Color.black, 17);
		try {
			TextHandler.appendText(this.textPane1,
					"Select a Workspace \n", saSet);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		saSet = (SimpleAttributeSet)TextHandler.setTextStyle(new SimpleAttributeSet(), Color.black, 11);
		try {
			TextHandler.appendText(this.textPane1,
					"\nPTV Platform stores your Visualisations in a folder called workspaceV \n" +
					"and FuzzySet-Creations in folder called workspaceE \n" +
					"\nChoose a workspace-top folder to use for this session.\n", saSet);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		this.addWindowListener(new WindowClosingAdapter());
//		this.setSize(600,250);
		this.pack();
		this.setLocation(
				(Toolkit.getDefaultToolkit().getScreenSize().width-getSize().width)/2,
				(Toolkit.getDefaultToolkit().getScreenSize().height-getSize().height)/2);
		this.setVisible(true);	
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getActionCommand().equals("Browse")){
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int fcValue = fc.showOpenDialog(this);
			if(fc.getSelectedFile().isDirectory() && fcValue==0){
				this.textField1.setText(fc.getSelectedFile().toString());
			}
		}else if(arg0.getActionCommand().equals("OK")){
			
			File dir = new File(this.textField1.getText());
			File dirViewer = new File(this.textField1.getText()+"/workspaceViewer");
			File dirEditor = new File(this.textField1.getText()+"/workspaceEditor");
			
			if(!dir.isDirectory()){
				dir.mkdirs();
			}
			if(!dirViewer.isDirectory()){
				dirViewer.mkdir();
			}
			if(!dirEditor.isDirectory()){
				dirEditor.mkdir();
			}
			
			this.startWindow.getStartSettings().setWorkspacePath(dir.toString());

			if(this.checkBox1.isSelected()){
				this.startWindow.getStartSettings().setShowWorkspaceLuncher(false);
			}
			try {
				FileOutputStream fos = new FileOutputStream(new File("./PTV/startsettings.obj"));
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(this.startWindow.getStartSettings());
				oos.close();
				fos.close();
				//WorkspaceSettings
//				FileOutputStream fos1 = new FileOutputStream(new File(this.textField1.getText()+"/settingsW.obj"));
//				ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
//				oos1.writeObject(this.eaMainW.getSettingsWorkspace());
//				oos1.close();
//				fos1.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.dispose();
			
		}else if(arg0.getActionCommand().equals("Cancel")){
			this.closeOwner = true;
			System.exit(0);
		}				
	}

}
