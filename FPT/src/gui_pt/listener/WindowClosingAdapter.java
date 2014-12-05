package gui_pt.listener;

import java.awt.event.WindowEvent;



public class WindowClosingAdapter extends DefaultWindowListener{
	
	@Override
	public void windowClosed(WindowEvent e) {
		
//		System.exit(0);
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		
		System.exit(0);
	}

}
