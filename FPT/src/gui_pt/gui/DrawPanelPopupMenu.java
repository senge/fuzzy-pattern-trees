package gui_pt.gui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class DrawPanelPopupMenu extends JPopupMenu implements ActionListener{
	
	JMenuItem moveFinger;
	JMenuItem defaultCursor;
	
	private DrawPanel dp;
		
	private static DrawPanelPopupMenu instance;
	
	private DrawPanelPopupMenu(int xPos, int yPos, DrawPanel dp)
	{				
		this.dp = dp;
		moveFinger = new JMenuItem(new ImageIcon("res/icons/openhand.png"));
		moveFinger.setActionCommand("move");
		moveFinger.addActionListener(this);
		
		defaultCursor = new JMenuItem(new ImageIcon("res/icons/cursor-arrow.png"));
		defaultCursor.setActionCommand("default");
		defaultCursor.addActionListener(this);
		
		this.add(defaultCursor);
		this.add(moveFinger);
		
		this.setLocation(xPos,yPos);
		this.setVisible(true);
	}
	
	public static DrawPanelPopupMenu getInstence(int xPos, int yPos, DrawPanel dp)
	{
		if(instance == null)
		{
			instance = new DrawPanelPopupMenu(xPos, yPos, dp);
			return instance;
		}
		
		instance.setLocation(xPos, yPos);
		instance.setDp(dp);
		instance.setVisible(true);
			
		return instance;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if(arg0.getActionCommand().equals("move"))
		{
			Cursor c = dp.getToolkit().createCustomCursor(
				new ImageIcon("res/icons/openhand2.png").getImage(),
				new Point(16,16), "move");
			this.dp.setCursor(c);
			this.setVisible(false);
		}
		else if(arg0.getActionCommand().equals("default"))
		{
			this.dp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			this.setVisible(false);
		}
	}
	
	public DrawPanel getDp() {
		return dp;
	}

	public void setDp(DrawPanel dp) {
		this.dp = dp;
	}


}
