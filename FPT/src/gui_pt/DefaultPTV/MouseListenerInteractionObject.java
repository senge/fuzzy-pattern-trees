package gui_pt.DefaultPTV;

import java.awt.event.MouseEvent;

public interface MouseListenerInteractionObject {
	
	public int PRESSED = 0;
	
	public abstract void interact(MouseEvent me, int eventType, Object stuff);
	public abstract boolean isEntered(MouseEvent me);
	public abstract void addToCleverMouseListener();
	
	/*
	 * class MyMouseListener extends CleverMouseListener
	 * ...
	 * 
	 * 
	 * ...
	 * e.g.
	 * mousePressed(MouseEvent arg0){
	 * 
	 * 		if(focusedObject != null)
	 * 		{
	 * 			if(focusedObject.isEntered(arg0))
	 * 			{
	 * 				focusedObject.interact(arg0, MouseEvent.PRESSED, null);
	 * 			}
	 * 		}
	 * 		
	 * 		for(MouseListenerInteractionObject mlio: pressedList)
	 * 		{
	 * 			if(mlio.isEntered(arg0))
	 * 			{
	 * 				mlio.interact(arg0, MouseEvent.PRESSED, null);
	 * 				focusedObject = mlio;
	 * 			}
	 * 		}
	 * 
	 *  }
	 *  ...
	 *
	 */

}
