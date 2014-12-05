package gui_pt.listener;

import gui_pt.accessLayer.loader.PTLoader;
import gui_pt.accessLayer.util.AccessPT;
import gui_pt.gui.DefaultPTCreatorDialog;
import gui_pt.gui.MainWindow;
import gui_pt.gui.OpenStreamDialog;
import gui_pt.gui.PTCreatorDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileMenuActionListener implements ActionListener, MenuListener{

	MainWindow mainW;
	
	public FileMenuActionListener(MainWindow mainW)
	{
		this.mainW = mainW;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getActionCommand().equals("createPT"))
		{
			PTCreatorDialog ptcd = new PTCreatorDialog(this.mainW);
		}
		else if(arg0.getActionCommand().equals("createDefaultPT"))
		{
			DefaultPTCreatorDialog dPTCDialog = new DefaultPTCreatorDialog(mainW);
		}
		else if(arg0.getActionCommand().equals("openPTStream"))
		{
			OpenStreamDialog osd = new OpenStreamDialog(mainW);
		}
		else if(arg0.getActionCommand().equals("loadPT"))
		{
			JFileChooser jfc = new JFileChooser();
			jfc.setFileFilter(new FileNameExtensionFilter(null, "arff"));
			
			int i = jfc.showOpenDialog(mainW);
			
			if(i == JFileChooser.APPROVE_OPTION)
			{
				AccessPT accPT;
				PTLoader pt = new PTLoader();
				accPT = pt.loadPT(jfc.getSelectedFile().toString());
				
				this.mainW.addAccPT(accPT, false, null);
				
				this.mainW.validate();
			}
		}
		else if(arg0.getActionCommand().equals("loadPTV"))
		{
			JFileChooser jfc = new JFileChooser();
			
			int load = jfc.showOpenDialog(this.mainW);
			
			if(load == JFileChooser.OPEN_DIALOG)
			{
				File file = jfc.getSelectedFile();
				
				AccessPT accPT = PTLoader.loadPTV(file.toString());
				
				this.mainW.addAccPT(accPT, false, null);
				
				this.mainW.validate();			
			}
		}
//		else if(arg0.getActionCommand().equals("saveAs"))
//		{
//			int index = mainW.getDrawTreeTabbedPane().getSelectedIndex();	
//			
//			if(index >= 0)
//			{				
//				File dir;
//				
//				if(mainW.getPTVstorePackList().get(index).getCurURL() == null)
//				{
//					dir = new File(mainW.getStartW().getStartSettings().getWorkspacePath()+"/workspaceViewer");
//				}
//				else
//				{
//					dir = new File(mainW.getPTVstorePackList().get(index).getCurURL());
//				}
//								
//				JFileChooser jfc = new JFileChooser();
//				jfc.setCurrentDirectory(dir);
//
//				int save = jfc.showSaveDialog(this.mainW);
//				
//				if(save == 0)
//				{
//					File file = null;
//					String strFile = jfc.getSelectedFile().toString();
//					
//					if(strFile.endsWith(".ptvo"))
//					{
//						file = new File(strFile);
//					}
//					else
//					{
//						file = new File(strFile+".ptvo");
//					} 
//					
//					mainW.getStartW().getStartSettings().setLastStorePath(jfc.getSelectedFile().toString());
//					mainW.getPTVstorePackList().get(index).setCurURL(jfc.getSelectedFile().toString());
//					mainW.getPTVstorePackList().get(index).setName(jfc.getSelectedFile().getName());
//					
//					try {
//						FileOutputStream fos = new FileOutputStream(file);
//						ObjectOutputStream oos = new ObjectOutputStream(fos);
//						oos.writeObject(mainW.getPTVstorePackList().get(index));
//						oos.close();
//						fos.close();
//											
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}			
//		}
//		else if(arg0.getActionCommand().equals("save"))
//		{
//			int index = mainW.getDrawTreeTabbedPane().getSelectedIndex();
//						
//			if(index >= 0)
//			{								
//				if(mainW.getPTVstorePackList().get(index).getCurURL() == null)
//				{
//					actionPerformed(new ActionEvent(mainW.getSaveAs(),1001,"saveAs"));
//				}
//				else
//				{
//					File file = new File(mainW.getPTVstorePackList().get(index).getCurURL()
//							+mainW.getPTVstorePackList().get(index).getName()
//							+".ptvo");
//					
//					try {
//						FileOutputStream fos = new FileOutputStream(file);
//						ObjectOutputStream oos = new ObjectOutputStream(fos);
//						oos.writeObject(mainW.getPTVstorePackList().get(index));
//						oos.close();
//						fos.close();
//											
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}				
//			}
//		}
//		else if(arg0.getActionCommand().equals("load"))
//		{
//			JFileChooser jfc = new JFileChooser();
//			jfc.setFileFilter(new FileNameExtensionFilter(null, "ptvo"));
//			
//			if(mainW.getStartW().getStartSettings().getLastStorePath() != null)
//			{
//				jfc.setCurrentDirectory(new File(mainW.getStartW().getStartSettings().getLastStorePath()));
//			}
//			
//			int load = jfc.showOpenDialog(this.mainW);
//			
//			if(load == JFileChooser.OPEN_DIALOG)
//			{
//				File file = jfc.getSelectedFile();
//				mainW.getStartW().getStartSettings().setLastStorePath(file.toString());
//				
//				String name = file.getName().split("\\.")[0];
//				
//				PTVstorePack ptvSP = null;
//				
//				try {
//					FileInputStream fis = new FileInputStream(file);
//					ObjectInputStream ois = new ObjectInputStream(fis);
//					ptvSP = (PTVstorePack)ois.readObject();
//					ois.close();
//					fis.close();
//										
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (ClassNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				AccessNode[] accRootPack = new AccessNode[ptvSP.getDrawTreePack().length];
//				for(int i=0; i<accRootPack.length; i++)
//				{
//					accRootPack[i] = ptvSP
//										.getDrawTreePack()[i]
//										.getRoot()
//										.getAccNode();
//				}
//				
//				mainW.buildDrawPanelCards(accRootPack
//											, ptvSP
//											, name
//											, new TreeClassPack()
//											, false);
//			}
//		}
	}

	@Override
	public void menuCanceled(MenuEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void menuDeselected(MenuEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void menuSelected(MenuEvent arg0) {
		
//		if(arg0.getSource() instanceof JMenu)
//		{
//			if(((JMenu)arg0.getSource()).getName().equals("file"))
//			{
//				if(mainW.getPTVstorePackList().size() == 0)
//				{
//					mainW.getSave().setEnabled(false);
//					mainW.getSaveAs().setEnabled(false);
//				}
//				else
//				{
//					mainW.getSave().setEnabled(true);
//					mainW.getSaveAs().setEnabled(true);
//				}
//			}
//		}		
	}
}
