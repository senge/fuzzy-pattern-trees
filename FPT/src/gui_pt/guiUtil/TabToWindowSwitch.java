package gui_pt.guiUtil;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * Diese Klasse verwaltet und realisiert den Tausch vom Tab eines JTabbedPane in ein JFrame und zurück.
 * @author Sascha
 *
 */
public class TabToWindowSwitch implements WindowListener{
	
	private JPanel barterPanel;
	private TabHeadPanel controler;
	private JTabbedPane tabbedPane;
	private JFrame jw;
	
	//###########################################################################################
	// CONSTRUCTOR
	//###########################################################################################
	
	public TabToWindowSwitch(JTabbedPane tabbedPane, JPanel barterPanel){
		
		this.barterPanel = barterPanel;
		this.tabbedPane = tabbedPane;
	}
	
	//###########################################################################################
	// METHODES
	//###########################################################################################
	
	/**
	 * 
	 */
	public void switchToWindow(){
		
		tabbedPane.remove(tabbedPane.indexOfComponent(barterPanel));
		tabbedPane.validate();
		jw = new JFrame();
		jw.addWindowListener(this);

		Container cp = jw.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(new JScrollPane(barterPanel), BorderLayout.CENTER);
		cp.add(this.controler, BorderLayout.NORTH);
		jw.setSize(600,600);
		jw.setLocation(400,200);
		jw.setVisible(true);
		
		this.controler.setWindow(true);
	}
	
	public void switchToTab(){
		
		tabbedPane.add(barterPanel);
		tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(barterPanel), controler);
		this.jw.dispose();
		this.controler.setWindow(false);
	}
	
	//###########################################################################################
	//GET and SET
	//###########################################################################################

	public JPanel getBarterPanel() {
		return barterPanel;
	}

	public void setBarterPanel(JPanel barterPanel) {
		this.barterPanel = barterPanel;
	}

	public TabHeadPanel getControler() {
		return controler;
	}

	public void setControler(TabHeadPanel controler) {
		this.controler = controler;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public void setTabbedPane(JTabbedPane tabbedPane) {
		this.tabbedPane = tabbedPane;
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	//#########################################################################################
	//WindowListener
	//#########################################################################################

	@Override
	public void windowClosed(WindowEvent arg0) {
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {

		this.controler.switchButton.doClick();	
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public JFrame getJw() {
		return jw;
	}

	public void setJw(JFrame jw) {
		this.jw = jw;
	}

}
