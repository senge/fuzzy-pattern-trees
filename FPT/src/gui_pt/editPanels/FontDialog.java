package gui_pt.editPanels;

import gui_pt.drawHelper.DrawNorm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FontDialog extends JDialog implements ActionListener, ChangeListener{
	
	JPanel mainPanel;
	
	JPanel buttonArrayPanel;
	JButton[] buttonArray1;
	
	JPanel panelEast;
	
	Font	fontSetting;
	JLabel currentFont;
	
	JPanel checkBoxPanel;
	JCheckBox fettBox;
	JCheckBox italicBox;
	
	JSpinner fontSize;
	
	JPanel buttonPanel;
	JButton okButton;
	JButton cancelButton;
	
	DrawNorm dn;
	EditInnerNodesPanel einP;
	
	public FontDialog(EditInnerNodesPanel einP, DrawNorm dn)
	{
		this.einP = einP;
		this.dn = dn;
		
		this.fontSetting = dn.getNormFont();
		
		this.fillFontArray();
		
		fettBox = new JCheckBox("Bold");
		fettBox.setActionCommand("bold");
		fettBox.addActionListener(this);
		italicBox = new JCheckBox("Italic");
		italicBox.setActionCommand("italic");
		italicBox.addActionListener(this);
		
		fontSize = new JSpinner(new SpinnerNumberModel(12,1,60,1));
		fontSize.addChangeListener(this);
		
		checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		checkBoxPanel.add(fettBox);
		checkBoxPanel.add(italicBox);
		checkBoxPanel.add(fontSize);		
				
		currentFont = new JLabel(fontSetting.getFamily());
		currentFont.setFont(fontSetting);
		
		okButton = new JButton("Ok");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		panelEast = new JPanel();
		panelEast.setLayout(new BorderLayout());
		panelEast.add(checkBoxPanel, BorderLayout.NORTH);
		panelEast.add(currentFont, BorderLayout.CENTER);
		panelEast.add(buttonPanel, BorderLayout.SOUTH);
		
		buttonArrayPanel = new JPanel();
		buttonArrayPanel.setLayout(new GridLayout(buttonArray1.length,1));
		
		for(int i=0; i< buttonArray1.length; i++)
		{
			buttonArrayPanel.add(buttonArray1[i]);
		}
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(new JScrollPane(buttonArrayPanel), BorderLayout.WEST);
		mainPanel.add(panelEast, BorderLayout.CENTER);
				
		this.add(mainPanel);
		this.setSize(600, 300);
		this.setLocation(300,300);
		this.setModal(true);
		this.setVisible(true);		
	}
	
	private void fillFontArray(){
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] arfonts = ge.getAvailableFontFamilyNames();	
		buttonArray1 = new JButton[arfonts.length];
		
		for(int i=0; i<buttonArray1.length; i++)
		{
			buttonArray1[i] = new JButton(arfonts[i]);
			buttonArray1[i].setBackground(Color.white);
			buttonArray1[i].setMargin(new Insets(0,0,0,0));
			buttonArray1[i].setFont(new Font(arfonts[i], Font.PLAIN, 16));
			buttonArray1[i].setActionCommand("fontSelect");
			buttonArray1[i].addActionListener(this);
		}
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(arg0.getActionCommand().equals("fontSelect"))
		{			
			currentFont.setText(((JButton)arg0.getSource()).getText());
			
			int style = Font.PLAIN;
			if(fettBox.isSelected() && italicBox.isSelected())
			{
				style = Font.BOLD + Font.ITALIC;
			}
			else if(fettBox.isSelected())
			{
				style = Font.BOLD;
			}
			else if(italicBox.isSelected())
			{
				style = Font.ITALIC;
			}
			
			this.fontSetting = new Font(((JButton)arg0.getSource()).getText()
					, style
					, ((Integer)fontSize.getValue()).intValue());
			currentFont.setFont(fontSetting);
		}
		else if(arg0.getActionCommand().equals("bold"))
		{
			int style = Font.PLAIN;
			if(fettBox.isSelected() && italicBox.isSelected())
			{
				style = Font.BOLD + Font.ITALIC;
			}
			else if(fettBox.isSelected())
			{
				style = Font.BOLD;
			}
			else if(italicBox.isSelected())
			{
				style = Font.ITALIC;
			}
			fontSetting = fontSetting.deriveFont(style);
			currentFont.setFont(fontSetting);
		}
		else if(arg0.getActionCommand().equals("italic"))
		{
			int style = Font.PLAIN;
			if(fettBox.isSelected() && italicBox.isSelected())
			{
				style = Font.BOLD + Font.ITALIC;
			}
			else if(fettBox.isSelected())
			{
				style = Font.BOLD;
			}
			else if(italicBox.isSelected())
			{
				style = Font.ITALIC;
			}
			fontSetting = fontSetting.deriveFont(style);
			currentFont.setFont(fontSetting);
		}
		else if(arg0.getActionCommand().equals("ok"))
		{
			this.dn.setNormFont(fontSetting);
			this.einP.getDp().calcMaxStringLength();
			this.einP.getDp().resizeINodes(this.einP.getDp().getMaxLength()
					+ (int)(this.einP.getDp().getMaxLength()*0.05));
			this.einP.getDp().repaint();
			this.dispose();
		}
		else if(arg0.getActionCommand().equals("cancel"))
		{
			this.dispose();
		}
		
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		
		fontSetting = fontSetting.deriveFont(((Integer)this.fontSize.getValue()).floatValue());
		currentFont.setFont(fontSetting);
		
	}

}
