package gui_pt.editPanels;

import gui_pt.drawHelper.DrawNorm;
import gui_pt.drawObjects.DrawDetail;
import gui_pt.gui.DrawPanel;
import gui_pt.listener.DefaultDocumentListener;
import gui_pt.listener.EditInnerNodePanelActionListener;
import gui_pt.visualisation.DefaultPTV;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class EditInnerNodesPanel extends JPanel {
	
	DefaultPTV dPTV;
	DrawPanel dp;
	
	private int normNumber = 3;
	private int normCounter = 0;
	
	//all Nodes -------------------------------------------------
	JPanel allNodePanel;
	
	JPanel sizePanel;
	JRadioButton rButtonDynamicSize;
	JRadioButton rButtonDefaultSize;
	JRadioButton rButtonCustomSize;
	ButtonGroup buttonG_all1;
	
	JSpinner customSizeSpinner;
	
	JPanel detailPanel;
	JPanel detailCheckPanel;
	JCheckBox attrNameCheck;
	JCheckBox fuzzySetCheck;
	JCheckBox performanceCheck;
	JCheckBox outputCheck;
	TreeMap<Integer,JCheckBox> checkBoxSet = new TreeMap<Integer,JCheckBox>();
	JRadioButton rButtonDetails;
	JRadioButton rButtonCustomDetails;
	ButtonGroup buttonG_all2;
	
	// special Nodes --------------------------------------------
	
	JPanel[] normPanel;
	
	JPanel[] buttonPanel_t1 = new JPanel[normNumber];
	JButton[]	normColorButton = new JButton[normNumber];
	JButton[] normFontButton = new JButton[normNumber];
	
	JPanel[] rButtonPanel_t1 = new JPanel[normNumber];
	JRadioButton[] rButton_t1 = new JRadioButton[normNumber];
	JRadioButton[] rButton_t2 = new JRadioButton[normNumber];
	JRadioButton[] rButton_t3 = new JRadioButton[normNumber];
	ButtonGroup[] buttonG_t1 = new ButtonGroup[normNumber];
	
	JPanel[] customPanel_t1 = new JPanel[normNumber];
	JTextArea[] textArea_t1 = new JTextArea[normNumber];
	JPanel[] imageTopPanel = new JPanel[normNumber];
	JPanel[] imagePanel = new JPanel[normNumber];
	Image[] tImage = new Image[normNumber];
	JPanel[] iconPanelHelp_1 = new JPanel[normNumber];
	JLabel[] imageLabel = new JLabel[normNumber];
	JButton[] browseButton = new JButton[normNumber];
	
	EditInnerNodePanelActionListener einpListener;
	
	//#########################################################################################
	// Constructor
	//#########################################################################################
	
	public EditInnerNodesPanel(DefaultPTV dPTV, DrawPanel showingDP)
	{				
		this.dPTV = dPTV;
		this.dp = showingDP;	
		
		einpListener = new EditInnerNodePanelActionListener(dPTV, this, showingDP.gettNormDraw() ,normCounter);
		
		rButtonDynamicSize = new JRadioButton("Dynamic-Size");
		rButtonDynamicSize.setActionCommand("dynamic");
		rButtonDynamicSize.addActionListener(einpListener);
				
		rButtonDefaultSize = new JRadioButton("Default-Size");
		rButtonDefaultSize.setActionCommand("default");
		rButtonDefaultSize.addActionListener(einpListener);
		rButtonDefaultSize.setSelected(true);
		
		rButtonCustomSize = new JRadioButton("Custom-Size");
		rButtonCustomSize.setActionCommand("custom");
		rButtonCustomSize.addActionListener(einpListener);
		
		buttonG_all1 = new ButtonGroup();
		buttonG_all1.add(rButtonDynamicSize);
		buttonG_all1.add(rButtonDefaultSize);
		buttonG_all1.add(rButtonCustomSize);
		
		customSizeSpinner = new JSpinner(new SpinnerNumberModel(30,1,100,1));
		customSizeSpinner.setPreferredSize(new Dimension(30,20));
		customSizeSpinner.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				if(rButtonCustomSize.isSelected())
				{
					dp.resizeINodes((Integer)customSizeSpinner.getValue());
					dp.repaint();
				}
			}			
		});
		customSizeSpinner.setName("spinner1");
		
		sizePanel = new JPanel();
		sizePanel.setLayout(new GridLayout(4,1));
		sizePanel.add(rButtonDynamicSize);
		sizePanel.add(rButtonDefaultSize);
		sizePanel.add(rButtonCustomSize);
		sizePanel.add(customSizeSpinner);
		
		rButtonDetails = new JRadioButton("Details");
		rButtonDetails.setActionCommand("details");
		rButtonDetails.addActionListener(einpListener);
		
		
		rButtonCustomDetails = new JRadioButton("Custom-Details");
		rButtonCustomDetails.setActionCommand("custom-details");
		rButtonCustomDetails.addActionListener(einpListener);
		
		buttonG_all2 = new ButtonGroup();
		buttonG_all2.add(rButtonDetails);
		buttonG_all2.add(rButtonCustomDetails);
		
		attrNameCheck = new JCheckBox("Attribute Name");
		attrNameCheck.setName(DrawDetail.ATTRIBUTE_NAME+"");
		attrNameCheck.setActionCommand("attrCheck");
		attrNameCheck.addActionListener(einpListener);
		fuzzySetCheck = new JCheckBox("Fuzzyset");
		fuzzySetCheck.setName(DrawDetail.FUZZYSET+"");
		fuzzySetCheck.setActionCommand("fuzzySetCheck");
		fuzzySetCheck.addActionListener(einpListener);
		performanceCheck = new JCheckBox("Performance");
		performanceCheck.setName(DrawDetail.PERFORMANCE+"");
		performanceCheck.setActionCommand("performanceCheck");
		performanceCheck.addActionListener(einpListener);
		outputCheck = new JCheckBox("Output");
		outputCheck.setName(DrawDetail.OUTPUT+"");
		outputCheck.setActionCommand("outputCheck");
		outputCheck.addActionListener(einpListener);
		
		checkBoxSet.put(DrawDetail.ATTRIBUTE_NAME, attrNameCheck);
		checkBoxSet.put(DrawDetail.FUZZYSET, fuzzySetCheck);
		checkBoxSet.put(DrawDetail.PERFORMANCE, performanceCheck);
		checkBoxSet.put(DrawDetail.OUTPUT, outputCheck);
		
		detailCheckPanel = new JPanel();
		detailCheckPanel.setLayout(new GridLayout(4,1));
		detailCheckPanel.add(attrNameCheck);
		detailCheckPanel.add(fuzzySetCheck);
		detailCheckPanel.add(performanceCheck);
		detailCheckPanel.add(outputCheck);
		
		this.selectCustomDetailChecks(dp.getDetailsToShow());
		if(dp.isShowDetails())
		{
			this.enableAllDetailChecks(false);
			rButtonDetails.setSelected(true);
		}
		else
		{
			this.enableAllDetailChecks(true);
			rButtonCustomDetails.setSelected(true);
		}
		
		
		detailPanel = new JPanel();
		detailPanel.setLayout(new GridLayout(2,1));
		detailPanel.add(rButtonDetails);
		detailPanel.add(rButtonCustomDetails);
		
		allNodePanel = new JPanel();
		allNodePanel.setLayout(new GridLayout(1,3));
		allNodePanel.setBorder(new TitledBorder("Base Settings"));
		allNodePanel.add(sizePanel);
		allNodePanel.add(detailPanel);
		allNodePanel.add(detailCheckPanel);
		
		normPanel = new JPanel[3];

		buildEditDrawNormPanel(this.dp.gettNormDraw(), DrawNorm.TNORM);
		normPanel[normCounter].setBorder(new TitledBorder("T-Norm"));
		normCounter++;
		buildEditDrawNormPanel(this.dp.gettCoNormDraw(), DrawNorm.TCONORM);
		normPanel[normCounter].setBorder(new TitledBorder("T-CoNorm"));
		normCounter++;
		buildEditDrawNormPanel(this.dp.getAverageDraw(), DrawNorm.AVERAGENORM);
		normPanel[normCounter].setBorder(new TitledBorder("Average"));
		
		
		this.setLayout(new GridLayout(1,4));
		
		this.add(allNodePanel);
		this.add(normPanel[0]);
		this.add(normPanel[1]);
		this.add(normPanel[2]);
	}
	
	private void buildEditDrawNormPanel(DrawNorm dn, int norm)
	{
		
		EditInnerNodePanelActionListener einpListener 
			= new EditInnerNodePanelActionListener(this.dPTV, this, dn, normCounter);
		DefaultDocumentListener ddListener 
			= new DefaultDocumentListener(this, dn, normCounter);
		
		normColorButton[normCounter] = new JButton("Color");
		normColorButton[normCounter].setBackground(dn.getNormColor());
		normColorButton[normCounter].setActionCommand("normColor");
		normColorButton[normCounter].addActionListener(einpListener);
		JPanel ncbPanel = new JPanel();
		ncbPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		ncbPanel.add(normColorButton[normCounter]);
		
		normFontButton[normCounter] = new JButton("Font");
		normFontButton[normCounter].setActionCommand("normFont");
		normFontButton[normCounter].addActionListener(einpListener);
		JPanel nfbPanel = new JPanel();
		nfbPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		nfbPanel.add(normFontButton[normCounter]);
		
		buttonPanel_t1[normCounter] = new JPanel();
		buttonPanel_t1[normCounter].setLayout(new GridLayout(2,1));
		buttonPanel_t1[normCounter].add(ncbPanel);
		buttonPanel_t1[normCounter].add(nfbPanel);
		
		rButton_t1[normCounter] = new JRadioButton("Symbol");
		rButton_t1[normCounter].setActionCommand("normSymbol");
		rButton_t1[normCounter].addActionListener(einpListener);
		
		rButton_t2[normCounter] = new JRadioButton("String");
		rButton_t2[normCounter].setActionCommand("normString");
		rButton_t2[normCounter].addActionListener(einpListener);
		rButton_t2[normCounter].setSelected(true);
		
		rButton_t3[normCounter] = new JRadioButton("Custom");
		rButton_t3[normCounter].setActionCommand("normCustom");
		rButton_t3[normCounter].addActionListener(einpListener);
		
		buttonG_t1[normCounter] = new ButtonGroup();
		buttonG_t1[normCounter].add(rButton_t1[normCounter]);
		buttonG_t1[normCounter].add(rButton_t2[normCounter]);
		buttonG_t1[normCounter].add(rButton_t3[normCounter]);
		
		rButtonPanel_t1[normCounter] = new JPanel();
		rButtonPanel_t1[normCounter].setLayout(new GridLayout(3,1));
		rButtonPanel_t1[normCounter].add(rButton_t1[normCounter]);
		rButtonPanel_t1[normCounter].add(rButton_t2[normCounter]);
		rButtonPanel_t1[normCounter].add(rButton_t3[normCounter]);
		
		textArea_t1[normCounter] = new JTextArea(3,8);
		textArea_t1[normCounter].getDocument().addDocumentListener(ddListener);	
		
		imagePanel[normCounter] = new JPanel(){
			
			public void paintComponents(Graphics g)
			{
				g.drawImage(EditInnerNodesPanel.this.tImage[normCounter]
						, 0
						, 0
						, 60
						, 60
						, this);
			}
		};
		
		imageLabel[normCounter] = new JLabel("Icon: ");
		browseButton[normCounter] = new JButton("browse");
		browseButton[normCounter].setActionCommand("tbrowse");
		browseButton[normCounter].addActionListener(einpListener);
		
		iconPanelHelp_1[normCounter] = new JPanel();
		iconPanelHelp_1[normCounter].setLayout(new FlowLayout(FlowLayout.LEFT));
		iconPanelHelp_1[normCounter].add(imageLabel[normCounter]);
		iconPanelHelp_1[normCounter].add(browseButton[normCounter]);
		
		imageTopPanel[normCounter] = new JPanel();
		imageTopPanel[normCounter].setLayout(new BorderLayout());
		imageTopPanel[normCounter].add(iconPanelHelp_1[normCounter], BorderLayout.NORTH);
		imageTopPanel[normCounter].add(imagePanel[normCounter], BorderLayout.CENTER);
		
		customPanel_t1[normCounter] = new JPanel();
		customPanel_t1[normCounter].setLayout(new BorderLayout());
		customPanel_t1[normCounter].add(new JScrollPane(textArea_t1[normCounter]), BorderLayout.WEST);
		customPanel_t1[normCounter].add(new JScrollPane(imageTopPanel[normCounter]), BorderLayout.CENTER);
		
		normPanel[normCounter] = new JPanel();
		normPanel[normCounter].setLayout(new BorderLayout());
		normPanel[normCounter].add(buttonPanel_t1[normCounter], BorderLayout.WEST);
		normPanel[normCounter].add(rButtonPanel_t1[normCounter], BorderLayout.CENTER);
		normPanel[normCounter].add(customPanel_t1[normCounter], BorderLayout.EAST);
		
	}
	
	public void enableAllDetailChecks(boolean enable){
		
		this.attrNameCheck.setEnabled(enable);
		this.fuzzySetCheck.setEnabled(enable);
		this.performanceCheck.setEnabled(enable);
		this.outputCheck.setEnabled(enable);
	}
	
	public void deselectAll(){
		
		this.attrNameCheck.setSelected(false);
		this.fuzzySetCheck.setSelected(false);
		this.performanceCheck.setSelected(false);
		this.outputCheck.setSelected(false);
	}
	
	public void selectCustomDetailChecks(TreeSet<Integer> details){
		
		deselectAll();
		
		for(Integer det: details){
			
			checkBoxSet.get(det).setSelected(true);
		}
	}
	
	//##################################################################################
	//GET and SET
	//##################################################################################

	public JTextArea[] getTextField_t1() {
		return textArea_t1;
	}

	public void setTextArea_t1(JTextArea[] textArea_t1) {
		this.textArea_t1 = textArea_t1;
	}

	public JPanel[] getCustomPanel_t1() {
		return customPanel_t1;
	}

	public void setCustomPanel_t1(JPanel[] customPanel_t1) {
		this.customPanel_t1 = customPanel_t1;
	}

	public void setImagePanel(JPanel[] imagePanel) {
		this.imagePanel = imagePanel;
	}

	public Image[] gettImage() {
		return tImage;
	}

	public void settImage(Image[] tImage) {
		this.tImage = tImage;
	}

	public DrawPanel getDp() {
		return dp;
	}

	public void setDp(DrawPanel dp) {
		this.dp = dp;
	}

	public JPanel[] getImagePanel() {
		return imagePanel;
	}

	public JSpinner getCustomSizeSpinner() {
		return customSizeSpinner;
	}

	public void setCustomSizeSpinner(JSpinner customSizeSpinner) {
		this.customSizeSpinner = customSizeSpinner;
	}


}
