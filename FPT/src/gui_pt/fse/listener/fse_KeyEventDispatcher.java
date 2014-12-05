package gui_pt.fse.listener;

import gui_pt.fse.FSE_Frame;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

public class fse_KeyEventDispatcher implements KeyEventDispatcher{
	
	private FSE_Frame fse_Frame;
	
	public fse_KeyEventDispatcher(FSE_Frame fse_Frame){
		
		this.fse_Frame = fse_Frame;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent arg0) {

		if(arg0.getID() == KeyEvent.KEY_PRESSED)
		{
			//Control Down
			if(arg0.isControlDown()){
				
				if(arg0.getKeyCode() == KeyEvent.VK_S)
				{
					fse_Frame.storeFocusedCFS();
				}			
			}
		}
		
		return false;
	}

}
