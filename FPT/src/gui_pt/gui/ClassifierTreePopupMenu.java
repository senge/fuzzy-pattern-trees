package gui_pt.gui;

import gui_pt.accessLayer.util.AccessPT;
import gui_pt.guiUtil.UserObjectWrapper;
import gui_pt.plugin.PTVisualisation;
import gui_pt.stream.StreamView;
import gui_pt.visualisation.FuzzyMapPTV;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;

import weka.core.Instance;

public class ClassifierTreePopupMenu extends JPopupMenu implements ActionListener{
	
	MainWindow mainW;
	
	JMenuItem addDefaultPTV;
	JMenuItem addFuzzyMapPTV;
	JMenuItem savePT;
	JMenuItem connect;
	
	UserObjectWrapper uop;
	int mode;
	DefaultMutableTreeNode parent;
	MouseEvent mouseEvent;
	
	public ClassifierTreePopupMenu(MainWindow mainW
			, UserObjectWrapper uop
			, int mode
			, DefaultMutableTreeNode parent
			, MouseEvent mouseEvent){
		
		this.mainW = mainW;
		
		this.uop = uop;
		this.mode = mode;
		this.parent = parent;
		this.mouseEvent = mouseEvent;
		
		addDefaultPTV = new JMenuItem("new DefaultPTV");
		addDefaultPTV.setActionCommand("default");
		addDefaultPTV.addActionListener(this);
		
		addFuzzyMapPTV = new JMenuItem("new FuzzyMapPTV");
		addFuzzyMapPTV.setActionCommand("fuzzyMap");
		addFuzzyMapPTV.addActionListener(this);
		
		savePT = new JMenuItem("Save Pattern-Tree");
		savePT.setActionCommand("savePT");
		savePT.addActionListener(this);
		
		connect = new JMenuItem("connect");
		connect.setActionCommand("connect");
		connect.addActionListener(this);
		
		this.add(addDefaultPTV);
		this.add(addFuzzyMapPTV);
		this.addSeparator();
		this.add(savePT);
		this.addSeparator();
		this.add(connect);
	}
	//##########################################################################################
	//ActionListener
	//##########################################################################################

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		if(arg0.getActionCommand().equals("default"))
		{
			
		}
		else if(arg0.getActionCommand().equals("fuzzyMap"))
		{
			FuzzyMapPTV ptv = new FuzzyMapPTV();
			DefaultMutableTreeNode ptvNode = null;
			
			switch(this.mode){
			
			case 0:
				AccessPT accPT = (AccessPT)uop.obj;
				ptv.buildVisualisation(accPT, null);
				ptvNode = mainW.addVisualisation(ptv, parent, 0);
				mainW.openVisualisation(ptv, ptvNode);
				break;
				
			case 1:
				StreamView sv = (StreamView)uop.obj;
				sv.getStreamAssists().add(ptv);
				ptv.linkStreamView(sv);
				ptv.buildVisualisation(null, null);

				ptvNode = mainW.addVisualisation(ptv, parent, 0);
				mainW.openVisualisation(ptv, ptvNode);
				break;
			}
		}
		else if(arg0.getActionCommand().equals("savePT"))
		{			
			AccessPT accPT= (AccessPT)uop.obj;
			
			JFileChooser jfc = new JFileChooser();
			
			int i = jfc.showSaveDialog(mainW);
			
			if(i == JFileChooser.APPROVE_OPTION)
			{
				try {
					FileOutputStream fos = new FileOutputStream(jfc.getSelectedFile());
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					
					oos.writeObject(accPT);
					
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
		else if(arg0.getActionCommand().equals("connect"))
		{
			ConnectionDialog cd = new ConnectionDialog(mainW, parent);
		}
		else if(arg0.getActionCommand().equals("connectOk"))
		{
			Container con = ((JMenuItem)arg0.getSource()).getParent();
			
			JCheckBox checkBox = null;
			boolean isFirst = true;
			Instance protoInstance = null;
			ArrayList<PTVisualisation> helpList = new ArrayList<PTVisualisation>();
			for(int i=0; i< con.getComponentCount()-1; i++)
			{
				checkBox = (JCheckBox)con.getComponent(i);
				if(checkBox.isSelected())
				{
					if(isFirst)
					{
						protoInstance = 
							((PTVisualisation)
							((UserObjectWrapper)
							((DefaultMutableTreeNode)
								parent.getChildAt(i)).getUserObject()).obj).getProtoInstance();
						
						isFirst = false;
					}
					else
					{
						((PTVisualisation)
								((UserObjectWrapper)
								((DefaultMutableTreeNode)
									parent.getChildAt(i)).getUserObject()).obj).setProtoInstance(protoInstance);
					}
					helpList.add((PTVisualisation)
							((UserObjectWrapper)
							((DefaultMutableTreeNode)
								parent.getChildAt(i)).getUserObject()).obj);										
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
		}	
	}
}
